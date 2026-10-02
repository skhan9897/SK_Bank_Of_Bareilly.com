package com.skbank.dao;

import com.skbank.model.PaymentTransaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PaymentTransactionDAO {
    PaymentTransaction findByIdempotencyKey(String key) throws SQLException;
    PaymentTransaction findByReference(String referenceNumber) throws SQLException;
    Long create(Connection conn, PaymentTransaction pt) throws SQLException;
    List<PaymentTransaction> findByCustomerId(Long customerId, int offset, int limit) throws SQLException;
}
