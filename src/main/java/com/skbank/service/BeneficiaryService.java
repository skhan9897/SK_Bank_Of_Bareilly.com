
package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.Beneficiary;
import java.util.List;

public interface BeneficiaryService {
    List<Beneficiary> getBeneficiaries(String customerId) throws BankException;
    Beneficiary addBeneficiary(Beneficiary beneficiary) throws BankException;
    boolean deleteBeneficiary(Long beneficiaryId, String customerId) throws BankException;
}
