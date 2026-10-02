package com.skbank.controller;

import com.skbank.model.Admin;
import com.skbank.model.Customer;
import com.skbank.model.User;
import com.skbank.model.UserRole;
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

@WebServlet(urlPatterns = {"/verify-otp"})
public class OtpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("PENDING_OTP_USER") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String devOtp = OtpUtil.getActiveOtpForDev(session);
        request.setAttribute("devOtp", devOtp != null ? devOtp : "123456");
        request.getRequestDispatcher("/WEB-INF/views/auth/otp.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession oldSession = request.getSession(false);
        if (oldSession == null || oldSession.getAttribute("PENDING_OTP_USER") == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=Session expired.");
            return;
        }

        User pendingUser = (User) oldSession.getAttribute("PENDING_OTP_USER");
        String inputOtp = request.getParameter("otp");
        String purpose = request.getParameter("purpose");

        if (OtpUtil.verifyOtp(oldSession, inputOtp, purpose)) {
            // Session Fixation Prevention: invalidate old session and create fresh
            oldSession.removeAttribute("PENDING_OTP_USER");
            oldSession.invalidate();

            HttpSession newSession = request.getSession(true);
            newSession.setMaxInactiveInterval(30 * 60); // 30 mins session timeout
            newSession.setAttribute("AUTHENTICATED_USER", pendingUser);
            newSession.setAttribute("ROLE", pendingUser.getRole());
            newSession.setAttribute("USER_ID", pendingUser.getId());

            if (pendingUser.getRole() == UserRole.CUSTOMER) {
                try {
                    Customer customer = authService.getCustomerByUserId(pendingUser.getId());
                    if (customer != null) {
                        newSession.setAttribute("CUSTOMER_ID", customer.getCustomerId());
                        newSession.setAttribute("CUSTOMER_NAME", customer.getFullName());
                    }
                } catch (Exception ignored) {}

                AuditUtil.logAction(pendingUser.getId(), "LOGIN_SUCCESS", "AUTH", "Customer completed OTP login.", request);

                String redirectUrl = (String) newSession.getAttribute("REDIRECT_AFTER_LOGIN");
                if (redirectUrl != null) {
                    newSession.removeAttribute("REDIRECT_AFTER_LOGIN");
                    response.sendRedirect(redirectUrl);
                } else {
                    response.sendRedirect(request.getContextPath() + "/customer/dashboard");
                }
            } else if (pendingUser.getRole() == UserRole.ADMIN) {
                try {
                    Admin admin = authService.getAdminByUserId(pendingUser.getId());
                    if (admin != null) {
                        newSession.setAttribute("ADMIN_ID", admin.getAdminId());
                        newSession.setAttribute("ADMIN_ROLE", admin.getAdminRole());
                        newSession.setAttribute("ADMIN_NAME", admin.getFullName() != null ? admin.getFullName() : admin.getUsername());
                    }
                } catch (Exception ignored) {}

                AuditUtil.logAction(pendingUser.getId(), "ADMIN_LOGIN_SUCCESS", "AUTH", "Admin completed OTP login.", request);

                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            }
        } else {
            String devOtp = OtpUtil.getActiveOtpForDev(oldSession);
            request.setAttribute("devOtp", devOtp != null ? devOtp : "123456");
            request.setAttribute("errorMessage", "Invalid or expired OTP. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/auth/otp.jsp").forward(request, response);
        }
    }
}
