package com.code.gen.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import com.code.gen.config.GenConfig;
import com.code.gen.mapper.entity.ColumnEntity;
import com.code.gen.mapper.entity.TableEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 代码生成器 工具类
 */
@Slf4j
public class CodeGenUtils {

    public static final String DEFAULT_TEMPLATE_PATH = "template//";

    private static final String ENTITY_JAVA_VM = "Entity.java.vm";

    private static final String MAPPER_XML_VM = "Mapper.xml.vm";

    private static final String MAPPER_XML_SUFFIX = ".xml";

    private static final String JAVA_SUFFIX = ".java";

    private static final String[] REMOVE_COLUMN_LIST = new String[]{"createdTime", "createdByUser", "createdByEmp", "updatedTime", "updatedByUser", "updatedByEmp"};

    /**
     * 列名转换成Java属性名
     */
    private static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * 表名转换成Java类名
     */
    private static String tableToJava(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * 表名转换成控制层的请求路径
     */
    private static String tableToRequestPath(String tableName, String tablePrefix) {
        if (StringUtils.isNotBlank(tablePrefix)) {
            tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return CharSequenceUtil.toUnderlineCase(columnToJava(tableName)).replace("_", "-");
    }


    /**
     * 获取配置信息
     */
    private static Setting getConfig() {
        return new Setting("generator.properties");
    }

    /**
     * 模板所需要的信息
     */
    public static Map<String, Object> getTemplateData(GenConfig genConfig,
                                                      Map<String, Object> table,
                                                      List<Map<String, Object>> columns) {
        TableEntity tableEntity = BeanUtil.fillBeanWithMap(table, new TableEntity(), false);
        tableEntity.setComments(genConfig.getComments());
        String className = tableToJava(tableEntity.getTableName(), genConfig.getTablePrefix());
        log.debug("生成的Java类名...{}", className);
        tableEntity.setClassName(className);
        tableEntity.setLowerClassname(StringUtils.uncapitalize(className));
        tableEntity.setPathName(tableToRequestPath(tableEntity.getTableName(), genConfig.getTablePrefix()));
        Setting configSetting = getConfig();
        // 列信息
        List<ColumnEntity> columnList = new ArrayList<>();
        int priCount = 1;
        for (Map<String, Object> column : columns) {
            ColumnEntity columnEntity = BeanUtil.fillBeanWithMap(column, new ColumnEntity(), false);
            // 列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setCaseAttrName(attrName);
            columnEntity.setLowerAttrName(StringUtils.uncapitalize(attrName));
            columnEntity.setCapitalAttrName(columnEntity.getColumnName().toUpperCase());
            // 列的数据类型，转换成Java类型
            columnEntity.setAttrType(configSetting.getStr(columnEntity.getDataType(), "unknowType"));
            // 是否主键
            if (priCount == 1 && "PRI".equalsIgnoreCase((String) column.get("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
                ++priCount;
            }
            columnList.add(columnEntity);
        }
        //完整表列的信息
        tableEntity.setColumns(columnList);
        List<ColumnEntity> entityList = columnList.stream()
                .filter(column -> !Arrays.asList(REMOVE_COLUMN_LIST).contains(column.getLowerAttrName()))
                .collect(Collectors.toList());
        //去除了相关字段的列表信息
        tableEntity.setColumnList(entityList);
        // 没主键，则第一个字段为主键
        if (Objects.isNull(tableEntity.getPk())) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }
        // 封装模板中的数据
        Map<String, Object> map = BeanUtil.beanToMap(genConfig);
        //类名全小写
        map.put("classname", tableEntity.getLowerClassname().toLowerCase());
        map.putAll(BeanUtil.beanToMap(tableEntity));
        return map;
    }

    /**
     * classpath路径下的模板文件
     *
     * @param genConfig       配置信息
     * @param templateDataMap 模板中所需要的数据
     * @return List<String>
     */
    public static List<String> fillingTemplate(GenConfig genConfig, Map<String, Object> templateDataMap) {
        String templatePath = genConfig.getTemplatePath();
        List<String> templateFilePaths = FileUtil.listFileNames(templatePath);
        if (CollUtil.isEmpty(templateFilePaths)) {
            return CollUtil.newArrayList();
        }
        log.info("templateFiles-{}", templateFilePaths);
        // 设置velocity资源加载器
        VelocityInitializer.initVelocity();
        log.info("VelocityInitializer initVelocity end");
        return templateFilePaths;
    }

    /**
     * 获取到压缩包中文件的相对路径
     *
     * @param template        模板名称
     * @param templateDataMap 配置信息
     * @return 压缩文件中的文件路径
     */
    public static String getZipFilePath(String template, Map<String, Object> templateDataMap) {
        String className = StrUtil.toString(templateDataMap.get("className"));
        String templateName = CharSequenceUtil.subBefore(template, StrPool.C_DOT, false);
        if (ENTITY_JAVA_VM.equals(template)) {
            templateName = CharSequenceUtil.subBefore(ENTITY_JAVA_VM, StrPool.C_DOT, false);
        }
        if (MAPPER_XML_VM.equals(template)) {
            templateName = CharSequenceUtil.subAfter(MAPPER_XML_SUFFIX, StrPool.C_DOT, false);
        }
        String lowerCaseTemplateName = templateName.toLowerCase();
        String importPath = null;
        Set<String> dataMapKey = templateDataMap.keySet();
        for (String mateDataKey : dataMapKey) {
            if (lowerCaseTemplateName.equals(mateDataKey.toLowerCase())) {
                importPath = StrUtil.toString(templateDataMap.getOrDefault(mateDataKey, null));
                break;
            }
        }
        if (MAPPER_XML_VM.equals(template)) {
            return handleXmlPath(CharSequenceUtil.subBefore(MAPPER_XML_VM, StrPool.C_DOT, false),
                    className, importPath);
        }
        return handleJavaPath(templateName, className, importPath);
    }

    private static String handleXmlPath(String templateName, String className, String importPath) {
        String xmlPath = "src//main//resources//" + importPath;
        return xmlPath.replace(".", "//") + "//" + className + templateName + MAPPER_XML_SUFFIX;
    }

    private static String handleJavaPath(String templateName, String className, String importPath) {
        String packagePath = "src//main//java//" + importPath;
        if (templateName.equals(CharSequenceUtil.subBefore(ENTITY_JAVA_VM, StrPool.C_DOT, false))) {
            return packagePath.replace(".", "//") + "//" + className + JAVA_SUFFIX;
        }
        return packagePath.replace(".", "//") + "//" + className + templateName + JAVA_SUFFIX;
    }


}
