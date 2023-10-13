package com.gen.code.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.gen.code.config.DbInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 获取SqlSession 和 Mapper 的配置类
 *
 * @author wangjunming
 */
@Slf4j
public class StaticSqlSession {

    private StaticSqlSession() {
    }

    public static StaticSqlSession me() {
        return new StaticSqlSession();
    }

    private <T> MybatisSqlSessionFactoryBean initMybatisPlusSqlSessionFactory(DbInfo dataSource, Class<T> classMapper) {
        MybatisConfiguration configuration = new MybatisConfiguration();
        //驼峰命名配置
        configuration.setMapUnderscoreToCamelCase(true);
        // 使用 #{} 以预编译的形式组装为最终SQL
        configuration.setUseGeneratedShortKey(true);
        // 配置日志框架
        configuration.setLogImpl(StdOutImpl.class);
        if (Objects.nonNull(classMapper)) {
            configuration.addMapper(classMapper);
        }
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setTransactionFactory(transactionFactory);
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setConfiguration(configuration);
//        sessionFactoryBean.setMapperLocations(resolveMapperLocations());
        sessionFactoryBean.setTypeAliasesPackage("com.gen.code.mapper.entity");
        return sessionFactoryBean;
    }

    private Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath*:com/gen/code/mapper/xml/*Mapper*.xml");
        List<Resource> resources = new ArrayList<>();
        for (String mapperLocation : mapperLocations) {
            try {
                Resource[] mappers = resourceResolver.getResources(mapperLocation);
                resources.addAll(Arrays.asList(mappers));
            } catch (Exception e) {
                log.error("加载mapper中的xml文件失败...", e);
            }
        }
        return resources.toArray(new Resource[mapperLocations.size()]);
    }

    private <T> SqlSession getSqlSession(DbInfo dbMessageInfo, Class<T> classMapper) {
        MybatisSqlSessionFactoryBean sqlSessionFactory = initMybatisPlusSqlSessionFactory(dbMessageInfo, classMapper);
        SqlSessionFactory sessionFactory;
        try {
            sessionFactory = sqlSessionFactory.getObject();
        } catch (Exception ex) {
            log.error("sqlSessionFactory.getObject(), 获取失败.", ex);
            throw new UnsupportedOperationException("获取 SqlSessionFactory 异常.", ex);
        }
        Assert.notNull(sessionFactory, "SqlSessionFactory 不能为空. ");
        assert sessionFactory != null;
        return sessionFactory.openSession(true);
    }

    public <T> T getStaticMapper(Class<T> clazz, DbInfo staticDbInfo) {
        return getSqlSession(staticDbInfo, clazz).getMapper(clazz);
    }


}
