package com.didiglobal.turbo.demo.pojo.response;

import java.util.List;

/**
 * @Author: james zhangxiao
 * @Date: 4/11/22
 * @Description:
 */
public class FlowModuleListResponse {

    private List<FlowModuleResponse> flowModuleList;
    private Long current;
    private Long size;
    private Long total;

    public List<FlowModuleResponse> getFlowModuleList() {
        return flowModuleList;
    }

    public void setFlowModuleList(List<FlowModuleResponse> flowModuleList) {
        this.flowModuleList = flowModuleList;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
