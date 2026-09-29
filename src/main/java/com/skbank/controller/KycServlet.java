package com.skbank.controller;

import com.skbank.model.Customer;
import com.skbank.model.KycDocument;
import com.skbank.service.KycService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/kyc")
public class KycServlet extends HttpServlet {

    private final KycService kycService = new KycService();

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
            List<KycDocument> docs = kycService.getCustomerKycDocuments(customer.getCustomerId());
            request.setAttribute("kycDocuments", docs);
            request.getRequestDispatcher("/kyc.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading KYC details: " + e.getMessage());
            request.getRequestDispatcher("/kyc.jsp").forward(request, response);
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

        String docType = request.getParameter("documentType");
        String filePath = request.getParameter("filePath");

        try {
            KycDocument doc = new KycDocument();
            doc.setCustomerId(customer.getCustomerId());
            doc.setDocumentType(docType);
            doc.setFilePath(filePath != null && !filePath.isEmpty() ? filePath : "uploads/kyc_sample.pdf");
            doc.setStatus("PENDING");

            boolean uploaded = kycService.uploadDocument(doc);
            if (uploaded) {
                request.setAttribute("successMessage", "KYC Document Uploaded successfully. Pending verification.");
            } else {
                request.setAttribute("errorMessage", "Failed to upload KYC document.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
