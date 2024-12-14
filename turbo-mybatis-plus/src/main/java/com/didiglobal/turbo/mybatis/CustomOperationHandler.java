package com.didiglobal.turbo.mybatis;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.SqlSessionFactory;

public interface CustomOperationHandler {
    /**
     * 自定义数据库表扩展处理
     * @param mappedStatement
     * @param parameterObject
     * @param originalResult
     * @param sqlSessionFactory
     */
    void handle(SqlCommandType commandType , MappedStatement mappedStatement, Object parameterObject, Object originalResult, SqlSessionFactory sqlSessionFactory);
}
