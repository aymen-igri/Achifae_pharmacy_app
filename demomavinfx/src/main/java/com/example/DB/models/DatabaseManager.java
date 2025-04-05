package com.example.DB.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // Remove static connection - let each operation manage its own connection
    public static Connection getConnection(String url) throws SQLException {
        return DriverManager.getConnection(url);
    }

    public static void closeConnection(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}