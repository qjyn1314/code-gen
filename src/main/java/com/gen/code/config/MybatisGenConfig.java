package com.gen.code.config;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

/**
 * <p>
 * explain:
 * </p>
 *
 * @author wangjunming
 * @since 2021/7/17 15:46
 */
public class MybatisGenConfig {

    private MybatisGenConfig() {
    }

    public static MybatisGenConfig me() {
        return new MybatisGenConfig();
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
