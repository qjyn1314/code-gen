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
//        System.exit(1);
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
     * 1.3 以 模板文件为驱动(没有相应的模板则不进行生成代码), 循环处理不同的模板, 设置数据, 设置生成的文件路径, 以及文件名.
     * <p>
     * 1.4 对应关系如下-->>
     * <p>
     * 模板: controller-> Controller.java.vm
     * <p>
     * 包名: controller-> com.authorization.life.system.api.controller
     * <p>
     * 文件名: controller-> Controller.java
     * <p>
     * 注意: 此处的文件名是当前的类文件名的后缀, 最后压缩包中的文件名为:
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
    }

    private static void genDefault(GenCodeInfo genCodeInfo) {

        genCodeInfo.setAuthor("code@code.com");

        String tableName = "lemd_tenant";
        log.info("生成代码的表名是....{}", tableName);
        genCodeInfo.setTableName(tableName);

        // 表名前缀
        genCodeInfo.setTablePrefix("lemd_");
        // 手动指定实体类名
        genCodeInfo.setClassName("");
        // 手动指定实体类名(首字母小写)
        genCodeInfo.setLowerClassName("");
        // 手动指定控制层的请求路径
        genCodeInfo.setPathName("");

        String comments = "数据源配置表";
        genCodeInfo.setComments(comments);
        log.info("生成代码的表备注是....{}", comments);

        // 类路径下默认的模板路径-此处必须加后缀斜杠
        genCodeInfo.setDefaultTempPath("template//");

        // 指定类路径下的模板路径, 根据不同的指定, 获取不同的包名,生成不同的文件名,使用不同的模板文件
//        genCodeInfo.setSpecificPath("template//test//");

        //包名-在模板中的import 使用此出配置的包名路径, 将生成最终文件所在的层级文件.
        genCodeInfo.setPackageMap(getStudyWorkPackMap(genCodeInfo));

        //使用指定的文件名, 此处的文件名默认是 生成最终文件名的后缀, 结果是 当前所需生成的类名(ConfDataSource) + 文件名的后缀(Controller.java),
        // 如: controller -> Controller.java --> 最终文件名: ConfDataSourceController.java
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
                .builder(GenCodeInfo.ENTITY, ".java")
                .put(GenCodeInfo.CONTROLLER, "Controller.java")
                .put(GenCodeInfo.SERVICE, "Service.java")
                .put(GenCodeInfo.SERVICE_IMPL, "ServiceImpl.java")
                .put(GenCodeInfo.MAPPER, "Mapper.java")
                .put(GenCodeInfo.MAPPER_XML, "Mapper.xml")
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
                .builder(GenCodeInfo.ENTITY, defaultTemp + GenCodeInfo.ENTITY_JAVA_VM)
                .put(GenCodeInfo.CONTROLLER, defaultTemp + GenCodeInfo.CONTROLLER_JAVA_VM)
                .put(GenCodeInfo.SERVICE, defaultTemp + GenCodeInfo.SERVICE_JAVA_VM)
                .put(GenCodeInfo.SERVICE_IMPL, defaultTemp + GenCodeInfo.SERVICE_IMPL_JAVA_VM)
                .put(GenCodeInfo.MAPPER, defaultTemp + GenCodeInfo.MAPPER_JAVA_VM)
                .put(GenCodeInfo.MAPPER_XML, defaultTemp + GenCodeInfo.MAPPER_XML_VM)
                .build();
    }

    /**
     * 获取默认的包名
     */
    private static Map<String, String> getDefaultPackMap(GenCodeInfo genCodeInfo) {
        return MapUtil
                .builder(GenCodeInfo.ENTITY, "com.authorization.life.flowable.infra.entity")
                .put(GenCodeInfo.CONTROLLER, "com.authorization.life.flowable.api.controller")
                .put(GenCodeInfo.SERVICE, "com.authorization.life.flowable.infra.service")
                .put(GenCodeInfo.SERVICE_IMPL, "com.authorization.life.flowable.infra.service.impl")
                .put(GenCodeInfo.MAPPER, "com.authorization.life.flowable.infra.mapper")
                .put(GenCodeInfo.MAPPER_XML, "mapper")
                .build();
    }

    /**
     * 获取默认的包名
     */
    private static Map<String, String> getStudyWorkPackMap(GenCodeInfo genCodeInfo) {
        return MapUtil
                .builder(GenCodeInfo.ENTITY, "com.hulunbuir.infra.entity")
                .put(GenCodeInfo.CONTROLLER, "com.hulunbuir.controller")
                .put(GenCodeInfo.SERVICE, "com.hulunbuir.infra.service")
                .put(GenCodeInfo.SERVICE_IMPL, "com.hulunbuir.infra.service.impl")
                .put(GenCodeInfo.MAPPER, "com.hulunbuir.infra.mapper")
                .put(GenCodeInfo.MAPPER_XML, "mapper")
                .build();
    }


}
