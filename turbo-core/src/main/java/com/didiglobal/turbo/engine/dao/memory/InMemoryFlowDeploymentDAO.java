package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存版流程部署 DAO。
 *
 * <p>每次调用 {@link #insert} 时会同时维护两个索引：
 * <ul>
 *   <li>{@code byDeployId}：通过 flowDeployId 精确查找某次部署记录</li>
 *   <li>{@code latestByModuleId}：通过 flowModuleId 查找该流程最近一次部署（后插入者覆盖）</li>
 * </ul>
 *
 * <p><b>与 Spring 版（FlowDeploymentDAOImpl）的区别：</b>
 * <ul>
 *   <li>Spring 版将记录写入数据库 {@code em_flow_deployment} 表，数据持久化；
 *       本类写入 JVM 内存，进程退出后数据消失。</li>
 *   <li>Spring 版 {@code selectRecentByFlowModuleId} 通过 SQL 的 {@code ORDER BY create_time DESC LIMIT 1}
 *       取最新部署；本类用 Map 覆盖语义（后 insert 者为最新）。</li>
 *   <li>Spring 版依赖 MyBatis-Plus 和数据库驱动；本类仅依赖 JDK 标准库。</li>
 * </ul>
 *
 * @see FlowDeploymentDAO
 * @see com.didiglobal.turbo.engine.engine.TurboEngineBuilder
 */
public class InMemoryFlowDeploymentDAO implements FlowDeploymentDAO {

    /** 以 flowDeployId 为 key 的全量索引 */
    private final Map<String, FlowDeploymentPO> byDeployId = new ConcurrentHashMap<>();

    /** 以 flowModuleId 为 key、记录最近一次部署的索引 */
    private final Map<String, FlowDeploymentPO> latestByModuleId = new ConcurrentHashMap<>();

    @Override
    public int insert(FlowDeploymentPO po) {
        byDeployId.put(po.getFlowDeployId(), po);
        latestByModuleId.put(po.getFlowModuleId(), po);
        return 1;
    }

    @Override
    public FlowDeploymentPO selectByDeployId(String flowDeployId) {
        return byDeployId.get(flowDeployId);
    }

    /**
     * 返回指定 flowModuleId 最近一次部署记录。
     *
     * <p>内存实现：返回最后一次 insert 的部署对象（Map 覆盖语义）。
     * <p>Spring 版：{@code SELECT ... ORDER BY create_time DESC LIMIT 1}
     */
    @Override
    public FlowDeploymentPO selectRecentByFlowModuleId(String flowModuleId) {
        return latestByModuleId.get(flowModuleId);
    }
}
