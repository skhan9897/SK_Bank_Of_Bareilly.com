package com.skbank.controller.customer;

import com.skbank.model.Account;
import com.skbank.service.AccountService;
import com.skbank.service.impl.AccountServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/cibil-score"})
public class CibilScoreServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            List<Account> accounts = accountService.getCustomerAccounts(customerId);
            
            // Calculate deterministic CIBIL Score based on customer ID for consistency
            int baseScore = 750 + (int)(customerId % 80);
            if (baseScore > 880) baseScore = 820;

            request.setAttribute("cibilScore", baseScore);
            request.setAttribute("accounts", accounts);
            request.getRequestDispatcher("/WEB-INF/views/customer/cibil-score.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading CIBIL score: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/cibil-score.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/customer/cibil-score?msg=CIBIL+Score+refreshed+successfully!");
    }
}
