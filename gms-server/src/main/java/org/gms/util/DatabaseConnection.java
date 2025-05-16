package org.gms.util;

import org.gms.manager.ServerManager;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * @author Frz (Big Daddy)
 * @author The Real Spookster - some modifications to this beautiful code
 * @author Ronan - some connection pool to this beautiful code
 */
public class DatabaseConnection {

    /**
     * 获取数据库连接。
     *
     * @return 返回数据库连接对象。
     * @throws SQLException 如果获取数据库连接时发生 SQL 异常，则抛出此异常。
     */
    public static Connection getConnection() throws SQLException {
        return ServerManager.getApplicationContext().getBean(DataSource.class).getConnection();
    }
}
