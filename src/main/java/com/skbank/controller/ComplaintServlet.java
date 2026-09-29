package com.skbank.controller;

import com.skbank.model.Complaint;
import com.skbank.model.Customer;
import com.skbank.service.ComplaintService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/complaints")
public class ComplaintServlet extends HttpServlet {

    private final ComplaintService complaintService = new ComplaintService();

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
            List<Complaint> complaints = complaintService.getCustomerComplaints(customer.getCustomerId());
            request.setAttribute("complaints", complaints);
            request.getRequestDispatcher("/complaints.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading complaints: " + e.getMessage());
            request.getRequestDispatcher("/complaints.jsp").forward(request, response);
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

        String subject = request.getParameter("subject");
        String category = request.getParameter("category");
        String description = request.getParameter("description");

        try {
            Complaint c = new Complaint();
            c.setCustomerId(customer.getCustomerId());
            c.setSubject(subject);
            c.setCategory(category);
            c.setDescription(description);

            boolean submitted = complaintService.submitComplaint(c);
            if (submitted) {
                request.setAttribute("successMessage", "Complaint Registered Successfully! Complaint ID: " + c.getComplaintId());
            } else {
                request.setAttribute("errorMessage", "Failed to register complaint.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
