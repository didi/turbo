package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存版流程实例映射 DAO。
 *
 * <p>流程实例映射（{@link FlowInstanceMappingPO}）用于记录调用子流程（CallActivity）时，
 * 父流程实例与子流程实例之间的关联关系。普通流程（不含 CallActivity）通常不会产生此类记录。
 *
 * <p><b>与 Spring 版（FlowInstanceMappingDAOImpl）的区别：</b>
 * <ul>
 *   <li>Spring 版持久化到数据库 {@code ei_flow_instance_mapping} 表；本类存储到内存 Map。</li>
 *   <li>Spring 版通过 SQL {@code WHERE flow_instance_id=? AND node_instance_id=?} 查询；
 *       本类通过复合 key {@code "flowInstanceId:nodeInstanceId"} 在 Map 中查找。</li>
 *   <li>{@link #updateType}：Spring 版执行 {@code UPDATE} 语句；本类直接修改内存对象字段。</li>
 * </ul>
 *
 * @see FlowInstanceMappingDAO
 * @see com.didiglobal.turbo.engine.engine.TurboEngineBuilder
 */
public class InMemoryFlowInstanceMappingDAO implements FlowInstanceMappingDAO {

    /** "flowInstanceId:nodeInstanceId" → 映射记录列表（同一节点可有多条，取最后一条为当前值） */
    private final Map<String, List<FlowInstanceMappingPO>> store = new ConcurrentHashMap<>();

    @Override
    public List<FlowInstanceMappingPO> selectFlowInstanceMappingPOList(String flowInstanceId, String nodeInstanceId) {
        return store.getOrDefault(compositeKey(flowInstanceId, nodeInstanceId), Collections.emptyList());
    }

    @Override
    public FlowInstanceMappingPO selectFlowInstanceMappingPO(String flowInstanceId, String nodeInstanceId) {
        List<FlowInstanceMappingPO> list = selectFlowInstanceMappingPOList(flowInstanceId, nodeInstanceId);
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    @Override
    public int insert(FlowInstanceMappingPO po) {
        store.computeIfAbsent(compositeKey(po.getFlowInstanceId(), po.getNodeInstanceId()),
                k -> new ArrayList<>()).add(po);
        return 1;
    }

    /**
     * 更新映射类型。
     *
     * <p>内存实现：取最近一条记录直接修改 {@code type} 字段。
     * <p>Spring 版：{@code UPDATE ei_flow_instance_mapping SET type=? WHERE flow_instance_id=? AND node_instance_id=?}
     */
    @Override
    public void updateType(String flowInstanceId, String nodeInstanceId, int type) {
        FlowInstanceMappingPO po = selectFlowInstanceMappingPO(flowInstanceId, nodeInstanceId);
        if (po != null) {
            po.setType(type);
        }
    }

    private static String compositeKey(String flowInstanceId, String nodeInstanceId) {
        return flowInstanceId + ":" + nodeInstanceId;
    }
}
