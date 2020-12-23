package com.didiglobal.turbo.engine.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@DS("engine")
public class BaseDAO<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IService<T> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseDAO.class);
}
