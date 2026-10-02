package com.skbank.controller;

import com.skbank.model.Customer;
import com.skbank.model.User;
import com.skbank.model.UserRole;
import com.skbank.service.CustomerService;
import com.skbank.service.impl.CustomerServiceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/profile-image", "/admin/profile-image"})
public class ProfileImageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CustomerService customerService = new CustomerServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("AUTHENTICATED_USER") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User user = (User) session.getAttribute("AUTHENTICATED_USER");
        UserRole role = (UserRole) session.getAttribute("ROLE");

        Long requestedCustId = null;
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                requestedCustId = Long.parseLong(idParam);
            } catch (NumberFormatException ignored) {}
        }

        // Customer can only view own image, admin can view any customer image
        if (role == UserRole.CUSTOMER) {
            Long sessionCustId = (Long) session.getAttribute("CUSTOMER_ID");
            if (requestedCustId == null) {
                requestedCustId = sessionCustId;
            } else if (!requestedCustId.equals(sessionCustId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return;
            }
        }

        if (requestedCustId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing customer ID");
            return;
        }

        try {
            Customer cust = customerService.getCustomerById(requestedCustId);
            if (cust == null || cust.getProfileImage() == null || cust.getProfileImage().trim().isEmpty()) {
                // Stream default avatar or 404
                response.sendRedirect(request.getContextPath() + "/assets/images/default-avatar.png");
                return;
            }

            File file = new File(cust.getProfileImage());
            if (!file.exists() || !file.canRead()) {
                response.sendRedirect(request.getContextPath() + "/assets/images/default-avatar.png");
                return;
            }

            String contentType = getServletContext().getMimeType(file.getName());
            if (contentType == null) {
                contentType = "image/jpeg";
            }
            response.setContentType(contentType);
            response.setContentLength((int) file.length());

            try (FileInputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading profile image");
        }
    }
}
