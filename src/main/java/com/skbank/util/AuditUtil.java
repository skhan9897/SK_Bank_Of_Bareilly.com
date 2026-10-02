package com.skbank.util;

import com.skbank.model.AuditLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpServletRequest;

public class AuditUtil {

    public static void logAction(Long userId, String action, String module, String description, HttpServletRequest request) {
        String ipAddress = request != null ? getClientIp(request) : "SYSTEM";
        String userAgent = request != null ? request.getHeader("User-Agent") : "SYSTEM";

        String sql = "INSERT INTO audit_logs (user_id, action, module, description, ip_address, user_agent, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (userId != null) {
                ps.setLong(1, userId);
            } else {
                ps.setNull(1, java.sql.Types.BIGINT);
            }
            ps.setString(2, action);
            ps.setString(3, module);
            ps.setString(4, sanitizeDescription(description));
            ps.setString(5, ipAddress);
            ps.setString(6, userAgent != null && userAgent.length() > 250 ? userAgent.substring(0, 250) : userAgent);

            ps.executeUpdate();
        } catch (Exception e) {
            // Log audit failure to console without breaking main workflow
            System.err.println("Audit log error: " + e.getMessage());
        }
    }

    private static String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isEmpty()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static String sanitizeDescription(String desc) {
        if (desc == null) return "";
        // Security rule: Never log passwords, OTPs, UPI PINs, CVVs, full Aadhaar/PAN
        return desc.replaceAll("(?i)password=[^&\\s]+", "password=***")
                   .replaceAll("(?i)otp=[^&\\s]+", "otp=***")
                   .replaceAll("(?i)upiPin=[^&\\s]+", "upiPin=***")
                   .replaceAll("(?i)cvv=[^&\\s]+", "cvv=***");
    }
}
