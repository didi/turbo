package com.didiglobal.turbo.plugin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.plugin.entity.JoinSourcePO;

/**
 * Mapper for ei_node_instance_join_source.
 * Tracks per-branch source node associations for parallel gateway join nodes,
 * decoupled from the core ei_node_instance.source_node_instance_id field.
 */
public interface JoinSourceMapper extends BaseMapper<JoinSourcePO> {
}
