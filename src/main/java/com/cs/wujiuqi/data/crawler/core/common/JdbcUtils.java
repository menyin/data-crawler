package com.cs.wujiuqi.data.crawler.core.common;


import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.dbutils.ResultSetHandler;

import javax.sql.DataSource;
import java.sql.*;


public class JdbcUtils {

    private static String driver = "com.mysql.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3306/data_crawler?useUnicode=true&characterEncoding=utf-8";
    private static String username = "root";
    private static String password = "123456";
//    private static ComboPooledDataSource dataSource;
    private static DruidDataSource dataSource;

    static {
       /* dataSource = new ComboPooledDataSource();
//        dataSource.setPassword("123456");
        try {
            dataSource.setDriverClass(driver);
            dataSource.setUser(username);
            dataSource.setJdbcUrl(url);
            dataSource.setInitialPoolSize(100);
            dataSource.setMaxPoolSize(110);
            dataSource.setMaxIdleTime(3000);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }*/

/*<!-- 数据源驱动类可不写，Druid默认会自动根据URL识别DriverClass -->
	    <property name="driverClassName" value="${jdbc.driver}" />

		<!-- 基本属性 url、user、password -->
		<property name="url" value="${jdbc.url}" />
		<property name="username" value="${jdbc.username}" />
		<property name="password" value="${jdbc.password}" />

		<!-- 配置初始化大小、最小、最大 -->
		<property name="initialSize" value="${jdbc.pool.init}" />
		<property name="minIdle" value="${jdbc.pool.minIdle}" />
		<property name="maxActive" value="${jdbc.pool.maxActive}" />*/
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(100);
        dataSource.setMaxActive(110);
        dataSource.setMaxIdle(1000);

    }

    /**
     * @return Connection数据库连接对象
     * @throws SQLException
     * @Method: getConnection
     * @Description: 获取数据库连接对象
     * @Anthor:孤傲苍狼
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * @param conn
     * @param st
     * @param rs
     * @Method: release
     * @Description: 释放资源， 要释放的资源包括Connection数据库连接对象，负责执行SQL命令的Statement对象，
     * 存储查询结果的ResultSet对象
     * @Anthor:孤傲苍狼
     */
    public static void release(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                // 关闭存储查询结果的ResultSet对象
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            rs = null;
        }
        if (st != null) {
            try {
                // 关闭负责执行SQL命令的Statement对象
                st.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                // 关闭Connection数据库连接对象
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param sql    要执行的SQL
     * @param params 执行SQL时使用的参数
     * @throws SQLException
     * @Method: update
     * @Description: 万能更新 所有实体的CUD操作代码基本相同，仅仅发送给数据库的SQL语句不同而已，
     * 因此可以把CUD操作的所有相同代码抽取到工具类的一个update方法中，并定义参数接收变化的SQL语句
     * @Anthor:孤傲苍狼
     */
    public static void update(String sql, Object params[]) throws SQLException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                st.setObject(i + 1, params[i]);
            }
            st.executeUpdate();

        } finally {
            release(conn, st, rs);
        }
    }

    /**
     * @param sql    要执行的SQL
     * @param params 执行SQL时使用的参数
     * @param rsh    查询返回的结果集处理器
     * @return
     * @throws SQLException
     * @Method: query
     * @Description:万能查询 实体的R操作，除SQL语句不同之外，根据操作的实体不同，对ResultSet的映射也各不相同，
     * 因此可义一个query方法，除以参数形式接收变化的SQL语句外，
     * 可以使用策略模式由qurey方法的调用者决定如何把ResultSet中的数据映射到实体对象中。
     * @Anthor:孤傲苍狼
     */
    public static Object query(String sql, Object params[], ResultSetHandler rsh) throws SQLException {

        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            st = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                st.setObject(i + 1, params[i]);
            }
            rs = st.executeQuery();
            /**
             * 对于查询返回的结果集处理使用到了策略模式，
             * 在设计query方法时，query方法事先是无法知道用户对返回的查询结果集如何进行处理的，即不知道结果集的处理策略，
             * 那么这个结果集的处理策略就让用户自己提供，query方法内部就调用用户提交的结果集处理策略进行处理
             * 为了能够让用户提供结果集的处理策略，需要对用户暴露出一个结果集处理接口ResultSetHandler
             * 用户只要实现了ResultSetHandler接口，那么query方法内部就知道用户要如何处理结果集了
             */
            return rsh.handle(rs);

        } finally {
            release(conn, st, rs);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
