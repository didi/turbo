package com.didiglobal.turbo.demo.enums;

/**
 * @Author: james zhangxiao
 * @Date: 5/11/22
 * @Description: 流程状态枚举
 */
public enum FlowModuleStatusEnum {

    DRAFT(1, "草稿态"),
    EDITING(2, "编辑中"),
    OFFLINE(3, "已下线"),
    PUBLISHED(4, "已发布");


    private int value;
    private String desc;

    FlowModuleStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
