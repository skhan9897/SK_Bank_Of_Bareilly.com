package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.Beneficiary;
import java.util.List;

public interface BeneficiaryService {
    List<Beneficiary> getBeneficiaries(Long customerId) throws BankException;
    Beneficiary addBeneficiary(Beneficiary beneficiary) throws BankException;
    boolean deleteBeneficiary(Long beneficiaryId, Long customerId) throws BankException;
}
