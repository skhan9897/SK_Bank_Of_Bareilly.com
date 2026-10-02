package com.skbank.controller.admin;

import com.skbank.model.Admin;
import com.skbank.service.AuthService;
import com.skbank.service.impl.AuthServiceImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/admin/profile"})
public class AdminProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute("USER_ID");

        try {
            Admin admin = authService.getAdminByUserId(userId);
            request.setAttribute("admin", admin);
            request.getRequestDispatcher("/WEB-INF/views/admin/profile.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading profile: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/profile.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute("USER_ID");

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "New password and confirm password do not match.");
            request.getRequestDispatcher("/WEB-INF/views/admin/profile.jsp").forward(request, response);
            return;
        }

        try {
            authService.changePassword(userId, currentPassword, newPassword);
            response.sendRedirect(request.getContextPath() + "/admin/profile?msg=Admin password changed successfully.");
        } catch (Exception e) {
            try {
                request.setAttribute("admin", authService.getAdminByUserId(userId));
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "Failed to change password: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/profile.jsp").forward(request, response);
        }
    }
}
