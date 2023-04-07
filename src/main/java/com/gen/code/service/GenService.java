package com.gen.code.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONUtil;
import com.gen.code.config.DbInfo;
import com.gen.code.config.GenCodeInfo;
import com.gen.code.config.SqlSessionService;
import com.gen.code.mapper.GenMapper;
import com.gen.code.util.CodeGenUtils;
import com.gen.code.util.VelocityInitializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        return SqlSessionService.me().handleSession(dbInfo, GenMapper.class).getMapper(GenMapper.class);
    }

    public void genCode(GenCodeInfo genCodeInfo) {
        GenMapper genMapper = getGenMapper(genCodeInfo.getDbInfo());
        String tableName = genCodeInfo.getTableName();
        Assert.notBlank(tableName, "请使用正确的表名。");

        //默认查询mysql数据库信息
        Map<String, Object> table = genMapper.queryTable(tableName);
        Assert.notNull(table, "未查找到表信息。");
        log.info("表信息是...{}", JSONUtil.toJsonStr(table));

        List<Map<String, Object>> columns = genMapper.queryMysqlColumns(tableName);

        genZipOutStream(genCodeInfo, table, columns);

    }

    private void genZipOutStream(GenCodeInfo genCodeInfo,
                                 Map<String, Object> table,
                                 List<Map<String, Object>> columns) {
        // 用于生成代码所用到的数据
        Map<String, Object> tempDataMap = CodeGenUtils.getTemplateData(genCodeInfo, table, columns);
        log.info("用于生成代码所用到的数据->{}", JSONUtil.toJsonStr(tempDataMap));

        Map<String, String> templatePathMap = genCodeInfo.getTemplatePathMap();
        Assert.notEmpty(templatePathMap, "获取不到模板文件。");
        // 设置velocity资源加载器
        VelocityInitializer.initVelocity();
        log.info("VelocityInitializer initVelocity end");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        // 将数据初始化到 VelocityContext 上下文中
        VelocityContext context = new VelocityContext(tempDataMap);

        for (Map.Entry<String, String> tempPath : templatePathMap.entrySet()) {
            String tempKey = tempPath.getKey();
            String filePath = CodeGenUtils.getZipFilePath(tempKey, genCodeInfo);
            String tempPathVal = tempPath.getValue();
            // 读取类路径下的模板
            Template emptyTemplate = Velocity.getTemplate(tempPathVal, CharsetUtil.UTF_8);
            // 将数据渲染模板到模板中
            StringWriter strWri = new StringWriter();
            emptyTemplate.merge(context, strWri);

            try {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(filePath));
                // 将数据写入到zip中
                IoUtil.write(zip, StandardCharsets.UTF_8, false, strWri);
                //然后关闭它
                IoUtil.close(strWri);
                zip.closeEntry();
            } catch (IOException e) {
                log.error("渲染模板失败：{0}", e);
            }
        }

        try {
            //关闭-释放资源
            IoUtil.close(zip);
            IoUtil.close(outputStream);
        } catch (Exception e) {
            log.error("渲染模板失败：{0}", e);
        }

        generatePath(genCodeInfo, outputStream);

        System.exit(1);

    }

    private void generatePath(GenCodeInfo genCodeInfo, ByteArrayOutputStream outputStream) {
        String genPath = genCodeInfo.getGenPath();
        Assert.notBlank(genPath, "请确定生成文件所输出的文件夹。");
        String tableName = genCodeInfo.getTableName();
        //创建文件夹
        File file = new File(genPath);
        if (!file.exists()) {
            FileUtil.mkdir(file);
        }
        //压缩文件生成的路径
        String fileGenPath = genPath + "//" + tableName + "_" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_FORMAT) + ".zip";
        log.info("生成的文件名...- file:///{}", fileGenPath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileGenPath);
            byte[] data = outputStream.toByteArray();
            IoUtil.write(fileOutputStream, Boolean.TRUE, data);
        } catch (Exception e) {
            log.error("生成文件失败，请检查文件路径是否正确。", e);
        } finally {
            IoUtil.close(fileOutputStream);
        }
    }


}
