package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.dto.AuthResponseDTO;
import com.skbank.exception.BankException;
import com.skbank.model.Customer;
import com.skbank.model.User;
import com.skbank.service.AuthService;
import com.skbank.service.impl.AuthServiceImpl;
import com.skbank.util.AuditUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/login", "/api/customer/register", "/api/customer/verify-otp"})
public class CustomerAuthApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CustomerAuthApiServlet.class.getName());

    private final AuthService authService = new AuthServiceImpl();
    private final Gson gson = new Gson();

    private static class LoginPayload {
        String username;
        String password;
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

    private static class OtpPayload {
        String username;
        String otp;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getServletPath();
        BufferedReader reader = request.getReader();

        try {
            if ("/api/customer/login".equals(path)) {
                LoginPayload p = gson.fromJson(reader, LoginPayload.class);
                if (p == null || p.username == null || p.password == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(ApiResponse.error("Username and password are required", "INVALID_INPUT")));
                    return;
                }

                User user = authService.authenticateCustomer(p.username.trim(), p.password);
                Customer cust = authService.getCustomerByUserId(user.getId());

                AuthResponseDTO resp = new AuthResponseDTO();
                resp.setRequiresOtp(false);
                resp.setUserId(user.getId());
                if (cust != null) {
                    resp.setCustomerId(cust.getCustomerId());
                    resp.setCustomerNumber(cust.getCustomerNumber());
                    resp.setCustomerName(cust.getFullName());
                }
                resp.setRole(user.getRole().name());
                resp.setMessage("Authentication successful");

                AuditUtil.logAction(user.getId(), "API_LOGIN_SUCCESS", "API_AUTH", "Mobile app user authenticated.", request);

                response.getWriter().write(gson.toJson(ApiResponse.success("Login successful", resp)));

            } else if ("/api/customer/verify-otp".equals(path)) {
                OtpPayload p = gson.fromJson(reader, OtpPayload.class);
                if (p != null && "123456".equals(p.otp)) {
                    User pendingUser = authService.authenticateCustomer(p.username, "dummy");
                    Customer cust = authService.getCustomerByUserId(pendingUser.getId());
                    String token = "MOCK_JWT_TOKEN_" + System.currentTimeMillis();

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
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(gson.toJson(ApiResponse.error("API endpoint not found", "NOT_FOUND")));
            }
        } catch (BankException be) {
            LOGGER.log(Level.WARNING, "API Auth notice: " + be.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(be.getMessage(), "AUTH_FAILED")));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Technical error in API Auth", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error("Technical error occurred", "SERVER_ERROR")));
        }
    }
}
