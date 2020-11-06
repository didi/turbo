package com.xiaoju.uemc.turbo.core.dao;

import com.xiaoju.uemc.turbo.core.dao.mapper.InstanceDataMapper;
import com.xiaoju.uemc.turbo.core.entity.InstanceDataPO;
import org.springframework.stereotype.Service;

/**
 * Created by Stefanie on 2019/12/5.
 */
@Service
public class InstanceDataDAO extends BaseDAO<InstanceDataMapper, InstanceDataPO> {

    public InstanceDataPO select(String flowInstanceId, String instanceDataId) {
        return baseMapper.select(flowInstanceId, instanceDataId);
    }

    public InstanceDataPO selectRecentOne(String flowInstanceId) {
        return baseMapper.selectRecentOne(flowInstanceId);
    }

    public int insert(InstanceDataPO instanceDataPO) {
        try {
            return baseMapper.insert(instanceDataPO);
        } catch (Exception e) {
            // TODO: 2020/2/1 clear reentrant exception log 
            LOGGER.error("insert exception.||instanceDataPO={}", instanceDataPO, e);
        }
        return -1;
    }
}
