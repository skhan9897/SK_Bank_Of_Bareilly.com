package com.skbank.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    // Configurable database credentials with environment variable support & fallback
    private static final String DEFAULT_URL = "jdbc:mysql://bjkcueu7xmg0w4f52r7x-mysql.services.clever-cloud.com:3306/bjkcueu7xmg0w4f52r7x?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private static final String DEFAULT_USER = "uqbtxyvc7q2exlt8";
    private static final String DEFAULT_PASS = "Osqa4c9dgHcCIZot4JdZ";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver Not Found in Classpath!", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : DEFAULT_URL;
        String user = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : DEFAULT_USER;
        String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : DEFAULT_PASS;

        return DriverManager.getConnection(url, user, password);
    }
}
