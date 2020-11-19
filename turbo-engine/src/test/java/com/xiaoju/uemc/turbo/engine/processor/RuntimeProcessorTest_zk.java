package com.xiaoju.uemc.turbo.engine.processor;

import com.xiaoju.uemc.turbo.engine.dto.CommitTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.StartProcessDTO;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.CommitTaskParam;
import com.xiaoju.uemc.turbo.engine.param.StartProcessParam;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>左凯测试运行时处理器单元测试类</>
 *
 * <p>测试使用的deploy_id : zk_deploy_id_1</>
 * <p>测试使用的module_id : zk_module_id_1</>
 */
public class RuntimeProcessorTest_zk extends BaseTest {

    @Resource
    RuntimeProcessor runtimeProcessor;

    public StartProcessDTO startProcess(){
        StartProcessParam startProcessParam = new StartProcessParam();
        startProcessParam.setFlowDeployId("zk_deploy_id_1");
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("orderId", "string", "123"));
        startProcessParam.setVariables(variables);
        try {
            StartProcessDTO startProcessDTO = runtimeProcessor.startProcess(startProcessParam);
            LOGGER.info("testStartProcess.||startProcessDTO={}", startProcessDTO);
            return startProcessDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testStartProcess() {
        startProcess();
    }

    @Test
    public void testCommit_1() {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 1));
        commitTaskParam.setVariables(variables);
        try {
            // user task -> end node
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            LOGGER.info("testCommit.||commitTaskDTO={}", commitTaskDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCommit_0() {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);
        try {
            // user task -> exclusive gateway node
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            LOGGER.info("testCommit.||commitTaskDTO={}", commitTaskDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
