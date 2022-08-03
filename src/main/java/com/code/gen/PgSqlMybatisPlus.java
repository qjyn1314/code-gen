package com.code.gen;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;

public class PgSqlMybatisPlus {
    /**
     * datasource:
     * driver-class-name: org.postgresql.Driver
     * url: jdbc:postgresql://39.105.109.251:5432/env-jiujiang-fxyj
     * username: postgres
     * password: S0uchu@ng.DB1
     */
    public static void main(String[] args) {
        //scanner("表名，多个英文逗号分割").split(",")
        String tableNames = "company_risk_info";
        String genPath = "D:\\workspace\\gen_code";
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(genPath + "/src/main/java");//设置代码生成路径
        gc.setFileOverride(true);//是否覆盖以前文件
        gc.setOpen(false);//是否打开生成目录
        gc.setAuthor("wangjunming");//设置项目作者名称
        gc.setIdType(IdType.AUTO);//设置主键策略
        gc.setBaseResultMap(true);//生成基本ResultMap
        gc.setBaseColumnList(true);//生成基本ColumnList
        gc.setServiceName("%sService");//去掉服务默认前缀
        gc.setDateType(DateType.ONLY_DATE);//设置时间类型
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDriverName("org.postgresql.Driver");
        dsc.setUrl("jdbc:postgresql://39.105.109.251:5432/env-jiujiang-fxyj");
        dsc.setUsername("postgres");
        dsc.setPassword("S0uchu@ng.DB1");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.yixin");
        pc.setMapper("mapper");
        pc.setXml("mapper.xml");
        pc.setEntity("pojo");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        pc.setController("controller");
        mpg.setPackageInfo(pc);

        // 策略配置
        StrategyConfig sc = new StrategyConfig();
        sc.setNaming(NamingStrategy.underline_to_camel);
        sc.setColumnNaming(NamingStrategy.underline_to_camel);
        sc.setEntityLombokModel(true);//自动lombok
        sc.setRestControllerStyle(true);
        sc.setControllerMappingHyphenStyle(true);

//        sc.setLogicDeleteFieldName("deleted");//设置逻辑删除

        //设置自动填充配置
        TableFill gmt_create = new TableFill("create_time", FieldFill.INSERT);
        TableFill gmt_modified = new TableFill("update_time", FieldFill.INSERT_UPDATE);
        ArrayList<TableFill> tableFills = new ArrayList<>();
        tableFills.add(gmt_create);
        tableFills.add(gmt_modified);
        sc.setTableFillList(tableFills);

        //乐观锁
        sc.setVersionFieldName("version");
        sc.setRestControllerStyle(true);//驼峰命名

        //  sc.setTablePrefix("tbl_"); 设置表名前缀
        sc.setInclude(tableNames);
        mpg.setStrategy(sc);

        // 生成代码
        mpg.execute();

    }
}
