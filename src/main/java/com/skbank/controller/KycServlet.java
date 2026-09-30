package com.skbank.controller;

import com.skbank.dao.CustomerDAO;
import com.skbank.model.Customer;
import com.skbank.model.CustomerKyc;
import com.skbank.service.KycService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(urlPatterns = {"/kyc", "/customer/kyc"})
public class KycServlet extends HttpServlet {

    private final KycService kycService = new KycService();
    private final CustomerDAO customerDAO = new CustomerDAO();

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
            CustomerKyc kyc = kycService.getKycStatus(customer.getCustomerId());
            request.setAttribute("kyc", kyc);
            
            // Forward to views/customer/kyc.jsp
            request.getRequestDispatcher("/WEB-INF/views/customer/kyc.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Unable to load KYC details: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/kyc.jsp").forward(request, response);
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

        String aadhaarNumber = request.getParameter("aadhaarNumber");
        String panNumber = request.getParameter("panNumber");

        try {
            CustomerKyc kyc = kycService.submitKyc(customer.getCustomerId(), aadhaarNumber, panNumber);

            if ("VERIFIED".equalsIgnoreCase(kyc.getKycStatus())) {
                request.setAttribute("successMessage", "KYC Verification Completed Successfully!");
            } else {
                request.setAttribute("errorMessage", "KYC Verification Failed. " + (kyc.getRejectionReason() != null ? kyc.getRejectionReason() : "Please re-verify your details."));
            }

            // Refresh customer profile in session
            Customer updatedCustomer = customerDAO.findByCustomerId(customer.getCustomerId());
            if (updatedCustomer != null) {
                session.setAttribute("customerProfile", updatedCustomer);
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            request.setAttribute("errorMessage", "KYC Submission Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
