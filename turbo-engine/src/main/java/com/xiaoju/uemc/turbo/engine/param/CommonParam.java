package com.xiaoju.uemc.turbo.engine.param;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 项目名称：optimus-prime
 * 类 名 称：CommonParam
 * 类 描 述：
 * 创建时间：2020/9/5 10:16 AM
 * 创 建 人：didiwangxing
 */
@Data
@AllArgsConstructor
public class CommonParam {
    private String tenant;
    private String caller;

}
