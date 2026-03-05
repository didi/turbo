package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.entity.FlowInstanceMappingPO;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link FlowInstanceMappingDAO}.
 * Stores flow instance mappings in a {@link ConcurrentHashMap} instead of a database,
 * enabling use of the engine without Spring or a database connection.
 */
public class InMemoryFlowInstanceMappingDAO implements FlowInstanceMappingDAO {

    private final Map<Long, FlowInstanceMappingPO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public List<FlowInstanceMappingPO> selectFlowInstanceMappingPOList(String flowInstanceId, String nodeInstanceId) {
        if (flowInstanceId == null || nodeInstanceId == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(po -> flowInstanceId.equals(po.getFlowInstanceId())
                        && nodeInstanceId.equals(po.getNodeInstanceId()))
                .collect(Collectors.toList());
    }

    @Override
    public FlowInstanceMappingPO selectFlowInstanceMappingPO(String flowInstanceId, String nodeInstanceId) {
        if (flowInstanceId == null || nodeInstanceId == null) {
            return null;
        }
        return store.values().stream()
                .filter(po -> flowInstanceId.equals(po.getFlowInstanceId())
                        && nodeInstanceId.equals(po.getNodeInstanceId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public int insert(FlowInstanceMappingPO po) {
        if (po == null) {
            return -1;
        }
        long id = idGenerator.incrementAndGet();
        po.setId(id);
        store.put(id, po);
        return 1;
    }

    @Override
    public void updateType(String flowInstanceId, String nodeInstanceId, int type) {
        if (flowInstanceId == null || nodeInstanceId == null) {
            return;
        }
        store.values().stream()
                .filter(po -> flowInstanceId.equals(po.getFlowInstanceId())
                        && nodeInstanceId.equals(po.getNodeInstanceId()))
                .forEach(po -> {
                    po.setType(type);
                    po.setModifyTime(new Date());
                });
    }
}
