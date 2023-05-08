package com.gen.code.service;

import com.gen.code.config.DbInfo;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class SqlSessionService {

    private SqlSessionService() {
    }

    public static SqlSessionService me() {
        return new SqlSessionService();
    }

    public final <T> SqlSession handleSession(DbInfo dbInfo, Class<T> classMapper) {
        return initMybatisSqlSessionFactory(dbInfo, classMapper).openSession(true);
    }

    public final <T> SqlSessionFactory initMybatisSqlSessionFactory(DbInfo dbInfo, Class<T> classMapper) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dbInfo);
        Configuration configuration = new Configuration(environment);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.addMapper(classMapper);
        return new SqlSessionFactoryBuilder().build(configuration);
    }

}
