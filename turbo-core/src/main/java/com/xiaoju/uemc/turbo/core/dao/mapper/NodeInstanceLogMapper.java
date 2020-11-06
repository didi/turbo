package com.xiaoju.uemc.turbo.core.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaoju.uemc.turbo.core.dao.provider.NodeInstanceLogProvider;
import com.xiaoju.uemc.turbo.core.entity.NodeInstanceLogPO;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Stefanie on 2019/12/1.
 */
public interface NodeInstanceLogMapper extends BaseMapper<NodeInstanceLogPO> {

    @InsertProvider(type = NodeInstanceLogProvider.class, method = "batchInsert")
    boolean batchInsert(@Param("flowInstanceId") String flowInstanceId,
                        @Param("nodeInstanceLogList") List<NodeInstanceLogPO> nodeInstanceLogList);

}
