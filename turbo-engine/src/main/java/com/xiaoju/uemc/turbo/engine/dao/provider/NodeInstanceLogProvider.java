package com.xiaoju.uemc.turbo.engine.dao.provider;

import com.xiaoju.uemc.turbo.engine.entity.NodeInstanceLogPO;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Stefanie on 2020/1/14.
 */
public class NodeInstanceLogProvider {
    private static final String TABLE_NAME = "ei_node_instance_log";
    private static final String COLUMN_ARRAY = "(node_instance_id, flow_instance_id, instance_data_id, " +
            "node_key, type, status, create_time, archive, tenant, caller)";

    public String batchInsert(Map map) {

        StringBuilder stringBuilder = new StringBuilder()
                .append("INSERT INTO ").append(TABLE_NAME).append(COLUMN_ARRAY).append(" VALUES ");

        MessageFormat mf = new MessageFormat("(" +
                "#'{'nodeInstanceLogList[{0}].nodeInstanceId}, " +
                "#'{'nodeInstanceLogList[{0}].flowInstanceId}, " +
                "#'{'nodeInstanceLogList[{0}].instanceDataId}, " +
                "#'{'nodeInstanceLogList[{0}].nodeKey}, " +
                "#'{'nodeInstanceLogList[{0}].type}, " +
                "#'{'nodeInstanceLogList[{0}].status}, " +
                "#'{'nodeInstanceLogList[{0}].createTime}, " +
                "#'{'nodeInstanceLogList[{0}].archive}," +
                "#'{'nodeInstanceLogList[{0}].tenant}," +
                "#'{'nodeInstanceLogList[{0}].caller}" +
                ")");

        List<NodeInstanceLogPO> nodeInstanceList = (List<NodeInstanceLogPO>) map.get("nodeInstanceLogList");
        for (int i = 0; i < nodeInstanceList.size(); i++) {
            stringBuilder.append(mf.format(new Object[]{i}));
            if (i < nodeInstanceList.size() - 1) {
                stringBuilder.append(",");
            }
        }

        return stringBuilder.toString();
    }
}
