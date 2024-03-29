package com.gen.code.mapper.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * 表属性
 *
 * @author wangjunming
 */
@Getter
@Setter
public class TableEntity {

    /**
     * 名称  user_info
     */
    private String tableName;

    /**
     * 备注 用户信息表
     */
    private String comments;

    /**
     * 驼峰类名(首字母大写) UserInfo
     */
    private String className;

    /**
     * 驼峰类名(首字母小写) userInfo
     */
    private String lowerClassname;

    /**
     * controller的请求路径  /lemd/user
     */
    private String pathName;

    /**
     * 所有的字段信息
     */
    public List<ColumnEntity> allColumns = new LinkedList<>();

    /**
     * 主键-可能会有多个主键, 此时需要指定某一列为主键
     */
    public ColumnEntity pk = new ColumnEntity();

    /**
     * 去除了一些审计字段的关键字段信息
     */
    public List<ColumnEntity> essentialColumns = new LinkedList<>();

}
