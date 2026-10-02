package com.skbank.dao;

import com.skbank.model.SystemSetting;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface SystemSettingsDAO {
    List<SystemSetting> findAll() throws SQLException;
    boolean updateSetting(String key, String value) throws SQLException;
    Map<String, String> loadAllAsMap() throws SQLException;
}
