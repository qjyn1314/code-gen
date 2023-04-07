package com.gen.code;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.gen.code.config.DbInfo;
import com.gen.code.config.GenCodeInfo;
import com.gen.code.service.GenService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * <p>
 * explain: 生成代码主类
 * </p>
 *
 * @author wangjunming
 * @since 2021/7/17 14:33
 */
@Slf4j
public final class GenExecute {

    public static void main(String[] args) {
        genCodeByMysql();
    }

    private static void genCodeByMysql() {
        DbInfo dbInfo = new DbInfo();
        dbInfo.setJdbcUrl(DbInfo.getMysqlUrl("127.0.0.1", "3350", "sync_test"));
        dbInfo.setUsername("root");
        dbInfo.setPassword("123456");
        dbInfo.setDriverClassName(DbInfo.MYSQL_DRIVER_CLASS_NAME);

        GenCodeInfo genCodeInfo = new GenCodeInfo().setDbInfo(dbInfo);

        // 配置基本信息
        genDefault(genCodeInfo);

        new GenService().genCode(genCodeInfo);

        log.info("Success....");
        System.exit(1);
    }

    private static void genDefault(GenCodeInfo genCodeInfo) {

        genCodeInfo.setAuthor("code@code.com");

        String tableName = "conf_datasource";
        log.info("生成代码的表名是....{}", tableName);
        genCodeInfo.setTableName(tableName);

        //表名前缀
        genCodeInfo.setTablePrefix("");

        String comments = "数据源配置表";
        genCodeInfo.setComments(comments);
        log.info("生成代码的表备注是....{}", comments);

        // 类路径下默认的模板路径
        genCodeInfo.setDefaultTempPath("template//");

        // 指定类路径下的模板路径, 根据不同的指定, 获取不同的包名,生成不同的文件名,使用不同的模板文件
//        genCodeInfo.setSpecificPath("template//test//");

        //包名-在模板中的import 使用此出配置的包名路径
        genCodeInfo.setPackageMap(getDefaultPackMap(genCodeInfo));

        //使用指定的文件名
        genCodeInfo.setFileMap(getFileMap(genCodeInfo));

        //使用特定的目录下的模板
        genCodeInfo.setTemplatePathMap(getTempPathMap(genCodeInfo));

        //生成的路径
        genCodeInfo.setGenPath("D://workspace//gen_code");

    }

    /**
     * 获取生成的文件名后缀, 其文件名后缀与 模板文件名.replaceAll(".vm","") 一致.
     */
    private static Map<String, String> getFileMap(GenCodeInfo genCodeInfo) {
        return MapUtil
                .builder(GenCodeInfo.entity, ".java")
                .put(GenCodeInfo.controller, "Controller.java")
                .put(GenCodeInfo.service, "Service.java")
                .put(GenCodeInfo.serviceImpl, "ServiceImpl.java")
                .put(GenCodeInfo.mapper, "Mapper.java")
                .put(GenCodeInfo.mapperXml, "Mapper.xml")
                .build();
    }

    /**
     * 获取模板文件名
     */
    private static Map<String, String> getTempPathMap(GenCodeInfo genCodeInfo) {
        String defaultTemp = genCodeInfo.getSpecificPath();
        // 如果没有指定模板路径则使用默认的
        if (StrUtil.isBlank(defaultTemp)) {
            defaultTemp = genCodeInfo.getDefaultTempPath();
        }
        return MapUtil
                .builder(GenCodeInfo.entity, defaultTemp + GenCodeInfo.entityPath)
                .put(GenCodeInfo.controller, defaultTemp + GenCodeInfo.controllerPath)
                .put(GenCodeInfo.service, defaultTemp + GenCodeInfo.servicePath)
                .put(GenCodeInfo.serviceImpl, defaultTemp + GenCodeInfo.serviceImplPath)
                .put(GenCodeInfo.mapper, defaultTemp + GenCodeInfo.mapperPath)
                .put(GenCodeInfo.mapperXml, defaultTemp + GenCodeInfo.mapperXmlPath)
                .build();
    }

    /**
     * 获取默认的包名
     */
    private static Map<String, String> getDefaultPackMap(GenCodeInfo genCodeInfo) {
        return MapUtil
                .builder(GenCodeInfo.entity, "com.authorization.life.system.infra.entity")
                .put(GenCodeInfo.controller, "com.authorization.life.system.api.controller")
                .put(GenCodeInfo.service, "com.authorization.life.system.infra.service")
                .put(GenCodeInfo.serviceImpl, "com.authorization.life.system.infra.service.impl")
                .put(GenCodeInfo.mapper, "com.authorization.life.system.infra.mapper")
                .put(GenCodeInfo.mapperXml, "mapper")
                .build();
    }


}
