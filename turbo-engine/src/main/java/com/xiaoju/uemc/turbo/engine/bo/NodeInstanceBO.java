package com.xiaoju.uemc.turbo.engine.bo;

import lombok.Data;

/**
 * Created by Stefanie on 2019/12/5.
 */
@Data
public class NodeInstanceBO {
    //used while updateById
    private Long id;
    private String nodeInstanceId;
    private String nodeKey;
    private String sourceNodeInstanceId;
    private String sourceNodeKey;
    private String instanceDataId;
    private int status;
}
