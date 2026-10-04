package com.skbank.controller;

import com.skbank.exception.BankException;
import com.skbank.model.Customer;
import com.skbank.service.AccountService;
import com.skbank.service.AuthService;
import com.skbank.service.impl.AccountServiceImpl;
import com.skbank.service.impl.AuthServiceImpl;
import com.skbank.util.AuditUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(urlPatterns = {"/register"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class CustomerRegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CustomerRegisterServlet.class.getName());

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
            LOGGER.log(Level.WARNING, "Error loading registration form", e);
            request.setAttribute("errorMessage", "Error loading registration form. Please refresh page.");
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
                cust.setDateOfBirth(parseFlexibleDate(dobStr.trim()));
            } else {
                cust.setDateOfBirth(Date.valueOf("1995-01-01"));
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

            // Optional Profile Image Upload
            try {
                Part filePart = request.getPart("profileImage");
                if (filePart != null && filePart.getSize() > 0) {
                    if (filePart.getSize() > 5 * 1024 * 1024) {
                        throw new BankException("Profile image size exceeds 5 MB limit.");
                    }
                    String contentType = filePart.getContentType();
                    if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/jpg") || contentType.equals("image/png"))) {
                        String ext = contentType.endsWith("png") ? ".png" : ".jpg";
                        String filename = "cust_" + UUID.randomUUID().toString() + ext;
                        String uploadDir = getServletContext().getRealPath("/") + "uploads/profile";
                        File dir = new File(uploadDir);
                        if (!dir.exists()) dir.mkdirs();
                        filePart.write(uploadDir + File.separator + filename);
                        cust.setProfileImage("uploads/profile/" + filename);
                    }
                }
            } catch (BankException be) {
                throw be;
            } catch (Exception ignored) {}

            String username = request.getParameter("username");
            if (username == null || username.trim().isEmpty()) {
                username = "AUTO";
            }

            String plainPassword = request.getParameter("password");

            String branchStr = request.getParameter("branchId");
            Long branchId = 1L;
            if (branchStr != null && !branchStr.trim().isEmpty()) {
                try { branchId = Long.parseLong(branchStr.trim()); } catch (NumberFormatException ignored) {}
            }

            String accountTypeStr = request.getParameter("accountTypeId");
            Long accountTypeId = 1L;
            if (accountTypeStr != null && !accountTypeStr.trim().isEmpty()) {
                try { accountTypeId = Long.parseLong(accountTypeStr.trim()); } catch (NumberFormatException ignored) {}
            }

            Customer created = authService.registerCustomer(cust, username, plainPassword, branchId, accountTypeId);

            AuditUtil.logAction(created.getUserId(), "CUSTOMER_REGISTRATION", "AUTH", "New customer registered: " + created.getCustomerNumber(), request);

            response.sendRedirect(request.getContextPath() + "/login?msg=Account created successfully! Your Customer ID is " + created.getCustomerNumber() + ". Please log in.");
        } catch (BankException be) {
            LOGGER.log(Level.INFO, "Registration validation notice: " + be.getMessage());
            reloadFormAndShowError(request, response, be.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Technical exception during registration", e);
            reloadFormAndShowError(request, response, "Registration is temporarily unavailable. Please try again later.");
        }
    }

    private void reloadFormAndShowError(HttpServletRequest request, HttpServletResponse response, String userMsg)
            throws ServletException, IOException {
        try {
            request.setAttribute("branches", accountService.getAllActiveBranches());
            request.setAttribute("accountTypes", accountService.getAllActiveAccountTypes());
        } catch (Exception ignored) {}

        request.setAttribute("errorMessage", userMsg);
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
    }

    private Date parseFlexibleDate(String dobStr) {
        String[] formats = new String[]{"yyyy-MM-dd", "dd-MM-yyyy", "dd/MM/yyyy", "yyyy/MM/dd"};
        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                java.util.Date parsed = sdf.parse(dobStr);
                return new Date(parsed.getTime());
            } catch (Exception ignored) {}
        }
        try {
            return Date.valueOf(dobStr);
        } catch (Exception e) {
            return Date.valueOf("1995-01-01");
        }
    }
}
