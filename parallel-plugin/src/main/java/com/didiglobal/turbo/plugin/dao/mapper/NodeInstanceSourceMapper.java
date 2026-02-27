package com.didiglobal.turbo.plugin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.plugin.entity.NodeInstanceSourcePO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 节点实例来源关联表 Mapper
 */
public interface NodeInstanceSourceMapper extends BaseMapper<NodeInstanceSourcePO> {

    /**
     * 根据流程实例ID和节点实例ID查询所有来源记录
     */
    @Select("SELECT * FROM ei_node_instance_source WHERE flow_instance_id=#{flowInstanceId} AND node_instance_id=#{nodeInstanceId}")
    List<NodeInstanceSourcePO> selectByNodeInstanceId(@Param("flowInstanceId") String flowInstanceId,
                                                      @Param("nodeInstanceId") String nodeInstanceId);

    /**
     * 根据流程实例ID和节点key查询所有来源记录
     */
    @Select("SELECT * FROM ei_node_instance_source WHERE flow_instance_id=#{flowInstanceId} AND node_key=#{nodeKey}")
    List<NodeInstanceSourcePO> selectByNodeKey(@Param("flowInstanceId") String flowInstanceId,
                                               @Param("nodeKey") String nodeKey);
}
