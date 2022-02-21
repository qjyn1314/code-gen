package com.code.gen.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

/**
 * <p>
 * explain:
 * </p>
 *
 * @author wangjunming
 * @since 2021/7/17 15:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MybatisGenConfig {

    private DbMessageInfo dbMessageInfo;

    private DataSource genDataSource() {
        DruidDataSource dataSource  = new DruidDataSource();
        dataSource.setDriverClassName(dbMessageInfo.driverClassName);
        dataSource.setUrl(dbMessageInfo.url);
        dataSource.setUsername(dbMessageInfo.username);
        dataSource.setPassword(dbMessageInfo.password);
        return dataSource;
    }


    @SafeVarargs
    public final <T> SqlSessionFactory initMybatisSqlSessionFactory(Class<T>... classList) {
        DataSource dataSource = genDataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        for (Class<T> mapperClass : classList) {
            configuration.addMapper(mapperClass);
        }
        return new SqlSessionFactoryBuilder().build(configuration);
    }


}
