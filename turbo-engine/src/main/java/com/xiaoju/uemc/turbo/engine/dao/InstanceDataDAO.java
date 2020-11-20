package com.xiaoju.uemc.turbo.engine.dao;

import com.xiaoju.uemc.turbo.engine.dao.mapper.InstanceDataMapper;
import com.xiaoju.uemc.turbo.engine.entity.InstanceDataPO;
import org.springframework.stereotype.Service;

/**
 * Created by Stefanie on 2019/12/5.
 */
@Service
public class InstanceDataDAO extends BaseDAO<InstanceDataMapper, InstanceDataPO> {

    /**
     * select InstanceDataPO by flowInstanceId and instanceDataId
     *
     * we will use flowInstanceId when relate divide table
     *
     * @param flowInstanceId
     * @param instanceDataId
     * @return
     */
    public InstanceDataPO select(String flowInstanceId, String instanceDataId) {
        return baseMapper.select(flowInstanceId, instanceDataId);
    }

    /**
     * find most new InstanceDataPO by flowInstanceId
     *
     * @param flowInstanceId
     * @return
     */
    public InstanceDataPO selectRecentOne(String flowInstanceId) {
        return baseMapper.selectRecentOne(flowInstanceId);
    }

    /**
     * insert instanceDataPO
     * if error, this will not throw exption but return -1
     *
     * @param instanceDataPO
     * @return
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
