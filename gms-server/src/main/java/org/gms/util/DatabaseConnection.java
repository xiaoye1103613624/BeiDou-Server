package org.gms.util;

import org.gms.manager.ServerManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接工厂
 * 从Spring容器获取DataSource，从连接池获取数据库连接
 * 
 * @author Frz (Big Daddy)
 * @author The Real Spookster - some modifications to this beautiful code
 * @author Ronan - some connection pool to this beautiful code
 */
public class DatabaseConnection {

    /**
     * 从连接池获取数据库连接
     *
     * @return 数据库连接对象
     * @throws SQLException 获取连接失败时抛出
     */
    public static Connection getConnection() throws SQLException {
        return ServerManager.getApplicationContext().getBean(DataSource.class).getConnection();
    }
}