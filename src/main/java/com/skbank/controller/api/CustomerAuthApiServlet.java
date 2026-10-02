package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.dto.AuthResponseDTO;
import com.skbank.model.Customer;
import com.skbank.model.User;
import com.skbank.service.AccountService;
import com.skbank.service.AuthService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.AuthServiceImpl;
import com.skbank.util.AuditUtil;
import com.skbank.util.DatabaseConnection;
import com.skbank.util.OtpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {
    "/api/customer/auth/login",
    "/api/customer/auth/verify-otp",
    "/api/customer/auth/logout",
    "/api/customer/register"
})
public class CustomerAuthApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();
    private final Gson gson = new Gson();

    private static class LoginPayload {
        String username;
        String password;
    }

    private static class OtpPayload {
        String otp;
        String purpose;
    }

    private static class RegisterPayload {
        String fullName;
        String dateOfBirth;
        String gender;
        String mobile;
        String email;
        String address;
        String city;
        String state;
        String pincode;
        String aadhaarNumber;
        String panNumber;
        String username;
        String password;
        Long branchId;
        Long accountTypeId;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getServletPath();

        try {
            BufferedReader reader = request.getReader();

            if ("/api/customer/auth/login".equals(path)) {
                LoginPayload payload = gson.fromJson(reader, LoginPayload.class);
                User user = authService.authenticateCustomer(payload.username, payload.password);

                HttpSession session = request.getSession(true);
                session.setAttribute("PENDING_OTP_USER", user);

                String devOtp = OtpUtil.generateOtp(session, "API_CUSTOMER_LOGIN");

                AuthResponseDTO resp = new AuthResponseDTO();
                resp.setRequiresOtp(true);
                resp.setUserId(user.getId());
                resp.setMessage("OTP dispatched to registered mobile. Dev OTP: " + (devOtp != null ? devOtp : "123456"));

                response.getWriter().write(gson.toJson(ApiResponse.success("Credentials verified. Enter OTP.", resp)));

            } else if ("/api/customer/auth/verify-otp".equals(path)) {
                OtpPayload payload = gson.fromJson(reader, OtpPayload.class);
                HttpSession session = request.getSession(false);

                if (session == null || session.getAttribute("PENDING_OTP_USER") == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(gson.toJson(ApiResponse.error("Session expired or invalid login step", "SESSION_EXPIRED")));
                    return;
                }

                User pendingUser = (User) session.getAttribute("PENDING_OTP_USER");

                if (OtpUtil.verifyOtp(session, payload.otp, payload.purpose)) {
                    session.removeAttribute("PENDING_OTP_USER");

                    // Issue mobile auth token
                    String token = "SKM_" + UUID.randomUUID().toString().replaceAll("-", "") + System.currentTimeMillis();
                    saveUserToken(pendingUser.getId(), token);

                    Customer cust = authService.getCustomerByUserId(pendingUser.getId());

                    AuthResponseDTO resp = new AuthResponseDTO();
                    resp.setRequiresOtp(false);
                    resp.setToken(token);
                    resp.setUserId(pendingUser.getId());
                    if (cust != null) {
                        resp.setCustomerId(cust.getCustomerId());
                        resp.setCustomerNumber(cust.getCustomerNumber());
                        resp.setCustomerName(cust.getFullName());
                    }
                    resp.setRole(pendingUser.getRole().name());
                    resp.setMessage("Authentication successful");

                    AuditUtil.logAction(pendingUser.getId(), "API_LOGIN_SUCCESS", "API_AUTH", "Mobile app user authenticated.", request);

                    response.getWriter().write(gson.toJson(ApiResponse.success("Login successful", resp)));
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(gson.toJson(ApiResponse.error("Invalid or expired OTP", "OTP_INVALID")));
                }

            } else if ("/api/customer/register".equals(path)) {
                RegisterPayload p = gson.fromJson(reader, RegisterPayload.class);

                Customer cust = new Customer();
                cust.setFullName(p.fullName);
                if (p.dateOfBirth != null && !p.dateOfBirth.trim().isEmpty()) {
                    cust.setDateOfBirth(Date.valueOf(p.dateOfBirth));
                }
                cust.setGender(p.gender);
                cust.setMobile(p.mobile);
                cust.setEmail(p.email);
                cust.setAddress(p.address);
                cust.setCity(p.city);
                cust.setState(p.state);
                cust.setPincode(p.pincode);
                cust.setAadhaarNumber(p.aadhaarNumber);
                cust.setPanNumber(p.panNumber);

                Customer created = authService.registerCustomer(cust, p.username, p.password, p.branchId, p.accountTypeId);

                AuthResponseDTO resp = new AuthResponseDTO();
                resp.setCustomerId(created.getCustomerId());
                resp.setCustomerNumber(created.getCustomerNumber());
                resp.setCustomerName(created.getFullName());
                resp.setMessage("Account created successfully with initial balance ₹0.00");

                response.getWriter().write(gson.toJson(ApiResponse.success("Account created successfully", resp)));

            } else if ("/api/customer/auth/logout".equals(path)) {
                Long userId = (Long) request.getAttribute("API_USER_ID");
                if (userId != null) {
                    clearUserToken(userId);
                }
                response.getWriter().write(gson.toJson(ApiResponse.success("Logged out successfully", null)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "AUTH_FAILED")));
        }
    }

    private void saveUserToken(Long userId, String token) throws Exception {
        String sql = "UPDATE users SET auth_token = ?, token_expiry = DATE_ADD(NOW(), INTERVAL 30 DAY) WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    private void clearUserToken(Long userId) throws Exception {
        String sql = "UPDATE users SET auth_token = NULL, token_expiry = NULL WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }
}
