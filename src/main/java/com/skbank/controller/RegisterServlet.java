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
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/register", "/customer/register"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 5 * 1024 * 1024,         // 5 MB
    maxRequestSize = 6 * 1024 * 1024       // 6 MB
)
public class RegisterServlet extends HttpServlet {

    private static final String VIEW_PATH = "/WEB-INF/views/customer/register.jsp";

    private final AuthenticationService authService = new AuthenticationService();
    private final AccountService accountService = new AccountService();
    private final CardService cardService = new CardService();
    private final AccountTypeDAO accountTypeDAO = new AccountTypeDAO();
    private final BranchDAO branchDAO = new BranchDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            loadFormData(request);
            request.getRequestDispatcher(VIEW_PATH).forward(request, response);
        } catch (Throwable t) {
            System.err.println("CRITICAL ERROR in RegisterServlet.doGet: " + t.getMessage());
            t.printStackTrace();
            request.setAttribute("accountTypes", createFallbackAccountTypes());
            request.setAttribute("branches", createFallbackBranches());
            request.getRequestDispatcher(VIEW_PATH).forward(request, response);
        }
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
            request.getRequestDispatcher(VIEW_PATH).forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            request.getRequestDispatcher(VIEW_PATH).forward(request, response);
            return;
        }

        if (!ValidationUtil.isStrongPassword(password)) {
            request.setAttribute("errorMessage", "Password must be at least 8 characters long and contain letters, numbers, and special characters.");
            request.getRequestDispatcher(VIEW_PATH).forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("errorMessage", "Invalid email address format.");
            request.getRequestDispatcher(VIEW_PATH).forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidMobile(mobile)) {
            request.setAttribute("errorMessage", "Invalid 10-digit mobile number.");
            request.getRequestDispatcher(VIEW_PATH).forward(request, response);
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

            request.getRequestDispatcher(VIEW_PATH).forward(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher(VIEW_PATH).forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Registration failed: " + e.getMessage());
            request.getRequestDispatcher(VIEW_PATH).forward(request, response);
        }
    }

    private void loadFormData(HttpServletRequest request) {
        try {
            List<AccountType> accountTypes = accountTypeDAO.findAllActive();
            if (accountTypes == null || accountTypes.isEmpty()) {
                accountTypes = createFallbackAccountTypes();
            }

            List<Branch> branches = branchDAO.findAllActive();
            if (branches == null || branches.isEmpty()) {
                branches = createFallbackBranches();
            }

            request.setAttribute("accountTypes", accountTypes);
            request.setAttribute("branches", branches);

        } catch (Throwable t) {
            System.err.println("Error loading registration form data: " + t.getMessage());
            request.setAttribute("accountTypes", createFallbackAccountTypes());
            request.setAttribute("branches", createFallbackBranches());
        }
    }

    private List<AccountType> createFallbackAccountTypes() {
        List<AccountType> list = new ArrayList<>();
        
        AccountType at1 = new AccountType();
        at1.setTypeId(1);
        at1.setTypeCode("SAVINGS");
        at1.setTypeName("Savings Account");
        at1.setDescription("Easy banking for daily personal transactions");
        list.add(at1);

        AccountType at2 = new AccountType();
        at2.setTypeId(2);
        at2.setTypeCode("CURRENT");
        at2.setTypeName("Current Account");
        at2.setDescription("Suitable for commercial & business transactions");
        list.add(at2);

        AccountType at3 = new AccountType();
        at3.setTypeId(3);
        at3.setTypeCode("SALARY");
        at3.setTypeName("Salary Account");
        at3.setDescription("Zero-balance account for corporate employees");
        list.add(at3);

        AccountType at4 = new AccountType();
        at4.setTypeId(4);
        at4.setTypeCode("BASIC_SAVINGS");
        at4.setTypeName("Basic Savings Account");
        at4.setDescription("Basic zero-maintenance savings account");
        list.add(at4);

        AccountType at5 = new AccountType();
        at5.setTypeId(5);
        at5.setTypeCode("SENIOR_SAVINGS");
        at5.setTypeName("Senior Citizen Savings Account");
        at5.setDescription("High-yield savings account for senior citizens");
        list.add(at5);

        return list;
    }

    private List<Branch> createFallbackBranches() {
        List<Branch> list = new ArrayList<>();

        Branch b1 = new Branch();
        b1.setBranchId(1);
        b1.setBranchCode("SKB001");
        b1.setBranchName("SK Bank Bareilly Main Branch");
        b1.setCity("Bareilly");
        b1.setIfscCode("SKB0002401");
        list.add(b1);

        Branch b2 = new Branch();
        b2.setBranchId(2);
        b2.setBranchCode("SKB002");
        b2.setBranchName("SK Bank Bareilly City Branch");
        b2.setCity("Bareilly");
        b2.setIfscCode("SKB0002402");
        list.add(b2);

        return list;
    }
}
