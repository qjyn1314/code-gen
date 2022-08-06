package com.code.gen.mapper.entity;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 列属性
 */
@Data
public class ColumnEntity {

    /**
     * 列表
     */
    private String columnname;

    public String getColumnname() {
        if (StrUtil.isBlank(columnname)) {
            return columnName;
        }
        return columnname;
    }

    /**
     * 列表
     */
    private String columnName;

    public String getColumnName() {
        if (StrUtil.isBlank(columnName)) {
            return columnname;
        }
        return columnName;
    }

    /**
     * 数据类型
     */
    private String datatype;

    public String getDatatype() {
        if (StrUtil.isBlank(datatype)) {
            return dataType;
        }
        return datatype;
    }


    /**
     * 数据类型
     */
    private String dataType;

    public String getDataType() {
        if (StrUtil.isBlank(dataType)) {
            return datatype;
        }
        return dataType;
    }

    /**
     * 备注
     */
    private String comments;

    /**
     * 驼峰属性
     */
    private String caseAttrName;

    /**
     * 普通属性
     */
    private String lowerAttrName;

    /**
     * 大写的属性名
     */
    private String capitalAttrName;

    /**
     * 属性类型
     */
    private String attrType;

    /**
     * 其他信息
     */
    private String extra;

    /**
     * 字段类型
     */
    private String columnType;

}
