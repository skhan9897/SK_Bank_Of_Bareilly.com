package com.skbank.controller.admin;

import com.skbank.model.KycDocument;
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
            List<KycDocument> pendingDocs = kycService.getAllPendingKyc();
            request.setAttribute("pendingKyc", pendingDocs);
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
        String kycIdStr = request.getParameter("kycId");
        String customerId = request.getParameter("customerId");
        String reason = request.getParameter("reason");

        try {
            int kycId = Integer.parseInt(kycIdStr);
            if ("approve".equalsIgnoreCase(action)) {
                kycService.updateKycStatus(kycId, customerId, "VERIFIED", null);
                request.setAttribute("successMessage", "KYC Approved.");
            } else if ("reject".equalsIgnoreCase(action)) {
                kycService.updateKycStatus(kycId, customerId, "REJECTED", reason);
                request.setAttribute("successMessage", "KYC Rejected.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
