package com.xiaoju.uemc.turbo.engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Stefanie on 2019/12/1.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceData {
    private String key;
    private String type;
    private Object value;

    public InstanceData(String key, Object value) {
        this.key = key;
        this.value = value;
    }
}
