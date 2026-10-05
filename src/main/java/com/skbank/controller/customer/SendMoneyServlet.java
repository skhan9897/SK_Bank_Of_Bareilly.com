package com.skbank.controller.customer;

import com.skbank.model.Account;
import com.skbank.model.Beneficiary;
import com.skbank.service.AccountService;
import com.skbank.service.BeneficiaryService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.BeneficiaryServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/send-money"})
public class SendMoneyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AccountService accountService = new AccountServiceImpl();
    private final BeneficiaryService beneficiaryService = new BeneficiaryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String customerId = (String) session.getAttribute("CUSTOMER_ID");

        try {
            List<Account> accounts = accountService.getCustomerAccounts(customerId);
            List<Beneficiary> beneficiaries = beneficiaryService.getBeneficiaries(customerId);

            request.setAttribute("accounts", accounts);
            request.setAttribute("beneficiaries", beneficiaries);
            request.getRequestDispatcher("/WEB-INF/views/customer/send-money.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading transfer options: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/send-money.jsp").forward(request, response);
        }
    }
}
