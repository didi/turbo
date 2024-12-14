package com.didiglobal.turbo.mybatis.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.mybatis.dao.provider.NodeInstanceProvider;
import com.didiglobal.turbo.mybatis.entity.NodeInstanceEntity;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface NodeInstanceMapper extends BaseMapper<NodeInstanceEntity> {

    @Select("SELECT * FROM ei_node_instance WHERE node_instance_id=#{nodeInstanceId}")
    NodeInstanceEntity selectByNodeInstanceId(@Param("flowInstanceId") String flowInstanceId,
                                              @Param("nodeInstanceId") String nodeInstanceId);

    @Select("SELECT * FROM ei_node_instance WHERE flow_instance_id=#{flowInstanceId} ORDER BY id DESC LIMIT 1")
    NodeInstanceEntity selectRecentOne(@Param("flowInstanceId") String flowInstanceId);

    @Select("SELECT * FROM ei_node_instance WHERE flow_instance_id=#{flowInstanceId} ORDER BY id")
    List<NodeInstanceEntity> selectByFlowInstanceId(@Param("flowInstanceId") String flowInstanceId);

    @Select("SELECT * FROM ei_node_instance WHERE flow_instance_id=#{flowInstanceId} ORDER BY id DESC")
    List<NodeInstanceEntity> selectDescByFlowInstanceId(@Param("flowInstanceId") String flowInstanceId);

    @Select("SELECT * FROM ei_node_instance WHERE flow_instance_id=#{flowInstanceId} and status=#{status} ORDER BY id" +
            " DESC LIMIT 1")
    NodeInstanceEntity selectRecentOneByStatus(@Param("flowInstanceId") String flowInstanceId, @Param("status") int status);

    @Select("SELECT * FROM ei_node_instance WHERE flow_instance_id=#{flowInstanceId} AND node_key=#{nodeKey} AND " +
            "source_node_instance_id=#{sourceNodeInstanceId}")
    NodeInstanceEntity selectBySourceInstanceId(@Param("flowInstanceId") String flowInstanceId,
                                                @Param("sourceNodeInstanceId") String sourceNodeInstanceId,
                                                @Param("nodeKey") String nodeKey);

    @Update("UPDATE ei_node_instance SET status=#{status}, modify_time=#{modifyTime} " +
            "WHERE node_instance_id=#{nodeInstanceId}")
    void updateStatus(NodeInstanceEntity entity);


    @InsertProvider(type = NodeInstanceProvider.class, method = "batchInsert")
    @Options(useGeneratedKeys = true, keyProperty = "list.id")
    boolean batchInsert(@Param("flowInstanceId") String flowInstanceId,
                        @Param("list") List<NodeInstanceEntity> nodeInstanceList);

    @Select("SELECT * FROM ei_node_instance WHERE flow_instance_id=#{flowInstanceId} AND node_key=#{nodeKey}")
    List<NodeInstanceEntity> selectByFlowInstanceIdAndNodeKey(@Param("flowInstanceId") String flowInstanceId, @Param("nodeKey") String nodeKey);
}
