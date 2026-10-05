package com.skbank.service.impl;

import com.skbank.dao.CardDAO;
import com.skbank.dao.CardTransactionDAO;
import com.skbank.dao.impl.CardDAOImpl;
import com.skbank.dao.impl.CardTransactionDAOImpl;
import com.skbank.exception.BankException;
import com.skbank.model.Card;
import com.skbank.model.CardStatus;
import com.skbank.model.CardTransaction;
import com.skbank.service.CardService;

import java.util.List;

public class CardServiceImpl implements CardService {

    private final CardDAO cardDAO = new CardDAOImpl();
    private final CardTransactionDAO cardTransactionDAO = new CardTransactionDAOImpl();

    @Override
    public List<Card> getCustomerCards(String customerId) throws BankException {
        try {
            return cardDAO.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new BankException("Error fetching cards", e);
        }
    }

    @Override
    public Card getCardById(Long cardId) throws BankException {
        try {
            Card card = cardDAO.findById(cardId);
            if (card == null) throw new BankException("Card not found");
            return card;
        } catch (Exception e) {
            throw new BankException("Error fetching card", e);
        }
    }

    @Override
    public boolean toggleCardStatus(Long cardId, String customerId) throws BankException {
        try {
            Card card = getCardById(cardId);
            if (!card.getCustomerId().equals(customerId)) {
                throw new BankException("Unauthorized card action");
            }

            String newStatus = (card.getCardStatus() == CardStatus.ACTIVE) ? "BLOCKED" : "ACTIVE";
            return cardDAO.updateStatus(cardId, newStatus);
        } catch (Exception e) {
            throw new BankException("Error updating card status", e);
        }
    }

    @Override
    public List<CardTransaction> getCardTransactions(Long cardId) throws BankException {
        try {
            return cardTransactionDAO.findByCardId(cardId);
        } catch (Exception e) {
            throw new BankException("Error fetching card transactions", e);
        }
    }
}
