package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.Complaint;
import com.skbank.model.ComplaintMessage;
import com.skbank.service.ComplaintService;
import com.skbank.service.impl.ComplaintServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/complaints", "/api/customer/complaint-details"})
public class ComplaintApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ComplaintService complaintService = new ComplaintServiceImpl();
    private final Gson gson = new Gson();

    private static class TicketPayload {
        String subject;
        String description;
        String priority;
    }

    private static class ReplyPayload {
        Long complaintId;
        String message;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = (String) request.getAttribute("API_CUSTOMER_ID");
        String path = request.getServletPath();

        try {
            if ("/api/customer/complaint-details".equals(path)) {
                Long complaintId = Long.parseLong(request.getParameter("id"));
                Complaint complaint = complaintService.getComplaintById(complaintId);
                if (!complaint.getCustomerId().equals(customerId)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(gson.toJson(ApiResponse.error("Access denied", "UNAUTHORIZED")));
                    return;
                }
                List<ComplaintMessage> messages = complaintService.getComplaintMessages(complaintId);

                Map<String, Object> data = new HashMap<>();
                data.put("complaint", complaint);
                data.put("messages", messages);

                response.getWriter().write(gson.toJson(ApiResponse.success("Ticket details", data)));
            } else {
                List<Complaint> complaints = complaintService.getCustomerComplaints(customerId);
                response.getWriter().write(gson.toJson(ApiResponse.success("Complaints loaded", complaints)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "COMPLAINT_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = (String) request.getAttribute("API_CUSTOMER_ID");
        Long userId = (Long) request.getAttribute("API_USER_ID");
        String action = request.getParameter("action");

        try {
            BufferedReader reader = request.getReader();

            if ("reply".equalsIgnoreCase(action)) {
                ReplyPayload p = gson.fromJson(reader, ReplyPayload.class);
                complaintService.addMessage(p.complaintId, userId, p.message);
                response.getWriter().write(gson.toJson(ApiResponse.success("Reply added", null)));
            } else {
                TicketPayload p = gson.fromJson(reader, TicketPayload.class);
                Complaint c = complaintService.createComplaint(customerId, p.subject, p.description, p.priority);
                response.getWriter().write(gson.toJson(ApiResponse.success("Complaint ticket created", c)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "ACTION_FAILED")));
        }
    }
}
