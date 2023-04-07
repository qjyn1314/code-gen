package com.gen.code.mapper.entity;

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
    private String tableName;

    /**
     * 备注
     */
    private String comments;

    /**
     * 驼峰类名(首字母大写)
     */
    private String className;

    /**
     * 驼峰类名(首字母小写)
     */
    private String lowerClassname;

    /**
     * controller的请求路径
     */
    private String pathName;

    /**
     * 所有的字段信息
     */
    private List<ColumnEntity> allColumns;

    /**
     * 主键-可能会有多个主键, 此时需要指定某一列为主键
     */
    private ColumnEntity pk;

    /**
     * 去除了一些审计字段的关键字段信息
     */
    private List<ColumnEntity> essentialColumns;

}
