package com.skbank.controller.admin;

import com.skbank.dao.AccountDAO;
import com.skbank.dao.CustomerDAO;
import com.skbank.dao.impl.AccountDAOImpl;
import com.skbank.dao.impl.CustomerDAOImpl;
import com.skbank.exception.BankException;
import com.skbank.model.Account;
import com.skbank.model.Customer;
import com.skbank.model.Transaction;
import com.skbank.service.AdminService;
import com.skbank.service.impl.AdminServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/admin/withdrawals"})
public class AdminWithdrawalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminServiceImpl();
    private final AccountDAO accountDAO = new AccountDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Account> accounts = adminService.getAllAccounts(1, 200, null);
            for (Account acc : accounts) {
                if (acc != null && acc.getCustomerId() != null) {
                    try {
                        Customer cust = customerDAO.findById(acc.getCustomerId());
                        if (cust != null) acc.setCustomerName(cust.getFullName());
                    } catch (Exception ignored) {}
                }
            }
            request.setAttribute("accounts", accounts);
        } catch (Exception ignored) {}

        request.getRequestDispatcher("/WEB-INF/views/admin/withdrawals.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long adminUserId = (Long) session.getAttribute("USER_ID");

        String identifier = request.getParameter("accountIdentifier");
        if (identifier == null || identifier.trim().isEmpty()) {
            identifier = request.getParameter("accountId");
        }

        try {
            Account targetAccount = resolveAccount(identifier);
            BigDecimal amount = new BigDecimal(request.getParameter("amount"));
            String reason = request.getParameter("reason");

            Transaction txn = adminService.processAdminWithdrawal(targetAccount.getAccountId(), amount, reason, adminUserId);

            response.sendRedirect(request.getContextPath() + "/admin/withdrawals?msg=Withdrawal of ₹" + amount + " from account " + targetAccount.getAccountNumber() + " processed. Ref: " + txn.getTransactionReference());
        } catch (Exception e) {
            try {
                List<Account> accounts = adminService.getAllAccounts(1, 200, null);
                request.setAttribute("accounts", accounts);
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "Withdrawal failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/withdrawals.jsp").forward(request, response);
        }
    }

    private Account resolveAccount(String identifier) throws BankException, SQLException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new BankException("Please select or enter an Account Number, Mobile Number, or Customer ID.");
        }
        String query = identifier.trim();

        // 1. Try Account Number
        Account acc = accountDAO.findByAccountNumber(query);
        if (acc != null) return acc;

        // 2. Try Mobile Number
        Customer cust = customerDAO.findByMobile(query);
        if (cust != null) {
            List<Account> accounts = accountDAO.findByCustomerId(cust.getCustomerId());
            if (!accounts.isEmpty()) return accounts.get(0);
        }

        // 3. Try Customer Number
        cust = customerDAO.findByCustomerNumber(query);
        if (cust != null) {
            List<Account> accounts = accountDAO.findByCustomerId(cust.getCustomerId());
            if (!accounts.isEmpty()) return accounts.get(0);
        }

        // 4. Try numeric Account ID
        try {
            Long accId = Long.parseLong(query);
            acc = accountDAO.findById(accId);
            if (acc != null) return acc;
        } catch (NumberFormatException ignored) {}

        throw new BankException("No active account found for '" + query + "'. Please verify Account Number, Mobile, or Customer Number.");
    }
}
