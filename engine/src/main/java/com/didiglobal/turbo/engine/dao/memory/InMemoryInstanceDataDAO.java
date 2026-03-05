package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link InstanceDataDAO}.
 * Stores instance data in a {@link ConcurrentHashMap} instead of a database,
 * enabling use of the engine without Spring or a database connection.
 */
public class InMemoryInstanceDataDAO implements InstanceDataDAO {

    private final Map<Long, InstanceDataPO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public InstanceDataPO select(String flowInstanceId, String instanceDataId) {
        if (flowInstanceId == null || instanceDataId == null) {
            return null;
        }
        return store.values().stream()
                .filter(po -> flowInstanceId.equals(po.getFlowInstanceId())
                        && instanceDataId.equals(po.getInstanceDataId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public InstanceDataPO selectRecentOne(String flowInstanceId) {
        if (flowInstanceId == null) {
            return null;
        }
        return store.values().stream()
                .filter(po -> flowInstanceId.equals(po.getFlowInstanceId()))
                .max(Comparator.comparingLong(po -> po.getId() == null ? 0L : po.getId()))
                .orElse(null);
    }

    @Override
    public int insert(InstanceDataPO po) {
        if (po == null) {
            return -1;
        }
        long id = idGenerator.incrementAndGet();
        po.setId(id);
        store.put(id, po);
        return 1;
    }

    @Override
    public int updateData(InstanceDataPO po) {
        if (po == null || po.getId() == null) {
            return -1;
        }
        InstanceDataPO existing = store.get(po.getId());
        if (existing == null) {
            return 0;
        }
        if (po.getInstanceData() != null) {
            existing.setInstanceData(po.getInstanceData());
        }
        if (po.getInstanceDataEncode() != null) {
            existing.setInstanceDataEncode(po.getInstanceDataEncode());
        }
        return 1;
    }

    @Override
    public int insertOrUpdate(InstanceDataPO mergePo) {
        if (mergePo.getId() != null) {
            return updateData(mergePo);
        }
        return insert(mergePo);
    }
}
