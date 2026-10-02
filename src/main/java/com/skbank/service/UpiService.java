package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.UpiAccount;

public interface UpiService {
    UpiAccount getUpiByCustomerId(Long customerId) throws BankException;
    UpiAccount createUpiAccount(Long customerId, Long accountId, String desiredUpiAddress, String plainPin) throws BankException;
    boolean changeUpiPin(Long customerId, String oldPin, String newPin) throws BankException;
    boolean changeUpiAddress(Long customerId, String newUpiAddress) throws BankException;
    boolean disableUpi(Long customerId) throws BankException;
}
