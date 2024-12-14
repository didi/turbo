package com.didiglobal.turbo.plugin.dao;

import com.didiglobal.turbo.engine.entity.NodeInstanceLogPO;
import com.didiglobal.turbo.engine.util.MapToObjectConverter;
import com.didiglobal.turbo.plugin.dao.mapper.ParallelNodeInstanceLogMapper;
import com.didiglobal.turbo.plugin.entity.ParallelNodeInstanceLogPO;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ParallelNodeInstanceLogHandler implements com.didiglobal.turbo.mybatis.CustomOperationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelNodeInstanceLogHandler.class);

    @Override
    public void handle(SqlCommandType commandType, MappedStatement mappedStatement, Object parameterObject, Object originalResult, SqlSessionFactory sqlSessionFactory) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            ParallelNodeInstanceLogMapper mapper = sqlSession.getMapper(ParallelNodeInstanceLogMapper.class);
            switch (commandType) {
                case INSERT:
                    handleInsert(parameterObject, mapper);
                    break;
                case UPDATE:
                    handleUpdate(parameterObject, mapper);
                    break;
                case DELETE:
                    handleDelete(parameterObject, mapper);
                    break;
                case SELECT:
                    handleSelect(originalResult, mapper);
                    break;
                default:
                    LOGGER.warn("Unhandled SqlCommandType: {}", commandType);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Exception in handling command.||commandType={}||parameterObject={}||originalResult={}", commandType, parameterObject, originalResult, e);
        } finally {
            sqlSession.close();
        }
    }

    private void handleInsert(Object parameterObject, ParallelNodeInstanceLogMapper mapper) throws IllegalAccessException, InstantiationException {
        if (parameterObject instanceof NodeInstanceLogPO) {
            ParallelNodeInstanceLogPO parallelNodeInstanceLogPO = convertToParallelLog((NodeInstanceLogPO) parameterObject);
            mapper.insert(parallelNodeInstanceLogPO);
        } else if (parameterObject instanceof Map) {
            List<Object> list = (List<Object>) ((Map<?, ?>) parameterObject).get("list");
            if (list != null) {
                List<ParallelNodeInstanceLogPO> parallelLogList = list.stream()
                        .map(this::convertToParallelLogSafe)
                        .collect(Collectors.toList());
                mapper.insertList(parallelLogList);
            }
        }
    }

    private void handleUpdate(Object parameterObject, ParallelNodeInstanceLogMapper mapper) throws IllegalAccessException, InstantiationException {
        if (parameterObject instanceof NodeInstanceLogPO) {
            ParallelNodeInstanceLogPO parallelNodeInstanceLogPO = convertToParallelLog((NodeInstanceLogPO) parameterObject);
            if (null == parallelNodeInstanceLogPO.getExecuteId()) {
                return;
            }
            mapper.updateById(parallelNodeInstanceLogPO);
        }
    }

    private void handleDelete(Object parameterObject, ParallelNodeInstanceLogMapper mapper) throws IllegalAccessException, InstantiationException {
        if (parameterObject instanceof NodeInstanceLogPO) {
            ParallelNodeInstanceLogPO parallelNodeInstanceLogPO = convertToParallelLog((NodeInstanceLogPO) parameterObject);
            if (null == parallelNodeInstanceLogPO.getId())
                mapper.deleteById(parallelNodeInstanceLogPO.getId());
        }
    }

    private void handleSelect(Object originalResult, ParallelNodeInstanceLogMapper mapper) {
        if (originalResult instanceof List) {
            List<NodeInstanceLogPO> nodeInstanceLogList = (List<NodeInstanceLogPO>) originalResult;
            nodeInstanceLogList.forEach(nodeInstanceLogPO -> {
                try {
                    ParallelNodeInstanceLogPO parallelNodeInstanceLogPO = mapper.selectById(nodeInstanceLogPO.getId());
                    if (parallelNodeInstanceLogPO != null) {
                        Map<String, Object> properties = MapToObjectConverter.convertObjectToMap(parallelNodeInstanceLogPO);
                        nodeInstanceLogPO.getProperties().putAll(properties);
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.error("Error converting ParallelNodeInstanceLogPO to map for ID: {}", nodeInstanceLogPO.getId(), e);
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private ParallelNodeInstanceLogPO convertToParallelLog(NodeInstanceLogPO nodeInstanceLogPO) throws IllegalAccessException, InstantiationException {
        ParallelNodeInstanceLogPO parallelNodeInstanceLogPO = MapToObjectConverter.convertMapToObject(nodeInstanceLogPO.getProperties(), ParallelNodeInstanceLogPO.class);
        parallelNodeInstanceLogPO.setId(nodeInstanceLogPO.getId());
        return parallelNodeInstanceLogPO;
    }

    private ParallelNodeInstanceLogPO convertToParallelLogSafe(Object obj) {
        try {
            if (obj instanceof NodeInstanceLogPO) {
                return convertToParallelLog((NodeInstanceLogPO) obj);
            } else {
                LOGGER.warn("Unexpected object type: {}", obj.getClass().getName());
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Error converting object to ParallelNodeInstanceLogPO", e);
            throw new RuntimeException(e);
        }
    }
}