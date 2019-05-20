package com.bgyfw.erp.resources.honor.util;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;

public class UpdateListProvider extends MapperTemplate {
    public UpdateListProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 批量更新
     *
     * @param ms
     */
    public String updateList(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //开始拼sql
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(" SET ");
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        EntityColumn id=null;
        for (EntityColumn entityColumn : columnList) {
            if (entityColumn.isId()) {
                id = entityColumn;
                break;
            }
        }

        int i = 1;
        for (EntityColumn column : columnList) {
            if (!column.isId() && column.isUpdatable()) {


                sql.append(column.getColumn() + " = CASE "+id.getColumn());
                sql.append("<foreach collection=\"list\" item=\"record\" >");
                sql.append(" WHEN " + id.getColumnHolder("record") + " THEN " + column.getColumnHolder("record"));
                sql.append("</foreach>");
                if (i == columnList.size()) {
                    sql.append("END");
                }else{
                    sql.append("END,");
                }
            }
            i++;
        }

        sql.append(" WHERE "+id.getColumn()+" IN (");

        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\">");
        sql.append(id.getColumnHolder("record"));
        sql.append("</foreach>");
        sql.append(")");



        return sql.toString();
    }
}
