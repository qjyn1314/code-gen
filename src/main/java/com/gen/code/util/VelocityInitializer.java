package com.gen.code.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;

import java.util.Properties;

/**
 * VelocityEngine工厂
 */
@Slf4j
public class VelocityInitializer {
    /**
     * 初始化vm方法
     */
    public static void initVelocity() {
        Properties p = new Properties();
        try {
            // 加载classpath目录下的vm文件
            p.setProperty("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            // 定义字符集
            p.setProperty(RuntimeConstants.INPUT_ENCODING, RuntimeConstants.ENCODING_DEFAULT);
            // 初始化Velocity引擎，指定配置Properties
            Velocity.init(p);
        } catch (Exception e) {
            log.error("初始化Velocity失败", e);
        }
    }
}
