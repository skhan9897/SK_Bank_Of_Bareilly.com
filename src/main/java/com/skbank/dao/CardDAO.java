package com.skbank.dao;

import com.skbank.model.Card;
import java.sql.SQLException;
import java.util.List;

public interface CardDAO {
    Card findById(Long cardId) throws SQLException;
    List<Card> findByCustomerId(Long customerId) throws SQLException;
    Long create(Card card) throws SQLException;
    boolean updateStatus(Long cardId, String status) throws SQLException;
    List<Card> findAllAdmin(int offset, int limit) throws SQLException;
    long countAllAdmin() throws SQLException;
}
