package com.didiglobal.turbo.engine.dao.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.didiglobal.turbo.engine.dao.BaseDAO;
import com.didiglobal.turbo.engine.dao.FlowDefinitionDAO;
import com.didiglobal.turbo.engine.dao.mapper.FlowDefinitionMapper;
import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;
import org.apache.commons.lang3.StringUtils;

@DS("engine")
public class FlowDefinitionDAOImpl extends BaseDAO<FlowDefinitionMapper, FlowDefinitionPO>
        implements FlowDefinitionDAO {

    @Override
    public int insert(FlowDefinitionPO flowDefinitionPO) {
        try {
            return baseMapper.insert(flowDefinitionPO);
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
            UpdateWrapper<FlowDefinitionPO> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("flow_module_id", flowModuleId);
            return baseMapper.update(flowDefinitionPO, updateWrapper);
        } catch (Exception e) {
            LOGGER.error("update exception.||flowDefinitionPO={}", flowDefinitionPO, e);
        }
        return -1;
    }

    @Override
    public FlowDefinitionPO selectByModuleId(String flowModuleId) {
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
}
