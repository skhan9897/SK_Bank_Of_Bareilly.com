package com.skbank.controller.admin;

import com.skbank.model.Account;
import com.skbank.model.Customer;
import com.skbank.service.AccountService;
import com.skbank.service.AdminService;
import com.skbank.service.CustomerService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.AdminServiceImpl;
import com.skbank.service.impl.CustomerServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/admin/customers", "/admin/customer-details"})
public class AdminCustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminServiceImpl();
    private final CustomerService customerService = new CustomerServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String action = request.getParameter("action");

        try {
            if ("status".equalsIgnoreCase(action)) {
                String customerId = request.getParameter("id");
                String newStatus = request.getParameter("status");
                adminService.setCustomerStatus(customerId, newStatus);
                response.sendRedirect(request.getContextPath() + "/admin/customers?msg=Customer status updated to " + newStatus);
                return;
            }

            if ("/admin/customer-details".equals(path)) {
                String customerId = request.getParameter("id");
                Customer customer = customerService.getCustomerById(customerId);
                List<Account> accounts = accountService.getCustomerAccounts(customerId);

                request.setAttribute("customer", customer);
                request.setAttribute("accounts", accounts);
                request.getRequestDispatcher("/WEB-INF/views/admin/customer-details.jsp").forward(request, response);
            } else {
                String search = request.getParameter("search");
                int page = 1;
                String pageStr = request.getParameter("page");
                if (pageStr != null && !pageStr.trim().isEmpty()) {
                    try { page = Integer.parseInt(pageStr); } catch (NumberFormatException ignored) {}
                }
                int pageSize = 10;

                List<Customer> customers = adminService.getAllCustomers(page, pageSize, search);
                long totalRecords = adminService.countCustomers(search);
                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

                request.setAttribute("customers", customers);
                request.setAttribute("currentPage", page);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("search", search);

                request.getRequestDispatcher("/WEB-INF/views/admin/customers.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading customer data: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/customers.jsp").forward(request, response);
        }
    }
}
