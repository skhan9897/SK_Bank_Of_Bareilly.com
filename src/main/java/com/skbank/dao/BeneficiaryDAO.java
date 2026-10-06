package com.skbank.dao;

import com.skbank.model.Beneficiary;
import java.sql.SQLException;
import java.util.List;

public interface BeneficiaryDAO {
    List<Beneficiary> findByCustomerId(Long customerId) throws SQLException;
    Beneficiary findById(Long beneficiaryId) throws SQLException;
    Long create(Beneficiary beneficiary) throws SQLException;
    boolean delete(Long beneficiaryId, Long customerId) throws SQLException;
}
