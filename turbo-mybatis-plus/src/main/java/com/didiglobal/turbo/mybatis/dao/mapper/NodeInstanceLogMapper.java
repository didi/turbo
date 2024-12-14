package com.didiglobal.turbo.mybatis.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.didiglobal.turbo.mybatis.dao.provider.NodeInstanceLogProvider;
import com.didiglobal.turbo.mybatis.entity.NodeInstanceLogEntity;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NodeInstanceLogMapper extends BaseMapper<NodeInstanceLogEntity> {

    @InsertProvider(type = NodeInstanceLogProvider.class, method = "batchInsert")
    @Options(useGeneratedKeys = true, keyProperty = "list.id")
    boolean batchInsert(@Param("flowInstanceId") String flowInstanceId,
                        @Param("list") List<NodeInstanceLogEntity> nodeInstanceLogList);

}
