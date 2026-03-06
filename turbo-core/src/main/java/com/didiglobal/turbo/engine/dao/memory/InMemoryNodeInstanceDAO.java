package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.common.NodeInstanceStatus;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 内存版节点实例 DAO。
 *
 * <p>节点实例（{@link NodeInstancePO}）记录流程执行过程中每个节点（StartEvent、
 * UserTask、ExclusiveGateway 等）的运行状态。一次流程实例执行通常会产生多条节点实例记录。
 *
 * <p>本类内部维护两个索引：
 * <ul>
 *   <li>{@code byNodeInstanceId}：{@code "flowInstanceId:nodeInstanceId"} → PO，用于精确查找。</li>
 *   <li>{@code byFlowInstanceId}：{@code flowInstanceId} → 有序列表，用于范围查找和"最近"查找。</li>
 * </ul>
 *
 * <p><b>与 Spring 版（NodeInstanceDAOImpl）的区别：</b>
 * <ul>
 *   <li>Spring 版存储到数据库 {@code ei_node_instance} 表；本类存储到 JVM 内存。</li>
 *   <li>{@link #insertOrUpdateList}：Spring 版对有 id 的记录执行 UPDATE，对无 id 的记录批量 INSERT；
 *       本类对有 id 的记录在内存中原地更新（{@code existing.setStatus(...)}），
 *       对无 id 的记录调用 {@link #insert}。</li>
 *   <li>"最近一条"查询：Spring 版通过 {@code ORDER BY id DESC LIMIT 1}；
 *       本类返回插入列表的最后一个元素。</li>
 *   <li>状态过滤：Spring 版通过 {@code WHERE status=?}；本类通过 Java Stream 过滤。</li>
 * </ul>
 *
 * @see NodeInstanceDAO
 * @see com.didiglobal.turbo.engine.engine.TurboEngineBuilder
 */
public class InMemoryNodeInstanceDAO implements NodeInstanceDAO {

    /** 自增主键模拟器 */
    private final AtomicLong idSeq = new AtomicLong(1);

    /** "flowInstanceId:nodeInstanceId" → PO */
    private final Map<String, NodeInstancePO> byNodeInstanceId = new ConcurrentHashMap<>();

    /** flowInstanceId → 按插入顺序排列的节点实例列表 */
    private final Map<String, List<NodeInstancePO>> byFlowInstanceId = new ConcurrentHashMap<>();

    @Override
    public int insert(NodeInstancePO po) {
        if (po.getId() == null) {
            po.setId(idSeq.getAndIncrement());
        }
        byNodeInstanceId.put(compositeKey(po.getFlowInstanceId(), po.getNodeInstanceId()), po);
        byFlowInstanceId.computeIfAbsent(po.getFlowInstanceId(), k -> new ArrayList<>()).add(po);
        return 1;
    }

    /**
     * 批量插入或更新节点实例。
     *
     * <p>内存实现规则：
     * <ul>
     *   <li>若 {@code po.getId() == null}：视为新记录，调用 {@link #insert}。</li>
     *   <li>若 {@code po.getId() != null}：视为已存在记录的更新，原地更新 status 等字段，
     *       保持对象引用不变（{@code byFlowInstanceId} 中的引用同步更新）。</li>
     * </ul>
     *
     * <p>Spring 版：有 id 的记录执行 {@code UPDATE ei_node_instance SET status=... WHERE id=?}；
     * 无 id 的记录执行批量 {@code INSERT}。
     */
    @Override
    public boolean insertOrUpdateList(List<NodeInstancePO> list) {
        for (NodeInstancePO po : list) {
            if (po.getId() == null) {
                insert(po);
            } else {
                String key = compositeKey(po.getFlowInstanceId(), po.getNodeInstanceId());
                NodeInstancePO existing = byNodeInstanceId.get(key);
                if (existing != null) {
                    // 原地更新，byFlowInstanceId 中持有相同对象引用，自动同步
                    existing.setStatus(po.getStatus());
                    existing.setInstanceDataId(po.getInstanceDataId());
                    existing.setSourceNodeInstanceId(po.getSourceNodeInstanceId());
                    existing.setSourceNodeKey(po.getSourceNodeKey());
                } else {
                    byNodeInstanceId.put(key, po);
                    byFlowInstanceId.computeIfAbsent(po.getFlowInstanceId(), k -> new ArrayList<>()).add(po);
                }
            }
        }
        return true;
    }

    @Override
    public NodeInstancePO selectByNodeInstanceId(String flowInstanceId, String nodeInstanceId) {
        return byNodeInstanceId.get(compositeKey(flowInstanceId, nodeInstanceId));
    }

    @Override
    public NodeInstancePO selectBySourceInstanceId(String flowInstanceId, String sourceNodeInstanceId, String nodeKey) {
        return byFlowInstanceId.getOrDefault(flowInstanceId, Collections.emptyList()).stream()
                .filter(n -> sourceNodeInstanceId.equals(n.getSourceNodeInstanceId())
                        && nodeKey.equals(n.getNodeKey()))
                .findFirst().orElse(null);
    }

    /** 返回最近插入的节点实例（列表末尾）。Spring 版：{@code ORDER BY id DESC LIMIT 1} */
    @Override
    public NodeInstancePO selectRecentOne(String flowInstanceId) {
        List<NodeInstancePO> list = byFlowInstanceId.getOrDefault(flowInstanceId, Collections.emptyList());
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    /** 返回最近插入的状态为 ACTIVE 的节点实例。Spring 版：{@code WHERE status=ACTIVE ORDER BY id DESC LIMIT 1} */
    @Override
    public NodeInstancePO selectRecentActiveOne(String flowInstanceId) {
        List<NodeInstancePO> list = new ArrayList<>(byFlowInstanceId.getOrDefault(flowInstanceId, Collections.emptyList()));
        for (int i = list.size() - 1; i >= 0; i--) {
            Integer status = list.get(i).getStatus();
            if (status != null && NodeInstanceStatus.ACTIVE == status) {
                return list.get(i);
            }
        }
        return null;
    }

    /** 返回最近插入的状态为 COMPLETED 的节点实例。Spring 版：{@code WHERE status=COMPLETED ORDER BY id DESC LIMIT 1} */
    @Override
    public NodeInstancePO selectRecentCompletedOne(String flowInstanceId) {
        List<NodeInstancePO> list = new ArrayList<>(byFlowInstanceId.getOrDefault(flowInstanceId, Collections.emptyList()));
        for (int i = list.size() - 1; i >= 0; i--) {
            Integer status = list.get(i).getStatus();
            if (status != null && NodeInstanceStatus.COMPLETED == status) {
                return list.get(i);
            }
        }
        return null;
    }

    /** 优先返回 ACTIVE 节点，否则返回 COMPLETED 节点。Spring 版通过两次条件查询实现。 */
    @Override
    public NodeInstancePO selectEnabledOne(String flowInstanceId) {
        NodeInstancePO active = selectRecentActiveOne(flowInstanceId);
        return active != null ? active : selectRecentCompletedOne(flowInstanceId);
    }

    @Override
    public List<NodeInstancePO> selectByFlowInstanceId(String flowInstanceId) {
        return new ArrayList<>(byFlowInstanceId.getOrDefault(flowInstanceId, Collections.emptyList()));
    }

    /** 返回按时间倒序的节点实例列表。内存实现：插入顺序列表反转。Spring 版：{@code ORDER BY id DESC} */
    @Override
    public List<NodeInstancePO> selectDescByFlowInstanceId(String flowInstanceId) {
        List<NodeInstancePO> list = new ArrayList<>(byFlowInstanceId.getOrDefault(flowInstanceId, Collections.emptyList()));
        Collections.reverse(list);
        return list;
    }

    /**
     * 更新节点实例状态。
     *
     * <p>内存实现：直接修改对象的 {@code status} 字段（对象引用已存在于索引中，无需重新 put）。
     * <p>Spring 版：{@code UPDATE ei_node_instance SET status=?, modify_time=? WHERE id=?}
     */
    @Override
    public void updateStatus(NodeInstancePO po, int status) {
        po.setStatus(status);
    }

    @Override
    public List<NodeInstancePO> selectByFlowInstanceIdAndNodeKey(String flowInstanceId, String nodeKey) {
        return byFlowInstanceId.getOrDefault(flowInstanceId, Collections.emptyList()).stream()
                .filter(n -> nodeKey.equals(n.getNodeKey()))
                .collect(Collectors.toList());
    }

    private static String compositeKey(String flowInstanceId, String nodeInstanceId) {
        return flowInstanceId + ":" + nodeInstanceId;
    }
}
