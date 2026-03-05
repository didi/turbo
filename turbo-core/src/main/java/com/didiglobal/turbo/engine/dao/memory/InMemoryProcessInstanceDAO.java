package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.engine.entity.FlowInstancePO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存版流程实例 DAO。
 *
 * <p>流程实例（{@link FlowInstancePO}）对应一次具体的流程执行记录。
 * 每次调用 {@code engine.startProcess()} 都会创建一条新的流程实例。
 *
 * <p><b>与 Spring 版（ProcessInstanceDAOImpl）的区别：</b>
 * <ul>
 *   <li>Spring 版将实例写入数据库 {@code ei_flow_instance} 表；
 *       本类将实例保存在内存 Map 中，进程退出后丢失。</li>
 *   <li>Spring 版 {@code updateStatus} 执行
 *       {@code UPDATE ei_flow_instance SET status=? WHERE flow_instance_id=?}；
 *       本类直接修改内存中对象的 {@code status} 字段。</li>
 *   <li>Spring 版支持并发安全的数据库事务；本类靠 {@link ConcurrentHashMap} 保证基本线程安全。</li>
 * </ul>
 *
 * @see ProcessInstanceDAO
 * @see com.didiglobal.turbo.engine.engine.TurboEngineBuilder
 */
public class InMemoryProcessInstanceDAO implements ProcessInstanceDAO {

    private final Map<String, FlowInstancePO> store = new ConcurrentHashMap<>();

    @Override
    public FlowInstancePO selectByFlowInstanceId(String flowInstanceId) {
        return store.get(flowInstanceId);
    }

    @Override
    public int insert(FlowInstancePO po) {
        store.put(po.getFlowInstanceId(), po);
        return 1;
    }

    /**
     * 根据 flowInstanceId 更新流程实例状态。
     *
     * <p>内存实现：从 Map 取出对象，直接修改 {@code status} 字段。
     * <p>Spring 版：{@code UPDATE ei_flow_instance SET status=? WHERE flow_instance_id=?}
     */
    @Override
    public void updateStatus(String flowInstanceId, int status) {
        FlowInstancePO po = store.get(flowInstanceId);
        if (po != null) {
            po.setStatus(status);
        }
    }

    /**
     * 直接更新传入对象的 status 字段（同时同步到数据库或内存）。
     *
     * <p>内存实现：直接调用 {@code po.setStatus(status)}（对象已在 Map 中，引用相同）。
     * <p>Spring 版：同上，{@code UPDATE} 语句基于 {@code po} 的主键。
     */
    @Override
    public void updateStatus(FlowInstancePO po, int status) {
        po.setStatus(status);
    }
}
