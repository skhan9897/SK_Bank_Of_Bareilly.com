package com.skbank.service.impl;

import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.dto.DigitalPassbookDTO;
import com.skbank.exception.BankException;
import com.skbank.model.*;
import com.skbank.service.PassbookService;

import java.util.List;

public class PassbookServiceImpl implements PassbookService {

    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final BranchDAO branchDAO = new BranchDAOImpl();
    private final AccountTypeDAO accountTypeDAO = new AccountTypeDAOImpl();

    @Override
    public DigitalPassbookDTO getPassbookByUserId(Long userId) throws BankException {
        try {
            Customer cust = customerDAO.findByUserId(userId);
            if (cust == null) {
                throw new BankException("Customer profile not found for user ID: " + userId);
            }
            return getPassbookByCustomerId(cust.getCustomerId());
        } catch (Exception e) {
            throw new BankException("Error building passbook: " + e.getMessage(), e);
        }
    }

    @Override
    public DigitalPassbookDTO getPassbookByCustomerId(String customerId) throws BankException {
        try {
            Customer cust = customerDAO.findById(customerId);
            if (cust == null) {
                throw new BankException("Customer not found for ID: " + customerId);
            }

            List<Account> accounts = accountDAO.findByCustomerId(customerId);
            Account primaryAcc = (!accounts.isEmpty()) ? accounts.get(0) : null;

            DigitalPassbookDTO dto = new DigitalPassbookDTO();
            dto.setCustomerId(cust.getCustomerId());
            dto.setCustomerNumber(cust.getCustomerNumber());
            dto.setCustomerName(cust.getFullName());
            dto.setDateOfBirth(cust.getDateOfBirth());
            dto.setGender(cust.getGender());
            dto.setMobile(cust.getMobile());
            dto.setEmail(cust.getEmail());
            dto.setAddress(cust.getAddress());
            dto.setCity(cust.getCity());
            dto.setState(cust.getState());
            dto.setPincode(cust.getPincode());
            dto.setAadhaarNumber(cust.getAadhaarNumber());
            dto.setPanNumber(cust.getPanNumber());
            dto.setKycStatus(cust.getKycStatus() != null ? cust.getKycStatus().name() : "VERIFIED");

            if (primaryAcc != null) {
                dto.setAccountId(primaryAcc.getAccountId());
                dto.setAccountNumber(primaryAcc.getAccountNumber());
                dto.setBalance(primaryAcc.getBalance());
                dto.setAvailableBalance(primaryAcc.getAvailableBalance());
                dto.setAccountStatus(primaryAcc.getStatus() != null ? primaryAcc.getStatus().name() : "ACTIVE");
                dto.setOpeningDate(primaryAcc.getOpenedAt());

                Branch b = branchDAO.findById(primaryAcc.getBranchId());
                if (b != null) {
                    dto.setBranchName(b.getBranchName());
                    dto.setBranchCode(b.getBranchCode());
                    dto.setIfscCode(b.getIfscCode());
                } else {
                    dto.setBranchName("Main Branch Bareilly");
                    dto.setBranchCode("SKB001");
                    dto.setIfscCode("SKBK0000001");
                }

                AccountType at = accountTypeDAO.findById(primaryAcc.getAccountTypeId());
                if (at != null) {
                    dto.setAccountType(at.getTypeName());
                } else {
                    dto.setAccountType("Savings Account");
                }
            } else {
                dto.setBranchName("Main Branch Bareilly");
                dto.setBranchCode("SKB001");
                dto.setIfscCode("SKBK0000001");
                dto.setAccountType("Savings Account");
                dto.setAccountStatus("ACTIVE");
            }

            return dto;
        } catch (Exception e) {
            throw new BankException("Error building passbook: " + e.getMessage(), e);
        }
    }
}
