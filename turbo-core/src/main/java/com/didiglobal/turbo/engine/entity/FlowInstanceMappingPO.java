package com.didiglobal.turbo.engine.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("ei_flow_instance_mapping")
public class FlowInstanceMappingPO extends CommonPO {

    private String flowInstanceId;
    private String nodeInstanceId;
    private String nodeKey;
    private String subFlowInstanceId;
    private Integer type;
    private Date modifyTime;

    public String getFlowInstanceId() {
        return flowInstanceId;
    }

    public void setFlowInstanceId(String flowInstanceId) {
        this.flowInstanceId = flowInstanceId;
    }


    public String getNodeInstanceId() {
        return nodeInstanceId;
    }

    public void setNodeInstanceId(String nodeInstanceId) {
        this.nodeInstanceId = nodeInstanceId;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public String getSubFlowInstanceId() {
        return subFlowInstanceId;
    }

    public void setSubFlowInstanceId(String subFlowInstanceId) {
        this.subFlowInstanceId = subFlowInstanceId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
