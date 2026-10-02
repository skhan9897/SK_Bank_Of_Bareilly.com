package com.skbank.controller.admin;

import com.skbank.model.Transaction;
import com.skbank.service.ReportService;
import com.skbank.service.impl.ReportServiceImpl;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/admin/reports"})
public class AdminReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ReportService reportService = new ReportServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String reportType = request.getParameter("type");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            Date startDate = (startDateStr != null && !startDateStr.trim().isEmpty()) ? Date.valueOf(startDateStr) : new Date(System.currentTimeMillis() - 30L * 24 * 3600 * 1000);
            Date endDate = (endDateStr != null && !endDateStr.trim().isEmpty()) ? Date.valueOf(endDateStr) : new Date(System.currentTimeMillis());

            int page = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
            }
            int pageSize = 20;

            List<Transaction> records = reportService.getAdminTransactionsReport(null, reportType, startDate, endDate, page, pageSize);
            long totalRecords = reportService.countAdminTransactionsReport(null, reportType, startDate, endDate);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            request.setAttribute("records", records);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("reportType", reportType);
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);

            request.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error generating report: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/reports.jsp").forward(request, response);
        }
    }
}
