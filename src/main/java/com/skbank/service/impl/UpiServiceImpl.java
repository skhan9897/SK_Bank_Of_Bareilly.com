package com.skbank.service.impl;

import com.skbank.dao.*;
import com.skbank.dao.impl.*;
import com.skbank.exception.BankException;
import com.skbank.model.Account;
import com.skbank.model.Customer;
import com.skbank.model.UpiAccount;
import com.skbank.service.UpiService;
import com.skbank.util.PasswordUtil;

import java.util.ArrayList;
import java.util.List;

public class UpiServiceImpl implements UpiService {

    private final UpiDAO upiDAO = new UpiDAOImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();

    @Override
    public UpiAccount getUpiByCustomerId(Long customerId) throws BankException {
        try {
            return upiDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new BankException("Error fetching UPI details", e);
        }
    }

    @Override
    public List<UpiAccount> getCustomerUpiAccounts(Long customerId) throws BankException {
        try {
            UpiAccount single = upiDAO.findByCustomerId(customerId);
            List<UpiAccount> list = new ArrayList<>();
            if (single != null) list.add(single);
            return list;
        } catch (Exception e) {
            throw new BankException("Error fetching UPI accounts", e);
        }
    }

    @Override
    public UpiAccount registerUpi(Long customerId, Long accountId, String desiredHandle, String plainPin) throws BankException {
        return createUpiAccount(customerId, accountId, desiredHandle, plainPin);
    }

    @Override
    public UpiAccount createUpiAccount(Long customerId, Long accountId, String desiredUpiAddress, String plainPin) throws BankException {
        if (plainPin == null || plainPin.length() != 4 || !plainPin.matches("\\d{4}")) {
            throw new BankException("UPI PIN must be a 4-digit number");
        }
        try {
            Account account = accountDAO.findById(accountId);
            if (account == null || !account.getCustomerId().equals(customerId)) {
                throw new BankException("Invalid account selected for UPI link");
            }

            Customer cust = customerDAO.findById(customerId);
            String vpa = (desiredUpiAddress != null && !desiredUpiAddress.trim().isEmpty()) ? desiredUpiAddress.trim() : (cust != null ? cust.getMobile() + "@skbank" : "user" + customerId + "@skbank");
            if (!vpa.contains("@")) {
                vpa = vpa + "@skbank";
            }

            if (upiDAO.findByUpiAddress(vpa) != null) {
                throw new BankException("UPI ID " + vpa + " is already registered.");
            }

            UpiAccount upi = new UpiAccount();
            upi.setCustomerId(customerId);
            upi.setAccountId(accountId);
            upi.setUpiAddress(vpa);
            upi.setPinHash(PasswordUtil.hashPassword(plainPin));
            upi.setStatus("ACTIVE");

            Long id = upiDAO.create(upi);
            upi.setUpiId(id);
            return upi;
        } catch (Exception e) {
            throw new BankException("Failed to register UPI: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean changeUpiPin(Long customerId, String oldPin, String newPin) throws BankException {
        if (newPin == null || newPin.length() != 4 || !newPin.matches("\\d{4}")) {
            throw new BankException("New UPI PIN must be a 4-digit number");
        }
        try {
            UpiAccount upi = upiDAO.findByCustomerId(customerId);
            if (upi == null) throw new BankException("UPI ID not found");
            if (!PasswordUtil.checkPassword(oldPin, upi.getPinHash())) {
                throw new BankException("Old UPI PIN is incorrect");
            }
            return upiDAO.updatePin(upi.getUpiId(), PasswordUtil.hashPassword(newPin));
        } catch (Exception e) {
            throw new BankException("Error changing UPI PIN: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean changeUpiAddress(Long customerId, String newUpiAddress) throws BankException {
        try {
            UpiAccount upi = upiDAO.findByCustomerId(customerId);
            if (upi == null) throw new BankException("UPI ID not found");
            return upiDAO.updateUpiAddress(upi.getUpiId(), newUpiAddress);
        } catch (Exception e) {
            throw new BankException("Error changing UPI ID: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean disableUpi(Long customerId) throws BankException {
        try {
            UpiAccount upi = upiDAO.findByCustomerId(customerId);
            if (upi == null) throw new BankException("UPI ID not found");
            return upiDAO.updateStatus(upi.getUpiId(), "DISABLED");
        } catch (Exception e) {
            throw new BankException("Error disabling UPI: " + e.getMessage(), e);
        }
    }
}
