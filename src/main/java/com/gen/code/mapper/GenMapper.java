package com.gen.code.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * explain:
 * </p>
 *
 * @author wangjunming
 * @since 2021/7/17 15:43
 */
public interface GenMapper {

    /**
     * 查询MySql表信息
     *
     * @param tableName 表名称
     */
    @Select("select table_name AS tableName, engine, table_comment as comments, NOW() datetime from information_schema.tables " +
            " where table_schema = (select database()) and table_name = #{tableName}" )
    Map<String, Object> queryMysqlTable(@Param("tableName") String tableName);

    /**
     * 查询PgSql表信息
     *
     * @param tableName 表名称
     */
    @Select("select tablename as tablename from pg_tables where schemaname='test_schema' and tablename = #{tableName}")
    Map<String, Object> queryPgSqlTable(@Param("tableName") String tableName);

    /**
     * 查询表列信息
     *
     * @param tableName 表名称
     */
    @Select("select column_name columnName, data_type dataType, column_comment comments, column_key columnKey, extra ," +
            "is_nullable as isNullable,column_type as columnType from information_schema.columns " +
            " where table_name = #{tableName} and table_schema = (select database()) order by ordinal_position" )
    List<Map<String, Object>> queryMysqlColumns(@Param("tableName") String tableName);

    /**
     * 查询表列信息
     *
     * @param tableName 表名称
     */
    @Select("SELECT pa.attname AS columnname, pt.typname AS datatype,pd.description AS comments," +
            "(CASE" +
            " WHEN ( SELECT COUNT ( * ) FROM pg_constraint WHERE conrelid = pa.attrelid AND conkey [ 1 ]= attnum AND contype = 'p' ) > 0 " +
            "THEN 'PRI' " +
            "ELSE 'NOPRI' " +
            "END " +
            ") AS columnkey "+
            "FROM pg_class pc, pg_attribute pa, pg_type pt, pg_description pd " +
            "WHERE pc.oid = pa.attrelid  AND pt.oid = pa.atttypid AND pd.objoid = pa.attrelid  AND pd.objsubid = pa.attnum " +
            "AND pc.relname = #{tableName} " +
            "ORDER BY pc.relname DESC, pa.attnum ASC")
    List<Map<String, Object>> queryPgSqlColumns(@Param("tableName") String tableName);



}
