package com.skbank.dao;

import com.skbank.model.Kyc;
import java.sql.Connection;
import java.sql.SQLException;

public interface KycDAO {
    Kyc findByCustomerId(String customerId) throws SQLException;
    Long create(Kyc kyc) throws SQLException;
    Long create(Connection conn, Kyc kyc) throws SQLException;
    boolean updateVerificationStatus(String customerId, String status) throws SQLException;
}
