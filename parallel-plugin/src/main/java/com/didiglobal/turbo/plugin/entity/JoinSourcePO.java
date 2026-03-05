package com.didiglobal.turbo.plugin.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * Records per-branch source associations for parallel gateway join nodes.
 * Each row maps one arriving branch (sourceNodeInstanceId) to its join node (joinNodeInstanceId).
 * This table replaces the previous approach of accumulating comma-separated IDs
 * into the core ei_node_instance.source_node_instance_id varchar field, which
 * would overflow for large numbers of parallel branches.
 */
@TableName("ei_node_instance_join_source")
public class JoinSourcePO {

    private Long id;
    private String flowInstanceId;
    private String joinNodeInstanceId;
    private String sourceNodeInstanceId;
    private String sourceNodeKey;
    private Date createTime;

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

    public String getJoinNodeInstanceId() {
        return joinNodeInstanceId;
    }

    public void setJoinNodeInstanceId(String joinNodeInstanceId) {
        this.joinNodeInstanceId = joinNodeInstanceId;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "JoinSourcePO{" +
                "id=" + id +
                ", flowInstanceId='" + flowInstanceId + '\'' +
                ", joinNodeInstanceId='" + joinNodeInstanceId + '\'' +
                ", sourceNodeInstanceId='" + sourceNodeInstanceId + '\'' +
                ", sourceNodeKey='" + sourceNodeKey + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
