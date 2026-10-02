package com.skbank.controller;

import com.skbank.model.Customer;
import com.skbank.service.AccountService;
import com.skbank.service.AuthService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.AuthServiceImpl;
import com.skbank.util.AuditUtil;

import java.io.IOException;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/register"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class CustomerRegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("branches", accountService.getAllActiveBranches());
            request.setAttribute("accountTypes", accountService.getAllActiveAccountTypes());
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading registration form: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Customer cust = new Customer();
            cust.setFullName(request.getParameter("fullName"));
            String dobStr = request.getParameter("dateOfBirth");
            if (dobStr != null && !dobStr.trim().isEmpty()) {
                cust.setDateOfBirth(Date.valueOf(dobStr));
            }
            cust.setGender(request.getParameter("gender"));
            cust.setMobile(request.getParameter("mobile"));
            cust.setEmail(request.getParameter("email"));
            cust.setAddress(request.getParameter("address"));
            cust.setCity(request.getParameter("city"));
            cust.setState(request.getParameter("state"));
            cust.setPincode(request.getParameter("pincode"));
            String rawAadhaar = request.getParameter("aadhaarNumber");
            cust.setAadhaarNumber(rawAadhaar != null ? rawAadhaar.replaceAll("\\s+", "").trim() : "");

            String rawPan = request.getParameter("panNumber");
            cust.setPanNumber(rawPan != null ? rawPan.replaceAll("\\s+", "").toUpperCase().trim() : "");

            String username = request.getParameter("username");
            String plainPassword = request.getParameter("password");
            Long branchId = Long.parseLong(request.getParameter("branchId"));
            Long accountTypeId = Long.parseLong(request.getParameter("accountTypeId"));

            Customer created = authService.registerCustomer(cust, username, plainPassword, branchId, accountTypeId);

            AuditUtil.logAction(created.getUserId(), "CUSTOMER_REGISTRATION", "AUTH", "New customer registered: " + created.getCustomerNumber(), request);

            response.sendRedirect(request.getContextPath() + "/login?msg=Account created successfully! Your Customer ID is " + created.getCustomerNumber() + ". Please log in.");
        } catch (Exception e) {
            try {
                request.setAttribute("branches", accountService.getAllActiveBranches());
                request.setAttribute("accountTypes", accountService.getAllActiveAccountTypes());
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }
}
