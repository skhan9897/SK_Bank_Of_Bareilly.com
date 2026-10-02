package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.FixedDeposit;

import java.math.BigDecimal;
import java.util.List;

public interface FdService {
    FixedDeposit openFd(Long customerId, Long accountId, BigDecimal principal, int tenureMonths) throws BankException;
    List<FixedDeposit> getCustomerFds(Long customerId) throws BankException;
    FixedDeposit getFdById(Long fdId) throws BankException;
}
