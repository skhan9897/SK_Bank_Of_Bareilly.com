package com.skbank.service;

import com.skbank.dto.RecipientLookupDTO;
import com.skbank.dto.TransferDTO;
import com.skbank.exception.BankException;
import com.skbank.model.TransferRequest;

public interface TransferService {
    RecipientLookupDTO lookupByMobile(String mobile, Long senderCustomerId) throws BankException;
    RecipientLookupDTO lookupByAccount(String accountNumber, Long senderCustomerId) throws BankException;
    RecipientLookupDTO lookupByUpi(String upiAddress, Long senderCustomerId) throws BankException;
    TransferRequest processTransfer(TransferDTO transferDTO, Long senderCustomerId) throws BankException;
}
