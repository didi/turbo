package com.didiglobal.turbo.plugin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didiglobal.turbo.plugin.common.Constants;
import com.didiglobal.turbo.plugin.common.ParallelErrorEnum;
import com.didiglobal.turbo.engine.exception.ProcessException;
import com.didiglobal.turbo.engine.model.FlowElement;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExecutorUtil {

    private static final String COMMA = ",";
    private static final String VERTICAL_LINE = "|";

    /**
     * Generate a new execution id list
     * If there is parallel nesting, the execution ID in the current context needs to be passed in.
     * eg.
     *   current execute id :  execute_A
     *   new execute id list:  [execute_B, execute_C]
     *   result: execute_A|execute_B, execute_C
     *
     * @param parentExecuteId The parallel execution id of the previous level
     * @param collection      New Parallel Execution id
     * @return A|b,c
     */
    public static String genExecuteIds(String parentExecuteId, Collection<String> collection) {
        if (StringUtils.isBlank(parentExecuteId)) {
            return Joiner.on(COMMA).join(collection);
        }
        if (parentExecuteId.endsWith(VERTICAL_LINE)) {
            return parentExecuteId + Joiner.on(COMMA).join(collection);
        } else {
            return parentExecuteId + VERTICAL_LINE + Joiner.on(COMMA).join(collection);
        }
    }

    /**
     * Generate a new execution Id
     *
     * @param parentExecuteId The parallel execution id of the previous level
     * @param executeId       current execute id;
     * @return
     */
    public static String genExecuteIdWithParent(String parentExecuteId, String executeId) {
        if (StringUtils.isBlank(parentExecuteId)) {
            return executeId;
        }
        if (parentExecuteId.endsWith(VERTICAL_LINE)) {
            return parentExecuteId + executeId;
        } else {
            return parentExecuteId + VERTICAL_LINE + executeId;
        }
    }

    /**
     * Get Execution ID List
     * Example:
     * Execution ID may be  "a|b|c,d"
     * The result is [c,d]
     *
     * @param executeIds execute id list str
     * @return set
     */
    public static Set<String> getExecuteIdSet(String executeIds) {
        if (StringUtils.isBlank(executeIds)) {
            return Sets.newHashSet();
        }
        if (StringUtils.containsNone(executeIds, VERTICAL_LINE)) {
            return splitStrToSet(executeIds);
        }
        return splitStrToSet(executeIds.substring(executeIds.lastIndexOf(VERTICAL_LINE) + 1));
    }

    /**
     * Get parent execute id
     *
     * @param executeIds execute id string, eg: a|b|c,d
     * @return parent execute id , eg: a|b
     */
    public static String getParentExecuteId(String executeIds) {
        if (StringUtils.isBlank(executeIds) || StringUtils.containsNone(executeIds, VERTICAL_LINE)) {
            return StringUtils.EMPTY;
        }
        return executeIds.substring(0, executeIds.lastIndexOf(VERTICAL_LINE));
    }

    /**
     * Get execute id
     *
     * @param executeId execute id string, eg: a|b|c
     * @return parent execute id , eg: c
     */
    public static String getCurrentExecuteId(String executeId) {
        if (StringUtils.isBlank(executeId)) {
            return StringUtils.EMPTY;
        }
        if(StringUtils.containsNone(executeId, VERTICAL_LINE)){
            return executeId;
        }
        return executeId.substring(executeId.lastIndexOf(VERTICAL_LINE) + 1);
    }

    /**
     * left is fork
     * right is join
     *
     * @param nodeModel
     * @return
     */
    public static Pair<String, String> getForkAndJoinNodeKey(FlowElement nodeModel) throws ProcessException {
        Map<String, Object> properties = nodeModel.getProperties();
        Object forkAndJoin = properties.get(Constants.ELEMENT_PROPERTIES.FORK_JOIN_MATCH);
        if (forkAndJoin == null) {
            throw new ProcessException(ParallelErrorEnum.UNSUPPORTED_DATA_MERGE_STRATEGY.getErrNo(), ParallelErrorEnum.UNSUPPORTED_DATA_MERGE_STRATEGY.getErrMsg());
        }
        JSONObject forkJoinMatch = JSON.parseObject(forkAndJoin.toString());
        String fork = forkJoinMatch.getString(Constants.ELEMENT_PROPERTIES.FORK);
        String join = forkJoinMatch.getString(Constants.ELEMENT_PROPERTIES.JOIN);

        return Pair.of(fork, join);
    }

    /**
     * Checks if any value from the delimited string exists in the executeIds collection.
     *
     * @param executeIds      a collection of strings representing the execute IDs
     * @param targetStr a string containing values separated by '|' delimiter
     * @return true if any value from the delimited string exists in the executeIds collection, false otherwise
     */
    public static boolean containsAny(Collection<String> executeIds, String targetStr) {
        if (executeIds == null || executeIds.isEmpty() || targetStr == null || targetStr.isEmpty()) {
            return false;
        }
        Set<String> executeIdSet = new HashSet<>(executeIds);
        String[] values = targetStr.split("\\|");
        for (String value : values) {
            if (executeIdSet.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFork(String nodeKey, Pair<String, String> pair) {
        return StringUtils.equalsIgnoreCase(nodeKey, pair.getLeft());
    }

    public static boolean isJoin(String nodeKey, Pair<String, String> pair) {
        return StringUtils.equalsIgnoreCase(nodeKey, pair.getRight());
    }

    public static String append(String sourceNodeInstanceId, String appendStr) {
        if (StringUtils.isNotBlank(sourceNodeInstanceId)) {
            return sourceNodeInstanceId + COMMA + appendStr;
        }
        return appendStr;
    }

    public static Set<String> splitStrToSet(String executeIds) {
        if (StringUtils.isBlank(executeIds)) {
            return Sets.newHashSet();
        }
        String[] split = StringUtils.split(executeIds, COMMA);
        return Sets.newHashSet(split);
    }

    public static boolean allArrived(Set<String> executeIds, Set<String> arrivedExecuteIds) {
        return executeIds != null && arrivedExecuteIds != null
                && executeIds.size() == arrivedExecuteIds.size()
                && executeIds.containsAll(arrivedExecuteIds);
    }
}
