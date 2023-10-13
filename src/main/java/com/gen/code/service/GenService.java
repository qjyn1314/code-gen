package com.gen.code.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.gen.code.config.DbInfo;
import com.gen.code.config.GenCodeInfo;
import com.gen.code.mapper.GenMapper;
import com.gen.code.util.GenCodeUtil;
import com.gen.code.util.VelocityInitializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * explain: 生成代码服务
 * </p>
 *
 * @author wangjunming
 * @since 2021/7/17 15:43
 */
@Slf4j
public class GenService {

    private GenMapper getGenMapper(DbInfo dbInfo) {
        return StaticSqlSession.me().getStaticMapper(GenMapper.class, dbInfo);
    }

    /**
     * explain : 查询表信息
     *
     * @param genCodeInfo 生成代码的配置信息
     * @author wangjunming > qjyn1314@163.com
     * @since 2023/10/13 20:30
     */
    public void genCode(GenCodeInfo genCodeInfo) {
        GenMapper genMapper = getGenMapper(genCodeInfo.getDbInfo());
        String tableName = genCodeInfo.getTableName();
        Assert.notBlank(tableName, "请使用正确的表名。");

        //默认查询mysql数据库信息
        Map<String, Object> table = null;
        try {
            table = genMapper.queryMysqlTable(tableName);
        } catch (Exception e) {
            log.error("查询表结构信息异常, 请检查数据库链接信息是否正确.->{}", e.getMessage());
            return;
        }
        Assert.notNull(table, "未查找到表信息。");
        log.info("表信息是...{}", JSONUtil.toJsonStr(table));

        List<Map<String, Object>> columns = genMapper.queryMysqlColumns(tableName);

        genCodeData(genCodeInfo, table, columns);

    }

    /**
     * explain : 处理生成代码需要的数据,并交给 Velocity 模板引擎
     *
     * @param genCodeInfo 生成代码的配置信息
     * @param table       表数据
     * @param columns     表中的字段信息
     * @author wangjunming > qjyn1314@163.com
     * @since 2023/10/13 20:30
     */
    private void genCodeData(GenCodeInfo genCodeInfo, Map<String, Object> table, List<Map<String, Object>> columns) {
        // 用于生成代码所用到的数据
        Map<String, Object> tempDataMap = GenCodeUtil.getTemplateData(genCodeInfo, table, columns);
        log.info("用于生成代码所用到的数据->{}", JSONUtil.toJsonStr(tempDataMap));

        // 设置velocity资源加载器
        VelocityInitializer.initVelocity();
        log.info("VelocityInitializer initVelocity end");
        // 将数据初始化到 VelocityContext 上下文中
        VelocityContext context = new VelocityContext(tempDataMap);

        genZipFile(genCodeInfo, context);
    }

    /**
     * explain : 将数据写入相应的模板中, 并生成压缩包
     *
     * @param genCodeInfo 生成代码的配置信息
     * @param context     Velocity工具模板信息
     * @author wangjunming > qjyn1314@163.com
     * @since 2023/10/13 20:31
     */
    private void genZipFile(GenCodeInfo genCodeInfo, VelocityContext context) {
        Map<String, String> tempPathMap = genCodeInfo.getTemplatePathMap();
        Assert.notEmpty(tempPathMap, "获取不到模板文件。");
        Iterator<Map.Entry<String, String>> tempEntryIterator = tempPathMap.entrySet().iterator();

        List<String> filePathList = CollUtil.newArrayList();
        List<InputStream> tempStreamList = CollUtil.newArrayList();
        while (tempEntryIterator.hasNext()) {
            Map.Entry<String, String> tempEntry = tempEntryIterator.next();
            String tempName = tempEntry.getKey();
            String tempPath = tempEntry.getValue();
            // 读取类路径下的模板
            Template emptyTemplate = Velocity.getTemplate(tempPath, CharsetUtil.UTF_8);
            // 将数据渲染模板到模板中
            StringWriter tempStrWrite = new StringWriter();
            emptyTemplate.merge(context, tempStrWrite);
            // 模板写入数据后的字节数据
            byte[] tempStrBytes = tempStrWrite.getBuffer().toString().getBytes(StandardCharsets.UTF_8);
            String filePath = GenCodeUtil.getZipFilePath(tempName, genCodeInfo);
            filePathList.add(filePath);
            tempStreamList.add(new ByteArrayInputStream(tempStrBytes));
        }

        String genPath = genCodeInfo.getGenPath();
        Assert.notBlank(genPath, "请确定生成文件所输出的文件夹。");
        String tableName = genCodeInfo.getTableName();
        //压缩文件生成的路径
        String fileGenPath = genPath + tableName + StrUtil.UNDERLINE + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_FORMAT) + ".zip";
        try {
            // 生成压缩文件
            ZipUtil.zip(FileUtil.file(fileGenPath), filePathList.toArray(new String[tempPathMap.size()]), tempStreamList.toArray(new InputStream[0]));
        } catch (Exception e) {
            log.error("生成压缩包失败...", e);
            return;
        }
        log.info("生成的文件名...- file:///{}", fileGenPath);
    }

}
