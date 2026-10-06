package com.skbank.dao;

import com.skbank.model.UpiAccount;
import java.sql.SQLException;

public interface UpiDAO {
    UpiAccount findByCustomerId(Long customerId) throws SQLException;
    UpiAccount findByUpiAddress(String upiAddress) throws SQLException;
    Long create(UpiAccount upiAccount) throws SQLException;
    boolean updatePin(Long upiId, String newPinHash) throws SQLException;
    boolean updateUpiAddress(Long upiId, String newUpiAddress) throws SQLException;
    boolean updateStatus(Long upiId, String status) throws SQLException;
}
