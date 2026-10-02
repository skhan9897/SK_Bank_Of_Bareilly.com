package com.skbank.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface IdempotencyDAO {
    String getExistingResponse(String key) throws SQLException;
    void saveIdempotency(Connection conn, String key, String refNumber, String jsonResponse) throws SQLException;
}
