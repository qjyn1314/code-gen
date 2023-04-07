package com.gen.code.config;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * explain:
 * </p>
 *
 * @author wangjunming
 * @since 2021/2/9 20:59
 */
@Accessors(chain = true)
public class DbInfo extends HikariDataSource implements Serializable {

    public static final String MYSQL_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    public static final String POSTGRESQL_DRIVER_CLASS_NAME = "org.postgresql.Driver";

    public static final String MYSQL_URL_FORMAT = "jdbc:mysql://{ip}:{port}/{database}?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    public static final String POSTGRESQL_URL_FORMAT = "jdbc:postgresql://{ip}:{port}/{database}?currentSchema={schema}&stringtype=unspecified";

    public static final String IP = "ip";
    public static final String PORT = "port";
    public static final String DATABASE = "database";
    public static final String SCHEMA = "schema";

    public static String getMysqlUrl(String ip, String port, String database) {
        return StrUtil.format(MYSQL_URL_FORMAT, MapUtil.builder(IP, ip).put(PORT, port).put(DATABASE, database).build());
    }

    public static String getPgsqlUrl(String ip, String port, String database, String schema) {
        return StrUtil.format(POSTGRESQL_URL_FORMAT, MapUtil.builder(IP, ip).put(PORT, port).put(DATABASE, database).put(SCHEMA, schema).build());
    }

}
