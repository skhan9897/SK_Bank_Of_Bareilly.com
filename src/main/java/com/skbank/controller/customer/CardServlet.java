package com.skbank.controller.customer;

import com.skbank.model.Card;
import com.skbank.model.CardTransaction;
import com.skbank.service.CardService;
import com.skbank.service.impl.CardServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/cards"})
public class CardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CardService cardService = new CardServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        String action = request.getParameter("action");
        if ("toggle".equalsIgnoreCase(action)) {
            try {
                Long cardId = Long.parseLong(request.getParameter("id"));
                cardService.toggleCardStatus(cardId, customerId);
                response.sendRedirect(request.getContextPath() + "/customer/cards?msg=Card status updated.");
                return;
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error updating card: " + e.getMessage());
            }
        }

        try {
            List<Card> cards = cardService.getCustomerCards(customerId);
            Map<Long, List<CardTransaction>> transactionsMap = new HashMap<>();

            for (Card c : cards) {
                transactionsMap.put(c.getCardId(), cardService.getCardTransactions(c.getCardId()));
            }

            request.setAttribute("cards", cards);
            request.setAttribute("transactionsMap", transactionsMap);
            request.getRequestDispatcher("/WEB-INF/views/customer/cards.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading cards: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/cards.jsp").forward(request, response);
        }
    }
}
