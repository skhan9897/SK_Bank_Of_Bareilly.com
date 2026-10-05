package com.skbank.service;

import com.skbank.dto.DigitalPassbookDTO;
import com.skbank.exception.BankException;

public interface PassbookService {
    DigitalPassbookDTO getPassbookByUserId(Long userId) throws BankException;
    DigitalPassbookDTO getPassbookByCustomerId(String customerId) throws BankException;
}
