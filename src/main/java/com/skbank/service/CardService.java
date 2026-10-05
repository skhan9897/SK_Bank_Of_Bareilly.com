package com.skbank.service;

import com.skbank.exception.BankException;
import com.skbank.model.Card;
import com.skbank.model.CardTransaction;

import java.util.List;

public interface CardService {
    List<Card> getCustomerCards(String customerId) throws BankException;
    Card getCardById(Long cardId) throws BankException;
    boolean toggleCardStatus(Long cardId, String customerId) throws BankException;
    List<CardTransaction> getCardTransactions(Long cardId) throws BankException;
}
