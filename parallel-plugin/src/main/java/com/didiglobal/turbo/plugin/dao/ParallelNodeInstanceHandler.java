package com.didiglobal.turbo.plugin.dao;

import com.didiglobal.turbo.engine.entity.NodeInstancePO;
import com.didiglobal.turbo.engine.plugin.CustomOperationHandler;
import com.didiglobal.turbo.engine.util.MapToObjectConverter;
import com.didiglobal.turbo.plugin.dao.mapper.ParallelNodeInstanceMapper;
import com.didiglobal.turbo.plugin.entity.ParallelNodeInstancePO;
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
public class ParallelNodeInstanceHandler implements CustomOperationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelNodeInstanceHandler.class);

    @Override
    public void handle(SqlCommandType commandType, MappedStatement mappedStatement, Object parameterObject, Object originalResult, SqlSessionFactory sqlSessionFactory) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            ParallelNodeInstanceMapper mapper = sqlSession.getMapper(ParallelNodeInstanceMapper.class);
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
                    LOGGER.warn("Unhandled command type: {}", commandType);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred during handling. CommandType={} | ParameterObject={} | OriginalResult={}",
                commandType, parameterObject, originalResult, e);
        } finally {
            sqlSession.close();
        }
    }

    private void handleInsert(Object parameterObject, ParallelNodeInstanceMapper mapper) {
        if (parameterObject instanceof NodeInstancePO) {
            ParallelNodeInstancePO parallelNodeInstancePO = convertToParallelInstance((NodeInstancePO) parameterObject);
            mapper.insert(parallelNodeInstancePO);
        } else if (parameterObject instanceof Map) {
            List<Object> list = (List<Object>) ((Map<?,?>) parameterObject).get("list");
            if (list != null) {
                List<ParallelNodeInstancePO> parallelLogList = list.stream()
                    .map(this::convertToParallelLogSafe)
                    .collect(Collectors.toList());
                mapper.insertList(parallelLogList);
            }
        }
    }

    private void handleUpdate(Object parameterObject, ParallelNodeInstanceMapper mapper) {
        NodeInstancePO nodeInstancePO = extractNodeInstancePO(parameterObject);
        if (nodeInstancePO != null) {
            ParallelNodeInstancePO parallelNodeInstancePO = convertToParallelInstance(nodeInstancePO);
            if (null == parallelNodeInstancePO.getExecuteId()) {
                return;
            }
            mapper.updateById(parallelNodeInstancePO);
        }
    }

    private void handleDelete(Object parameterObject, ParallelNodeInstanceMapper mapper) {
        NodeInstancePO nodeInstancePO = extractNodeInstancePO(parameterObject);
        if (nodeInstancePO != null) {
            ParallelNodeInstancePO parallelNodeInstancePO = convertToParallelInstance(nodeInstancePO);
            if (null != parallelNodeInstancePO.getId()) {
                mapper.deleteById(parallelNodeInstancePO.getId());
            }
        }
    }

    /**
     * 从参数对象中提取 NodeInstancePO
     * 处理直接传入 NodeInstancePO 或 MyBatis-Plus 包装的 Map 参数
     *
     * @param parameterObject 参数对象
     * @return NodeInstancePO 对象，如果未找到则返回 null
     */
    private NodeInstancePO extractNodeInstancePO(Object parameterObject) {
        if (parameterObject instanceof NodeInstancePO) {
            return (NodeInstancePO) parameterObject;
        } else if (parameterObject instanceof Map) {
            // MyBatis-Plus updateById/deleteById 等方法会将实体对象包装在 Map 中
            Map<?, ?> paramMap = (Map<?, ?>) parameterObject;
            // 尝试从常见的键中获取实体对象
            String[] possibleKeys = {"et", "param1", "entity"};
            for (String key : possibleKeys) {
                if (paramMap.containsKey(key)) {
                    Object value = paramMap.get(key);
                    if (value instanceof NodeInstancePO) {
                        return (NodeInstancePO) value;
                    }
                }
            }
        }
        return null;
    }

    private void handleSelect(Object originalResult, ParallelNodeInstanceMapper mapper) {
        if (originalResult instanceof List) {
            List<NodeInstancePO> nodeInstancePOList = (List<NodeInstancePO>) originalResult;
            nodeInstancePOList.forEach(nodeInstancePO -> {
                try {
                    ParallelNodeInstancePO parallelNodeInstancePO = mapper.selectById(nodeInstancePO.getId());
                    Map<String, Object> properties = MapToObjectConverter.convertObjectToMap(parallelNodeInstancePO);
                    nodeInstancePO.getProperties().putAll(properties);
                } catch (IllegalAccessException e) {
                    LOGGER.error("Error converting ParallelNodeInstancePO to map. ID={}", nodeInstancePO.getId(), e);
                }
            });
        }
    }

    private ParallelNodeInstancePO convertToParallelInstance(NodeInstancePO nodeInstancePO) {
        try {
            ParallelNodeInstancePO parallelNodeInstancePO = MapToObjectConverter.convertMapToObject(nodeInstancePO.getProperties(), ParallelNodeInstancePO.class);
            parallelNodeInstancePO.setId(nodeInstancePO.getId());
            return parallelNodeInstancePO;
        } catch (IllegalAccessException | InstantiationException e) {
            LOGGER.error("Error converting NodeInstancePO to ParallelNodeInstancePO. ID={}", nodeInstancePO.getId(), e);
            throw new RuntimeException(e);
        }
    }

    private ParallelNodeInstancePO convertToParallelLogSafe(Object obj) {
        try {
            if (obj instanceof NodeInstancePO) {
                return convertToParallelInstance((NodeInstancePO) obj);
            } else {
                LOGGER.warn("Unexpected object type: {}", obj.getClass().getName());
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Error converting object to ParallelNodeInstancePO", e);
            throw new RuntimeException(e);
        }
    }
}
