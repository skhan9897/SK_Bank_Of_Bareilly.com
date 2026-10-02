package com.skbank.dao.impl;

import com.skbank.dao.SystemSettingsDAO;
import com.skbank.model.SystemSetting;
import com.skbank.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemSettingsDAOImpl implements SystemSettingsDAO {

    @Override
    public List<SystemSetting> findAll() throws SQLException {
        List<SystemSetting> list = new ArrayList<>();
        String sql = "SELECT * FROM system_settings ORDER BY setting_key ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SystemSetting ss = new SystemSetting();
                ss.setSettingId(rs.getLong("setting_id"));
                ss.setSettingKey(rs.getString("setting_key"));
                ss.setSettingValue(rs.getString("setting_value"));
                ss.setDescription(rs.getString("description"));
                ss.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(ss);
            }
        }
        return list;
    }

    @Override
    public boolean updateSetting(String key, String value) throws SQLException {
        String sql = "UPDATE system_settings SET setting_value = ?, updated_at = NOW() WHERE setting_key = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setString(2, key);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Map<String, String> loadAllAsMap() throws SQLException {
        Map<String, String> map = new HashMap<>();
        String sql = "SELECT setting_key, setting_value FROM system_settings";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
        }
        return map;
    }
}
