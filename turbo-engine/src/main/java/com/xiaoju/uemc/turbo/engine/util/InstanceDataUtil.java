package com.xiaoju.uemc.turbo.engine.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaoju.uemc.turbo.engine.common.DataType;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by Stefanie on 2019/12/6.
 */
public class InstanceDataUtil {

    public static Map<String, InstanceData> getInstanceDataMap(List<InstanceData> instanceDataList) {
        if (CollectionUtils.isEmpty(instanceDataList)) {
            return MapUtils.EMPTY_MAP;
        }
        Map<String, InstanceData> instanceDataMap = Maps.newHashMap();
        instanceDataList.forEach(instanceData -> {
            instanceDataMap.put(instanceData.getKey(), instanceData);
        });
        return instanceDataMap;
    }

    public static Map<String, InstanceData> getInstanceDataMap(String instanceDataStr) {
        if (StringUtils.isBlank(instanceDataStr)) {
            return MapUtils.EMPTY_MAP;
        }
        List<InstanceData> instanceDataList = JSON.parseArray(instanceDataStr, InstanceData.class);
        return getInstanceDataMap(instanceDataList);
    }

    public static List<InstanceData> getInstanceDataList(Map<String, InstanceData> instanceDataMap) {
        if (MapUtils.isEmpty(instanceDataMap)) {
            return Lists.newArrayList();
        }
        List<InstanceData> instanceDataList = Lists.newArrayList();
        instanceDataMap.forEach((key, instanceData) -> {
            instanceDataList.add(instanceData);
        });
        return instanceDataList;
    }

    public static String getInstanceDataListStr(Map<String, InstanceData> instanceDataMap) {
        if (MapUtils.isEmpty(instanceDataMap)) {
            return JSONObject.toJSONString(CollectionUtils.EMPTY_COLLECTION);
        }
        return JSONObject.toJSONString(instanceDataMap.values());
    }

    public static Map<String, Object> parseInstanceDataMap(Map<String, InstanceData> instanceDataMap) {
        if (MapUtils.isEmpty(instanceDataMap)) {
            return MapUtils.EMPTY_MAP;
        }
        Map<String, Object> dataMap = Maps.newHashMap();
        instanceDataMap.forEach((keyName, instanceData) -> {
            dataMap.put(keyName, parseInstanceData(instanceData));
        });
        return dataMap;
    }

    private static Object parseInstanceData(InstanceData instanceData) {
        if (instanceData == null) {
            return null;
        }
        String dataTypeStr = instanceData.getType();
        DataType dataType = DataType.getType(dataTypeStr);

        // TODO: 2019/12/16
        return instanceData.getValue();
    }
}
