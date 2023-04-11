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

    /**
     * 生成代码的原理:
     * <p>
     * 1.准备数据(查询数据库中的表信息,以表名为条件)
     * <p>
     * 1.1 以 com.gen.code.config.GenCodeInfo#entity 为key, 配置包名路径的map, 指定生成文件名的map, 指定目录下的模板.
     * <p>
     * 1.2 如果此时需要增加多个文件, 则需要先增加 map中的key, 然后增加 模板文件, 增加指定生成的文件名, 即可在配置好模板的前提下短时间生成代码.
     * <p>
     * 1.3 以 模板文件为主, 循环处理不同的模板, 设置数据, 设置生成的文件路径, 以及文件名.
     * <p>
     * 1.4 对应关系-->>
     * <p>
     * 模板: controller-> Controller.java.vm
     * <p>
     * 包名: controller-> com.authorization.life.system.api.controller
     * <p>
     * 文件名: controller-> Controller.java
     * <p>
     * 注意: 此处的文件名前缀是当前的类名, 最后压缩包中的文件名为:
     * <p>
     * 结果: src/main/java/com/authorization/life/system/api/controller/xxxController.java
     * <p>
     * 2.准备Velocity
     * <p>
     * 3.写入文件
     * <p>
     * 4.打包为压缩包
     */
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

        // 表名前缀
        genCodeInfo.setTablePrefix("");
        // 手动指定实体类名
        genCodeInfo.setClassName("");
        // 手动指定实体类名(首字母小写)
        genCodeInfo.setLowerClassName("");
        // 手动指定控制层的请求路径
        genCodeInfo.setPathName("");

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
