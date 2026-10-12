package com.bank.skminibank.model;

import java.util.List;

public class StoreResponse {
    private String status;
    private String message;
    private List<StoreItem> stores;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<StoreItem> getStores() { return stores; }
}
