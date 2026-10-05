package com.skbank.dao;

import com.skbank.model.BillPayment;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface BillPaymentDAO {
    Long create(Connection conn, BillPayment bp) throws SQLException;
    List<BillPayment> findByCustomerId(String customerId) throws SQLException;
    List<BillPayment> findAllAdmin(int offset, int limit) throws SQLException;
    long countAllAdmin() throws SQLException;
}
