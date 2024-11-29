package com.didiglobal.turbo.plugin.executor;

import com.didiglobal.turbo.plugin.common.ParallelErrorEnum;
import com.didiglobal.turbo.plugin.common.MergeStrategy;
import com.didiglobal.turbo.engine.exception.TurboException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MergeStrategyFactory
 *
 * @author didi
 */
@Component
public class MergeStrategyFactory {

    @Resource
    private DataMergeAll dataMergeAll;

    @Resource
    private DataMergeNone dataMergeNone;

    @Resource
    private DataMergeCustom dataMergeCustom;

    @Resource
    private BranchMergeJoinAll branchMergeJoinAll;

    @Resource
    private BranchMergeAnyOne branchMergeAnyOne;

    @Resource
    private BranchMergeCustom branchMergeCustom;

    public DataMergeStrategy getDataMergeStrategy(String dataMerge) throws TurboException {
        switch (dataMerge) {
            case MergeStrategy.DATA_MERGE.ALL:
                return dataMergeAll;
            case MergeStrategy.DATA_MERGE.NONE:
                return dataMergeNone;
            case MergeStrategy.DATA_MERGE.CUSTOM:
                return dataMergeCustom;
            default:
                throw new TurboException(ParallelErrorEnum.UNSUPPORTED_DATA_MERGE_STRATEGY.getErrNo(), ParallelErrorEnum.UNSUPPORTED_DATA_MERGE_STRATEGY.getErrMsg());
        }
    }

    public BranchMergeStrategy getBranchMergeStrategy(String branchMerge) throws TurboException {
        switch (branchMerge) {
            case MergeStrategy.BRANCH_MERGE.JOIN_ALL:
                return branchMergeJoinAll;
            case MergeStrategy.BRANCH_MERGE.ANY_ONE:
                return branchMergeAnyOne;
            case MergeStrategy.BRANCH_MERGE.CUSTOM:
                return branchMergeCustom;
            default:
                throw new TurboException(ParallelErrorEnum.UNSUPPORTED_BRANCH_MERGE_STRATEGY.getErrNo(), ParallelErrorEnum.UNSUPPORTED_BRANCH_MERGE_STRATEGY.getErrMsg());
        }
    }
}