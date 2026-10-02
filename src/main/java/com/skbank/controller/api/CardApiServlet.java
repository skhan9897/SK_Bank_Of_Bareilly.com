package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.Card;
import com.skbank.service.CardService;
import com.skbank.service.impl.CardServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/cards"})
public class CardApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CardService cardService = new CardServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            List<Card> cards = cardService.getCustomerCards(customerId);
            response.getWriter().write(gson.toJson(ApiResponse.success("Cards loaded", cards)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "CARD_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        String cardIdStr = request.getParameter("cardId");

        try {
            Long cardId = Long.parseLong(cardIdStr);
            boolean ok = cardService.toggleCardStatus(cardId, customerId);
            response.getWriter().write(gson.toJson(ApiResponse.success("Card status toggled", ok)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "CARD_TOGGLE_FAILED")));
        }
    }
}
