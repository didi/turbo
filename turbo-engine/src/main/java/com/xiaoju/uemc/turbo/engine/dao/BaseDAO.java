package com.xiaoju.uemc.turbo.engine.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.didiglobal.reportlogger.LoggerFactory;
import com.didiglobal.reportlogger.ReportLogger;

/**
 * Created by Stefanie on 2019/12/5.
 */
@DS("engine")
public class BaseDAO<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IService<T> {
    protected static final ReportLogger LOGGER = LoggerFactory.getLogger(BaseDAO.class);
}
