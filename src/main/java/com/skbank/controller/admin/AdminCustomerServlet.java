package com.skbank.controller.admin;

import com.skbank.dao.CustomerDAO;
import com.skbank.dao.UserDAO;
import com.skbank.model.Customer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/customers")
public class AdminCustomerServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String searchQuery = request.getParameter("search");
        try {
            List<Customer> customers;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                customers = customerDAO.searchCustomers(searchQuery.trim());
            } else {
                customers = customerDAO.findAll();
            }
            request.setAttribute("customers", customers);
            request.setAttribute("searchQuery", searchQuery);
            request.getRequestDispatcher("/admin/customers.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading customer list: " + e.getMessage());
            request.getRequestDispatcher("/admin/customers.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String userIdStr = request.getParameter("userId");

        try {
            int userId = Integer.parseInt(userIdStr);
            if ("block".equalsIgnoreCase(action)) {
                userDAO.updateStatus(userId, "BLOCKED");
                request.setAttribute("successMessage", "Customer account blocked.");
            } else if ("unblock".equalsIgnoreCase(action)) {
                userDAO.updateStatus(userId, "ACTIVE");
                request.setAttribute("successMessage", "Customer account unblocked.");
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Action Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
