package com.skbank.controller.admin;

import com.skbank.model.Loan;
import com.skbank.model.LoanPayment;
import com.skbank.service.AdminService;
import com.skbank.service.LoanService;
import com.skbank.service.impl.AdminServiceImpl;
import com.skbank.service.impl.LoanServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/admin/loans", "/admin/loan-details"})
public class AdminLoanServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminServiceImpl();
    private final LoanService loanService = new LoanServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long adminUserId = (Long) session.getAttribute("USER_ID");

        String path = request.getServletPath();
        String action = request.getParameter("action");

        try {
            if ("approve".equalsIgnoreCase(action)) {
                Long loanId = Long.parseLong(request.getParameter("id"));
                adminService.approveLoan(loanId, adminUserId);
                response.sendRedirect(request.getContextPath() + "/admin/loans?msg=Loan approved successfully.");
                return;
            } else if ("reject".equalsIgnoreCase(action)) {
                Long loanId = Long.parseLong(request.getParameter("id"));
                adminService.rejectLoan(loanId, adminUserId);
                response.sendRedirect(request.getContextPath() + "/admin/loans?msg=Loan application rejected.");
                return;
            }

            if ("/admin/loan-details".equals(path)) {
                Long loanId = Long.parseLong(request.getParameter("id"));
                Loan loan = loanService.getLoanById(loanId);
                List<LoanPayment> payments = loanService.getLoanPayments(loanId);

                request.setAttribute("loan", loan);
                request.setAttribute("payments", payments);
                request.getRequestDispatcher("/WEB-INF/views/admin/loan-details.jsp").forward(request, response);
            } else {
                String statusFilter = request.getParameter("status");
                int page = 1;
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.trim().isEmpty()) {
                    try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
                }
                int pageSize = 10;

                List<Loan> loans = adminService.getAllLoans(page, pageSize, statusFilter);
                long totalRecords = adminService.countLoans(statusFilter);
                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

                request.setAttribute("loans", loans);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("statusFilter", statusFilter);

                request.getRequestDispatcher("/WEB-INF/views/admin/loans.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error processing loan request: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/loans.jsp").forward(request, response);
        }
    }
}
