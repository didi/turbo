## Turbo插件开发指南

### 1. **概述**
Turbo支持插件扩展功能，开发者可以通过编写插件实现自定义逻辑，而无需修改主框架代码。插件使用 SPI 机制进行加载，支持在运行时动态发现和加载。

插件目前支持应用场景包括：
- 增加新的元素节点处理能力
- 使用自定义的ID生成器
- 使用自定义的表达式计算器
---
### 2. **插件结构和要求**
#### 插件目录结构
```
src/
└── main/
   └── resources/
       ├── plugin.properties
       └── META-INF/
           └── services/
               ├── com.didiglobal.turbo.engine.plugin.ElementPlugin
               ├── com.didiglobal.turbo.engine.plugin.ExpressionCalculatorPlugin
               └── com.didiglobal.turbo.engine.plugin.IdGeneratorPlugin
```
#### 根据插件类型需要实现的接口
**插件顶层接口**：
```java
public interface Plugin {
    // turbo插件开关配置格式建议统一为turbo.plugin.support.${pluginName}
    String PLUGIN_SUPPORT_PREFIX = "turbo.plugin.support.";
    // turbo插件初始化文件配置格式建议统一为turbo.plugin.init_sql.${pluginName}
    String PLUGIN_INIT_SQL_FILE_PREFIX = "turbo.plugin.init_sql.";
    /**
     * 插件名称，唯一标识
     */
    String getName();
    /**
     * 插件开关
     */
    Boolean support();
    /**
     * 插件初始化
     */
    Boolean init();
}
```
- **ElementPlugin**：实现该接口，扩展新的元素节点处理能力。
```java
public interface ElementPlugin extends Plugin{
    String ELEMENT_TYPE_PREFIX = "turbo.plugin.element_type.";
    ElementExecutor getElementExecutor();
    ElementValidator getElementValidator();
    Integer getFlowElementType();
}
```
- **ExpressionCalculatorPlugin**：实现该接口，使用自定义的表达式计算器。
```java
public interface ExpressionCalculatorPlugin extends Plugin{
    ExpressionCalculator getExpressionCalculator();
}
```
- **IdGeneratorPlugin**：实现该接口，使用自定义的ID生成器。
```java
public interface IdGeneratorPlugin extends Plugin{
    IdGenerator getIdGenerator();
}
```
--- 
### 3. **开发流程**
- 创建项目（可以用 Maven/Gradle 或直接在现有项目中新增模块）。
- 实现插件接口或继承插件基类。
- 编写配置文件，声明插件初始化等信息。
- 测试插件功能。

#### 步骤 1：创建插件项目
使用 Maven 构建插件项目：
```shell
mvn archetype:generate -DgroupId=com.example.plugin -DartifactId=MyPlugin
```

#### 步骤 2：实现插件功能
示例：
```java
public class MyPlugin implements IdGeneratorPlugin {
   @Override
   public String getName() {
      return "MyPluginName";
   }
   @Override
   public Boolean support() {
      return true;
   }
   @Override
   public Boolean init() {
      System.out.println("MyPlugin initialized");
      return true;
   }
   @Override
   public IdGenerator getIdGenerator() {
      return new MyDefinedIdGenerator();
   }
}
```

#### 步骤 3：添加配置文件，指定加载插件类
在 `src/main/resources/plugin.properties` 中定义插件必要信息，如初始化脚本文件路径等：
```
turbo.plugin.init_sql.ParallelGatewayElementPlugin=sql/parallelGateway.sql
```
在 `src/main/resources/META-INF/services/` 文件夹下创建扩展插件类型接口对应全路径路名文件，并指定插件实现类：

创建 `src/main/resources/META-INF/services/com.didiglobal.turbo.engine.plugin.ElementPlugin` 文件，并写入：
```
com.didiglobal.turbo.plugin.ParallelGatewayElementPlugin
```
#### 步骤 4：测试插件功能
- 初始化测试：
  - 检查插件的初始化逻辑是否正确执行。 
  - 验证插件是否能正确加载配置（如 plugin.properties 或其他配置文件）。
- 功能点测试：
  - 调用插件的主要功能方法，验证输出是否符合预期。 
  - 如果插件涉及外部接口或服务，检查是否能正常连接并获取数据。
--- 
### 4. **插件加载机制**
#### 插件发现与加载
主应用会在初始化时通过 SPI 机制自动发现并加载插件。确保以下条件满足：
- `src/main/resources/META-INF/services/`文件夹下存在对应插件类型的全路径类名文件。
- 全路径类名文件中指定了插件实现类的全路径
#### 插件启动过程
1. 通过 `ServiceLoader` 加载所有插件。
2. 调用插件的 `getName` 方法，检查是否存在插件名称冲突。
3. 调用插件的 `support` 方法，判断是否需要使用该插件。
   1. 如果为元素节点插件，会调用 `getFlowElementType` 方法，判断该类型元素节点是否存在冲突。
4. 调用插件的 `init` 方法，进行初始化操作。
--- 
### 5. **插件DAO扩展**
Turbo为维持DAO层的简洁，未提供直接在DAO层的扩展能力。为解决部分插件需要在原有DAO层进行扩展，Turbo提供通过Mybatis拦截器的方式进行扩展。
#### 步骤 1：实现`CustomOperationHandler`接口
示例：
```java
public class ParallelNodeInstanceHandler implements CustomOperationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelNodeInstanceHandler.class);
    @Override
    public void handle(SqlCommandType commandType, MappedStatement mappedStatement, Object parameterObject, Object originalResult, SqlSessionFactory sqlSessionFactory) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            ParallelNodeInstanceMapper mapper = sqlSession.getMapper(ParallelNodeInstanceMapper.class);
            switch (commandType) {
                case INSERT:
                    handleInsert(parameterObject, mapper);
                    break;
                case UPDATE:
                    handleUpdate(parameterObject, mapper);
                    break;
                case DELETE:
                    handleDelete(parameterObject, mapper);
                    break;
                case SELECT:
                    handleSelect(originalResult, mapper);
                    break;
                default:
                    LOGGER.warn("Unhandled command type: {}", commandType);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred during handling. CommandType={} | ParameterObject={} | OriginalResult={}",
                commandType, parameterObject, originalResult, e);
        } finally {
            sqlSession.close();
        }
    }
}
```
#### 步骤 2：注册自定义操作处理器，并指定处理的PO类型
示例：
```java
@Configuration
@ComponentScan("com.didiglobal.turbo.plugin")
@MapperScan("com.didiglobal.turbo.plugin.dao")
@EnableAutoConfiguration(exclude = {DruidDataSourceAutoConfigure.class})
public class ParallelPluginConfig {

    @PostConstruct
    public void init() {
        CustomOperationHandlerRegistry.registerHandler(EntityPOEnum.NODE_INSTANCE, new ParallelNodeInstanceHandler());
        CustomOperationHandlerRegistry.registerHandler(EntityPOEnum.NODE_INSTANCE_LOG, new ParallelNodeInstanceLogHandler());
    }
}
```
---
### 6. **示例插件**
[并行网关插件](../parallel-plugin/src/main/java/com/didiglobal/turbo/plugin/ParallelGatewayElementPlugin.java)

--- 
### 7. **插件相关配置**
以下是我们希望插件开发者遵循的一些配置项规范

| 配置项                              | 配置名称       | 示例                                                                         | 配置说明                                 |
|----------------------------------|------------|----------------------------------------------------------------------------|--------------------------------------|
|turbo.plugin.support.${pluginName}| 插件开关配置     | turbo.plugin.support.ParallelGatewayElementPlugin=false                    | 用于控制support方法的返回值，默认返回true           |
|turbo.plugin.init_sql.${pluginName}| 数据库初始化脚本路径 | turbo.plugin.init_sql.ParallelGatewayElementPlugin=sql/parallelGateway.sql | 用于指定初始化脚本位置，这个脚本应该是幂等的               |
|turbo.plugin.element_type.${pluginName}| 元素节点类型 | turbo.plugin.element_type.ParallelGatewayElementPlugin=9                   | 支持插件使用方自己去指定元素节点类型，避免多个插件使用相同的元素类型导致冲突 |