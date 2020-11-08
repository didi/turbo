package com.xiaoju.uemc.turbo.engine.dao.provider;

import com.xiaoju.uemc.turbo.engine.entity.NodeInstancePO;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Stefanie on 2020/1/13.
 */
public class NodeInstanceProvider {

    private static final String TABLE_NAME = "ei_node_instance";
    private static final String COLUMN_ARRAY = "(flow_instance_id, flow_deploy_id, instance_data_id, " +
            "node_instance_id, source_node_instance_id, node_key, source_node_key, status, create_time, modify_time, " +
            "archive, tenant, caller)";

    public String batchInsert(Map parameters) {

        StringBuilder stringBuilder = new StringBuilder()
                .append("INSERT INTO ").append(TABLE_NAME).append(COLUMN_ARRAY).append(" VALUES ");

        MessageFormat mf = new MessageFormat("(" +
                "#'{'nodeInstanceList[{0}].flowInstanceId}, " +
                "#'{'nodeInstanceList[{0}].flowDeployId}, " +
                "#'{'nodeInstanceList[{0}].instanceDataId}, " +
                "#'{'nodeInstanceList[{0}].nodeInstanceId}, " +
                "#'{'nodeInstanceList[{0}].sourceNodeInstanceId}, " +
                "#'{'nodeInstanceList[{0}].nodeKey}, " +
                "#'{'nodeInstanceList[{0}].sourceNodeKey}, " +
                "#'{'nodeInstanceList[{0}].status}, " +
                "#'{'nodeInstanceList[{0}].createTime}, " +
                "#'{'nodeInstanceList[{0}].modifyTime}, " +
                "#'{'nodeInstanceList[{0}].archive}," +
                "#'{'nodeInstanceList[{0}].tenant}, " +
                "#'{'nodeInstanceList[{0}].caller}" +
                ")");

        List<NodeInstancePO> nodeInstanceList = (List<NodeInstancePO>) parameters.get("nodeInstanceList");
        for (int i = 0; i < nodeInstanceList.size(); i++) {
            stringBuilder.append(mf.format(new Object[]{i}));
            if (i < nodeInstanceList.size() - 1) {
                stringBuilder.append(",");
            }
        }

        return stringBuilder.toString();
    }
}
