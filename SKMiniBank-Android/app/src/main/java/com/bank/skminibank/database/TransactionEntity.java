package com.bank.skminibank.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class TransactionEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String ownerAccountNumber;
    private String transactionId;
    private String type;
    private double amount;
    private String description;
    private String date;
    private double balanceAfter;

    public TransactionEntity(String ownerAccountNumber, String transactionId, String type, double amount, String description, String date, double balanceAfter) {
        this.ownerAccountNumber = ownerAccountNumber;
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.balanceAfter = balanceAfter;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getOwnerAccountNumber() { return ownerAccountNumber; }
    public void setOwnerAccountNumber(String ownerAccountNumber) { this.ownerAccountNumber = ownerAccountNumber; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public double getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(double balanceAfter) { this.balanceAfter = balanceAfter; }
}
