package com.skbank.controller.admin;

import com.skbank.dao.BillPaymentDAO;
import com.skbank.dao.impl.BillPaymentDAOImpl;
import com.skbank.model.BillPayment;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/admin/bill-payments"})
public class AdminBillServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final BillPaymentDAO billDAO = new BillPaymentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int page = 1;
            String pageStr = request.getParameter("page");
            if (pageStr != null && !pageStr.trim().isEmpty()) {
                try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
            }
            int pageSize = 10;

            List<BillPayment> bills = billDAO.findAllAdmin((page - 1) * pageSize, pageSize);
            long totalRecords = billDAO.countAllAdmin();
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            request.setAttribute("bills", bills);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/WEB-INF/views/admin/bill-payments.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading bill payments: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/bill-payments.jsp").forward(request, response);
        }
    }
}
