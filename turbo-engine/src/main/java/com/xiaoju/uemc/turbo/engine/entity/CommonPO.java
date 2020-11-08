package com.xiaoju.uemc.turbo.engine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * Created by Stefanie on 2019/11/29.
 */
@Data
public class CommonPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String tenant;
    private String caller;
    private Date createTime;
    private Integer archive = 0;
}
