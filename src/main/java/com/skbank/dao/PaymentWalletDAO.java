package com.skbank.dao;

import com.skbank.model.PaymentWallet;
import java.sql.Connection;
import java.sql.SQLException;

public interface PaymentWalletDAO {
    PaymentWallet findByCustomerId(Long customerId) throws SQLException;
    Long create(Connection conn, PaymentWallet wallet) throws SQLException;
}
