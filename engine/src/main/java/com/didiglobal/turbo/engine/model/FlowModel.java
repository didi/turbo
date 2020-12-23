package com.didiglobal.turbo.engine.model;

import com.google.common.base.MoreObjects;

import java.util.List;

public class FlowModel {
    private List<FlowElement> flowElementList;

    public List<FlowElement> getFlowElementList() {
        return flowElementList;
    }

    public void setFlowElementList(List<FlowElement> flowElementList) {
        this.flowElementList = flowElementList;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("flowElementList", flowElementList)
                .toString();
    }
}
