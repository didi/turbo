package com.didiglobal.turbo.mybatis.dao.provider;

import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class NodeInstanceLogProvider {
    private static final String TABLE_NAME = "ei_node_instance_log";
    private static final String COLUMN_ARRAY = "(node_instance_id, flow_instance_id, instance_data_id, " +
            "node_key, type, status, create_time, archive, tenant, caller)";

    public String batchInsert(Map map) {

        StringBuilder stringBuilder = new StringBuilder()
                .append("INSERT INTO ").append(TABLE_NAME).append(COLUMN_ARRAY).append(" VALUES ");

        MessageFormat mf = new MessageFormat("(" +
                "#'{'list[{0}].nodeInstanceId}, " +
                "#'{'list[{0}].flowInstanceId}, " +
                "#'{'list[{0}].instanceDataId}, " +
                "#'{'list[{0}].nodeKey}, " +
                "#'{'list[{0}].type}, " +
                "#'{'list[{0}].status}, " +
                "#'{'list[{0}].createTime}, " +
                "#'{'list[{0}].archive}," +
                "#'{'list[{0}].tenant}," +
                "#'{'list[{0}].caller}" +
                ")");

        List<NodeInstanceLogPO> nodeInstanceList = (List<NodeInstanceLogPO>) map.get("list");
        for (int i = 0; i < nodeInstanceList.size(); i++) {
            stringBuilder.append(mf.format(new Object[]{i}));
            if (i < nodeInstanceList.size() - 1) {
                stringBuilder.append(",");
            }
        }

        return stringBuilder.toString();
    }
}
