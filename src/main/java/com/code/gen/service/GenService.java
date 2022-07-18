package com.code.gen.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.code.gen.config.DbMessageInfo;
import com.code.gen.config.GenConfig;
import com.code.gen.config.SqlSessionService;
import com.code.gen.mapper.GenMapper;
import com.code.gen.util.CodeGenUtils;
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

    private GenMapper getGenMapper(GenConfig genConfig) {
        DbMessageInfo dbMessageInfo = genConfig.getDbMessageInfo();
        return SqlSessionService.me().handleSession(dbMessageInfo, GenMapper.class).getMapper(GenMapper.class);
    }

    public void genCode(GenConfig genConfig) {
        GenMapper genMapper = getGenMapper(genConfig);

        String tableName = genConfig.getTableName();
        Assert.notBlank(tableName, "请使用正确的表名。");

        Map<String, Object> table = genMapper.queryTable(tableName);
        Assert.notNull(table, "未查找到表信息。");
        log.info("表信息是...{}", JSONUtil.toJsonStr(table));

        List<Map<String, Object>> columns = genMapper.queryColumns(tableName);
        log.info("表的列信息是...{}", JSONUtil.toJsonStr(columns));

        genZipOutStream(genConfig, table, columns);

    }

    private void genZipOutStream(GenConfig genConfig,
                                 Map<String, Object> table,
                                 List<Map<String, Object>> columns) {

        Map<String, Object> templateDataMap = CodeGenUtils.getTemplateData(genConfig, table, columns);

        List<String> filledOutTemplateList = CodeGenUtils.fillingTemplate(genConfig, templateDataMap);
        Assert.notEmpty(filledOutTemplateList, "获取不到模板文件。");

        String templatePath = genConfig.getTemplatePath();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        VelocityContext context = new VelocityContext(templateDataMap);

        for (String template : filledOutTemplateList) {
            String filePath = CodeGenUtils.getZipFilePath(template, templateDataMap);
            log.info("压缩包中的文件路径是-{}", filePath);
            template = templatePath + template;
            //从类路径下读取所需要的模板
            Template emptyTemplate = Velocity.getTemplate(template, CharsetUtil.UTF_8);
            // 渲染模板
            StringWriter strWri = new StringWriter();
            emptyTemplate.merge(context, strWri);

            try {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(filePath));
                //将数据写入到zip中
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

        generatePath(genConfig, outputStream);

    }

    private void generatePath(GenConfig genConfig, ByteArrayOutputStream outputStream) {
        String genPath = genConfig.getGenPath();
        Assert.notBlank(genPath, "请确定生成文件所输出的文件夹。");
        String tableName = genConfig.getTableName();
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
