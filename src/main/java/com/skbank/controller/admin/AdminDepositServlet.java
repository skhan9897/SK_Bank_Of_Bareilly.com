package com.skbank.controller.admin;

import com.skbank.model.Transaction;
import com.skbank.service.AdminService;
import com.skbank.service.impl.AdminServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/admin/deposits"})
public class AdminDepositServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/deposits.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long adminUserId = (Long) session.getAttribute("USER_ID");

        try {
            Long accountId = Long.parseLong(request.getParameter("accountId"));
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            String remarks = request.getParameter("remarks");

            Transaction txn = adminService.processAdminDeposit(accountId, amount, remarks, adminUserId);

            response.sendRedirect(request.getContextPath() + "/admin/deposits?msg=Deposit of ₹" + amount + " successful. Ref: " + txn.getTransactionReference());
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Deposit failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/deposits.jsp").forward(request, response);
        }
    }
}
