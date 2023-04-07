package com.gen.code.config;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class SqlSessionService {

    private SqlSessionService() {
    }

    public static SqlSessionService me() {
        return new SqlSessionService();
    }

    @SafeVarargs
    public final <T> SqlSession handleSession(DbInfo dbInfo, Class<T>... classList) {
        SqlSessionFactory sqlSessionFactory = MybatisGenConfig.me().initMybatisSqlSessionFactory(dbInfo, classList);
        return sqlSessionFactory.openSession(true);
    }

}
