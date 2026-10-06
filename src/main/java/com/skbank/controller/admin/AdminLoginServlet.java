package com.skbank.controller.admin;

import com.skbank.model.User;
import com.skbank.service.AuthService;
import com.skbank.service.impl.AuthServiceImpl;
import com.skbank.util.AuditUtil;
import com.skbank.util.OtpUtil;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/admin/login"})
public class AdminLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("AUTHENTICATED_USER") != null && session.getAttribute("ADMIN_ROLE") != null) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Admin username and password are required");
            request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
            return;
        }

        try {
            User user = authService.authenticateAdmin(username.trim(), password);

            HttpSession session = request.getSession(true);
            session.setAttribute("PENDING_OTP_USER", user);

            String devOtp = OtpUtil.generateOtp(session, "ADMIN_LOGIN");

            AuditUtil.logAction(user.getId(), "ADMIN_LOGIN_STEP1_SUCCESS", "ADMIN_AUTH", "Admin credentials verified.", request);

            response.sendRedirect(request.getContextPath() + "/verify-otp?purpose=ADMIN_LOGIN");
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
        }
    }
}
