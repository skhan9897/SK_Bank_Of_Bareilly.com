package com.skbank.controller.customer;

import com.skbank.model.Customer;
import com.skbank.service.CustomerService;
import com.skbank.service.impl.CustomerServiceImpl;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet(urlPatterns = {"/customer/profile"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class CustomerProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CustomerService customerService = new CustomerServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        try {
            Customer customer = customerService.getCustomerById(customerId);
            request.setAttribute("customer", customer);
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading profile: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long customerId = (Long) session.getAttribute("CUSTOMER_ID");

        String action = request.getParameter("action");

        try {
            Customer cust = customerService.getCustomerById(customerId);

            if ("uploadPhoto".equalsIgnoreCase(action)) {
                Part filePart = request.getPart("profileImage");
                if (filePart != null && filePart.getSize() > 0) {
                    if (filePart.getSize() > 5 * 1024 * 1024) {
                        throw new Exception("File size exceeds 5 MB limit.");
                    }
                    String contentType = filePart.getContentType();
                    if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/jpg") && !contentType.equals("image/png"))) {
                        throw new Exception("Only JPEG, JPG, and PNG images are allowed.");
                    }

                    String ext = contentType.endsWith("png") ? ".png" : ".jpg";
                    String filename = "cust_" + UUID.randomUUID().toString() + ext;

                    String uploadDir = getServletContext().getRealPath("/") + "uploads/profile";
                    File dir = new File(uploadDir);
                    if (!dir.exists()) dir.mkdirs();

                    String filePath = uploadDir + File.separator + filename;
                    filePart.write(filePath);

                    customerService.updateProfileImage(customerId, "uploads/profile/" + filename);
                }
                response.sendRedirect(request.getContextPath() + "/customer/profile?msg=Profile image updated successfully.");
            } else {
                cust.setFullName(request.getParameter("fullName"));
                cust.setMobile(request.getParameter("mobile"));
                cust.setEmail(request.getParameter("email"));
                cust.setAddress(request.getParameter("address"));
                cust.setCity(request.getParameter("city"));
                cust.setState(request.getParameter("state"));
                cust.setPincode(request.getParameter("pincode"));

                customerService.updateProfile(cust);
                session.setAttribute("CUSTOMER_NAME", cust.getFullName());

                response.sendRedirect(request.getContextPath() + "/customer/profile?msg=Profile details updated successfully.");
            }
        } catch (Exception e) {
            try {
                request.setAttribute("customer", customerService.getCustomerById(customerId));
            } catch (Exception ignored) {}
            request.setAttribute("errorMessage", "Profile update failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/profile.jsp").forward(request, response);
        }
    }
}
