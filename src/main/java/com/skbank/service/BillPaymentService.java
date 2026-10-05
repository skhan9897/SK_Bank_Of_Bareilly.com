package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.BillPayment;

import java.math.BigDecimal;
import java.util.List;

public interface BillPaymentService {
    BillPayment processBillPayment(String customerId, Long accountId, String billerType, String billerName, String consumerNumber, BigDecimal amount) throws BankException;
    List<BillPayment> getCustomerBillPayments(String customerId) throws BankException;
}
