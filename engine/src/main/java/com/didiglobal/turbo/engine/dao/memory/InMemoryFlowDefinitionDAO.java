package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.dao.FlowDefinitionDAO;
import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of {@link FlowDefinitionDAO}.
 * Stores flow definitions in a {@link ConcurrentHashMap} instead of a database,
 * enabling use of the engine without Spring or a database connection.
 */
public class InMemoryFlowDefinitionDAO implements FlowDefinitionDAO {

    private final Map<String, FlowDefinitionPO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public int insert(FlowDefinitionPO po) {
        if (po == null || po.getFlowModuleId() == null) {
            return -1;
        }
        po.setId(idGenerator.incrementAndGet());
        store.put(po.getFlowModuleId(), po);
        return 1;
    }

    @Override
    public int updateByModuleId(FlowDefinitionPO po) {
        if (po == null || po.getFlowModuleId() == null) {
            return -1;
        }
        FlowDefinitionPO existing = store.get(po.getFlowModuleId());
        if (existing == null) {
            return 0;
        }
        if (po.getFlowModel() != null) {
            existing.setFlowModel(po.getFlowModel());
        }
        if (po.getFlowName() != null) {
            existing.setFlowName(po.getFlowName());
        }
        if (po.getFlowKey() != null) {
            existing.setFlowKey(po.getFlowKey());
        }
        if (po.getStatus() != null) {
            existing.setStatus(po.getStatus());
        }
        return 1;
    }

    @Override
    public FlowDefinitionPO selectByModuleId(String flowModuleId) {
        if (flowModuleId == null) {
            return null;
        }
        return store.get(flowModuleId);
    }
}
