package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 内存版实例数据 DAO。
 *
 * <p>实例数据（{@link InstanceDataPO}）存储流程运行时的变量快照（每次提交任务时可能产生新的快照）。
 * 引擎根据 {@code instanceDataId} 定位某一时刻的变量集合。
 *
 * <p><b>与 Spring 版（InstanceDataDAOImpl）的区别：</b>
 * <ul>
 *   <li>Spring 版持久化到数据库 {@code ei_instance_data} 表；本类存储到内存 Map。</li>
 *   <li>{@link #selectRecentOne}：Spring 版通过 {@code ORDER BY id DESC LIMIT 1} 查最近一条；
 *       本类通过 Stream reduce 取最后 put 的记录（插入顺序即为时间顺序）。</li>
 *   <li>{@link #insertOrUpdate}：Spring 版判断 id 是否存在决定 INSERT 或 UPDATE；
 *       本类同逻辑：id 为 null 则 insert（分配自增 id），否则 update（直接覆盖）。</li>
 * </ul>
 *
 * @see InstanceDataDAO
 * @see com.didiglobal.turbo.engine.engine.TurboEngineBuilder
 */
public class InMemoryInstanceDataDAO implements InstanceDataDAO {

    private final AtomicLong idSeq = new AtomicLong(1);

    /** "flowInstanceId:instanceDataId" → PO */
    private final Map<String, InstanceDataPO> store = new ConcurrentHashMap<>();

    @Override
    public InstanceDataPO select(String flowInstanceId, String instanceDataId) {
        return store.get(compositeKey(flowInstanceId, instanceDataId));
    }

    /**
     * 返回指定流程实例最近一次写入的实例数据。
     *
     * <p>内存实现：过滤同一 flowInstanceId 的记录，取 reduce 末尾元素（最后插入）。
     * <p>Spring 版：{@code SELECT ... WHERE flow_instance_id=? ORDER BY id DESC LIMIT 1}
     */
    @Override
    public InstanceDataPO selectRecentOne(String flowInstanceId) {
        return store.values().stream()
                .filter(p -> flowInstanceId.equals(p.getFlowInstanceId()))
                .reduce((a, b) -> b)
                .orElse(null);
    }

    @Override
    public int insert(InstanceDataPO po) {
        if (po.getId() == null) {
            po.setId(idSeq.getAndIncrement());
        }
        store.put(compositeKey(po.getFlowInstanceId(), po.getInstanceDataId()), po);
        return 1;
    }

    @Override
    public int updateData(InstanceDataPO po) {
        store.put(compositeKey(po.getFlowInstanceId(), po.getInstanceDataId()), po);
        return 1;
    }

    /** id 为 null 则 insert，否则 update（覆盖）。Spring 版行为一致。 */
    @Override
    public int insertOrUpdate(InstanceDataPO po) {
        return po.getId() != null ? updateData(po) : insert(po);
    }

    private static String compositeKey(String flowInstanceId, String instanceDataId) {
        return flowInstanceId + ":" + instanceDataId;
    }
}
