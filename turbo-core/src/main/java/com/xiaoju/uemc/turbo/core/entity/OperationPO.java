package com.xiaoju.uemc.turbo.core.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * Created by Stefanie on 2019/12/2.
 */
@Data
@ToString(callSuper = true)
public class OperationPO extends CommonPO {
    private String operator;
    private Date modifyTime;
    private String remark;
}
