package com.skbank.controller.api;

import com.google.gson.Gson;
import com.skbank.dto.ApiResponse;
import com.skbank.model.Notification;
import com.skbank.service.NotificationService;
import com.skbank.service.impl.NotificationServiceImpl;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/api/customer/notifications"})
public class NotificationApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final NotificationService notificationService = new NotificationServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long userId = (Long) request.getAttribute("API_USER_ID");

        try {
            List<Notification> notifications = notificationService.getUserNotifications(userId, 50);
            response.getWriter().write(gson.toJson(ApiResponse.success("Notifications loaded", notifications)));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "NOTIFICATION_ERROR")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long userId = (Long) request.getAttribute("API_USER_ID");
        String action = request.getParameter("action");

        try {
            if ("markAllRead".equalsIgnoreCase(action)) {
                notificationService.markAllAsRead(userId);
                response.getWriter().write(gson.toJson(ApiResponse.success("All notifications marked as read", null)));
            } else {
                Long notifId = Long.parseLong(request.getParameter("id"));
                notificationService.markAsRead(notifId, userId);
                response.getWriter().write(gson.toJson(ApiResponse.success("Notification marked as read", null)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage(), "ACTION_FAILED")));
        }
    }
}
