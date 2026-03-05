package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of {@link ProcessInstanceDAO}.
 * Stores flow instances in a {@link ConcurrentHashMap} instead of a database,
 * enabling use of the engine without Spring or a database connection.
 */
public class InMemoryProcessInstanceDAO implements ProcessInstanceDAO {

    private final Map<String, FlowInstancePO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public FlowInstancePO selectByFlowInstanceId(String flowInstanceId) {
        if (flowInstanceId == null) {
            return null;
        }
        return store.get(flowInstanceId);
    }

    @Override
    public int insert(FlowInstancePO po) {
        if (po == null || po.getFlowInstanceId() == null) {
            return -1;
        }
        po.setId(idGenerator.incrementAndGet());
        store.put(po.getFlowInstanceId(), po);
        return 1;
    }

    @Override
    public void updateStatus(String flowInstanceId, int status) {
        FlowInstancePO po = selectByFlowInstanceId(flowInstanceId);
        if (po != null) {
            updateStatus(po, status);
        }
    }

    @Override
    public void updateStatus(FlowInstancePO po, int status) {
        if (po == null) {
            return;
        }
        po.setStatus(status);
        po.setModifyTime(new Date());
    }
}
