package com.xiaoju.uemc.turbo.engine.result;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 项目名称：turbo
 * 类 名 称：InstanceDataListResult
 * 类 描 述：
 * 创建时间：2020/11/26 11:07 AM
 * 创 建 人：didiwangxing
 */
@Data
@ToString(callSuper = true)
public class InstanceDataListResult extends CommonResult {
    private List<InstanceData> variables;

    public InstanceDataListResult(ErrorEnum errorEnum) {
        super(errorEnum);
    }
}
