package com.didiglobal.turbo.mybatis.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.turbo.engine.dao.FlowDefinitionDAO;
import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;
import com.didiglobal.turbo.engine.util.TurboBeanUtils;
import com.didiglobal.turbo.mybatis.dao.mapper.FlowDefinitionMapper;
import com.didiglobal.turbo.mybatis.entity.FlowDefinitionEntity;
import org.apache.commons.lang3.StringUtils;

public class FlowDefinitionDAOImpl implements FlowDefinitionDAO {

    private final FlowDefinitionMapper baseMapper;

    public FlowDefinitionDAOImpl(FlowDefinitionMapper flowDefinitionMapper) {
        this.baseMapper = flowDefinitionMapper;
    }

    @Override
    public int insert(FlowDefinitionPO flowDefinitionPO) {
        try {
            FlowDefinitionEntity flowDefinitionEntity = FlowDefinitionEntity.of(flowDefinitionPO);
            int insert = baseMapper.insert(flowDefinitionEntity);
            TurboBeanUtils.copyProperties(flowDefinitionEntity, flowDefinitionPO);
            return insert;
        } catch (Exception e) {
            LOGGER.error("insert exception.||flowDefinitionPO={}", flowDefinitionPO, e);
        }
        return -1;
    }

    @Override
    public int updateByModuleId(FlowDefinitionPO flowDefinitionPO) {
        if (null == flowDefinitionPO) {
            LOGGER.warn("updateByModuleId failed: flowDefinitionPO is null.");
            return -1;
        }
        try {
            String flowModuleId = flowDefinitionPO.getFlowModuleId();
            if (StringUtils.isBlank(flowModuleId)) {
                LOGGER.warn("updateByModuleId failed: flowModuleId is empty.||flowDefinitionPO={}", flowDefinitionPO);
                return -1;
            }
            UpdateWrapper<FlowDefinitionEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("flow_module_id", flowModuleId);
            return baseMapper.update(FlowDefinitionEntity.of(flowDefinitionPO), updateWrapper);
        } catch (Exception e) {
            LOGGER.error("update exception.||flowDefinitionPO={}", flowDefinitionPO, e);
        }
        return -1;
    }

    @Override
    public FlowDefinitionEntity selectByModuleId(String flowModuleId) {
        if (StringUtils.isBlank(flowModuleId)) {
            LOGGER.warn("getById failed: flowModuleId is empty.");
            return null;
        }
        try {
            return baseMapper.selectByFlowModuleId(flowModuleId);
        } catch (Exception e) {
            LOGGER.error("getById exception.||flowModuleId={}", flowModuleId, e);
        }
        return null;
    }

    public IPage<FlowDefinitionEntity> page(Page<FlowDefinitionEntity> page, QueryWrapper<FlowDefinitionEntity> queryWrapper) {
        return baseMapper.selectPage(page, queryWrapper);
    }
}
