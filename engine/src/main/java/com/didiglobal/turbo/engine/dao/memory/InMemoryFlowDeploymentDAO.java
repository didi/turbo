package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of {@link FlowDeploymentDAO}.
 * Stores flow deployments in a {@link ConcurrentHashMap} instead of a database,
 * enabling use of the engine without Spring or a database connection.
 */
public class InMemoryFlowDeploymentDAO implements FlowDeploymentDAO {

    private final Map<String, FlowDeploymentPO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public int insert(FlowDeploymentPO po) {
        if (po == null || po.getFlowDeployId() == null) {
            return -1;
        }
        po.setId(idGenerator.incrementAndGet());
        store.put(po.getFlowDeployId(), po);
        return 1;
    }

    @Override
    public FlowDeploymentPO selectByDeployId(String flowDeployId) {
        if (flowDeployId == null) {
            return null;
        }
        return store.get(flowDeployId);
    }

    @Override
    public FlowDeploymentPO selectRecentByFlowModuleId(String flowModuleId) {
        if (flowModuleId == null) {
            return null;
        }
        return store.values().stream()
                .filter(po -> flowModuleId.equals(po.getFlowModuleId()))
                .max(Comparator.comparingLong(po -> po.getId() == null ? 0L : po.getId()))
                .orElse(null);
    }
}
