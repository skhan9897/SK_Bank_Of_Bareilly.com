package com.skbank.service;

import com.skbank.dao.CardDAO;
import com.skbank.model.Card;
import com.skbank.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class CardService {

    private final CardDAO cardDAO = new CardDAO();

    public List<Card> getCustomerCards(String customerId) throws SQLException {
        List<Card> cards = cardDAO.findByCustomerId(customerId);
        if (cards.isEmpty()) {
            // Auto issue a Debit Card if customer has none
            createDebitCardForCustomer(customerId, 1, "CUSTOMER DEBIT CARD");
            cards = cardDAO.findByCustomerId(customerId);
        }
        return cards;
    }

    public boolean createDebitCardForCustomer(String customerId, int accountId, String holderName) throws SQLException {
        Random random = new Random();
        long rand12 = 400000000000L + (long)(random.nextDouble() * 500000000000L);
        String cardNumber = String.valueOf(rand12);
        String expiryDate = "12/28";

        Card card = new Card();
        card.setCustomerId(customerId);
        card.setAccountId(accountId);
        card.setCardNumber(cardNumber);
        card.setCardHolderName(holderName.toUpperCase());
        card.setCardType("DEBIT");
        card.setExpiryDate(expiryDate);
        card.setCvvHash(PasswordUtil.hashPassword("123"));
        card.setPinHash(PasswordUtil.hashPassword("1234"));
        card.setDailyLimit(50000.0);
        card.setStatus("ACTIVE");

        return cardDAO.createCard(card);
    }

    public boolean toggleCardBlock(int cardId, String currentStatus) throws SQLException {
        String newStatus = "ACTIVE".equalsIgnoreCase(currentStatus) ? "BLOCKED" : "ACTIVE";
        return cardDAO.updateCardStatus(cardId, newStatus);
    }

    public boolean changeCardPin(int cardId, String newPin) throws SQLException {
        if (newPin == null || newPin.length() != 4 || !newPin.matches("\\d+")) {
            throw new IllegalArgumentException("PIN must be a 4-digit number.");
        }
        return cardDAO.updatePin(cardId, PasswordUtil.hashPassword(newPin));
    }
}
