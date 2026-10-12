package com.bank.skminibank.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE ownerAccountNumber = :ownerAcc AND contactMobile = :mobile ORDER BY id ASC")
    List<ChatMessageEntity> getChatHistory(String ownerAcc, String mobile);

    @Query("SELECT EXISTS(SELECT 1 FROM chat_messages WHERE ownerAccountNumber = :ownerAcc AND transactionId = :txnId)")
    boolean isTransactionExists(String ownerAcc, String txnId);

    @Query("SELECT * FROM chat_messages WHERE ownerAccountNumber = :ownerAcc ORDER BY id DESC")
    List<ChatMessageEntity> getAllMessages(String ownerAcc);

    @Insert
    void insertMessage(ChatMessageEntity message);
}