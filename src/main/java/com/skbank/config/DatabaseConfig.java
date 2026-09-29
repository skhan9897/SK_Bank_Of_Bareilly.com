package com.skbank.config;

import com.skbank.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {

    public static Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }
}
