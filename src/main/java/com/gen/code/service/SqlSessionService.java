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

    @SafeVarargs
    public final <T> SqlSession handleSession(DbInfo dbInfo, Class<T>... classList) {
        SqlSessionFactory sqlSessionFactory = initMybatisSqlSessionFactory(dbInfo, classList);
        return sqlSessionFactory.openSession(true);
    }

    @SafeVarargs
    public final <T> SqlSessionFactory initMybatisSqlSessionFactory(DbInfo dbInfo, Class<T>... classList) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dbInfo);
        Configuration configuration = new Configuration(environment);
        configuration.setMapUnderscoreToCamelCase(true);
        for (Class<T> mapperClass : classList) {
            configuration.addMapper(mapperClass);
        }
        return new SqlSessionFactoryBuilder().build(configuration);
    }

}
