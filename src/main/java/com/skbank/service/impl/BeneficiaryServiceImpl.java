package com.skbank.service.impl;

import com.skbank.dao.AccountDAO;
import com.skbank.dao.BeneficiaryDAO;
import com.skbank.dao.CustomerDAO;
import com.skbank.dao.impl.AccountDAOImpl;
import com.skbank.dao.impl.BeneficiaryDAOImpl;
import com.skbank.dao.impl.CustomerDAOImpl;
import com.skbank.exception.BankException;
import com.skbank.model.Account;
import com.skbank.model.Beneficiary;
import com.skbank.model.Customer;
import com.skbank.service.BeneficiaryService;

import java.util.List;

public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryDAO beneficiaryDAO = new BeneficiaryDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();

    @Override
    public List<Beneficiary> getBeneficiaries(String customerId) throws BankException {
        try {
            return beneficiaryDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new BankException("Error fetching beneficiaries", e);
        }
    }

    @Override
    public Beneficiary addBeneficiary(Beneficiary b) throws BankException {
        if (b.getAccountNumber() == null || b.getAccountNumber().trim().isEmpty()) {
            throw new BankException("Account number is required");
        }
        try {
            // Auto lookup for SK Bank accounts
            Account targetAcc = accountDAO.findByAccountNumber(b.getAccountNumber().trim());
            if (targetAcc != null) {
                Customer cust = customerDAO.findById(targetAcc.getCustomerId());
                if (cust != null) {
                    b.setBeneficiaryName(cust.getFullName());
                }
                b.setBankName("SK BANK OF BAREILLY");
                b.setIfscCode("SKBK0000001");
            } else if (b.getBeneficiaryName() == null || b.getBeneficiaryName().trim().isEmpty()) {
                throw new BankException("Beneficiary name is required for external bank accounts");
            }

            Long id = beneficiaryDAO.create(b);
            b.setBeneficiaryId(id);
            return b;
        } catch (Exception e) {
            throw new BankException("Error adding beneficiary: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteBeneficiary(Long beneficiaryId, String customerId) throws BankException {
        try {
            return beneficiaryDAO.delete(beneficiaryId, customerId);
        } catch (Exception e) {
            throw new BankException("Error deleting beneficiary", e);
        }
    }
}
