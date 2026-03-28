package com.didiglobal.turbo.engine.dao.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.dao.BaseDAO;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.mapper.InstanceDataMapper;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.exception.TurboException;

@DS("engine")
public class InstanceDataDAOImpl extends BaseDAO<InstanceDataMapper, InstanceDataPO>
        implements InstanceDataDAO {

    @Override
    public InstanceDataPO select(String flowInstanceId, String instanceDataId) {
        return baseMapper.select(flowInstanceId, instanceDataId);
    }

    @Override
    public InstanceDataPO selectRecentOne(String flowInstanceId) {
        return baseMapper.selectRecentOne(flowInstanceId);
    }

    @Override
    public int insert(InstanceDataPO instanceDataPO) {
        try {
            return baseMapper.insert(instanceDataPO);
        } catch (Exception e) {
            LOGGER.error("insert exception.||instanceDataPO={}", instanceDataPO, e);
        }
        return -1;
    }

    @Override
    public int updateData(InstanceDataPO instanceDataPO) {
        try {
            return baseMapper.updateData(instanceDataPO);
        } catch (Exception e) {
            LOGGER.error("update instance data exception.||instanceDataPO={}", instanceDataPO, e);
            throw new TurboException(ErrorEnum.UPDATE_INSTANCE_DATA_FAILED);
        }
    }

    @Override
    public int insertOrUpdate(InstanceDataPO mergePo) {
        if (mergePo.getId() != null) {
            return updateData(mergePo);
        }
        return insert(mergePo);
    }
}
