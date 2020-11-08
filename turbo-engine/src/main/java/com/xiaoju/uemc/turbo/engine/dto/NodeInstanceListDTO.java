package com.xiaoju.uemc.turbo.engine.dto;

import com.google.common.base.MoreObjects;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;

import java.util.List;

/**
 * Created by Stefanie on 2020/1/6.
 */
public class NodeInstanceListDTO extends CommonDTO {

    private List<NodeInstanceDTO> nodeInstanceDTOList;

    public NodeInstanceListDTO(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public List<NodeInstanceDTO> getNodeInstanceDTOList() {
        return nodeInstanceDTOList;
    }

    public void setNodeInstanceDTOList(List<NodeInstanceDTO> nodeInstanceDTOList) {
        this.nodeInstanceDTOList = nodeInstanceDTOList;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("errCode", getErrCode())
                .add("errMsg", getErrMsg())
                .add("nodeInstanceDTOList", nodeInstanceDTOList)
                .toString();
    }
}
