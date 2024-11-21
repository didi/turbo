package com.didiglobal.turbo.plugin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.plugin.entity.ParallelNodeInstanceLogPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ParallelNodeInstanceLogMapper extends BaseMapper<ParallelNodeInstanceLogPO> {
    @Insert({
        "<script>",
        "INSERT INTO ei_node_instance_log_parallel(id, execute_id) VALUES ",
        "<foreach collection='parallelNodeInstanceLogPOS' item='item' separator=','>",
        "(#{item.id}, #{item.executeId})",
        "</foreach>",
        "</script>"
    })
    boolean insertList(@Param("parallelNodeInstanceLogPOS") List<ParallelNodeInstanceLogPO> parallelNodeInstanceLogPOS);
}
