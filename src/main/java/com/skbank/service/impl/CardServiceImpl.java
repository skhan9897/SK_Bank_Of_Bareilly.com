package com.skbank.service.impl;

import com.skbank.dao.CardDAO;
import com.skbank.dao.impl.CardDAOImpl;
import com.skbank.exception.BankException;
import com.skbank.model.Card;
import com.skbank.model.CardStatus;
import com.skbank.model.CardTransaction;
import com.skbank.service.CardService;

import java.util.ArrayList;
import java.util.List;

public class CardServiceImpl implements CardService {

    private final CardDAO cardDAO = new CardDAOImpl();

    @Override
    public List<Card> getCustomerCards(Long customerId) throws BankException {
        try {
            List<Card> cards = cardDAO.findByCustomerId(customerId);
            return cards != null ? cards : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Card getCardById(Long cardId) throws BankException {
        try {
            return cardDAO.findById(cardId);
        } catch (Exception e) {
            throw new BankException("Error fetching card details", e);
        }
    }

    @Override
    public boolean toggleCardStatus(Long cardId, Long customerId) throws BankException {
        try {
            Card card = cardDAO.findById(cardId);
            if (card == null || !card.getCustomerId().equals(customerId)) {
                throw new BankException("Card not found or unauthorized");
            }
            String newStatus = (card.getCardStatus() == CardStatus.ACTIVE) ? "BLOCKED" : "ACTIVE";
            return cardDAO.updateStatus(cardId, newStatus);
        } catch (Exception e) {
            throw new BankException("Error updating card status", e);
        }
    }

    @Override
    public List<CardTransaction> getCardTransactions(Long cardId) throws BankException {
        return new ArrayList<>();
    }
}
