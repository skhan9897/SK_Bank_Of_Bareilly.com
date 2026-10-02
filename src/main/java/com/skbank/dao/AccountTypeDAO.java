package com.skbank.dao;

import com.skbank.model.AccountType;
import java.sql.SQLException;
import java.util.List;

public interface AccountTypeDAO {
    AccountType findById(Long accountTypeId) throws SQLException;
    AccountType findByCode(String typeCode) throws SQLException;
    List<AccountType> findAllActive() throws SQLException;
}
