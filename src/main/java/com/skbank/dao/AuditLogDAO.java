package com.skbank.dao;

import com.skbank.model.AuditLog;
import java.sql.SQLException;
import java.util.List;

public interface AuditLogDAO {
    List<AuditLog> findAll(int offset, int limit, String moduleFilter) throws SQLException;
    long countAll(String moduleFilter) throws SQLException;
}
