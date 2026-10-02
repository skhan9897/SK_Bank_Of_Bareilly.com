package com.skbank.controller.customer;

import com.skbank.service.AuthService;
import com.skbank.service.impl.AuthServiceImpl;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/security"})
public class SecurityServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/customer/security.jsp").forward(request, response);
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
            request.getRequestDispatcher("/WEB-INF/views/customer/security.jsp").forward(request, response);
            return;
        }

        try {
            authService.changePassword(userId, currentPassword, newPassword);
            response.sendRedirect(request.getContextPath() + "/customer/security?msg=Password updated successfully.");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Password change failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/security.jsp").forward(request, response);
        }
    }
}
