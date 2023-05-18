package com.didiglobal.turbo.engine.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.engine.dao.provider.NodeInstanceProvider;
import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface NodeInstanceMapper extends BaseMapper<NodeInstancePO> {

    @Select("SELECT * FROM ei_node_instance WHERE node_instance_id=#{nodeInstanceId}")
    NodeInstancePO selectByNodeInstanceId(@Param("flowInstanceId") String flowInstanceId,
                                          @Param("nodeInstanceId") String nodeInstanceId);

    @Select("/*{\"router\":\"m\"}*/SELECT * FROM ei_node_instance WHERE node_instance_id=#{nodeInstanceId}")
    NodeInstancePO selectByNodeInstanceIdByMaster(@Param("flowInstanceId") String flowInstanceId,
                                                  @Param("nodeInstanceId") String nodeInstanceId);

    @Select("SELECT * FROM ei_node_instance WHERE flow_instance_id=#{flowInstanceId} ORDER BY id DESC LIMIT 1")
    NodeInstancePO selectRecentOne(@Param("flowInstanceId") String flowInstanceId);

    @Select("SELECT * FROM ei_node_instance WHERE flow_instance_id=#{flowInstanceId} ORDER BY id")
    List<NodeInstancePO> selectByFlowInstanceId(@Param("flowInstanceId") String flowInstanceId);

    @Select("SELECT * FROM ei_node_instance WHERE flow_instance_id=#{flowInstanceId} ORDER BY id DESC")
    List<NodeInstancePO> selectDescByFlowInstanceId(@Param("flowInstanceId") String flowInstanceId);

    @Select("SELECT * FROM ei_node_instance WHERE flow_instance_id=#{flowInstanceId} and status=#{status} ORDER BY id" +
            " DESC LIMIT 1")
    NodeInstancePO selectRecentOneByStatus(@Param("flowInstanceId") String flowInstanceId, @Param("status") int status);

    @Select("SELECT * FROM ei_node_instance WHERE flow_instance_id=#{flowInstanceId} AND node_key=#{nodeKey} AND " +
            "source_node_instance_id=#{sourceNodeInstanceId}")
    NodeInstancePO selectBySourceInstanceId(@Param("flowInstanceId") String flowInstanceId,
                                            @Param("sourceNodeInstanceId") String sourceNodeInstanceId,
                                            @Param("nodeKey") String nodeKey);

    @Update("UPDATE ei_node_instance SET status=#{status}, modify_time=#{modifyTime} " +
            "WHERE node_instance_id=#{nodeInstanceId}")
    void updateStatus(NodeInstancePO entity);


    @InsertProvider(type = NodeInstanceProvider.class, method = "batchInsert")
    boolean batchInsert(@Param("flowInstanceId") String flowInstanceId,
                        @Param("nodeInstanceList") List<NodeInstancePO> nodeInstanceList);

}
