package com.didiglobal.turbo.engine.util;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class PluginSqlExecutorUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginSqlExecutorUtil.class);

    private static DruidDataSource dataSource;
    private static final String JDBC_URL = "turbo.plugin.jdbc.url";
    private static final String USERNAME = "turbo.plugin.jdbc.username";
    private static final String PASSWORD = "turbo.plugin.jdbc.password";
    private static final String DRIVER_CLASS_NAME = "turbo.plugin.jdbc.driver";
    private static final String MAX_POOL_SIZE = "turbo.plugin.jdbc.maximumPoolSize";

    static {
        try {
            // 初始化 HikariCP 数据源
            Properties properties = new Properties();
            ClassLoader classLoader = PluginSqlExecutorUtil.class.getClassLoader();
            if (classLoader.getResource("plugin.properties") == null) {
                // 如果配置文件不存在，则不初始化数据源
                LOGGER.warn("Configuration file 'plugin.properties' not found. Skipping database initialization.");
                dataSource = null;
            } else {
                properties.load(PluginSqlExecutorUtil.class.getClassLoader().getResourceAsStream("plugin.properties"));
                dataSource = new DruidDataSource();
                dataSource.setUrl(properties.getProperty(JDBC_URL));
                dataSource.setUsername(properties.getProperty(USERNAME));
                dataSource.setPassword(properties.getProperty(PASSWORD));
                dataSource.setDriverClassName(properties.getProperty(DRIVER_CLASS_NAME));
                // 配置 Druid 数据源的特性
                dataSource.setInitialSize(1);
                dataSource.setMinIdle(1);
                dataSource.setMaxActive(Integer.parseInt(properties.getProperty(MAX_POOL_SIZE, "10")));
                dataSource.setMaxWait(60000);
                // 验证配置
                if (!validateConfig(dataSource)) {
                    dataSource = null;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    private static boolean validateConfig(DruidDataSource dataSource) {
        if (dataSource == null) {
            return false;
        }
        if (StringUtils.isEmpty(dataSource.getUrl())) {
            LOGGER.error("Plugin JDBC URL is empty");
            return false;
        }
        if (StringUtils.isEmpty(dataSource.getUsername())) {
            LOGGER.error("Plugin JDBC username is empty");
            return false;
        }
        if (StringUtils.isEmpty(dataSource.getPassword())) {
            LOGGER.warn("Plugin JDBC password is empty");
        }
        if (StringUtils.isEmpty(dataSource.getDriverClassName())) {
            LOGGER.error("Plugin JDBC driver class name is empty");
            return false;
        }
        return true;
    }

    /**
     * 执行 SQL 文件
     *
     * @param sqlFilePath    SQL 文件路径
     * @param useTransaction 是否启用事务
     * @param delimiter      SQL 语句的分隔符
     * @throws IOException  读取文件异常
     * @throws SQLException 数据库操作异常
     */
    public static void executeSqlFile(String sqlFilePath, boolean useTransaction, String delimiter) throws IOException, SQLException {
        if (delimiter == null || delimiter.isEmpty()) {
            delimiter = ";"; // 默认使用分号作为分隔符
        }

        try (Connection connection = dataSource.getConnection();
             BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath))) {

            connection.setAutoCommit(!useTransaction);

            try (Statement statement = connection.createStatement()) {
                StringBuilder sqlBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("#")) {
                        continue; // 跳过注释行或空行
                    }
                    sqlBuilder.append(line).append(" "); // 防止语句分割时丢失空格

                    // 检测语句是否完成（以分隔符结尾）
                    if (line.endsWith(delimiter)) {
                        String sql = sqlBuilder.toString().replace(delimiter, "").trim(); // 移除分隔符
                        try {
                            statement.execute(sql);
                            LOGGER.info("Executed SQL: " + sql);
                        } catch (SQLException e) {
                            throw new SQLException("Error executing SQL: " + sql, e);
                        }
                        sqlBuilder.setLength(0); // 清空当前 SQL 构造器
                    }
                }
                // 提交事务
                if (useTransaction) {
                    connection.commit();
                }
            } catch (SQLException e) {
                if (useTransaction) {
                    connection.rollback();
                }
                connection.close();
                throw e;
            }
        }
    }

    /**
     * 关闭数据源
     */
    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
