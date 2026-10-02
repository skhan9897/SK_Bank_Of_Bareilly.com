package com.skbank.controller.admin;

import com.skbank.model.Complaint;
import com.skbank.model.ComplaintMessage;
import com.skbank.service.AdminService;
import com.skbank.service.ComplaintService;
import com.skbank.service.impl.AdminServiceImpl;
import com.skbank.service.impl.ComplaintServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/admin/complaints"})
public class AdminComplaintServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminServiceImpl();
    private final ComplaintService complaintService = new ComplaintServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");

        try {
            if (idStr != null && !idStr.trim().isEmpty()) {
                Long complaintId = Long.parseLong(idStr);
                Complaint complaint = complaintService.getComplaintById(complaintId);
                List<ComplaintMessage> messages = complaintService.getComplaintMessages(complaintId);

                request.setAttribute("complaint", complaint);
                request.setAttribute("messages", messages);
                request.getRequestDispatcher("/WEB-INF/views/admin/complaint-details.jsp").forward(request, response);
            } else {
                String statusFilter = request.getParameter("status");
                int page = 1;
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.trim().isEmpty()) {
                    try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
                }
                int pageSize = 10;

                List<Complaint> complaints = adminService.getAllComplaints(page, pageSize, statusFilter);
                long totalRecords = adminService.countComplaints(statusFilter);
                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

                request.setAttribute("complaints", complaints);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("statusFilter", statusFilter);

                request.getRequestDispatcher("/WEB-INF/views/admin/complaints.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading complaints: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/complaints.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long adminUserId = (Long) session.getAttribute("USER_ID");

        String action = request.getParameter("action");

        try {
            if ("reply".equalsIgnoreCase(action)) {
                Long complaintId = Long.parseLong(request.getParameter("complaintId"));
                String message = request.getParameter("message");

                complaintService.addMessage(complaintId, adminUserId, message);
                response.sendRedirect(request.getContextPath() + "/admin/complaints?id=" + complaintId + "&msg=Reply sent.");
            } else if ("status".equalsIgnoreCase(action)) {
                Long complaintId = Long.parseLong(request.getParameter("complaintId"));
                String status = request.getParameter("status");

                complaintService.updateStatus(complaintId, status);
                response.sendRedirect(request.getContextPath() + "/admin/complaints?id=" + complaintId + "&msg=Complaint status updated to " + status);
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/complaints?error=" + e.getMessage());
        }
    }
}
