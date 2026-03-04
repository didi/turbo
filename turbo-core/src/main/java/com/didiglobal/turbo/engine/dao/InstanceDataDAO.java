package com.didiglobal.turbo.engine.dao;

import com.didiglobal.turbo.engine.entity.InstanceDataPO;

/**
 * DAO interface for instance data persistence.
 */
public interface InstanceDataDAO {

    InstanceDataPO select(String flowInstanceId, String instanceDataId);

    InstanceDataPO selectRecentOne(String flowInstanceId);

    int insert(InstanceDataPO instanceDataPO);

    int updateData(InstanceDataPO instanceDataPO);

    int insertOrUpdate(InstanceDataPO mergePo);
}
