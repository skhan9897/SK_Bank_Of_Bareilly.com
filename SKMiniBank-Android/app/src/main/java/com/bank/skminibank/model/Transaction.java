package com.bank.skminibank.model;

import com.google.gson.annotations.SerializedName;

public class Transaction {
    @SerializedName(value = "transactionId", alternate = {"id", "txnId", "referenceId", "transaction_id", "txn_id", "ref_id"})
    private String transactionId;

    @SerializedName(value = "type", alternate = {"transactionType", "txnType", "operation", "txn_type"})
    private String type; // CREDIT or DEBIT

    @SerializedName(value = "amount", alternate = {"txnAmount", "val", "amt", "txn_amount"})
    private double amount;

    @SerializedName(value = "description", alternate = {"remarks", "desc", "name", "transaction_desc", "details", "note"})
    private String description;

    @SerializedName(value = "date", alternate = {"transactionDate", "txnDate", "timestamp", "created_at", "txn_date"})
    private String date;

    @SerializedName(value = "fromAccount", alternate = {"senderAccount", "sourceAccount", "debitAccount"})
    private String fromAccount;

    @SerializedName(value = "toAccount", alternate = {"receiverAccount", "destinationAccount", "creditAccount"})
    private String toAccount;

    @SerializedName(value = "fromMobile", alternate = {"senderMobile", "sourceMobile"})
    private String fromMobile;

    @SerializedName(value = "toMobile", alternate = {"receiverMobile", "destinationMobile"})
    private String toMobile;

    @SerializedName(value = "senderName", alternate = {"fromName", "sender"})
    private String senderName;

    @SerializedName(value = "receiverName", alternate = {"toName", "receiver"})
    private String receiverName;

    @SerializedName(value = "balanceAfter", alternate = {"closingBalance", "afterBalance", "balance"})
    private double balanceAfter;

    public Transaction() {}

    public Transaction(String transactionId, String type, double amount, String description, String date, double balanceAfter) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.balanceAfter = balanceAfter;
    }

    public String getTransactionId() { return transactionId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description == null ? "Transaction" : description; }
    public String getDate() { return date == null ? "N/A" : date; }
    public double getBalanceAfter() { return balanceAfter; }
    public double getClosingBalance() { return balanceAfter; }
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public String getFromMobile() { return fromMobile; }
    public String getToMobile() { return toMobile; }
    public String getSenderName() { return senderName; }
    public String getReceiverName() { return receiverName; }
}