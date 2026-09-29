package com.skbank.controller;

import com.skbank.model.Account;
import com.skbank.model.Card;
import com.skbank.model.Customer;
import com.skbank.service.AccountService;
import com.skbank.service.CardService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/cards")
public class CardServlet extends HttpServlet {

    private final CardService cardService = new CardService();
    private final AccountService accountService = new AccountService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer customer = (session != null) ? (Customer) session.getAttribute("customerProfile") : null;

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<Card> cards = cardService.getCustomerCards(customer.getCustomerId());
            request.setAttribute("cards", cards);
            request.getRequestDispatcher("/cards.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading card details: " + e.getMessage());
            request.getRequestDispatcher("/cards.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer customer = (session != null) ? (Customer) session.getAttribute("customerProfile") : null;

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("requestNewCard".equalsIgnoreCase(action)) {
                List<Account> accounts = accountService.getCustomerAccounts(customer.getCustomerId());
                int accId = !accounts.isEmpty() ? accounts.get(0).getAccountId() : 1;
                String holderName = request.getParameter("holderName");

                cardService.createDebitCardForCustomer(customer.getCustomerId(), accId, holderName != null ? holderName : customer.getFullName());
                request.setAttribute("successMessage", "New Card Issued Successfully!");

            } else if ("toggleBlock".equalsIgnoreCase(action)) {
                int cardId = Integer.parseInt(request.getParameter("cardId"));
                String currentStatus = request.getParameter("currentStatus");
                cardService.toggleCardBlock(cardId, currentStatus);
                request.setAttribute("successMessage", "Card status updated successfully.");

            } else if ("changePin".equalsIgnoreCase(action)) {
                int cardId = Integer.parseInt(request.getParameter("cardId"));
                String newPin = request.getParameter("newPin");
                cardService.changeCardPin(cardId, newPin);
                request.setAttribute("successMessage", "Card PIN changed successfully.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
