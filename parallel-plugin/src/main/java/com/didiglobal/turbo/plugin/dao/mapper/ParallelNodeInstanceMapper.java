package com.didiglobal.turbo.plugin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.plugin.entity.ParallelNodeInstancePO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ParallelNodeInstanceMapper extends BaseMapper<ParallelNodeInstancePO> {
    @Insert({
        "<script>",
        "INSERT INTO ei_node_instance_parallel(id, execute_id) VALUES ",
        "<foreach collection='insertParallelNodeInstanceList' item='item' separator=','>",
        "(#{item.id}, #{item.executeId})",
        "</foreach>",
        "</script>"
    })
    boolean insertList(@Param("insertParallelNodeInstanceList") List<ParallelNodeInstancePO> insertParallelNodeInstanceList);
}
