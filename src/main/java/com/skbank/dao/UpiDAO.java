package com.skbank.dao;

import com.skbank.model.UpiAccount;
import java.sql.SQLException;
import java.util.List;

public interface UpiDAO {
    UpiAccount findByUpiAddress(String upiAddress) throws SQLException;
    UpiAccount findByCustomerId(Long customerId) throws SQLException;
    Long create(UpiAccount upiAccount) throws SQLException;
    boolean updateUpiAddress(Long upiAccountId, String newAddress) throws SQLException;
    boolean updateUpiPin(Long upiAccountId, String newPinHash) throws SQLException;
    boolean updateStatus(Long upiAccountId, String status) throws SQLException;
    List<UpiAccount> findAllByCustomerId(Long customerId) throws SQLException;
}
