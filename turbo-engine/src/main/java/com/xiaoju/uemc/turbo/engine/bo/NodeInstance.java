package com.xiaoju.uemc.turbo.engine.bo;

import lombok.Data;

import java.util.Date;

/**
 * Created by Stefanie on 2019/12/5.
 */
@Data
public class NodeInstance extends ElementInstance {
    private String nodeInstanceId;
    private Date createTime;
    private Date modifyTime;
}
