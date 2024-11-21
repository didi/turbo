package com.didiglobal.turbo.plugin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
@TableName("ei_node_instance_parallel")
public class ParallelNodeInstancePO{
    private Long id;
    private String executeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExecuteId() {
        return executeId;
    }

    public void setExecuteId(String executeId) {
        this.executeId = executeId;
    }

    @Override
    public String toString() {
        return "ParallelNodeInstancePO{" +
            "id=" + id +
            ", executeId='" + executeId + '\'' +
            '}';
    }
}
