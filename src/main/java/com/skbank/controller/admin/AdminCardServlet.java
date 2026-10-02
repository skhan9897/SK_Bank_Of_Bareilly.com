package com.skbank.controller.admin;

import com.skbank.dao.CardDAO;
import com.skbank.dao.impl.CardDAOImpl;
import com.skbank.model.Card;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/admin/cards"})
public class AdminCardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CardDAO cardDAO = new CardDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int page = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
            }
            int pageSize = 10;

            List<Card> cards = cardDAO.findAllAdmin((page - 1) * pageSize, pageSize);
            long totalRecords = cardDAO.countAllAdmin();
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            request.setAttribute("cards", cards);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/WEB-INF/views/admin/cards.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading cards: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/cards.jsp").forward(request, response);
        }
    }
}
