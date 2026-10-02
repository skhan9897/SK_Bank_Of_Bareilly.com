package com.skbank.controller.admin;

import com.skbank.dao.FixedDepositDAO;
import com.skbank.dao.impl.FixedDepositDAOImpl;
import com.skbank.model.FixedDeposit;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/admin/fixed-deposits"})
public class AdminFdServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final FixedDepositDAO fdDAO = new FixedDepositDAOImpl();

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

            List<FixedDeposit> fds = fdDAO.findAllAdmin((page - 1) * pageSize, pageSize);
            long totalRecords = fdDAO.countAllAdmin();
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

            request.setAttribute("fds", fds);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/WEB-INF/views/admin/fixed-deposits.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading fixed deposits: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/fixed-deposits.jsp").forward(request, response);
        }
    }
}
