package com.cs.wujiuqi.data.crawler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;


public class JdbcTest {
    public static void main(String[] args) throws SQLException {
        dbUtiladd();
//        hashIsEqual();//测试hashcode是否可做版本戳
    }


    public static void dbUtiladd() throws SQLException {
        String sqlInsert = "insert into zhaopin_job values(?,?,?,?,?,?,?,?)";
        String sqlUpdate = "update zhaopin_job values(?,?,?,?,?,?,?,?)";
        String sqlConditions = "SELECT (SELECT count(*) from zhaopin_job WHERE id=?) as isNotExist, (SELECT count(*) from zhaopin_job WHERE id=? and modTime!=?) as isDoUpdate;";
        long startT = System.currentTimeMillis();
        int updates=0;
        for (int i = 0; i <100 ; i++) {
            QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
            Object[] params = new Object[]{i,i,"55"};
            boolean[] conditions = qr.query(sqlConditions, new ResultSetHandler<boolean[]>() {
                public boolean[] handle(ResultSet rs) throws SQLException {
                    if (rs.next()) {
                        boolean[] conditions = new boolean[2];
                        conditions[0]=rs.getInt("isNotExist")==0;
                        conditions[1]=rs.getInt("isDoUpdate")>0;
                        return conditions;
                    }
                    return null;
                }
            }, params);
            if(conditions[0]){//isNotExist
                int update = qr.update(sqlInsert, i, "_id_id_id", 11, "jack", "厦门", "json数据", 55, 55);
                updates+=update;
            }else if(conditions[1]){//isDoUpdate
                int update = qr.update(sqlUpdate, i, "_id_id_id", 11, "jack", "厦门", "json数据", 55, 55);
            }
        }
        System.out.println("共耗时：" + (System.currentTimeMillis() - startT));
        System.out.println("影响行数："+updates);

    }


    public static void hashIsEqual() throws SQLException {
        long startT = System.currentTimeMillis();
        int updates=0;
            QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
           String jname= qr.query("select jname from zhaopin_job where id=?", new ResultSetHandler<String>() {
                public String handle(ResultSet rs) throws SQLException {
                    if (rs.next()) {
                        return rs.getString("jname");
                    }
                    return null;
                }
            }, 1);
        System.out.println(new String("jack").hashCode());
        System.out.println(jname.hashCode());


        System.out.println("共耗时：" + (System.currentTimeMillis() - startT));

    }


}
