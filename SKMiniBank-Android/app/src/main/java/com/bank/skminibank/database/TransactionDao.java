package com.bank.skminibank.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTransaction(TransactionEntity transaction);

    @Query("SELECT * FROM transactions WHERE ownerAccountNumber = :accNo ORDER BY id DESC")
    List<TransactionEntity> getAllTransactions(String accNo);

    @Query("SELECT EXISTS(SELECT 1 FROM transactions WHERE transactionId = :txnId AND ownerAccountNumber = :accNo)")
    boolean isTransactionExists(String accNo, String txnId);

    @Query("DELETE FROM transactions")
    void clearAll();
}
