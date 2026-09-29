package com.skbank.controller.admin;

import com.skbank.model.User;
import com.skbank.service.AuthenticationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {

    private final AuthenticationService authService = new AuthenticationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedInUser") != null) {
            User user = (User) session.getAttribute("loggedInUser");
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                return;
            }
        }
        request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Read form parameters from JSP form inputs ("username" and "password")
        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            username = request.getParameter("SKBOB9897");
        }

        String password = request.getParameter("password");
        if (password == null || password.trim().isEmpty()) {
            password = request.getParameter("Admin9897");
        }

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Please enter both Admin Username and Password.");
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
            return;
        }

        try {
            User user = authService.authenticate(username.trim(), password);
            if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
                HttpSession session = request.getSession(true);
                session.setAttribute("loggedInUser", user);
                session.setMaxInactiveInterval(3600); // 1 hour session for admin

                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                request.setAttribute("errorMessage", "Invalid Admin credentials or unauthorized role.");
                request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
            }
        } catch (IllegalStateException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Login Error: " + e.getMessage());
            request.getRequestDispatcher("/admin/login.jsp").forward(request, response);
        }
    }
}
