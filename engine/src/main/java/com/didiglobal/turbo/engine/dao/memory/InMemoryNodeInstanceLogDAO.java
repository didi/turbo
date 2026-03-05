package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of {@link NodeInstanceLogDAO}.
 * Stores node instance logs in a {@link ConcurrentHashMap} instead of a database,
 * enabling use of the engine without Spring or a database connection.
 */
public class InMemoryNodeInstanceLogDAO implements NodeInstanceLogDAO {

    private final Map<Long, NodeInstanceLogPO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public int insert(NodeInstanceLogPO po) {
        if (po == null) {
            return -1;
        }
        long id = idGenerator.incrementAndGet();
        po.setId(id);
        store.put(id, po);
        return 1;
    }

    @Override
    public boolean insertList(List<NodeInstanceLogPO> nodeInstanceLogList) {
        if (nodeInstanceLogList == null || nodeInstanceLogList.isEmpty()) {
            return true;
        }
        for (NodeInstanceLogPO po : nodeInstanceLogList) {
            insert(po);
        }
        return true;
    }
}
