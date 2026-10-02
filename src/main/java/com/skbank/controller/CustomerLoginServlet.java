package com.skbank.controller;

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

@WebServlet(urlPatterns = {"/login"})
public class CustomerLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("AUTHENTICATED_USER") != null) {
            response.sendRedirect(request.getContextPath() + "/customer/dashboard");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = authService.authenticateCustomer(username, password);

            HttpSession session = request.getSession(true);
            session.setAttribute("PENDING_OTP_USER", user);

            String devOtp = OtpUtil.generateOtp(session, "CUSTOMER_LOGIN");

            AuditUtil.logAction(user.getId(), "LOGIN_STEP1_SUCCESS", "AUTH", "Customer username and password verified.", request);

            response.sendRedirect(request.getContextPath() + "/verify-otp?purpose=CUSTOMER_LOGIN");
        } catch (Exception e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }
}
