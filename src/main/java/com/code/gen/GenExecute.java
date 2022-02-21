package com.code.gen;

import com.alibaba.fastjson.JSON;
import com.code.gen.config.DbMessageInfo;
import com.code.gen.config.GenConfig;
import com.code.gen.service.GenService;
import lombok.extern.slf4j.Slf4j;

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
        DbMessageInfo dbMessageInfo = new DbMessageInfo();
        // TODO 此处配置数据库连接信息
        dbMessageInfo.setUrl("");
        dbMessageInfo.setUsername("");
        dbMessageInfo.setPassword("");
        GenConfig genConfig = new GenConfig();
        genConfig.setDbMessageInfo(dbMessageInfo);

        genDefault(genConfig);

        log.info("Success....");
        log.warn("注意使用360压缩打开文件，对于winrar软件打不开者，请下载并安装   360 压缩软件  并打开。。");
    }

    private static void genDefault(GenConfig genConfig) {
        //需要生成代码的表名
        String tableName = "test_apply";
        log.info("生成代码的表名是....{}", tableName);
        genConfig.setTableName(tableName);
        //自定义表备注
//        genConfig.setComments("架构师");
        //表名前缀
        genConfig.setTablePrefix("test_");
        //包名-在模板中的import 使用此出配置的包名路径
        genConfig.setEntity("com.code.domain.entity");
        genConfig.setXml("mapper");
        genConfig.setMapper("com.code.infra.mapper");
        genConfig.setRepository("com.code.domain.repository");
        genConfig.setRepositoryImpl("com.code.domain.repository.impl");
        genConfig.setService("com.code.app.service");
        genConfig.setServiceImpl("com.code.app.service.impl");
        genConfig.setController("com.code.api.controller.v1");
        //生成的路径
        genConfig.setGenPath("D:\\gen_code");
        //使用特定的目录下的模板
        genConfig.setSpecialTemplate("test");
        genConfig.setAuthor("code@code.com");
        log.info("生成代码的基本信息是....{}", JSON.toJSON(genConfig));
        GenService genService = new GenService();
        genService.genCode(genConfig);
    }


}
