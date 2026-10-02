package com.skbank.dao;

import com.skbank.model.TransferRequest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface TransferDAO {
    TransferRequest findById(Long transferId) throws SQLException;
    TransferRequest findByReference(String referenceNumber) throws SQLException;
    Long create(Connection conn, TransferRequest transferRequest) throws SQLException;
    List<TransferRequest> findByAccountId(Long accountId, int offset, int limit) throws SQLException;
}
