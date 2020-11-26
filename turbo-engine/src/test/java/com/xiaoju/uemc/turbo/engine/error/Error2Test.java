package com.xiaoju.uemc.turbo.engine.error;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.dto.StartProcessDTO;
import com.xiaoju.uemc.turbo.engine.param.StartProcessParam;
import com.xiaoju.uemc.turbo.engine.processor.RuntimeProcessor;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * 错误码覆盖单元测试类之2000~2999 通用业务错误
 */
public class Error2Test extends BaseTest {

    @Resource
    private RuntimeProcessor runtimeProcessor;

    // ErrorEnum.PARAM_INVALID
    @Test
    public void error_2001() throws Exception {
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId(null);
        startProcessParam.setFlowModuleId(null);
        StartProcessDTO startProcessDTO = runtimeProcessor.startProcess(startProcessParam);
        Assert.assertTrue(startProcessDTO.getErrCode() == ErrorEnum.PARAM_INVALID.getErrNo());
    }
}
