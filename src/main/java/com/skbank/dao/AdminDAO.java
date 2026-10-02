package com.skbank.dao;

import com.skbank.model.Admin;
import java.sql.SQLException;
import java.util.List;

public interface AdminDAO {
    Admin findByUserId(Long userId) throws SQLException;
    Admin findById(Long adminId) throws SQLException;
    List<Admin> findAll() throws SQLException;
}
