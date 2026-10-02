package com.skbank.controller.admin;

import com.skbank.model.SystemSetting;
import com.skbank.service.AdminService;
import com.skbank.service.impl.AdminServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/admin/settings"})
public class AdminSettingsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AdminService adminService = new AdminServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<SystemSetting> settings = adminService.getSystemSettings();
            request.setAttribute("settings", settings);
            request.getRequestDispatcher("/WEB-INF/views/admin/settings.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading system settings: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/settings.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long adminUserId = (Long) session.getAttribute("USER_ID");

        try {
            Map<String, String> newSettings = new HashMap<>();
            newSettings.put("MAX_TRANSFER_AMOUNT", request.getParameter("MAX_TRANSFER_AMOUNT"));
            newSettings.put("DAILY_TRANSFER_LIMIT", request.getParameter("DAILY_TRANSFER_LIMIT"));
            newSettings.put("MAX_WITHDRAWAL_AMOUNT", request.getParameter("MAX_WITHDRAWAL_AMOUNT"));
            newSettings.put("DAILY_WITHDRAWAL_LIMIT", request.getParameter("DAILY_WITHDRAWAL_LIMIT"));
            newSettings.put("MAX_UPI_TRANSACTION_AMOUNT", request.getParameter("MAX_UPI_TRANSACTION_AMOUNT"));
            newSettings.put("OTP_EXPIRY_MINUTES", request.getParameter("OTP_EXPIRY_MINUTES"));
            newSettings.put("SESSION_TIMEOUT_MINUTES", request.getParameter("SESSION_TIMEOUT_MINUTES"));

            adminService.updateSystemSettings(newSettings, adminUserId);

            response.sendRedirect(request.getContextPath() + "/admin/settings?msg=System settings updated successfully.");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error updating settings: " + e.getMessage());
            try {
                request.setAttribute("settings", adminService.getSystemSettings());
            } catch (Exception ignored) {}
            request.getRequestDispatcher("/WEB-INF/views/admin/settings.jsp").forward(request, response);
        }
    }
}
