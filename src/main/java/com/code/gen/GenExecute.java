package com.code.gen;

import cn.hutool.json.JSONUtil;
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
        // 此处配置数据库连接信息
        dbMessageInfo.setUrl("jdbc:mysql://127.0.0.1:3308/oauth_original?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC");
        dbMessageInfo.setUsername("root");
        dbMessageInfo.setPassword("123456");
        GenConfig genConfig = new GenConfig();
        genConfig.setDbMessageInfo(dbMessageInfo);
        // 配置生成的信息
        genDefault(genConfig);
        log.info("生成代码的基本信息是....{}", JSONUtil.toJsonStr(genConfig));
        GenService genService = new GenService();
        genService.genCode(genConfig);
        log.info("Success....");
    }

    private static void genDefault(GenConfig genConfig) {
        //需要生成代码的表名
        String tableName = "oauth2_authorization";
        log.info("生成代码的表名是....{}", tableName);
        genConfig.setTableName(tableName);
        //自定义表备注
//        genConfig.setComments("架构师");
        //表名前缀
        genConfig.setTablePrefix("test");
        //包名-在模板中的import 使用此出配置的包名路径
        genConfig.setEntity("com.after.entity");
        genConfig.setXml("mapper");
        genConfig.setMapper("com.after.mapper");
        genConfig.setRepository("com.after.repository");
        genConfig.setRepositoryImpl("com.after.repository.impl");
        genConfig.setService("com.after.service");
        genConfig.setServiceImpl("com.after.service.impl");
        genConfig.setController("com.after.controller");
        //生成的路径
        genConfig.setGenPath("D://gen_code");
        //使用特定的目录下的模板
        genConfig.setSpecialTemplate("test");
        genConfig.setAuthor("code@code.com");
    }


}
