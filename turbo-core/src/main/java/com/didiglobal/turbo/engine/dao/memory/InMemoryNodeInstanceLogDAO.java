package com.didiglobal.turbo.engine.dao.memory;

import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;

import java.util.ArrayList;
import java.util.List;

/**
 * 内存版节点实例日志 DAO。
 *
 * <p>节点实例日志（{@link NodeInstanceLogPO}）记录节点执行、提交、回滚等操作的历史轨迹，
 * 主要用于审计和问题排查。每次节点状态变更时引擎都会写入一条日志。
 *
 * <p><b>与 Spring 版（NodeInstanceLogDAOImpl）的区别：</b>
 * <ul>
 *   <li>Spring 版持久化到数据库 {@code ei_node_instance_log} 表；本类存储到 JVM 内存列表。</li>
 *   <li>Spring 版支持查询日志；本类仅追加写入，不提供查询方法（接口层面暂未定义查询）。</li>
 *   <li>Spring 版批量写入通过 MyBatis 的 {@code batchInsert}；本类通过 {@code List.addAll}。</li>
 * </ul>
 *
 * <p>如需在非 Spring 项目中持久化日志，可将本类的 {@code logs} 替换为数据库写入逻辑。
 *
 * @see NodeInstanceLogDAO
 * @see com.didiglobal.turbo.engine.engine.TurboEngineBuilder
 */
public class InMemoryNodeInstanceLogDAO implements NodeInstanceLogDAO {

    /** 按写入顺序保存的所有节点执行日志 */
    private final List<NodeInstanceLogPO> logs = new ArrayList<>();

    @Override
    public int insert(NodeInstanceLogPO po) {
        logs.add(po);
        return 1;
    }

    @Override
    public boolean insertList(List<NodeInstanceLogPO> list) {
        logs.addAll(list);
        return true;
    }
}
