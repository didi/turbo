package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.dao.mapper.InstanceDataMapper;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import org.springframework.stereotype.Service;

@Service
public class InstanceDataDAO extends BaseDAO<InstanceDataMapper, InstanceDataPO> {

    public InstanceDataPO select(String flowInstanceId, String instanceDataId) {
        return baseMapper.select(flowInstanceId, instanceDataId);
    }

    /**
     * select recent InstanceData order by id desc
     *
     * @param flowInstanceId
     * @return
     */
    public InstanceDataPO selectRecentOne(String flowInstanceId) {
        return baseMapper.selectRecentOne(flowInstanceId);
    }

    /**
     * insert instanceDataPO
     *
     * @param instanceDataPO
     * @return -1 while insert failed
     */
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
