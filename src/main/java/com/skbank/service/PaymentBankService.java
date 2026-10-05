package com.skbank.service;

import com.skbank.dto.PaymentRequestDTO;
import com.skbank.exception.BankException;
import com.skbank.model.PaymentProvider;
import com.skbank.model.PaymentTransaction;
import com.skbank.model.PaymentWallet;

import java.util.List;

public interface PaymentBankService {
    PaymentWallet getWallet(String customerId) throws BankException;
    PaymentTransaction processPayment(String customerId, PaymentRequestDTO request) throws BankException;
    List<PaymentTransaction> getPaymentHistory(String customerId, int page, int pageSize) throws BankException;
    List<PaymentProvider> getProvidersByType(String providerType) throws BankException;
}
