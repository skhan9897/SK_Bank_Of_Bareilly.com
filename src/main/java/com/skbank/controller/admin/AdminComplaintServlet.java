package com.skbank.controller.admin;

import com.skbank.model.Complaint;
import com.skbank.service.ComplaintService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/complaints")
public class AdminComplaintServlet extends HttpServlet {

    private final ComplaintService complaintService = new ComplaintService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Complaint> complaints = complaintService.getAllComplaints();
            request.setAttribute("complaints", complaints);
            request.getRequestDispatcher("/admin/complaints.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading complaints: " + e.getMessage());
            request.getRequestDispatcher("/admin/complaints.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String complaintId = request.getParameter("complaintId");
        String status = request.getParameter("status");

        try {
            complaintService.updateComplaintStatus(complaintId, status);
            request.setAttribute("successMessage", "Complaint Status Updated.");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
