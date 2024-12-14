package com.didiglobal.turbo.mybatis.dao.impl;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.exception.TurboException;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;
import com.didiglobal.turbo.mybatis.dao.mapper.InstanceDataMapper;
import com.didiglobal.turbo.mybatis.entity.InstanceDataEntity;

public class InstanceDataDAOImpl implements InstanceDataDAO {

    private final InstanceDataMapper baseMapper;

    public InstanceDataDAOImpl(InstanceDataMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

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
            InstanceDataEntity instanceDataEntity = InstanceDataEntity.of(instanceDataPO);
            int insert = baseMapper.insert(instanceDataEntity);
            TurboBeanUtils.copyProperties(instanceDataEntity, instanceDataPO);
            return insert;
        } catch (Exception e) {
            // TODO: 2020/2/1 clear reentrant exception log 
            LOGGER.error("insert exception.||instanceDataPO={}", instanceDataPO, e);
        }
        return -1;
    }

    @Override
    public int updateData(InstanceDataPO instanceDataPO) {
        try {
            return baseMapper.updateData(InstanceDataEntity.of(instanceDataPO));
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
