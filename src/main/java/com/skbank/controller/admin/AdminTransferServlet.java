package com.skbank.controller.admin;

import com.skbank.model.Transaction;
import com.skbank.service.ReportService;
import com.skbank.service.impl.ReportServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/admin/transfers"})
public class AdminTransferServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ReportService reportService = new ReportServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int page = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
            }
            int pageSize = 15;

            List<Transaction> transfers = reportService.getAdminTransactionsReport(null, "TRANSFER", null, null, page, pageSize);
            long totalRecords = reportService.countAdminTransactionsReport(null, "TRANSFER", null, null);
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            request.setAttribute("transfers", transfers);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/WEB-INF/views/admin/transfers.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading transfers: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/transfers.jsp").forward(request, response);
        }
    }
}
