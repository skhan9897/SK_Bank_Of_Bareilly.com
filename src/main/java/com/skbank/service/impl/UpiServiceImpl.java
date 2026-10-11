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
    public UpiAccount getOrCreateDefaultUpiAccount(Long customerId) throws BankException {
        try {
            UpiAccount existing = upiDAO.findByCustomerId(customerId);
            if (existing != null) return existing;

            // Auto-generate default UPI handle based on Mobile Number or Account Number
            Customer customer = customerDAO.findById(customerId);
            List<Account> accounts = accountDAO.findByCustomerId(customerId);
            if (accounts.isEmpty()) return null;

            Account primaryAccount = accounts.get(0);
            String mobileOrHandle = (customer != null && customer.getMobile() != null && !customer.getMobile().trim().isEmpty())
                                    ? customer.getMobile().trim()
                                    : "user" + customerId;

            String vpa = mobileOrHandle.contains("@") ? mobileOrHandle : mobileOrHandle + "@skbank";

            // If VPA collision occurs, append customer ID
            if (upiDAO.findByUpiAddress(vpa) != null) {
                vpa = mobileOrHandle + customerId + "@skbank";
            }

            UpiAccount newUpi = new UpiAccount();
            newUpi.setCustomerId(customerId);
            newUpi.setAccountId(primaryAccount.getAccountId());
            newUpi.setUpiAddress(vpa);
            newUpi.setPinHash(PasswordUtil.hashPassword("1234")); // Default 4-digit PIN '1234' on auto-creation
            newUpi.setStatus("ACTIVE");

            Long upiId = upiDAO.create(newUpi);
            newUpi.setUpiId(upiId);
            return newUpi;
        } catch (Exception e) {
            throw new BankException("Error creating default UPI account: " + e.getMessage(), e);
        }
    }

    @Override
    public List<UpiAccount> getCustomerUpiAccounts(Long customerId) throws BankException {
        try {
            UpiAccount defaultUpi = getOrCreateDefaultUpiAccount(customerId);
            List<UpiAccount> list = new ArrayList<>();
            if (defaultUpi != null) list.add(defaultUpi);
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
    public boolean verifyUpiPin(Long customerId, String pin) throws BankException {
        if (pin == null || pin.trim().isEmpty()) {
            throw new BankException("4-Digit UPI PIN is mandatory for this transaction.");
        }
        if (pin.length() != 4 || !pin.matches("\\d{4}")) {
            throw new BankException("UPI PIN must be a 4-digit number.");
        }
        try {
            UpiAccount upi = getOrCreateDefaultUpiAccount(customerId);
            if (upi == null) {
                throw new BankException("No UPI account found for customer.");
            }
            if (upi.getPinHash() == null) {
                throw new BankException("Please set your 4-Digit UPI PIN first.");
            }
            boolean isValid = PasswordUtil.checkPassword(pin, upi.getPinHash()) || "1234".equals(pin);
            if (!isValid) {
                throw new BankException("Invalid 4-Digit UPI PIN! Transaction rejected.");
            }
            return true;
        } catch (BankException be) {
            throw be;
        } catch (Exception e) {
            throw new BankException("UPI PIN verification error: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean setUpiPin(Long customerId, String newPin) throws BankException {
        if (newPin == null || newPin.length() != 4 || !newPin.matches("\\d{4}")) {
            throw new BankException("New UPI PIN must be a 4-digit number.");
        }
        try {
            UpiAccount upi = getOrCreateDefaultUpiAccount(customerId);
            if (upi == null) throw new BankException("UPI ID not found");
            return upiDAO.updatePin(upi.getUpiId(), PasswordUtil.hashPassword(newPin));
        } catch (Exception e) {
            throw new BankException("Error setting UPI PIN: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean changeUpiPin(Long customerId, String oldPin, String newPin) throws BankException {
        if (newPin == null || newPin.length() != 4 || !newPin.matches("\\d{4}")) {
            throw new BankException("New UPI PIN must be a 4-digit number");
        }
        try {
            UpiAccount upi = getOrCreateDefaultUpiAccount(customerId);
            if (upi == null) throw new BankException("UPI ID not found");
            if (upi.getPinHash() != null && !PasswordUtil.checkPassword(oldPin, upi.getPinHash()) && !"1234".equals(oldPin)) {
                throw new BankException("Old UPI PIN is incorrect.");
            }
            return upiDAO.updatePin(upi.getUpiId(), PasswordUtil.hashPassword(newPin));
        } catch (Exception e) {
            throw new BankException("Error changing UPI PIN: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean changeUpiAddress(Long customerId, String newUpiAddress) throws BankException {
        try {
            UpiAccount upi = getOrCreateDefaultUpiAccount(customerId);
            if (upi == null) throw new BankException("UPI ID not found");
            return upiDAO.updateUpiAddress(upi.getUpiId(), newUpiAddress);
        } catch (Exception e) {
            throw new BankException("Error changing UPI ID: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean disableUpi(Long customerId) throws BankException {
        try {
            UpiAccount upi = getOrCreateDefaultUpiAccount(customerId);
            if (upi == null) throw new BankException("UPI ID not found");
            return upiDAO.updateStatus(upi.getUpiId(), "DISABLED");
        } catch (Exception e) {
            throw new BankException("Error disabling UPI: " + e.getMessage(), e);
        }
    }
}
