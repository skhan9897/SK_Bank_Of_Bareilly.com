package com.skbank.controller;

import com.skbank.dao.CustomerDAO;
import com.skbank.model.Customer;
import com.skbank.model.User;
import com.skbank.service.AuthenticationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AuthenticationService authService = new AuthenticationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            Customer customer = customerDAO.findByUserId(user.getUserId());
            session.setAttribute("customerProfile", customer);
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading profile: " + e.getMessage());
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("updateProfile".equalsIgnoreCase(action)) {
                Customer customer = customerDAO.findByUserId(user.getUserId());
                if (customer != null) {
                    customer.setFirstName(request.getParameter("firstName"));
                    customer.setLastName(request.getParameter("lastName"));
                    customer.setMobile(request.getParameter("mobile"));
                    customer.setEmail(request.getParameter("email"));
                    customer.setAddress(request.getParameter("address"));
                    customer.setCity(request.getParameter("city"));
                    customer.setState(request.getParameter("state"));
                    customer.setPincode(request.getParameter("pincode"));
                    customer.setOccupation(request.getParameter("occupation"));

                    customerDAO.updateProfile(customer);
                    session.setAttribute("customerProfile", customer);
                    request.setAttribute("successMessage", "Profile updated successfully!");
                }
            } else if ("changePassword".equalsIgnoreCase(action)) {
                String currentPass = request.getParameter("currentPassword");
                String newPass = request.getParameter("newPassword");
                String confirmPass = request.getParameter("confirmPassword");

                if (!newPass.equals(confirmPass)) {
                    request.setAttribute("errorMessage", "New password and Confirm password do not match.");
                } else {
                    boolean changed = authService.changePassword(user.getUserId(), currentPass, newPass);
                    if (changed) {
                        request.setAttribute("successMessage", "Password changed successfully!");
                    } else {
                        request.setAttribute("errorMessage", "Failed to change password.");
                    }
                }
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }
}
