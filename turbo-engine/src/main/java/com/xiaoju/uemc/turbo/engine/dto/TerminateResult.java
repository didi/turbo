package com.xiaoju.uemc.turbo.engine.dto;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Stefanie on 2019/12/19.
 */
@Data
@ToString(callSuper = true)
public class TerminateResult extends RuntimeResult {

    public TerminateResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
