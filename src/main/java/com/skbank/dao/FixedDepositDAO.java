package com.skbank.dao;

import com.skbank.model.FixedDeposit;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface FixedDepositDAO {
    List<FixedDeposit> findByCustomerId(Long customerId) throws SQLException;
    FixedDeposit findById(Long fdId) throws SQLException;
    Long create(Connection conn, FixedDeposit fd) throws SQLException;
    long countActiveFds() throws SQLException;
    BigDecimal getTotalFdInvestmentByCustomerId(Long customerId) throws SQLException;
    List<FixedDeposit> findAllAdmin(int offset, int limit) throws SQLException;
    long countAllAdmin() throws SQLException;
}
