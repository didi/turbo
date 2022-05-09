package com.didiglobal.turbo.demo;

import com.didiglobal.turbo.demo.controller.FlowController;
import com.didiglobal.turbo.demo.pojo.request.CreateFlowRequest;
import com.didiglobal.turbo.demo.pojo.request.DeployFlowRequest;
import com.didiglobal.turbo.demo.pojo.request.GetFlowModuleListRequest;
import com.didiglobal.turbo.demo.pojo.request.GetFlowModuleRequest;
import com.didiglobal.turbo.demo.pojo.request.UpdateFlowRequest;
import com.didiglobal.turbo.demo.pojo.response.BaseResponse;
import com.didiglobal.turbo.demo.pojo.response.CreateFlowResponse;
import com.didiglobal.turbo.demo.pojo.response.DeployFlowResponse;
import com.didiglobal.turbo.demo.pojo.response.FlowModuleListResponse;
import com.didiglobal.turbo.demo.pojo.response.GetFlowModuleResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 *
 * demo页面中没有呈现但接口中支持的字段（例如租户标识），使用方可以根据自己业务需求酌情添加
 * @Author: james zhangxiao
 * @Date: 5/7/22
 * @Description: 接口测试类
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class FlowControllerTest {

    @Resource
    private FlowController flowController;

    private String flowModuleId = null;

    private String flowDeployId = null;

    @Test
    public void run() {
        createFlow();
        saveFlowModel();
        deployFlow();
        queryFlow();
        queryFlowList();
    }

    /**
     * 创建流程接口
     */
    @Test
    public void createFlow() {
        CreateFlowRequest createFlowRequest = new CreateFlowRequest();
        createFlowRequest.setFlowName("测试流程"); // 模型名称  非必需
        createFlowRequest.setFlowKey("testFlowKey");  //流程业务标识 非必需
        createFlowRequest.setTenant("didi");// 租户标识 必需
        createFlowRequest.setCaller("testCaller");// 使用方标识 必需
        createFlowRequest.setOperator("didiOperator");// 操作人 非必需
        createFlowRequest.setRemark("备注test");  //备注  非必需
        BaseResponse<CreateFlowResponse> res = flowController.createFlow(createFlowRequest);
        if (res.getErrCode() == 1000) {
            flowModuleId = res.getData().getFlowModuleId();//模型唯一标识  必需
            String str = "createFlow 处理成功 flowModuleId:%s";
            System.out.println(String.format(str,flowModuleId));
        } else {
            throw new RuntimeException("createFlow error");
        }
    }

    /**
     * 保存流程模型接口
     */
    @Test
    public void saveFlowModel() {
        String flowModel = "{\"flowElementList\":[{\"incoming\":[],\"outgoing\":[\"Flow_3599vu7\"],\"dockers\":[],\"type\":2,\"properties\":{\"name\":\"开始\",\"x\":310,\"y\":200,\"text\":{\"x\":310,\"y\":240,\"value\":\"开始\"}},\"key\":\"Event_0vbtunu\"},{\"incoming\":[\"Flow_3599vu7\"],\"outgoing\":[\"Flow_1f2ei89\"],\"dockers\":[],\"type\":4,\"properties\":{\"name\":\"\",\"x\":520,\"y\":200,\"text\":\"\"},\"key\":\"Activity_0ivtksn\"},{\"incoming\":[\"Flow_1f2ei89\"],\"outgoing\":[\"Flow_1rkk099\"],\"dockers\":[],\"type\":6,\"properties\":{\"name\":\"\",\"x\":730,\"y\":200,\"text\":\"\"},\"key\":\"Gateway_2qj55i1\"},{\"incoming\":[\"Flow_1rkk099\"],\"outgoing\":[],\"dockers\":[],\"type\":3,\"properties\":{\"name\":\"结束\",\"x\":950,\"y\":200,\"text\":{\"x\":950,\"y\":240,\"value\":\"结束\"}},\"key\":\"Event_03pjf39\"},{\"incoming\":[\"Event_0vbtunu\"],\"outgoing\":[\"Activity_0ivtksn\"],\"type\":1,\"dockers\":[],\"properties\":{\"name\":\"\",\"text\":\"\",\"startPoint\":\"{\\\"x\\\":328,\\\"y\\\":200}\",\"endPoint\":\"{\\\"x\\\":470,\\\"y\\\":200}\",\"pointsList\":\"\\\"\\\"\"},\"key\":\"Flow_3599vu7\"},{\"incoming\":[\"Activity_0ivtksn\"],\"outgoing\":[\"Gateway_2qj55i1\"],\"type\":1,\"dockers\":[],\"properties\":{\"name\":\"\",\"text\":\"\",\"startPoint\":\"{\\\"x\\\":570,\\\"y\\\":200}\",\"endPoint\":\"{\\\"x\\\":705,\\\"y\\\":200}\",\"pointsList\":\"\\\"\\\"\"},\"key\":\"Flow_1f2ei89\"},{\"incoming\":[\"Gateway_2qj55i1\"],\"outgoing\":[\"Event_03pjf39\"],\"type\":1,\"dockers\":[],\"properties\":{\"conditionsequenceflow\":\"a==1\",\"name\":\"\",\"text\":\"\",\"startPoint\":\"{\\\"x\\\":755,\\\"y\\\":200}\",\"endPoint\":\"{\\\"x\\\":932,\\\"y\\\":200}\",\"pointsList\":\"\\\"\\\"\"},\"key\":\"Flow_1rkk099\"}]}";
        UpdateFlowRequest updateFlowRequest = new UpdateFlowRequest();
        updateFlowRequest.setFlowModuleId(flowModuleId); //模型唯一标识  必需 （使用createFlow中返回的flowModuleId）
        updateFlowRequest.setFlowModel(flowModel);//模型内容 必需
        updateFlowRequest.setCaller("testCaller");// 使用方标识  必需
        updateFlowRequest.setTenant("didi");// 租户标识 必需
        updateFlowRequest.setFlowKey("testFlowKey");//流程业务标识 非必需
        updateFlowRequest.setFlowName("测试流程"); // 模型名称  非必需
        updateFlowRequest.setRemark("备注test");//备注  非必需
        updateFlowRequest.setOperator("didiOperator");// 操作人 非必需
        BaseResponse<String> res = flowController.saveFlowModel(updateFlowRequest);
        if (res.getErrCode() == 1000) {
            System.out.println("saveFlowModel 处理成功");
        } else {
            throw new RuntimeException("saveFlowModel error");
        }

    }


    /**
     * 发布流程接口
     */
    @Test
    public void deployFlow() {
        DeployFlowRequest deployFlowRequest = new DeployFlowRequest();
        deployFlowRequest.setFlowModuleId(flowModuleId);//模型唯一标识 必需
        deployFlowRequest.setTenant("didi"); //租户标识   必需
        deployFlowRequest.setCaller("testCaller");// 使用方标识  必需
        deployFlowRequest.setOperator("didiOperator"); // 操作人 非必需
        BaseResponse<DeployFlowResponse> res = flowController.deployFlow(deployFlowRequest);
        if (res.getErrCode() == 1000) {
            String flowModuleId = res.getData().getFlowModuleId();//模型唯一标识  必需
                   flowDeployId = res.getData().getFlowDeployId();//模型一次部署唯一标识  必需
            String str = "deployFlow 处理成功 flowModuleId:%s flowDeployId：%s";
            System.out.println(String.format(str,flowModuleId,flowDeployId));
        } else {
            throw new RuntimeException("deployFlow error");
        }
    }

    /**
     * 查询单个流程接口
     */
    @Test
    public void queryFlow() {
        GetFlowModuleRequest getFlowModuleRequest = new GetFlowModuleRequest();
        getFlowModuleRequest.setFlowModuleId(flowModuleId);//模型唯一标识 两个参数必须传入一个
        getFlowModuleRequest.setFlowDeployId(flowDeployId);//模型一次部署唯一标识 两个参数必须传入一个
        BaseResponse<GetFlowModuleResponse> res = flowController.queryFlow(getFlowModuleRequest);
        if (res.getErrCode() == 1000) {
            GetFlowModuleResponse getFlowModuleResponse = res.getData();
            getFlowModuleResponse.getFlowModel();//模型内容 必需
            getFlowModuleResponse.getFlowModuleId();////模型唯一标识 必需
            getFlowModuleResponse.getCaller();// 使用方标识  必需
            getFlowModuleResponse.getTenant();//租户标识   必需
            getFlowModuleResponse.getFlowName();// 模型名称  非必需
            getFlowModuleResponse.getFlowKey();//流程业务标识  非必需
            getFlowModuleResponse.getStatus();//状态码  非必需
            getFlowModuleResponse.getOperator();// 操作人  非必需
            getFlowModuleResponse.getRemark();//备注   非必需
            getFlowModuleResponse.getModifyTime();// 必需
            System.out.println("queryFlow 处理成功 ");
        } else {
            throw new RuntimeException("queryFlow error");
        }
    }

    /**
     * 查询流程列表接口
     */
    @Test
    public void queryFlowList() {
        GetFlowModuleListRequest getFlowModuleListRequest = new GetFlowModuleListRequest();
        getFlowModuleListRequest.setFlowName("测试流程");  // 模型名称  非必需
        getFlowModuleListRequest.setFlowModuleId(null);//模型唯一标识 非必需
        getFlowModuleListRequest.setFlowDeployId(null);//模型一次部署唯一标识 非必需
        getFlowModuleListRequest.setCurrent(1);//当前页  非必需 默认为1
        getFlowModuleListRequest.setSize(10);//每页条数   非必需 默认为10
        BaseResponse<FlowModuleListResponse> res = flowController.queryFlowList(getFlowModuleListRequest);
        if (res.getErrCode() == 1000) {
            String str = "queryFlowList 处理成功   总条数:%s  当前页：%s 每页条数：%s";
            str= String.format(str, res.getData().getTotal(),res.getData().getCurrent(),res.getData().getSize());
            System.out.println(str);
        } else {
            throw new RuntimeException("queryFlowList error");
        }
    }

}
