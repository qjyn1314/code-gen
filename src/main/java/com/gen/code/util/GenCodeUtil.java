package com.gen.code.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.NamingCase;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import com.gen.code.config.GenCodeInfo;
import com.gen.code.mapper.entity.ColumnEntity;
import com.gen.code.mapper.entity.TableEntity;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 代码生成器 工具类
 */
@Slf4j
public class GenCodeUtil {

    public static final String xml_File = ".xml";
    public static final String java_File = ".java";
    public static final String xml_File_Path = "src\\main\\resources\\";
    public static final String java_File_Path = "src\\main\\java\\";


    /**
     * 获取数据库字段对应的java类型配置信息
     */
    private static Map<String, String> getConfig() {
        Setting setting = new Setting("generator.properties");
        setting.autoLoad(true);
        return new HashMap<>(setting);
    }

    /**
     * 模板所需要的信息
     */
    public static Map<String, Object> getTemplateData(GenCodeInfo genCodeInfo, Map<String, Object> table, List<Map<String, Object>> columns) {
        TableEntity tableEntity = BeanUtil.fillBeanWithMap(table, new TableEntity(), false);
        // 设置类名
        tableEntity.setClassName(handleClassName(genCodeInfo));
        // 设置首字母小写的类名
        tableEntity.setLowerClassname(handleLowerClassName(tableEntity, genCodeInfo));
        // 设置备注
        tableEntity.setComments(handleComments(tableEntity, genCodeInfo));
        // 设置控制层的请求路径
        tableEntity.setPathName(handlePathName(tableEntity, genCodeInfo));
        // 设置全部的字段信息
        tableEntity.setAllColumns(handleAllColumns(genCodeInfo, columns));
        // 设置主键字段信息
        tableEntity.setPk(handlePkColumn(tableEntity));
        // 设置关键字段信息
        tableEntity.setEssentialColumns(handleEssentialColumn(tableEntity));
        // 将配置信息转换为map
        Map<String, Object> allDataMap = BeanUtil.beanToMap(genCodeInfo);
        // 设置已处理后的信息
        allDataMap.putAll(BeanUtil.beanToMap(tableEntity));
        allDataMap.putAll(genCodeInfo.getPackageMap());
        return allDataMap;
    }

    private static final String[] REMOVE_COLUMN = new String[]{"createdTime", "createdByUser", "createdByEmp", "updatedTime", "updatedByUser", "updatedByEmp"};
    private static final List<String> REMOVE_COLUMN_LIST = Arrays.stream(REMOVE_COLUMN).toList();

    /**
     * 处理关键字段信息
     */
    private static List<ColumnEntity> handleEssentialColumn(TableEntity tableEntity) {
        return tableEntity.getAllColumns().stream().filter(item -> !REMOVE_COLUMN_LIST.contains(item.getLowerAttrName())).collect(Collectors.toList());
    }

    /**
     * 处理主键字段
     */
    private static ColumnEntity handlePkColumn(TableEntity tableEntity) {
        return tableEntity.getAllColumns().stream().filter(ColumnEntity::getPriFlag).findFirst().orElse(null);
    }

    /**
     * 处理所有的字段信息
     */
    private static List<ColumnEntity> handleAllColumns(GenCodeInfo genCodeInfo, List<Map<String, Object>> columns) {
        Map<String, String> attrTypeMap = getConfig();
        return columns.stream().map(item -> {
            ColumnEntity column = BeanUtil.fillBeanWithMap(item, new ColumnEntity(), false);
            column.setAttrType(handleColumnType(attrTypeMap, column));
            column.setLowerAttrName(handleLowerAttrName(column));
            column.setCapitalAttrName(handleCapitalAttrName(column));
            column.setPriFlag(handlePriColumn(column));
            return column;
        }).collect(Collectors.toList());
    }

    /**
     * 处理字段是否是主键字段
     */
    private static Boolean handlePriColumn(ColumnEntity column) {
        String columnKey = column.getColumnKey();
        if (StrUtil.isBlank(columnKey)) {
            return false;
        }
        return "PRI".equalsIgnoreCase(columnKey);
    }

    /**
     * 处理java类中字段的名称, 转换为大写
     */
    private static String handleCapitalAttrName(ColumnEntity column) {
        String columnName = column.getColumnName();
        return "FIELD_" + columnName.toUpperCase();
    }

    /**
     * 处理java类中字段的名称
     */
    private static String handleLowerAttrName(ColumnEntity column) {
        String columnName = column.getColumnName();
        return StrUtil.toCamelCase(columnName);
    }

    /**
     * 处理java类中字段的类型
     */
    private static String handleColumnType(Map<String, String> attrTypeMap, ColumnEntity column) {
        String dataType = column.getDataType();
        for (Map.Entry<String, String> attrType : attrTypeMap.entrySet()) {
            if (dataType.contains(attrType.getKey())) {
                return attrType.getValue();
            }
        }
        return null;
    }

    /**
     * 处理控制层的请求路径
     */
    private static String handlePathName(TableEntity tableEntity, GenCodeInfo genCodeInfo) {
        if (StrUtil.isNotBlank(genCodeInfo.getPathName())) {
            return genCodeInfo.getPathName();
        }
        String pathName = tableEntity.getPathName();
        if (StrUtil.isBlank(pathName)) {
            return StrUtil.replace(tableEntity.getTableName(), StrUtil.UNDERLINE, StrUtil.SLASH);
        }
        return pathName;
    }

    /**
     * 处理表的备注
     */
    private static String handleComments(TableEntity tableEntity, GenCodeInfo genCodeInfo) {
        if (StrUtil.isBlank(tableEntity.getComments())) {
            return genCodeInfo.getComments();
        }
        return tableEntity.getComments();
    }

    /**
     * 处理类名首字母小写
     */
    private static String handleLowerClassName(TableEntity tableEntity, GenCodeInfo genCodeInfo) {
        if (StrUtil.isNotBlank(genCodeInfo.getLowerClassName())) {
            return genCodeInfo.getLowerClassName();
        }
        String lowerClassName = StrUtil.toCamelCase(tableEntity.getTableName());
        genCodeInfo.setLowerClassName(lowerClassName);
        return lowerClassName;
    }

    /**
     * 将表名转换为类名
     */
    private static String handleClassName(GenCodeInfo genCodeInfo) {
        if (StrUtil.isNotBlank(genCodeInfo.getClassName())) {
            return genCodeInfo.getClassName();
        }
        String tableName = genCodeInfo.getTableName();
        String tablePrefix = genCodeInfo.getTablePrefix();
        String className = "";
        if (StrUtil.isBlank(tablePrefix)) {
            className = NamingCase.toPascalCase(tableName);
            genCodeInfo.setClassName(className);
            return className;
        }
        className = NamingCase.toPascalCase(StrUtil.removeAll(tableName, tablePrefix));
        genCodeInfo.setClassName(className);
        return className;
    }

    /**
     * 获取到压缩包中文件的相对路径
     *
     * @param tempKey     模板名称对应的key
     * @param genCodeInfo 模板配置信息
     * @return 压缩文件中的文件路径
     */
    public static String getZipFilePath(String tempKey, GenCodeInfo genCodeInfo) {
        Map<String, String> packageMap = genCodeInfo.getPackageMap();
        Assert.notEmpty(packageMap, "包名集合不能为空");
        Map<String, String> fileMap = genCodeInfo.getFileMap();
        Assert.notEmpty(packageMap, "指定的文件名集合不能为空");
        // 生成的类名
        String className = genCodeInfo.getClassName();
        // 获取包名
        String packageName = packageMap.get(tempKey);
        // 获取文件名
        String fileName = fileMap.get(tempKey);
        // 包路径
        String packagePath = StrUtil.replace(packageName, ".", "\\");

        if (fileName.contains(xml_File)) {
            return xml_File_Path + packagePath + File.separator + className + fileName;
        }
        return java_File_Path + packagePath + File.separator + className + fileName;
    }


}
