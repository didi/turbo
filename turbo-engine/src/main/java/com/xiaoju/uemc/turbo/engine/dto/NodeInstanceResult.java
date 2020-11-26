package com.xiaoju.uemc.turbo.engine.dto;

import com.xiaoju.uemc.turbo.engine.bo.NodeInstance;
import lombok.Data;

/**
 * 项目名称：turbo
 * 类 名 称：NodeInstanceResult
 * 类 描 述：
 * 创建时间：2020/11/26 2:14 PM
 * 创 建 人：didiwangxing
 */
@Data
public class NodeInstanceResult extends CommonResult {
    private NodeInstance nodeInstance;
}
