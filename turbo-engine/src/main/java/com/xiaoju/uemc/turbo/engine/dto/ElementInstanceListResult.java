package com.xiaoju.uemc.turbo.engine.dto;

import com.google.common.base.MoreObjects;
import com.xiaoju.uemc.turbo.engine.bo.ElementInstance;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;

import java.util.List;

/**
 * Created by Stefanie on 2019/12/19.
 */
public class ElementInstanceListResult extends CommonResult {

    private List<ElementInstance> elementInstanceList;

    public ElementInstanceListResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }

    public List<ElementInstance> getElementInstanceList() {
        return elementInstanceList;
    }

    public void setElementInstanceList(List<ElementInstance> elementInstanceList) {
        this.elementInstanceList = elementInstanceList;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("errCode", getErrCode())
                .add("errMsg", getErrMsg())
                .add("elementInstanceList", elementInstanceList)
                .toString();
    }
}
