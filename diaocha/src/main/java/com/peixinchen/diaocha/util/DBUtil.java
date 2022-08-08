package com.peixinchen.diaocha.util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBUtil {
    private static final DataSource dataSource;

    static {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl("jdbc:mysql://127.0.0.1:3306/java22_diaocha?characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai");
        mysqlDataSource.setUser("root");
        mysqlDataSource.setPassword("123456");
        dataSource = mysqlDataSource;
    }

    @SneakyThrows   // lombok 提供的注解，目的让抛出受查异常时，也不需要额外添加 throws 了
    public static Connection connection() {
        return dataSource.getConnection();
    }
}
