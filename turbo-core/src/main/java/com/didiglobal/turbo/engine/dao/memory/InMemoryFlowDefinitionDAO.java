package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.dao.FlowDefinitionDAO;
import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存版流程定义 DAO — 用途、原理与和 Spring 版本的区别
 *
 * <h2>这是什么？</h2>
 * <p>{@code InMemoryFlowDefinitionDAO} 是 {@link FlowDefinitionDAO} 接口的一个
 * <b>纯内存实现</b>。它把流程定义对象（{@link FlowDefinitionPO}）直接存储在
 * JVM 堆内存的 {@link ConcurrentHashMap} 里，<b>不依赖任何数据库、不依赖 Spring 框架</b>。
 *
 * <h2>和引入 Spring 后的版本有什么区别？</h2>
 *
 * <table border="1" cellpadding="4">
 *   <tr>
 *     <th>对比维度</th>
 *     <th>InMemoryFlowDefinitionDAO（本类）</th>
 *     <th>FlowDefinitionDAOImpl（Spring + MyBatis-Plus 版）</th>
 *   </tr>
 *   <tr>
 *     <td>存储位置</td>
 *     <td>JVM 堆内存（{@code ConcurrentHashMap}）</td>
 *     <td>关系数据库（MySQL 等），表名 {@code em_flow_definition}</td>
 *   </tr>
 *   <tr>
 *     <td>数据持久化</td>
 *     <td>❌ 进程退出后数据全部丢失</td>
 *     <td>✅ 数据持久化到数据库，进程重启后仍然存在</td>
 *   </tr>
 *   <tr>
 *     <td>框架依赖</td>
 *     <td>无（零依赖，只用 JDK 标准库）</td>
 *     <td>依赖 Spring、MyBatis-Plus、dynamic-datasource</td>
 *   </tr>
 *   <tr>
 *     <td>启动方式</td>
 *     <td>{@code new InMemoryFlowDefinitionDAO()} 直接 new，无需容器</td>
 *     <td>{@code @Autowired} 或从 Spring ApplicationContext 获取</td>
 *   </tr>
 *   <tr>
 *     <td>事务支持</td>
 *     <td>❌ 无事务，多线程写入依赖 ConcurrentHashMap 的线程安全性</td>
 *     <td>✅ 支持 Spring 声明式事务（{@code @Transactional}）</td>
 *   </tr>
 *   <tr>
 *     <td>适合场景</td>
 *     <td>单元测试、快速验证、不依赖 Spring 的轻量集成</td>
 *     <td>生产环境，需要持久化存储的业务系统</td>
 *   </tr>
 * </table>
 *
 * <h2>使用场景</h2>
 * <ol>
 *   <li><b>单元 / 集成测试</b>：测试时不需要真实数据库，直接用内存版即可。</li>
 *   <li><b>非 Spring 项目接入</b>：将本类与 {@code TurboEngineBuilder} 配合使用，
 *       无需引入任何 Spring 依赖即可运行完整工作流引擎。</li>
 *   <li><b>自定义实现的参考模板</b>：如需对接其他存储（Redis、MongoDB、JDBC 等），
 *       可以参考本类的逻辑实现对应的 {@link FlowDefinitionDAO}。</li>
 * </ol>
 *
 * <h2>如何对接真实数据库（不用 Spring）</h2>
 * <p>如果你不使用 Spring 但仍需数据库持久化，只需自行实现 {@link FlowDefinitionDAO}
 * 接口，用 JDBC / JPA / 任何 ORM 替换 {@code ConcurrentHashMap} 操作即可，
 * {@code TurboEngineBuilder} 接受任意实现：
 * <pre>{@code
 * ProcessEngine engine = TurboEngineBuilder.create()
 *     .flowDefinitionDAO(new MyJdbcFlowDefinitionDAO(dataSource))
 *     // ...
 *     .build();
 * }</pre>
 *
 * @see FlowDefinitionDAO
 * @see com.didiglobal.turbo.engine.engine.TurboEngineBuilder
 */
public class InMemoryFlowDefinitionDAO implements FlowDefinitionDAO {

    /**
     * 流程定义的内存存储。
     * key = flowModuleId（流程模块唯一标识），value = 流程定义对象。
     * <p>使用 {@link ConcurrentHashMap} 保证多线程读写安全。
     */
    private final Map<String, FlowDefinitionPO> store = new ConcurrentHashMap<>();

    /**
     * 新增一条流程定义记录。
     *
     * <p><b>内存实现</b>：直接 put 到 Map，key 为 {@code po.getFlowModuleId()}。
     * <p><b>Spring 版等价操作</b>：{@code baseMapper.insert(po)} → 执行
     * {@code INSERT INTO em_flow_definition ...}
     *
     * @param po 流程定义对象
     * @return 1 表示成功
     */
    @Override
    public int insert(FlowDefinitionPO po) {
        store.put(po.getFlowModuleId(), po);
        return 1;
    }

    /**
     * 根据 flowModuleId 更新流程定义。
     *
     * <p><b>内存实现</b>：从 Map 取出已存在的对象，逐字段更新非空属性（partial update 语义）。
     * <p><b>Spring 版等价操作</b>：{@code baseMapper.update(po, updateWrapper)} →
     * 执行 {@code UPDATE em_flow_definition SET ... WHERE flow_module_id = ?}
     *
     * @param po 包含要更新字段的流程定义对象（flowModuleId 不可为空）
     * @return 1 表示成功更新，0 表示未找到对应记录
     */
    @Override
    public int updateByModuleId(FlowDefinitionPO po) {
        FlowDefinitionPO existing = store.get(po.getFlowModuleId());
        if (existing == null) {
            return 0;
        }
        // 只更新非空字段，与 Spring 版的 updateWrapper 行为一致
        if (po.getFlowModel() != null) existing.setFlowModel(po.getFlowModel());
        if (po.getFlowName() != null)  existing.setFlowName(po.getFlowName());
        if (po.getFlowKey() != null)   existing.setFlowKey(po.getFlowKey());
        if (po.getStatus() != null)    existing.setStatus(po.getStatus());
        return 1;
    }

    /**
     * 根据 flowModuleId 查询流程定义。
     *
     * <p><b>内存实现</b>：直接 {@code map.get(flowModuleId)}。
     * <p><b>Spring 版等价操作</b>：{@code baseMapper.selectByFlowModuleId(flowModuleId)} →
     * 执行 {@code SELECT * FROM em_flow_definition WHERE flow_module_id = ? LIMIT 1}
     *
     * @param flowModuleId 流程模块 ID
     * @return 流程定义对象，不存在时返回 {@code null}
     */
    @Override
    public FlowDefinitionPO selectByModuleId(String flowModuleId) {
        return store.get(flowModuleId);
    }
}
