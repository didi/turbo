/**
 * 内存版 DAO 实现包 —— 无需 Spring、无需数据库
 *
 * <h2>本包的作用</h2>
 * <p>本包提供了 {@code turbo-core} 中所有 DAO 接口的<b>内存实现</b>，
 * 数据存储在 JVM 堆内存（{@code ConcurrentHashMap} / {@code ArrayList}）中，
 * 无需任何 Spring 容器、数据库连接或 ORM 框架。
 *
 * <h2>提供的类</h2>
 * <ul>
 *   <li>{@link com.didiglobal.turbo.engine.dao.memory.InMemoryFlowDefinitionDAO}</li>
 *   <li>{@link com.didiglobal.turbo.engine.dao.memory.InMemoryFlowDeploymentDAO}</li>
 *   <li>{@link com.didiglobal.turbo.engine.dao.memory.InMemoryProcessInstanceDAO}</li>
 *   <li>{@link com.didiglobal.turbo.engine.dao.memory.InMemoryNodeInstanceDAO}</li>
 *   <li>{@link com.didiglobal.turbo.engine.dao.memory.InMemoryInstanceDataDAO}</li>
 *   <li>{@link com.didiglobal.turbo.engine.dao.memory.InMemoryFlowInstanceMappingDAO}</li>
 *   <li>{@link com.didiglobal.turbo.engine.dao.memory.InMemoryNodeInstanceLogDAO}</li>
 * </ul>
 *
 * <h2>和引入 Spring 后的区别一览</h2>
 *
 * <pre>
 * ┌─────────────────────┬───────────────────────────────────────────┬──────────────────────────────────────────┐
 * │ 对比维度             │ 本包（内存版）                              │ engine 模块（Spring + MyBatis-Plus 版）     │
 * ├─────────────────────┼───────────────────────────────────────────┼──────────────────────────────────────────┤
 * │ 数据存储             │ JVM 堆内存（Map / List）                   │ 关系数据库（MySQL 等）                       │
 * │ 数据持久化           │ ❌ 进程退出后数据全部消失                    │ ✅ 数据永久存储到数据库                       │
 * │ 依赖框架             │ 无（仅 JDK 标准库）                         │ Spring、MyBatis-Plus、dynamic-datasource  │
 * │ 初始化方式           │ new InMemoryXxxDAO() 直接实例化             │ @Autowired 或 Spring 容器注入               │
 * │ 事务支持             │ ❌ 无事务                                   │ ✅ Spring @Transactional                  │
 * │ 适合场景             │ 单元测试、快速验证、非 Spring 轻量接入        │ 生产环境，需要持久化的业务系统                 │
 * └─────────────────────┴───────────────────────────────────────────┴──────────────────────────────────────────┘
 * </pre>
 *
 * <h2>快速上手（非 Spring 项目）</h2>
 * <pre>{@code
 * // 1. 用内存版 DAO 构建引擎（无需任何 Spring 或数据库配置）
 * ProcessEngine engine = TurboEngineBuilder.create()
 *     .flowDefinitionDAO(new InMemoryFlowDefinitionDAO())
 *     .flowDeploymentDAO(new InMemoryFlowDeploymentDAO())
 *     .processInstanceDAO(new InMemoryProcessInstanceDAO())
 *     .nodeInstanceDAO(new InMemoryNodeInstanceDAO())
 *     .instanceDataDAO(new InMemoryInstanceDataDAO())
 *     .flowInstanceMappingDAO(new InMemoryFlowInstanceMappingDAO())
 *     .nodeInstanceLogDAO(new InMemoryNodeInstanceLogDAO())
 *     .build();
 *
 * // 2. 正常使用引擎
 * CreateFlowResult result = engine.createFlow(param);
 * }</pre>
 *
 * <h2>对接真实数据库（不用 Spring）</h2>
 * <p>如果你不使用 Spring 但需要持久化，可以自行实现 DAO 接口（用 JDBC、JPA 等），
 * 替换上述 {@code InMemoryXxxDAO}，{@code TurboEngineBuilder} 接受任意实现。
 *
 * @see com.didiglobal.turbo.engine.engine.TurboEngineBuilder
 */
package com.didiglobal.turbo.engine.dao.memory;
