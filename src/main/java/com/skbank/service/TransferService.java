package com.skbank.service;

import com.skbank.dto.RecipientLookupDTO;
import com.skbank.dto.TransferDTO;
import com.skbank.exception.BankException;
import com.skbank.model.TransferRequest;

public interface TransferService {
    RecipientLookupDTO lookupByMobile(String mobile, String senderCustomerId) throws BankException;
    RecipientLookupDTO lookupByAccount(String accountNumber, String senderCustomerId) throws BankException;
    RecipientLookupDTO lookupByUpi(String upiAddress, String senderCustomerId) throws BankException;
    TransferRequest processTransfer(TransferDTO transferDTO, String senderCustomerId) throws BankException;
}
