package com.skbank.dao;

import com.skbank.model.CardTransaction;
import java.sql.SQLException;
import java.util.List;

public interface CardTransactionDAO {
    List<CardTransaction> findByCardId(Long cardId) throws SQLException;
    Long create(CardTransaction ct) throws SQLException;
}
