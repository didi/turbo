package com.xiaoju.uemc.turbo.engine.processor;

import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;
import com.google.common.collect.Lists;
import com.xiaoju.uemc.turbo.engine.dto.CommitTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.RecallTaskDTO;
import com.xiaoju.uemc.turbo.engine.dto.StartProcessDTO;
import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import com.xiaoju.uemc.turbo.engine.param.CommitTaskParam;
import com.xiaoju.uemc.turbo.engine.param.RecallTaskParam;
import com.xiaoju.uemc.turbo.engine.param.StartProcessParam;
import com.xiaoju.uemc.turbo.engine.runner.BaseTest;
import com.xiaoju.uemc.turbo.engine.util.EntityBuilder;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by Stefanie on 2019/12/13.
 */
public class RuntimeProcessorTest extends BaseTest {
    private static final ReportLogger LOGGER = LoggerFactory.getLogger(RuntimeProcessorTest.class);

    //deploy_id:ff9015c2-27b8-11ea-b85e-5ef9e2914105
    //module_id:3bb38aba-27b4-11ea-9109-5ef9e2914105
    //flow_key:Tpr4Rezf
    //flow_name:test-for-yiyang

    @Resource
    RuntimeProcessor runtimeProcessor;

    @Test
    public void testStartProcess() {
        StartProcessParam startProcessParam = EntityBuilder.buildStartProcessParam("08ec9356-27e4-11ea-a4b4-5ef9e2914105");
        try {
            StartProcessDTO startProcessDTO = runtimeProcessor.startProcess(startProcessParam);
            LOGGER.info("testStartProcess.||startProcessDTO={}", startProcessDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCommit() {
        String flowInstanceId = "3b072624-3037-11ea-8ca4-ee2a453bdfc2";
        String userTask1InstanceId = "536f36f9-3047-11ea-b806-ee2a453bdfc2";
        String userTask2InstanceId = "d95078e4-2d73-11ea-89fd-ae4f86b932f6";
        String userTask3InstanceId = "ff22ef96-2d73-11ea-89d3-ae4f86b932f6";
        List<InstanceData> variables = Lists.newArrayList();
        InstanceData instanceData1 = new InstanceData("a", "integer", 2);
        variables.add(instanceData1);
        InstanceData instanceData2 = new InstanceData("b", "integer", 1);
        variables.add(instanceData2);

        CommitTaskParam commitTaskParam = EntityBuilder.buildCommitTaskParam(flowInstanceId, userTask1InstanceId);
        commitTaskParam.setVariables(variables);
        try {
            System.out.println("\ncurrentStartTime:" + (new Date()));
            CommitTaskDTO commitTaskDTO = runtimeProcessor.commit(commitTaskParam);
            System.out.println("\ncurrentEndTime:" + (new Date()));
            LOGGER.info("testCommit.||commitTaskDTO={}", commitTaskDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRecall() {
        String flowInstanceId = "3b072624-3037-11ea-8ca4-ee2a453bdfc2";
        String userTask1InstanceId = "14d9a36c-3047-11ea-9d98-ee2a453bdfc2";
        String userTask2InstanceId = "b9b61c1b-2d73-11ea-9415-ae4f86b932f6";
        String userTask3InstanceId = "1e02de27-2bc4-11ea-8b8f-ae63b1c4eb76";
        String userTask4InstanceId = "a321982c-2bc9-11ea-9220-ae63b1c4eb76";
        RecallTaskParam recallTaskParam = EntityBuilder.buildRecallTaskParam(flowInstanceId, userTask1InstanceId);
        try {
            RecallTaskDTO recallTaskDTO = runtimeProcessor.recall(recallTaskParam);
            LOGGER.info("testCommit.||testRecall={}", recallTaskDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetInstanceData() {
        String flowInstanceId = "296d6b46-32a9-11ea-be8c-5ef9e2914105";
        try {
            List<InstanceData> instanceDataList = runtimeProcessor.getInstanceData(flowInstanceId);
            LOGGER.info("testGetInstanceData.||instanceDataList={}", instanceDataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
