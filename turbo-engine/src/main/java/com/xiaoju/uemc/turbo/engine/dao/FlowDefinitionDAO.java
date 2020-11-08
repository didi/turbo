package com.xiaoju.uemc.turbo.engine.dao;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xiaoju.uemc.turbo.engine.dao.mapper.FlowDefinitionMapper;
import com.xiaoju.uemc.turbo.engine.entity.FlowDefinitionPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Created by Stefanie on 2019/11/27.
 */
@Service
public class FlowDefinitionDAO extends BaseDAO<FlowDefinitionMapper, FlowDefinitionPO> {

    public int insert(FlowDefinitionPO flowDefinitionPO) {
        try {
            return baseMapper.insert(flowDefinitionPO);
        } catch (Exception e) {
            LOGGER.error("insert exception.||flowDefinitionPO={}", flowDefinitionPO, e);
        }
        return -1;
    }

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
