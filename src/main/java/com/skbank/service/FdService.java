package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.FixedDeposit;

import java.math.BigDecimal;
import java.util.List;

public interface FdService {
    FixedDeposit openFd(String customerId, Long accountId, BigDecimal principal, int tenureMonths) throws BankException;
    List<FixedDeposit> getCustomerFds(String customerId) throws BankException;
    FixedDeposit getFdById(Long fdId) throws BankException;
}
