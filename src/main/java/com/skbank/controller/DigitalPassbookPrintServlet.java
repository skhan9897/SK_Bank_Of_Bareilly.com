package com.skbank.controller;

import com.skbank.dto.DigitalPassbookDTO;
import com.skbank.model.User;
import com.skbank.service.PassbookService;
import com.skbank.service.impl.PassbookServiceImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/passbook/print"})
public class DigitalPassbookPrintServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PassbookService passbookService = new PassbookServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("AUTHENTICATED_USER") : null;

        Long customerIdParam = null;
        String custIdStr = request.getParameter("customerId");
        if (custIdStr != null && !custIdStr.trim().isEmpty()) {
            try { customerIdParam = Long.parseLong(custIdStr.trim()); } catch (NumberFormatException ignored) {}
        }

        try {
            DigitalPassbookDTO passbook;
            if (user != null) {
                passbook = passbookService.getPassbookByUserId(user.getId());
            } else if (customerIdParam != null) {
                passbook = passbookService.getPassbookByCustomerId(customerIdParam);
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            request.setAttribute("passbook", passbook);
            request.getRequestDispatcher("/WEB-INF/views/customer/passbook-print.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Could not load passbook for print: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }
}
