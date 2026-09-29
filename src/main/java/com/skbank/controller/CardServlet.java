package com.skbank.controller;

import com.skbank.model.Card;
import com.skbank.model.Customer;
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
        String cardIdStr = request.getParameter("cardId");

        try {
            int cardId = Integer.parseInt(cardIdStr);

            if ("toggleBlock".equalsIgnoreCase(action)) {
                String currentStatus = request.getParameter("currentStatus");
                cardService.toggleCardBlock(cardId, currentStatus);
                request.setAttribute("successMessage", "Card status updated successfully.");

            } else if ("changePin".equalsIgnoreCase(action)) {
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
