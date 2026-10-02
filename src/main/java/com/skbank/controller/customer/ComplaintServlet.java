package com.skbank.controller.customer;

import com.skbank.model.Complaint;
import com.skbank.model.ComplaintMessage;
import com.skbank.service.ComplaintService;
import com.skbank.service.impl.ComplaintServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/complaints"})
public class ComplaintServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ComplaintService complaintService = new ComplaintServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        String idStr = request.getParameter("id");

        try {
            if (idStr != null && !idStr.trim().isEmpty()) {
                Long complaintId = Long.parseLong(idStr);
                Complaint complaint = complaintService.getComplaintById(complaintId);
                if (!complaint.getCustomerId().equals(customerId)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                List<ComplaintMessage> messages = complaintService.getComplaintMessages(complaintId);

                request.setAttribute("complaint", complaint);
                request.setAttribute("messages", messages);
                request.getRequestDispatcher("/WEB-INF/views/customer/complaint-details.jsp").forward(request, response);
            } else {
                List<Complaint> complaints = complaintService.getCustomerComplaints(customerId);
                request.setAttribute("complaints", complaints);
                request.getRequestDispatcher("/WEB-INF/views/customer/complaints.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading complaints: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/complaints.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");
        Long userId = (Long) session.getAttribute("USER_ID");

        String action = request.getParameter("action");

        try {
            if ("reply".equalsIgnoreCase(action)) {
                Long complaintId = Long.parseLong(request.getParameter("complaintId"));
                String message = request.getParameter("message");

                complaintService.addMessage(complaintId, userId, message);
                response.sendRedirect(request.getContextPath() + "/customer/complaints?id=" + complaintId);
            } else {
                String subject = request.getParameter("subject");
                String description = request.getParameter("description");
                String priority = request.getParameter("priority");

                Complaint c = complaintService.createComplaint(customerId, subject, description, priority);

                response.sendRedirect(request.getContextPath() + "/customer/complaints?msg=Complaint Ticket #" + c.getComplaintId() + " created successfully.");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/customer/complaints?error=" + e.getMessage());
        }
    }
}
