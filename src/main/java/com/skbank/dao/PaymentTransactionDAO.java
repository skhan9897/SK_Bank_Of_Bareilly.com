package com.skbank.dao;

import com.skbank.model.PaymentTransaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface PaymentTransactionDAO {
    List<PaymentTransaction> findByCustomerId(Long customerId, int offset, int limit) throws SQLException;
    Long create(Connection conn, PaymentTransaction txn) throws SQLException;
}
