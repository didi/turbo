package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.InstanceDataPO;

public interface InstanceDataDAO {

    /**
     * Select: query instanceDataPO by flowInstanceId and instanceDataId.
     *
     * @param flowInstanceId
     * @param instanceDataId
     * @return instanceDataPO
     */
    InstanceDataPO select(String flowInstanceId, String instanceDataId);

    /**
     * SelectRecentOne: select recent InstanceData order by id desc.
     *
     * @param flowInstanceId
     * @return instanceDataPO
     */
    InstanceDataPO selectRecentOne(String flowInstanceId);

    /**
     * Insert: insert instanceDataPO, return -1 while insert failed.
     *
     * @param instanceDataPO
     * @return int
     */
    int insert(InstanceDataPO instanceDataPO);

    /**
     * UpdateData: update instanceData.
     *
     * @param instanceDataPO
     * @return int
     */
    int updateData(InstanceDataPO instanceDataPO);

    /**
     * InsertOrUpdate: insert or update instanceData.
     *
     * @param mergePo
     * @return int
     */
    int insertOrUpdate(InstanceDataPO mergePo);
}
