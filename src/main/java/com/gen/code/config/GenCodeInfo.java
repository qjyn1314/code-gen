package com.gen.code.config;

import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 生成配置
 */
@Setter
@Getter
@Accessors(chain = true)
public class GenCodeInfo implements Serializable {

    public static final String entity = "entity";
    public static final String controller = "controller";
    public static final String service = "service";
    public static final String serviceImpl = "serviceImpl";
    public static final String mapper = "mapper";
    public static final String mapperXml = "mapperXml";


    public static final String entityPath = "Entity.java.vm";
    public static final String controllerPath = "Controller.java.vm";
    public static final String servicePath = "Service.java.vm";
    public static final String serviceImplPath = "ServiceImpl.java.vm";
    public static final String mapperPath = "Mapper.java.vm";
    public static final String mapperXmlPath = "Mapper.xml.vm";


    /**
     * 数据库连接信息
     */
    private DbInfo dbInfo;
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
     * java类名
     */
    private String className;
    /**
     * java类名(首字母小写)
     */
    private String lowerClassName;
    /**
     * 表备注
     */
    private String comments;
    /**
     * 创建时间
     */
    private String datetime = DateUtil.now();
    /**
     * 生成的路径-默认路径
     */
    private String genPath;
    /**
     * 默认使用的模板路径
     */
    private String defaultTempPath;
    /**
     * 指定的模板路径
     */
    private String specificPath;
    /**
     * 需要生成的包名集合, {"entity","com.gen.code.controller"}
     */
    private Map<String, String> packageMap;
    /**
     * 需要生成的文件名后缀集合, {"entity","Controller.java"}
     */
    private Map<String, String> fileMap;
    /**
     * 所属的模板集合, {"entity","template"},{"entity","template//test"},
     */
    private Map<String, String> templatePathMap;

}
