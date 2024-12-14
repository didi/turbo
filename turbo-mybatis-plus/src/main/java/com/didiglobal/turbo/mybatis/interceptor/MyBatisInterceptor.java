package com.didiglobal.turbo.mybatis.interceptor;

import com.baomidou.mybatisplus.annotation.TableName;
import com.didiglobal.turbo.mybatis.CustomOperationHandler;
import com.didiglobal.turbo.mybatis.CustomOperationHandlerRegistry;
import com.didiglobal.turbo.engine.common.EntityPOEnum;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class MyBatisInterceptor implements Interceptor {

    private SqlSessionFactory sqlSessionFactory;

    public MyBatisInterceptor(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    /**
     * 拦截 MyBatis 的调用，用于在查询或更新操作前后执行自定义逻辑。
     *
     * @param invocation 包含调用的上下文和参数
     * @return 方法调用的结果
     * @throws Throwable 可能抛出的任何异常
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameterObject = invocation.getArgs()[1];
        SqlCommandType commandType = mappedStatement.getSqlCommandType();

        // 调用原始查询，获取原始结果集
        Object originalResult = invocation.proceed();

        handleCustomOperation(commandType, mappedStatement, parameterObject, originalResult);

        return originalResult;
    }

    /**
     * 根据传入的命令类型、映射语句、参数和结果处理自定义操作。
     *
     * @param commandType     SQL 命令类型
     * @param mappedStatement MyBatis 映射语句
     * @param parameterObject 输入参数对象
     * @param originalResult  原始查询或更新的结果
     */
    private void handleCustomOperation(SqlCommandType commandType, MappedStatement mappedStatement, Object parameterObject, Object originalResult) {
        String tableName = getTableName(parameterObject);
        if (tableName == null) {
            tableName = getTableName(originalResult);
        }
        EntityPOEnum entityEnum = getEntityPOEnumByTableName(tableName);

        if (entityEnum != null) {
            List<CustomOperationHandler> handlers = CustomOperationHandlerRegistry.getHandlers(entityEnum);
            if (handlers != null) {
                for (CustomOperationHandler handler : handlers) {
                    handler.handle(commandType, mappedStatement, parameterObject, originalResult, sqlSessionFactory);
                }
            }
        }
    }

    /**
     * 获取对象对应的表名，如果对象是集合或数组，递归获取其中第一个元素的表名。
     *
     * @param object 要提取表名的对象
     * @return 表名，如果对象未注解或为空则返回 null
     */
    private String getTableName(Object object) {
        if (object == null) {
            return null;
        }
        // 处理批量插入时的 Map 参数
        if (object instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) object;
            Object value = paramMap.getOrDefault("list", null);
            if (value != null) {
                // 假设批量插入的实体在 Map 中以特定键存在，如 "list" 或其他约定
                if (value instanceof List) {
                    List<?> list = (List<?>) value;
                    if (!list.isEmpty()) {
                        return getTableName(list.get(0));
                    }
                } else if (value.getClass().isArray()) {
                    Object[] array = (Object[]) value;
                    if (array.length > 0) {
                        return getTableName(array[0]);
                    }
                } else {
                    return getTableName(value);
                }
            }
        }
        // 处理单个对象的情况
        if (object instanceof List) {
            List<?> list = (List<?>) object;
            if (!list.isEmpty()) {
                return getTableName(list.get(0));
            }
        } else if (object.getClass().isArray()) {
            Object[] array = (Object[]) object;
            if (array.length > 0) {
                return getTableName(array[0]);
            }
        }
        Class<?> clazz = object.getClass();
        if (clazz.isAnnotationPresent(TableName.class)) {
            TableName tableNameAnnotation = clazz.getAnnotation(TableName.class);
            return tableNameAnnotation.value();
        }
        return null;
    }

    private EntityPOEnum getEntityPOEnumByTableName(String tableName) {
        for (EntityPOEnum entity : EntityPOEnum.values()) {
            if (entity.getTableName().equals(tableName)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
