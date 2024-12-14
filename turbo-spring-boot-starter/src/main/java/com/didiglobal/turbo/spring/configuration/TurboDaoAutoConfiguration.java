package com.didiglobal.turbo.spring.configuration;

import com.didiglobal.turbo.engine.dao.FlowDefinitionDAO;
import com.didiglobal.turbo.engine.dao.FlowDeploymentDAO;
import com.didiglobal.turbo.engine.dao.FlowInstanceMappingDAO;
import com.didiglobal.turbo.engine.dao.InstanceDataDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceDAO;
import com.didiglobal.turbo.engine.dao.NodeInstanceLogDAO;
import com.didiglobal.turbo.engine.dao.ProcessInstanceDAO;
import com.didiglobal.turbo.mybatis.dao.impl.FlowDefinitionDAOImpl;
import com.didiglobal.turbo.mybatis.dao.impl.FlowDeploymentDAOImpl;
import com.didiglobal.turbo.mybatis.dao.impl.FlowInstanceMappingDAOImpl;
import com.didiglobal.turbo.mybatis.dao.impl.InstanceDataDAOImpl;
import com.didiglobal.turbo.mybatis.dao.impl.NodeInstanceDAOImpl;
import com.didiglobal.turbo.mybatis.dao.impl.NodeInstanceLogDAOImpl;
import com.didiglobal.turbo.mybatis.dao.impl.ProcessInstanceDAOImpl;
import com.didiglobal.turbo.mybatis.dao.mapper.FlowDefinitionMapper;
import com.didiglobal.turbo.mybatis.dao.mapper.FlowDeploymentMapper;
import com.didiglobal.turbo.mybatis.dao.mapper.FlowInstanceMappingMapper;
import com.didiglobal.turbo.mybatis.dao.mapper.InstanceDataMapper;
import com.didiglobal.turbo.mybatis.dao.mapper.NodeInstanceLogMapper;
import com.didiglobal.turbo.mybatis.dao.mapper.NodeInstanceMapper;
import com.didiglobal.turbo.mybatis.dao.mapper.ProcessInstanceMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * turbo dao auto configuration
 *
 * @author fxz
 */
@Configuration
public class TurboDaoAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public FlowDefinitionDAO flowDefinitionDAO(FlowDefinitionMapper flowDefinitionMapper) {
        return new FlowDefinitionDAOImpl(flowDefinitionMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    public FlowDeploymentDAO flowDeploymentDAO(FlowDeploymentMapper flowDeploymentMapper) {
        return new FlowDeploymentDAOImpl(flowDeploymentMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    public FlowInstanceMappingDAO flowInstanceMappingDAO(FlowInstanceMappingMapper flowInstanceMappingMapper) {
        return new FlowInstanceMappingDAOImpl(flowInstanceMappingMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    public InstanceDataDAO instanceDataDAO(InstanceDataMapper instanceDataMapper) {
        return new InstanceDataDAOImpl(instanceDataMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    public NodeInstanceDAO nodeInstanceDAO(NodeInstanceMapper nodeInstanceMapper) {
        return new NodeInstanceDAOImpl(nodeInstanceMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    public NodeInstanceLogDAO nodeInstanceLogDAO(NodeInstanceLogMapper nodeInstanceLogMapper) {
        return new NodeInstanceLogDAOImpl(nodeInstanceLogMapper);
    }

    @ConditionalOnMissingBean
    @Bean
    public ProcessInstanceDAO processInstanceDAO(ProcessInstanceMapper processInstanceMapper) {
        return new ProcessInstanceDAOImpl(processInstanceMapper);
    }

}