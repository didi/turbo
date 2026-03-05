package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link NodeInstanceDAO}.
 * Stores node instances in a {@link ConcurrentHashMap} instead of a database,
 * enabling use of the engine without Spring or a database connection.
 */
public class InMemoryNodeInstanceDAO implements NodeInstanceDAO {

    private final Map<Long, NodeInstancePO> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public int insert(NodeInstancePO po) {
        if (po == null) {
            return -1;
        }
        long id = idGenerator.incrementAndGet();
        po.setId(id);
        store.put(id, po);
        return 1;
    }

    @Override
    public boolean insertOrUpdateList(List<NodeInstancePO> nodeInstanceList) {
        if (nodeInstanceList == null || nodeInstanceList.isEmpty()) {
            return true;
        }
        for (NodeInstancePO po : nodeInstanceList) {
            if (po.getId() == null) {
                insert(po);
            } else {
                NodeInstancePO existing = store.get(po.getId());
                if (existing != null) {
                    existing.setStatus(po.getStatus());
                    existing.setModifyTime(po.getModifyTime() != null ? po.getModifyTime() : new Date());
                }
            }
        }
        return true;
    }

    @Override
    public NodeInstancePO selectByNodeInstanceId(String flowInstanceId, String nodeInstanceId) {
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
    public NodeInstancePO selectBySourceInstanceId(String flowInstanceId, String sourceNodeInstanceId, String nodeKey) {
        if (flowInstanceId == null || sourceNodeInstanceId == null || nodeKey == null) {
            return null;
        }
        return store.values().stream()
                .filter(po -> flowInstanceId.equals(po.getFlowInstanceId())
                        && sourceNodeInstanceId.equals(po.getSourceNodeInstanceId())
                        && nodeKey.equals(po.getNodeKey()))
                .max(Comparator.comparingLong(po -> po.getId() == null ? 0L : po.getId()))
                .orElse(null);
    }

    @Override
    public NodeInstancePO selectRecentOne(String flowInstanceId) {
        if (flowInstanceId == null) {
            return null;
        }
        return store.values().stream()
                .filter(po -> flowInstanceId.equals(po.getFlowInstanceId()))
                .max(Comparator.comparingLong(po -> po.getId() == null ? 0L : po.getId()))
                .orElse(null);
    }

    @Override
    public NodeInstancePO selectRecentActiveOne(String flowInstanceId) {
        return selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.ACTIVE);
    }

    @Override
    public NodeInstancePO selectRecentCompletedOne(String flowInstanceId) {
        return selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.COMPLETED);
    }

    @Override
    public NodeInstancePO selectEnabledOne(String flowInstanceId) {
        NodeInstancePO po = selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.ACTIVE);
        if (po == null) {
            po = selectRecentOneByStatus(flowInstanceId, NodeInstanceStatus.COMPLETED);
        }
        return po;
    }

    @Override
    public List<NodeInstancePO> selectByFlowInstanceId(String flowInstanceId) {
        if (flowInstanceId == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(po -> flowInstanceId.equals(po.getFlowInstanceId()))
                .sorted(Comparator.comparingLong(po -> po.getId() == null ? 0L : po.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<NodeInstancePO> selectDescByFlowInstanceId(String flowInstanceId) {
        if (flowInstanceId == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(po -> flowInstanceId.equals(po.getFlowInstanceId()))
                .sorted(Comparator.comparingLong((NodeInstancePO po) -> po.getId() == null ? 0L : po.getId()).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(NodeInstancePO po, int status) {
        if (po == null) {
            return;
        }
        po.setStatus(status);
        po.setModifyTime(new Date());
    }

    @Override
    public List<NodeInstancePO> selectByFlowInstanceIdAndNodeKey(String flowInstanceId, String nodeKey) {
        if (flowInstanceId == null || nodeKey == null) {
            return Collections.emptyList();
        }
        return store.values().stream()
                .filter(po -> flowInstanceId.equals(po.getFlowInstanceId())
                        && nodeKey.equals(po.getNodeKey()))
                .sorted(Comparator.comparingLong(po -> po.getId() == null ? 0L : po.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateById(NodeInstancePO po) {
        if (po == null || po.getId() == null) {
            return false;
        }
        NodeInstancePO existing = store.get(po.getId());
        if (existing == null) {
            return false;
        }
        store.put(po.getId(), po);
        return true;
    }

    private NodeInstancePO selectRecentOneByStatus(String flowInstanceId, int status) {
        if (flowInstanceId == null) {
            return null;
        }
        return store.values().stream()
                .filter(po -> flowInstanceId.equals(po.getFlowInstanceId())
                        && po.getStatus() != null && po.getStatus() == status)
                .max(Comparator.comparingLong(po -> po.getId() == null ? 0L : po.getId()))
                .orElse(null);
    }
}
