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

@WebServlet(urlPatterns = {"/admin/transactions"})
public class AdminTransactionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ReportService reportService = new ReportServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String search = request.getParameter("search");
            String typeFilter = request.getParameter("type");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            Date startDate = (startDateStr != null && !startDateStr.trim().isEmpty()) ? Date.valueOf(startDateStr) : null;
            Date endDate = (endDateStr != null && !endDateStr.trim().isEmpty()) ? Date.valueOf(endDateStr) : null;

            int page = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
            }
            int pageSize = 15;

            List<Transaction> transactions = reportService.getAdminTransactionsReport(search, typeFilter, startDate, endDate, page, pageSize);
            long totalRecords = reportService.countAdminTransactionsReport(search, typeFilter, startDate, endDate);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            request.setAttribute("transactions", transactions);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("search", search);
            request.setAttribute("typeFilter", typeFilter);
            request.setAttribute("startDate", startDateStr);
            request.setAttribute("endDate", endDateStr);

            request.getRequestDispatcher("/WEB-INF/views/admin/transactions.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading transactions: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/transactions.jsp").forward(request, response);
        }
    }
}
