package com.code.gen.mapper.entity;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.List;

/**
 * 表属性
 */
@Data
public class TableEntity {

    /**
     * 名称
     */
    private String tablename;

    public String getTablename() {
        if (StrUtil.isBlank(tablename)) {
            return tableName;
        }
        return tablename;
    }

    /**
     * 名称
     */
    private String tableName;

    public String getTableName() {
        if (StrUtil.isBlank(tableName)) {
            return tablename;
        }
        return tableName;
    }

    /**
     * 备注
     */
    private String comments;

    /**
     * 主键
     */
    private ColumnEntity pk;

    /**
     * 所有的列名
     */
    private List<ColumnEntity> columns;

    /**
     * 去除了一些审计字段的所有列名
     */
    private List<ColumnEntity> columnList;

    /**
     * 驼峰类型
     */
    private String className;

    /**
     * 普通类型
     */
    private String lowerClassname;

    /**
     * controller的请求路径
     */
    private String pathName;

}
