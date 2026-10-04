package com.skbank.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3306/sk_bank_of_bareilly?useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&serverTimezone=UTC&characterEncoding=UTF-8";
    private static String username = "root";
    private static String password = "root";

    static {
        try {
            Properties props = new Properties();
            InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("application.properties");
            if (is != null) {
                props.load(is);
                driver = props.getProperty("db.driver", driver);
                url = props.getProperty("db.url", url);
                username = props.getProperty("db.username", username);
                password = props.getProperty("db.password", password);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load application.properties, using default DB settings", e);
        }

        // Environment variables override properties if defined
        String envUrl = System.getenv("DB_URL");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            envUrl = envUrl.trim();
            if (envUrl.startsWith("mysql://")) {
                envUrl = "jdbc:" + envUrl;
            } else if (!envUrl.startsWith("jdbc:")) {
                envUrl = "jdbc:mysql://" + envUrl;
            }

            if (!envUrl.contains("useSSL=")) {
                envUrl += (envUrl.contains("?") ? "&" : "?") + "useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&connectTimeout=10000";
            }
            url = envUrl;
        }

        String envUser = System.getenv("DB_USERNAME");
        if (envUser != null && !envUser.trim().isEmpty()) {
            username = envUser.trim();
        }
        String envPass = System.getenv("DB_PASSWORD");
        if (envPass != null) {
            password = envPass;
        }

        try {
            Class.forName(driver);
            LOGGER.info("Database Connection Configured: " + sanitizeUrl(url) + " | DB User: " + username);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found!", e);
        }
    }

    private static String sanitizeUrl(String rawUrl) {
        if (rawUrl == null) return "null";
        int paramIdx = rawUrl.indexOf('?');
        return paramIdx > 0 ? rawUrl.substring(0, paramIdx) : rawUrl;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static void close(Connection conn, AutoCloseable stmt, AutoCloseable rs) {
        if (rs != null) {
            try { rs.close(); } catch (Exception ignored) {}
        }
        if (stmt != null) {
            try { stmt.close(); } catch (Exception ignored) {}
        }
        if (conn != null) {
            try { conn.close(); } catch (Exception ignored) {}
        }
    }
}
