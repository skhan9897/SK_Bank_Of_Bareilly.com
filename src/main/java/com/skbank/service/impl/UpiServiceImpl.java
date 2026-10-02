package com.skbank.service.impl;

import com.skbank.dao.UpiDAO;
import com.skbank.dao.impl.UpiDAOImpl;
import com.skbank.exception.BankException;
import com.skbank.model.UpiAccount;
import com.skbank.service.UpiService;
import com.skbank.util.PasswordUtil;

public class UpiServiceImpl implements UpiService {

    private final UpiDAO upiDAO = new UpiDAOImpl();

    @Override
    public UpiAccount getUpiByCustomerId(Long customerId) throws BankException {
        try {
            return upiDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new BankException("Error fetching UPI details", e);
        }
    }

    @Override
    public UpiAccount createUpiAccount(Long customerId, Long accountId, String desiredUpiAddress, String plainPin) throws BankException {
        if (desiredUpiAddress == null || !desiredUpiAddress.contains("@skbank")) {
            throw new BankException("UPI address must end with @skbank");
        }
        if (plainPin == null || !plainPin.matches("^\\d{4,6}$")) {
            throw new BankException("UPI PIN must be 4 or 6 digits");
        }

        try {
            if (upiDAO.findByUpiAddress(desiredUpiAddress) != null) {
                throw new BankException("UPI handle '" + desiredUpiAddress + "' is already taken");
            }

            UpiAccount existing = upiDAO.findByCustomerId(customerId);
            if (existing != null) {
                // Update existing
                upiDAO.updateUpiAddress(existing.getUpiAccountId(), desiredUpiAddress);
                upiDAO.updateUpiPin(existing.getUpiAccountId(), PasswordUtil.hashPassword(plainPin));
                upiDAO.updateStatus(existing.getUpiAccountId(), "ACTIVE");
                existing.setUpiAddress(desiredUpiAddress);
                return existing;
            } else {
                UpiAccount upi = new UpiAccount();
                upi.setCustomerId(customerId);
                upi.setAccountId(accountId);
                upi.setUpiAddress(desiredUpiAddress);
                upi.setUpiPinHash(PasswordUtil.hashPassword(plainPin));
                upi.setStatus("ACTIVE");
                Long id = upiDAO.create(upi);
                upi.setUpiAccountId(id);
                return upi;
            }
        } catch (Exception e) {
            throw new BankException("Error creating UPI ID: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean changeUpiPin(Long customerId, String oldPin, String newPin) throws BankException {
        if (newPin == null || !newPin.matches("^\\d{4,6}$")) {
            throw new BankException("New UPI PIN must be 4 or 6 digits");
        }
        try {
            UpiAccount upi = upiDAO.findByCustomerId(customerId);
            if (upi == null) throw new BankException("UPI account not found");

            if (!PasswordUtil.checkPassword(oldPin, upi.getUpiPinHash())) {
                throw new BankException("Current UPI PIN is incorrect");
            }

            String newHash = PasswordUtil.hashPassword(newPin);
            return upiDAO.updateUpiPin(upi.getUpiAccountId(), newHash);
        } catch (Exception e) {
            throw new BankException("Error changing UPI PIN: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean changeUpiAddress(Long customerId, String newUpiAddress) throws BankException {
        if (newUpiAddress == null || !newUpiAddress.contains("@skbank")) {
            throw new BankException("UPI address must end with @skbank");
        }
        try {
            if (upiDAO.findByUpiAddress(newUpiAddress) != null) {
                throw new BankException("UPI handle '" + newUpiAddress + "' is already taken");
            }
            UpiAccount upi = upiDAO.findByCustomerId(customerId);
            if (upi == null) throw new BankException("UPI account not found");

            return upiDAO.updateUpiAddress(upi.getUpiAccountId(), newUpiAddress);
        } catch (Exception e) {
            throw new BankException("Error updating UPI address: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean disableUpi(Long customerId) throws BankException {
        try {
            UpiAccount upi = upiDAO.findByCustomerId(customerId);
            if (upi == null) throw new BankException("UPI account not found");
            return upiDAO.updateStatus(upi.getUpiAccountId(), "DISABLED");
        } catch (Exception e) {
            throw new BankException("Error disabling UPI", e);
        }
    }
}
