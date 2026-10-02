package com.skbank.controller.admin;

import com.skbank.model.AuditLog;
import com.skbank.service.AdminService;
import com.skbank.service.impl.AdminServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/admin/audit-logs"})
public class AdminAuditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String module = request.getParameter("module");
            int page = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
            }
            int pageSize = 20;

            List<AuditLog> auditLogs = adminService.getAuditLogs(page, pageSize, module);
            long totalRecords = adminService.countAuditLogs(module);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            request.setAttribute("auditLogs", auditLogs);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("moduleFilter", module);

            request.getRequestDispatcher("/WEB-INF/views/admin/audit-logs.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading audit logs: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/audit-logs.jsp").forward(request, response);
        }
    }
}
