package com.skbank.dao;

import com.skbank.model.Branch;
import java.sql.SQLException;
import java.util.List;

public interface BranchDAO {
    Branch findById(Long branchId) throws SQLException;
    Branch findByBranchCode(String branchCode) throws SQLException;
    List<Branch> findAllActive() throws SQLException;
}
