package com.gen.code.mapper.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 列属性
 */
@Setter
@Getter
public class ColumnEntity {

    /**
     * 字段名
     */
    private String columnName;

    /**
     * 数据库中的数据类型
     */
    private String dataType;

    /**
     * 字段备注
     */
    private String comments;

    /**
     * 驼峰命名的字段名(首字母小写)
     */
    private String lowerAttrName;

    /**
     * 大写的字段名,(id->ID,user_id->USER_ID)
     */
    private String capitalAttrName;

    /**
     * java中的数据类型
     */
    private String attrType;

    /**
     * 主键类型
     */
    private String columnKey;

    /**
     * 是否是主键字段
     */
    private Boolean priFlag;

}
