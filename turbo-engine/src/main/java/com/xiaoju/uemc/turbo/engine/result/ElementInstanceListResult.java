package com.xiaoju.uemc.turbo.engine.result;

import com.xiaoju.uemc.turbo.engine.bo.ElementInstance;
import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by Stefanie on 2019/12/19.
 */
@Data
@ToString(callSuper = true)
public class ElementInstanceListResult extends CommonResult {
    private List<ElementInstance> elementInstanceList;

    public ElementInstanceListResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
