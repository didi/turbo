package com.didiglobal.turbo.demo.controller;

import com.didiglobal.turbo.demo.pojo.request.*;
import com.didiglobal.turbo.demo.pojo.response.*;
import com.didiglobal.turbo.demo.service.FlowServiceImpl;
import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.result.CreateFlowResult;
import com.didiglobal.turbo.engine.result.DeployFlowResult;
import com.didiglobal.turbo.engine.result.FlowModuleResult;
import com.didiglobal.turbo.engine.result.UpdateFlowResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 *
 * @Author: james zhangxiao
 * @Date: 4/1/22
 * @Description: logigcFlow与turbo交互样例
 */
@RestController
@RequestMapping("/logicFlow")
public class FlowController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowController.class);

    @Resource
    private FlowServiceImpl logicFlowService;
    /**
     * 创建流程
     * @param createFlowParam
     * @return
     */
    @RequestMapping(value = "/createFlow", method = {RequestMethod.POST})
    public BaseResponse<CreateFlowResponse> createFlow(@RequestBody CreateFlowRequest createFlowParam) {
        try{
            CreateFlowResult createFlowResult = logicFlowService.createFlow(createFlowParam);
            CreateFlowResponse createFlowResponse = new CreateFlowResponse();
            BaseResponse<CreateFlowResponse> baseResponse =  BaseResponse.make(createFlowResponse);
            BeanUtils.copyProperties(createFlowResult,baseResponse);
            BeanUtils.copyProperties(createFlowResult,createFlowResponse);
            return baseResponse;
        }catch(Exception e){
            LOGGER.error("createFlow error",e);
           return  new BaseResponse<>(ErrorEnum.SYSTEM_ERROR);
        }
    }

    /**
     * 保存流程模型数据
     * @param updateFlowRequest   flowModuleId 使用createFlow返回的flowModuleId
     * @return  成功 or 失败
     */
    @RequestMapping(value = "/saveFlowModel", method = {RequestMethod.POST})
    public BaseResponse<String> saveFlowModel(@RequestBody UpdateFlowRequest updateFlowRequest) {
        try{
            UpdateFlowResult updateFlowResult = logicFlowService.updateFlow(updateFlowRequest);
            BaseResponse<String> baseResponse = new BaseResponse<>(ErrorEnum.SUCCESS);
            BeanUtils.copyProperties(updateFlowResult,baseResponse);
            return baseResponse;
        }catch(Exception e){
            LOGGER.error("saveFlowModel error",e);
            return  new BaseResponse<>(ErrorEnum.SYSTEM_ERROR);
        }
    }

    /**
     * 发布流程
     * @param deployFlowRequest   flowModuleId 使用createFlow返回的flowModuleId
     * @return
     */
    @RequestMapping(value = "/deployFlow", method = {RequestMethod.POST})
    public BaseResponse<DeployFlowResponse> deployFlow(@RequestBody DeployFlowRequest deployFlowRequest) {
        try{
            DeployFlowResult deployFlowResult = logicFlowService.deployFlow(deployFlowRequest);
            DeployFlowResponse deployFlowResponse = new DeployFlowResponse();
            BaseResponse<DeployFlowResponse> baseResponse =  BaseResponse.make(deployFlowResponse);
            BeanUtils.copyProperties(deployFlowResult,baseResponse);
            BeanUtils.copyProperties(deployFlowResult,deployFlowResponse);
            return baseResponse;
        }catch(Exception e){
            LOGGER.error("deployFlow error",e);
            return  new BaseResponse<>(ErrorEnum.SYSTEM_ERROR);
        }
    }


    /**
     * 查询流程
     * @param getFlowModuleRequest  单个流程请求参数
     * @return  单个流程数据
     */
    @RequestMapping(value = "/queryFlow", method = {RequestMethod.POST})
    public BaseResponse<GetFlowModuleResponse> queryFlow(@RequestBody GetFlowModuleRequest getFlowModuleRequest) {
        try{
            FlowModuleResult flowModuleResult = logicFlowService.getFlowModule(getFlowModuleRequest);
            GetFlowModuleResponse getFlowModuleResponse = new GetFlowModuleResponse();
            BaseResponse<GetFlowModuleResponse> baseResponse =  BaseResponse.make(getFlowModuleResponse);
            BeanUtils.copyProperties(flowModuleResult,baseResponse);
            BeanUtils.copyProperties(flowModuleResult,getFlowModuleResponse);
            return baseResponse;
        }catch(Exception e){
            LOGGER.error("queryFlow error",e);
            return  new BaseResponse<>(ErrorEnum.SYSTEM_ERROR);
        }
    }


    /**
     * 分页查询流程列表
     * @param getFlowModuleListRequest  分页请求参数
     * @return 返回列表数据
     */
    @RequestMapping(value = "/queryFlowList", method = {RequestMethod.POST})
    public BaseResponse<FlowModuleListResponse> queryFlowList(@RequestBody GetFlowModuleListRequest getFlowModuleListRequest) {
        try{
            FlowModuleListResponse flowModuleListResponse = logicFlowService.getFlowModuleList(getFlowModuleListRequest);
            BaseResponse<FlowModuleListResponse> baseResponse =  BaseResponse.make(flowModuleListResponse);
            baseResponse.setErrCode(ErrorEnum.SUCCESS.getErrNo());
            baseResponse.setErrMsg(ErrorEnum.SUCCESS.getErrMsg());
            return baseResponse;
        }catch(Exception e){
            LOGGER.error(" queryFlowList error",e);
            return  new BaseResponse<>(ErrorEnum.SYSTEM_ERROR);
        }
    }
}
