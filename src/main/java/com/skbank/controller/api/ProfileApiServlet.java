package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.Customer;
import com.skbank.service.CustomerService;
import com.skbank.service.impl.CustomerServiceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(urlPatterns = {"/api/customer/profile", "/api/customer/profile/photo"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class ProfileApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CustomerService customerService = new CustomerServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");

        try {
            Customer customer = customerService.getCustomerById(customerId);
            response.getWriter().write(gson.toJson(ApiResponse.success("Profile loaded", customer)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "PROFILE_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long customerId = (Long) request.getAttribute("API_CUSTOMER_ID");
        String path = request.getServletPath();

        try {
            if ("/api/customer/profile/photo".equals(path)) {
                Part filePart = request.getPart("photo");
                if (filePart == null || filePart.getSize() <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(ApiResponse.error("Photo file is required", "BAD_REQUEST")));
                    return;
                }
                if (filePart.getSize() > 5 * 1024 * 1024) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(ApiResponse.error("File size exceeds 5 MB limit", "FILE_TOO_LARGE")));
                    return;
                }

                String contentType = filePart.getContentType();
                if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/jpg") && !contentType.equals("image/png"))) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write(gson.toJson(ApiResponse.error("Only JPG, JPEG, and PNG images allowed", "INVALID_FILE_TYPE")));
                    return;
                }

                String ext = contentType.endsWith("png") ? ".png" : ".jpg";
                String filename = "customer_" + UUID.randomUUID().toString() + ext;

                String uploadDir = getServletContext().getRealPath("/") + "uploads/profile";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                String filePath = uploadDir + File.separator + filename;
                filePart.write(filePath);

                customerService.updateProfileImage(customerId, filePath);

                response.getWriter().write(gson.toJson(ApiResponse.success("Profile photo updated", filePath)));
            } else {
                BufferedReader reader = request.getReader();
                Customer updateReq = gson.fromJson(reader, Customer.class);

                Customer cust = customerService.getCustomerById(customerId);
                cust.setFullName(updateReq.getFullName());
                cust.setMobile(updateReq.getMobile());
                cust.setEmail(updateReq.getEmail());
                cust.setAddress(updateReq.getAddress());
                cust.setCity(updateReq.getCity());
                cust.setState(updateReq.getState());
                cust.setPincode(updateReq.getPincode());

                customerService.updateProfile(cust);

                response.getWriter().write(gson.toJson(ApiResponse.success("Profile updated successfully", cust)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "PROFILE_UPDATE_FAILED")));
        }
    }
}
