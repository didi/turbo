package com.xiaoju.uemc.turbo.engine.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by Stefanie on 2019/11/22.
 */
@Data
public class FlowElement {
    private String key; // 流程内元素唯一key resourceId
    private int type; // stencil 类型
    private List<String> outgoing;
    private Map<String, Object> properties; // 配置属性
    private List<String> incoming;
}
