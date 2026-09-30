package com.skbank.controller;

import com.skbank.dao.AccountTypeDAO;
import com.skbank.dao.BranchDAO;
import com.skbank.model.Account;
import com.skbank.model.AccountType;
import com.skbank.model.Branch;
import com.skbank.model.Card;
import com.skbank.model.Customer;
import com.skbank.model.User;
import com.skbank.service.AccountService;
import com.skbank.service.AuthenticationService;
import com.skbank.service.CardService;
import com.skbank.util.FileUploadUtil;
import com.skbank.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(urlPatterns = {"/register", "/customer/register"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 5 * 1024 * 1024,         // 5 MB
    maxRequestSize = 6 * 1024 * 1024       // 6 MB
)
public class RegisterServlet extends HttpServlet {

    private final AuthenticationService authService = new AuthenticationService();
    private final AccountService accountService = new AccountService();
    private final CardService cardService = new CardService();
    private final AccountTypeDAO accountTypeDAO = new AccountTypeDAO();
    private final BranchDAO branchDAO = new BranchDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadFormData(request);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadFormData(request);

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String dobStr = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String mobile = request.getParameter("mobile");
        String email = request.getParameter("email");
        String aadhaar = request.getParameter("aadhaar");
        String pan = request.getParameter("pan");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String state = request.getParameter("state");
        String pincode = request.getParameter("pincode");
        String occupation = request.getParameter("occupation");
        String typeIdStr = request.getParameter("accountTypeId");
        String branchIdStr = request.getParameter("branchId");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Process Optional Profile Photo
        String photoPath = "assets/images/default-avatar.png";
        try {
            Part photoPart = request.getPart("profilePhoto");
            if (photoPart != null && photoPart.getSize() > 0) {
                String realPath = getServletContext().getRealPath("/uploads/profile");
                if (realPath == null) {
                    realPath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "profile";
                }
                photoPath = FileUploadUtil.processProfilePhoto(photoPart, realPath);
            }
        } catch (Exception ignored) {}

        // Validations
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || mobile == null) {
            request.setAttribute("errorMessage", "All required fields must be filled.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isStrongPassword(password)) {
            request.setAttribute("errorMessage", "Password must be at least 8 characters long and contain letters, numbers, and special characters.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("errorMessage", "Invalid email address format.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidMobile(mobile)) {
            request.setAttribute("errorMessage", "Invalid 10-digit mobile number.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        try {
            int accountTypeId = (typeIdStr != null && !typeIdStr.isEmpty()) ? Integer.parseInt(typeIdStr) : 1;
            int branchId = (branchIdStr != null && !branchIdStr.isEmpty()) ? Integer.parseInt(branchIdStr) : 1;

            User user = new User();
            user.setUsername(username.trim());
            user.setPasswordHash(password);

            Customer customer = new Customer();
            customer.setFirstName(firstName != null ? firstName.trim() : "");
            customer.setLastName(lastName != null ? lastName.trim() : "");
            customer.setDob(Date.valueOf(dobStr));
            customer.setGender(gender);
            customer.setMobile(mobile.trim());
            customer.setEmail(email.trim());
            customer.setAadhaar(aadhaar != null ? aadhaar.trim() : "");
            customer.setPan(pan != null ? pan.trim().toUpperCase() : "");
            customer.setAddress(address != null ? address.trim() : "");
            customer.setCity(city != null ? city.trim() : "");
            customer.setState(state != null ? state.trim() : "");
            customer.setPincode(pincode != null ? pincode.trim() : "");
            customer.setOccupation(occupation != null ? occupation.trim() : "");
            customer.setProfilePhoto(photoPath);

            Customer registered = authService.registerCustomer(user, customer);

            // Fetch created default account & virtual debit card
            List<Account> accounts = accountService.getCustomerAccounts(registered.getCustomerId());
            Account defaultAccount = !accounts.isEmpty() ? accounts.get(0) : null;

            List<Card> cards = cardService.getCustomerCards(registered.getCustomerId());
            Card defaultCard = !cards.isEmpty() ? cards.get(0) : null;

            request.setAttribute("successMessage", "Account Successfully Opened! Your Initial Balance is ₹0.00.");
            request.setAttribute("registeredCustomer", registered);
            request.setAttribute("registeredAccount", defaultAccount);
            request.setAttribute("registeredCard", defaultCard);
            request.setAttribute("registeredUsername", username.trim());

            request.getRequestDispatcher("/register.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }

    private void loadFormData(HttpServletRequest request) {
        try {
            List<AccountType> accountTypes = accountTypeDAO.findAllActive();
            List<Branch> branches = branchDAO.findAllActive();
            request.setAttribute("accountTypes", accountTypes);
            request.setAttribute("branches", branches);
        } catch (Exception e) {
            System.err.println("Error loading registration form data: " + e.getMessage());
        }
    }
}
