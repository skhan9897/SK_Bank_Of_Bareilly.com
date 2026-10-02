package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.service.AuthService;
import com.skbank.service.impl.AuthServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/security"})
public class SecurityApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthServiceImpl();
    private final Gson gson = new Gson();

    private static class PasswordPayload {
        String currentPassword;
        String newPassword;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long userId = (Long) request.getAttribute("API_USER_ID");

        try {
            BufferedReader reader = request.getReader();
            PasswordPayload p = gson.fromJson(reader, PasswordPayload.class);

            authService.changePassword(userId, p.currentPassword, p.newPassword);

            response.getWriter().write(gson.toJson(ApiResponse.success("Password changed successfully", null)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "PASSWORD_CHANGE_FAILED")));
        }
    }
}
