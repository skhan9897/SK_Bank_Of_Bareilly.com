package com.skbank.controller.admin;

import com.skbank.model.CustomerKyc;
import com.skbank.service.KycService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/kyc")
public class AdminKycServlet extends HttpServlet {

    private final KycService kycService = new KycService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<CustomerKyc> pendingKyc = kycService.getAllPendingKyc();
            List<CustomerKyc> allKyc = kycService.getAllKyc();
            request.setAttribute("pendingKyc", pendingKyc);
            request.setAttribute("allKyc", allKyc);
            request.getRequestDispatcher("/admin/kyc.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading KYC applications: " + e.getMessage());
            request.getRequestDispatcher("/admin/kyc.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String customerId = request.getParameter("customerId");
        String reason = request.getParameter("reason");

        try {
            if ("approve".equalsIgnoreCase(action)) {
                kycService.updateKycStatus(customerId, "VERIFIED", null);
                request.setAttribute("successMessage", "KYC Approved for Customer " + customerId);
            } else if ("reject".equalsIgnoreCase(action)) {
                kycService.updateKycStatus(customerId, "REJECTED", reason != null ? reason : "Identity verification failed.");
                request.setAttribute("successMessage", "KYC Rejected for Customer " + customerId);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating KYC: " + e.getMessage());
        }

        doGet(request, response);
    }
}
