package com.skbank.controller;

import com.skbank.dao.CustomerDAO;
import com.skbank.model.Customer;
import com.skbank.model.User;
import com.skbank.service.AuthenticationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthenticationService authService = new AuthenticationService();
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedInUser") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please enter both Customer ID/Username and Password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            User user = authService.authenticate(username.trim(), password);
            if (user != null) {
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    request.setAttribute("errorMessage", "Admin account detected. Please use the Admin Login Portal.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    return;
                }

                Customer customer = customerDAO.findByUserId(user.getUserId());
                HttpSession session = request.getSession(true);
                session.setAttribute("loggedInUser", user);
                session.setAttribute("customerProfile", customer);
                session.setMaxInactiveInterval(1800); // 30 minutes session timeout

                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                request.setAttribute("errorMessage", "Invalid Username/Customer ID or Password.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (IllegalStateException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "An error occurred during authentication: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
