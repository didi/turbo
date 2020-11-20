package com.xiaoju.uemc.turbo.engine.processor;

import com.xiaoju.uemc.turbo.engine.common.ErrorEnum;
import com.xiaoju.uemc.turbo.engine.dto.*;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.CommitTaskParam;
import com.xiaoju.uemc.turbo.engine.param.StartProcessParam;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    private StartProcessDTO startProcess(){
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
            // user task -> exclusive gateway node -> user task
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            LOGGER.info("testCommit.||commitTaskDTO={}", commitTaskDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRollback() {

    }

    @Test
    public void testTerminateProcess() {
        StartProcessDTO startProcessDTO = startProcess();
        try {
            TerminateDTO terminateDTO = runtimeProcessor.terminateProcess(startProcessDTO.getFlowInstanceId());
            LOGGER.info("testTerminateProcess.||terminateDTO={}", terminateDTO);
            Assert.assertTrue(terminateDTO.getErrCode() == ErrorEnum.SUCCESS.getErrNo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetHistoryUserTaskList() {
        StartProcessDTO startProcessDTO = startProcess();
        CommitTaskParam commitTaskParam = new CommitTaskParam();
        commitTaskParam.setFlowInstanceId(startProcessDTO.getFlowInstanceId());
        commitTaskParam.setTaskInstanceId(startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
        List<InstanceData> variables = new ArrayList<>();
        variables.add(new InstanceData("danxuankuang_ytgyk", "int", 0));
        commitTaskParam.setVariables(variables);
        try {
            // user task -> exclusive gateway node -> user task
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            NodeInstanceListDTO nodeInstanceListDTO = runtimeProcessor.getHistoryUserTaskList(commitTaskDTO.getFlowInstanceId());
            LOGGER.info("testGetHistoryUserTaskList.||nodeInstanceListDTO={}", nodeInstanceListDTO);
            StringBuilder sb = new StringBuilder();
            for(ElementInstanceDTO elementInstanceDTO : nodeInstanceListDTO.getNodeInstanceDTOList()){
                sb.append("[");
                sb.append(elementInstanceDTO.getModelKey());
                sb.append(" ");
                sb.append(elementInstanceDTO.getStatus());
                sb.append("]->");
            }
            LOGGER.info("testGetHistoryUserTaskList.||snapshot={}", sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testGetHistoryElementList() {
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
            ElementInstanceListDTO elementInstanceListDTO = runtimeProcessor.getHistoryElementList(commitTaskDTO.getFlowInstanceId());
            LOGGER.info("testGetHistoryElementList.||elementInstanceListDTO={}", elementInstanceListDTO);
            StringBuilder sb = new StringBuilder();
            for(ElementInstanceDTO elementInstanceDTO : elementInstanceListDTO.getElementInstanceDTOList()){
                sb.append("[");
                sb.append(elementInstanceDTO.getModelKey());
                sb.append(" ");
                sb.append(elementInstanceDTO.getStatus());
                sb.append("]->");
            }
            LOGGER.info("testGetHistoryElementList.||snapshot={}", sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testGetInstanceData() {
        StartProcessDTO startProcessDTO = startProcess();
        String flowInstanceId = startProcessDTO.getFlowInstanceId();
        try {
            List<InstanceData> instanceDataList = runtimeProcessor.getInstanceData(flowInstanceId);
            LOGGER.info("testGetInstanceData.||instanceDataList={}", instanceDataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetNodeInstance() {
        StartProcessDTO startProcessDTO = startProcess();
        String flowInstanceId = startProcessDTO.getFlowInstanceId();
        try {
            NodeInstanceDTO nodeInstanceDTO = runtimeProcessor.getNodeInstance(flowInstanceId, startProcessDTO.getActiveTaskInstance().getNodeInstanceId());
            LOGGER.info("testGetNodeInstance.||nodeInstanceDTO={}", nodeInstanceDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
