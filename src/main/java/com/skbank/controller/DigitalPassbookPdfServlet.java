package com.skbank.controller;

import com.skbank.dto.DigitalPassbookDTO;
import com.skbank.service.PassbookService;
import com.skbank.service.impl.PassbookServiceImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/passbook/pdf"})
public class DigitalPassbookPdfServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final PassbookService passbookService = new PassbookServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("AUTHENTICATED_USER") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String custIdStr = request.getParameter("customerId");
        Long sessionCustId = (Long) session.getAttribute("CUSTOMER_ID");

        Long customerId = sessionCustId;
        if (custIdStr != null && !custIdStr.trim().isEmpty()) {
            try { customerId = Long.parseLong(custIdStr.trim()); } catch (NumberFormatException ignored) {}
        }

        try {
            DigitalPassbookDTO passbook = passbookService.getPassbookByCustomerId(customerId);
            request.setAttribute("passbook", passbook);
            request.getRequestDispatcher("/WEB-INF/views/customer/passbook-print.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error rendering passbook: " + e.getMessage());
        }
    }
}
