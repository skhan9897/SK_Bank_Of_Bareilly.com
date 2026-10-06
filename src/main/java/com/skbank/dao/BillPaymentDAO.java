package com.skbank.dao;

import com.skbank.model.BillPayment;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface BillPaymentDAO {
    List<BillPayment> findByCustomerId(Long customerId) throws SQLException;
    Long create(BillPayment billPayment) throws SQLException;
    Long create(Connection conn, BillPayment billPayment) throws SQLException;
    List<BillPayment> findAllAdmin(int offset, int limit) throws SQLException;
    long countAllAdmin() throws SQLException;
}
