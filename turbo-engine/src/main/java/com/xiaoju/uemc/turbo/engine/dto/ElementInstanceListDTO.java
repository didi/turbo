package com.xiaoju.uemc.turbo.engine.dto;

import com.google.common.base.MoreObjects;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;

import java.util.List;

/**
 * Created by Stefanie on 2019/12/19.
 */
public class ElementInstanceListDTO extends CommonDTO {

    private List<ElementInstanceDTO> elementInstanceDTOList;

    public ElementInstanceListDTO(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public List<ElementInstanceDTO> getElementInstanceDTOList() {
        return elementInstanceDTOList;
    }

    public void setElementInstanceDTOList(List<ElementInstanceDTO> elementInstanceDTOList) {
        this.elementInstanceDTOList = elementInstanceDTOList;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("errCode", getErrCode())
                .add("errMsg", getErrMsg())
                .add("elementInstanceDTOList", elementInstanceDTOList)
                .toString();
    }
}
