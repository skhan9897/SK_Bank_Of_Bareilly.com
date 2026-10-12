package com.bank.skminibank.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TransactionResponse {
    @SerializedName(value = "status", alternate = {"success"})
    private String status;

    @SerializedName(value = "transactions", alternate = {"data", "list", "history", "records", "transactionList", "transaction_history", "txn_history", "all_transactions"})
    private List<Transaction> transactions;

    @SerializedName(value = "message", alternate = {"msg", "info", "error"})
    private String message;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<Transaction> getTransactions() { return transactions; }
}
