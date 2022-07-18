package com.code.gen.config;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.code.gen.util.CodeGenUtils;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * 生成配置
 */
@Data
public class GenConfig implements Serializable {

    /**
     * 作者
     */
    private String author;
    /**
     * 表前缀
     */
    private String tablePrefix;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 表备注
     */
    private String comments;
    /**
     * 生成的路径-默认路径
     */
    private String genPath;
    /**
     * 数据库连接信息
     */
    private DbMessageInfo dbMessageInfo;
    /**
     * Entity包名
     */
    private String entity;
    /**
     * Controller包名
     */
    private String controller;
    /**
     * Service包名
     */
    private String service;
    /**
     * Service Impl包名
     */
    private String serviceImpl;
    /**
     * Repository包名
     */
    private String repository;
    /**
     * Repository Impl包名
     */
    private String repositoryImpl;
    /**
     * Mapper包名
     */
    private String mapper;
    /**
     * Mapper XML包名
     */
    private String xml;
    /**
     * template 文件夹下其他的模板使用
     */
    private String specialTemplate;
    /**
     * template 文件夹下其他的模板使用
     */
    private String templatePath;

    public String getTemplatePath() {
        return CharSequenceUtil.isBlank(this.specialTemplate) ? CodeGenUtils.DEFAULT_TEMPLATE_PATH : CodeGenUtils.DEFAULT_TEMPLATE_PATH + this.specialTemplate + File.separator;
    }

    /**
     * 创建时间
     */
    private String datetime = DateUtil.now();

}
