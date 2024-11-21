package com.didiglobal.turbo.engine.dao.provider;

import com.didiglobal.turbo.engine.entity.NodeInstancePO;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class NodeInstanceProvider {

    private static final String TABLE_NAME = "ei_node_instance";
    private static final String COLUMN_ARRAY = "(flow_instance_id, flow_deploy_id, instance_data_id, " +
            "node_instance_id, source_node_instance_id, node_type, node_key, source_node_key, status, create_time, modify_time, " +
            "archive, tenant, caller)";

    public String batchInsert(Map parameters) {

        StringBuilder stringBuilder = new StringBuilder()
                .append("INSERT INTO ").append(TABLE_NAME).append(COLUMN_ARRAY).append(" VALUES ");

        MessageFormat mf = new MessageFormat("(" +
                "#'{'list[{0}].flowInstanceId}, " +
                "#'{'list[{0}].flowDeployId}, " +
                "#'{'list[{0}].instanceDataId}, " +
                "#'{'list[{0}].nodeInstanceId}, " +
                "#'{'list[{0}].sourceNodeInstanceId}, " +
                "#'{'list[{0}].nodeType}, " +
                "#'{'list[{0}].nodeKey}, " +
                "#'{'list[{0}].sourceNodeKey}, " +
                "#'{'list[{0}].status}, " +
                "#'{'list[{0}].createTime}, " +
                "#'{'list[{0}].modifyTime}, " +
                "#'{'list[{0}].archive}," +
                "#'{'list[{0}].tenant}, " +
                "#'{'list[{0}].caller}" +
                ")");

        List<NodeInstancePO> nodeInstanceList = (List<NodeInstancePO>) parameters.get("list");
        for (int i = 0; i < nodeInstanceList.size(); i++) {
            stringBuilder.append(mf.format(new Object[]{i}));
            if (i < nodeInstanceList.size() - 1) {
                stringBuilder.append(",");
            }
        }

        return stringBuilder.toString();
    }
}
