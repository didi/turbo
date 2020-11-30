package com.xiaoju.uemc.turbo.engine.bo;

import com.google.common.base.MoreObjects;
import lombok.Data;

import java.util.Map;

/**
 * Created by Stefanie on 2019/12/19.
 */
@Data
public class ElementInstance {

    private String modelKey;
    private String modelName;
    private Map<String, Object> properties;
    private int status;

    public ElementInstance() {
        super();
    }

    public ElementInstance(String modelKey, int status) {
        super();
        this.modelKey = modelKey;
        this.status = status;
    }
}
