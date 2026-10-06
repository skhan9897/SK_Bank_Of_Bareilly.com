package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.Complaint;
import com.skbank.service.ComplaintService;
import com.skbank.service.impl.ComplaintServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/complaints"})
public class ComplaintApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ComplaintService complaintService = new ComplaintServiceImpl();
    private final Gson gson = new Gson();

    private static class ComplaintPayload {
        String subject;
        String description;
        String priority;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            List<Complaint> complaints = complaintService.getCustomerComplaints(customerId);
            response.getWriter().write(gson.toJson(ApiResponse.success("Complaints loaded", complaints)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "COMPLAINT_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            BufferedReader reader = request.getReader();
            ComplaintPayload p = gson.fromJson(reader, ComplaintPayload.class);

            Complaint c = complaintService.createComplaint(customerId, p.subject, p.description, p.priority);

            response.getWriter().write(gson.toJson(ApiResponse.success("Complaint ticket created", c)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "COMPLAINT_FAILED")));
        }
    }
}
