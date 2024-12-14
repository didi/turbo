package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.InstanceDataPO;

public interface InstanceDataDAO extends BaseDAO{

    InstanceDataPO select(String flowInstanceId, String instanceDataId);

    /**
     * select recent InstanceData order by id desc
     *
     * @param flowInstanceId
     * @return
     */
    InstanceDataPO selectRecentOne(String flowInstanceId);

    /**
     * insert instanceDataPO
     *
     * @param instanceDataPO
     * @return -1 while insert failed
     */
    int insert(InstanceDataPO instanceDataPO);

    /**
     * update instanceData
     *
     * @param instanceDataPO
     * @return
     */
    int updateData(InstanceDataPO instanceDataPO);

    /**
     * insert or update instanceData
     *
     * @param mergePo
     * @return
     */
    int insertOrUpdate(InstanceDataPO mergePo);

}
