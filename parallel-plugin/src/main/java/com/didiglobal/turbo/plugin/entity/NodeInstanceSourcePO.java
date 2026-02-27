package com.didiglobal.turbo.plugin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 节点实例来源关联表 PO
 * <p>
 * 用于记录一个节点实例（如并行网关 join 节点）的多个来源节点信息。
 * 每条记录代表一个来源分支。
 */
@TableName("ei_node_instance_source")
public class NodeInstanceSourcePO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 流程实例ID
     */
    private String flowInstanceId;

    /**
     * 当前节点实例ID（join 节点）
     */
    private String nodeInstanceId;

    /**
     * 当前节点 key（join 节点）
     */
    private String nodeKey;

    /**
     * 来源节点实例ID（分支最后一个节点）
     */
    private String sourceNodeInstanceId;

    /**
     * 来源节点 key（分支最后一个节点）
     */
    private String sourceNodeKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getSourceNodeInstanceId() {
        return sourceNodeInstanceId;
    }

    public void setSourceNodeInstanceId(String sourceNodeInstanceId) {
        this.sourceNodeInstanceId = sourceNodeInstanceId;
    }

    public String getSourceNodeKey() {
        return sourceNodeKey;
    }

    public void setSourceNodeKey(String sourceNodeKey) {
        this.sourceNodeKey = sourceNodeKey;
    }

    @Override
    public String toString() {
        return "NodeInstanceSourcePO{" +
            "id=" + id +
            ", flowInstanceId='" + flowInstanceId + '\'' +
            ", nodeInstanceId='" + nodeInstanceId + '\'' +
            ", nodeKey='" + nodeKey + '\'' +
            ", sourceNodeInstanceId='" + sourceNodeInstanceId + '\'' +
            ", sourceNodeKey='" + sourceNodeKey + '\'' +
            '}';
    }
}
