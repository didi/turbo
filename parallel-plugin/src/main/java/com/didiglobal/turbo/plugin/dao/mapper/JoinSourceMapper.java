package com.didiglobal.turbo.plugin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.plugin.entity.JoinSourcePO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper for ei_node_instance_join_source.
 * Tracks per-branch source node associations for parallel gateway join nodes,
 * decoupled from the core ei_node_instance.source_node_instance_id field.
 */
public interface JoinSourceMapper extends BaseMapper<JoinSourcePO> {

    @Select("SELECT * FROM ei_node_instance_join_source WHERE join_node_instance_id = #{joinNodeInstanceId}")
    List<JoinSourcePO> selectByJoinNodeInstanceId(@Param("joinNodeInstanceId") String joinNodeInstanceId);
}
