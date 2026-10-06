package com.skbank.controller.customer;

import com.skbank.model.Customer;
import com.skbank.model.Kyc;
import com.skbank.service.CustomerService;
import com.skbank.service.impl.CustomerServiceImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/kyc"})
public class KycServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CustomerService customerService = new CustomerServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            Customer customer = customerService.getCustomerById(customerId);
            Kyc kyc = customerService.getKycByCustomerId(customerId);

            request.setAttribute("customer", customer);
            request.setAttribute("kyc", kyc);
            request.getRequestDispatcher("/WEB-INF/views/customer/kyc.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading KYC details: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/kyc.jsp").forward(request, response);
        }
    }
}
