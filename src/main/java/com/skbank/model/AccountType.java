package com.skbank.model;

public class AccountType {
    private int typeId;
    private String typeName;
    private double minBalance;
    private double interestRate;
    private String description;

    public AccountType() {}

    public int getTypeId() { return typeId; }
    public void setTypeId(int typeId) { this.typeId = typeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public double getMinBalance() { return minBalance; }
    public void setMinBalance(double minBalance) { this.minBalance = minBalance; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
