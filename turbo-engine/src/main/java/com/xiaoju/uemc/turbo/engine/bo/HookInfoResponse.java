package com.xiaoju.uemc.turbo.engine.bo;

import lombok.Data;

import java.util.Map;

/**
 * Created by Stefanie on 2019/12/13.
 */
@Data
public class HookInfoResponse {
    private int status;
    private String error;
    private String detailMessage;
    private Map<String, Object> data;
}
