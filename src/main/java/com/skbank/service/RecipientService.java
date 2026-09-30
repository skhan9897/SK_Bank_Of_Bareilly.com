package com.skbank.service;

import com.skbank.dao.RecipientDAO;
import com.skbank.model.RecipientDTO;

import java.sql.SQLException;

public class RecipientService {

    private final RecipientDAO recipientDAO = new RecipientDAO();

    public RecipientDTO findRecipientByMobile(String mobile) throws SQLException {
        if (mobile == null || mobile.trim().length() != 10) {
            return null;
        }
        return recipientDAO.findByMobile(mobile.trim());
    }

    public RecipientDTO findRecipientByAccountNumber(String accountNumber) throws SQLException {
        if (accountNumber == null || accountNumber.trim().length() < 5) {
            return null;
        }
        return recipientDAO.findByAccountNumber(accountNumber.trim());
    }

    public RecipientDTO findRecipientByUpiId(String upiId) throws SQLException {
        if (upiId == null || !upiId.contains("@")) {
            return null;
        }
        return recipientDAO.findByUpiId(upiId.trim());
    }
}
