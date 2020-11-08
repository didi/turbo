package com.xiaoju.uemc.turbo.engine.dto;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;

/**
 * Created by Stefanie on 2019/12/19.
 */
public class TerminateDTO extends RuntimeDTO {

    public TerminateDTO(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
