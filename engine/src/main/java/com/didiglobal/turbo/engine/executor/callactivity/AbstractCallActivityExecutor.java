package com.didiglobal.turbo.engine.executor.callactivity;

import com.alibaba.fastjson.JSON;
import com.didiglobal.turbo.engine.bo.DataTransferBO;
import com.didiglobal.turbo.engine.common.Constants;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.common.InstanceDataType;
import com.didiglobal.turbo.engine.common.RuntimeContext;
import com.didiglobal.turbo.engine.config.BusinessConfig;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.entity.InstanceDataPO;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.executor.ElementExecutor;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.didiglobal.turbo.engine.model.InstanceData;
import com.didiglobal.turbo.engine.processor.RuntimeProcessor;
import com.didiglobal.turbo.engine.service.NodeInstanceService;
import com.didiglobal.turbo.engine.util.InstanceDataUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Some common CallActivity methods
 */
public abstract class AbstractCallActivityExecutor extends ElementExecutor {

    @Resource
    protected RuntimeProcessor runtimeProcessor;

    @Resource
    protected FlowDeploymentDAO flowDeploymentDAO;

    @Resource
    protected NodeInstanceService nodeInstanceService;

    @Resource
    protected BusinessConfig businessConfig;

    protected List<InstanceData> getCallActivityVariables(RuntimeContext runtimeContext) throws ProcessException {
        List<InstanceData> callActivityInitData = InstanceDataUtil.getInstanceDataList(runtimeContext.getInstanceDataMap());
        List<InstanceData> instanceDataFromMainFlow = calculateCallActivityInParamFromMainFlow(runtimeContext);
        // merge data
        Map<String, InstanceData> callActivityInitDataMap = InstanceDataUtil.getInstanceDataMap(callActivityInitData);
        Map<String, InstanceData> instanceDataFromMainFlowMap = InstanceDataUtil.getInstanceDataMap(instanceDataFromMainFlow);

        Map<String, InstanceData> allInstanceData = new HashMap<>();
        allInstanceData.putAll(callActivityInitDataMap);
        allInstanceData.putAll(instanceDataFromMainFlowMap);
        return InstanceDataUtil.getInstanceDataList(allInstanceData);
    }

    // main > sub
    protected List<InstanceData> calculateCallActivityInParamFromMainFlow(RuntimeContext runtimeContext) throws ProcessException {
        FlowElement currentNodeModel = runtimeContext.getCurrentNodeModel();

        InstanceDataPO instanceDataPO = instanceDataDAO.select(runtimeContext.getFlowInstanceId(), runtimeContext.getInstanceDataId());
        Map<String, InstanceData> mainInstanceDataMap = InstanceDataUtil.getInstanceDataMap(instanceDataPO.getInstanceData());

        return calculateCallActivityDataTransfer(currentNodeModel, mainInstanceDataMap,
            Constants.ELEMENT_PROPERTIES.CALL_ACTIVITY_IN_PARAM_TYPE,
            Constants.ELEMENT_PROPERTIES.CALL_ACTIVITY_IN_PARAM);
    }

    // sub > main
    protected List<InstanceData> calculateCallActivityOutParamFromSubFlow(RuntimeContext runtimeContext, List<InstanceData> subFlowData) throws ProcessException {
        FlowElement currentNodeModel = runtimeContext.getCurrentNodeModel();
        return calculateCallActivityDataTransfer(currentNodeModel, InstanceDataUtil.getInstanceDataMap(subFlowData),
            Constants.ELEMENT_PROPERTIES.CALL_ACTIVITY_OUT_PARAM_TYPE,
            Constants.ELEMENT_PROPERTIES.CALL_ACTIVITY_OUT_PARAM);
    }

    private List<InstanceData> calculateCallActivityDataTransfer(FlowElement currentNodeModel, Map<String, InstanceData> instanceDataMap, String callActivityParamType, String callActivityParam) throws ProcessException {
        // default FULL
        String callActivityInParamType = (String) currentNodeModel.getProperties().getOrDefault(callActivityParamType, Constants.CALL_ACTIVITY_PARAM_TYPE.FULL);
        if (callActivityInParamType.equals(Constants.CALL_ACTIVITY_PARAM_TYPE.NONE)) {
            return new ArrayList<>();
        }
        if (callActivityInParamType.equals(Constants.CALL_ACTIVITY_PARAM_TYPE.PART)) {
            List<InstanceData> instanceDataList = Lists.newArrayList();
            String callActivityInParam = (String) currentNodeModel.getProperties().getOrDefault(callActivityParam, StringUtils.EMPTY);
            List<DataTransferBO> callActivityDataTransfers = JSON.parseArray(callActivityInParam, DataTransferBO.class);
            for (DataTransferBO callActivityDataTransfer : callActivityDataTransfers) {
                if (Constants.CALL_ACTIVITY_DATA_TRANSFER_TYPE.SOURCE_TYPE_CONTEXT.equals(callActivityDataTransfer.getSourceType())) {
                    InstanceData sourceInstanceData = instanceDataMap.get(callActivityDataTransfer.getSourceKey());
                    Object sourceValue = sourceInstanceData == null ? null : sourceInstanceData.getValue();
                    InstanceData instanceData = new InstanceData(callActivityDataTransfer.getTargetKey(), sourceValue);
                    instanceDataList.add(instanceData);
                } else if (Constants.CALL_ACTIVITY_DATA_TRANSFER_TYPE.SOURCE_TYPE_FIXED.equals(callActivityDataTransfer.getSourceType())) {
                    InstanceData instanceData = new InstanceData(callActivityDataTransfer.getTargetKey(), callActivityDataTransfer.getSourceValue());
                    instanceDataList.add(instanceData);
                } else {
                    throw new ProcessException(ErrorEnum.MODEL_UNKNOWN_ELEMENT_VALUE);
                }
            }
            return instanceDataList;
        }
        if (callActivityInParamType.equals(Constants.CALL_ACTIVITY_PARAM_TYPE.FULL)) {
            return InstanceDataUtil.getInstanceDataList(instanceDataMap);
        }
        throw new ProcessException(ErrorEnum.MODEL_UNKNOWN_ELEMENT_VALUE);
    }

    protected InstanceDataPO buildCallActivityEndInstanceData(String instanceDataId, RuntimeContext runtimeContext) {
        InstanceDataPO instanceDataPO = new InstanceDataPO();
        BeanUtils.copyProperties(runtimeContext, instanceDataPO);
        instanceDataPO.setInstanceDataId(instanceDataId);
        instanceDataPO.setInstanceData(InstanceDataUtil.getInstanceDataListStr(runtimeContext.getInstanceDataMap()));
        instanceDataPO.setNodeInstanceId(runtimeContext.getCurrentNodeInstance().getNodeInstanceId());
        instanceDataPO.setNodeKey(runtimeContext.getCurrentNodeModel().getKey());
        instanceDataPO.setType(InstanceDataType.UPDATE);
        instanceDataPO.setCreateTime(new Date());
        return instanceDataPO;
    }
}
