package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.UpiAccount;

public interface UpiService {
    UpiAccount getUpiByCustomerId(String customerId) throws BankException;
    UpiAccount createUpiAccount(String customerId, Long accountId, String desiredUpiAddress, String plainPin) throws BankException;
    boolean changeUpiPin(String customerId, String oldPin, String newPin) throws BankException;
    boolean changeUpiAddress(String customerId, String newUpiAddress) throws BankException;
    boolean disableUpi(String customerId) throws BankException;
}
