package com.skbank.dao;

import com.skbank.model.PaymentWallet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public interface PaymentWalletDAO {
    PaymentWallet findByCustomerId(String customerId) throws SQLException;
    PaymentWallet findForUpdate(Connection conn, String customerId) throws SQLException;
    Long create(Connection conn, PaymentWallet wallet) throws SQLException;
    boolean updateBalance(Connection conn, Long walletId, BigDecimal newBalance) throws SQLException;
}
