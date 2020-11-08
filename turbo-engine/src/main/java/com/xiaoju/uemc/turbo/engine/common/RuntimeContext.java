package com.xiaoju.uemc.turbo.engine.common;

import com.xiaoju.uemc.turbo.engine.bo.NodeInstanceBO;
import com.xiaoju.uemc.turbo.engine.model.FlowElement;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by Stefanie on 2019/12/1.
 */
@Data
public class RuntimeContext {

    //1.flow info
    private String flowDeployId;
    private String flowModuleId;
    private String tenant;
    private String caller;
    private Map<String, FlowElement> flowElementMap;

    //2.runtime info
    //2.1 flowInstance info
    private String flowInstanceId;
    private int flowInstanceStatus;
    private NodeInstanceBO suspendNodeInstance; //point to the userTaskInstance to commit/recall
    private List<NodeInstanceBO> nodeInstanceList;  //processed nodeInstance list

    //2.2 current info
    private FlowElement currentNodeModel;
    private NodeInstanceBO currentNodeInstance;

    //2.3 data info
    private String instanceDataId;
    private Map<String, InstanceData> instanceDataMap;

    //2.4 process status
    private int processStatus;
}
