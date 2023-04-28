package com.gen.code.config;

import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 生成配置
 */
@Setter
@Getter
@Accessors(chain = true)
public class GenCodeInfo implements Serializable {

    public static final String ENTITY = "entity";
    public static final String CONTROLLER = "controller";
    public static final String SERVICE = "service";
    public static final String SERVICE_IMPL = "serviceImpl";
    public static final String MAPPER = "mapper";
    public static final String MAPPER_XML = "mapperXml";


    public static final String ENTITY_JAVA_VM = "Entity.java.vm";
    public static final String CONTROLLER_JAVA_VM = "Controller.java.vm";
    public static final String SERVICE_JAVA_VM = "Service.java.vm";
    public static final String SERVICE_IMPL_JAVA_VM = "ServiceImpl.java.vm";
    public static final String MAPPER_JAVA_VM = "Mapper.java.vm";
    public static final String MAPPER_XML_VM = "Mapper.xml.vm";


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
     * controller的请求路径
     */
    private String pathName;
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
    public Map<String, String> packageMap = new LinkedHashMap<>();
    /**
     * 需要生成的文件名后缀集合, {"entity","Controller.java"}
     */
    public Map<String, String> fileMap = new LinkedHashMap<>();
    /**
     * 所属的模板集合, {"entity","template"},{"entity","template//test"},
     */
    public Map<String, String> templatePathMap = new LinkedHashMap<>();

}
