package com.skbank.controller;

import com.skbank.model.Notification;
import com.skbank.model.User;
import com.skbank.service.NotificationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/notifications")
public class NotificationServlet extends HttpServlet {

    private final NotificationService notificationService = new NotificationService();

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
            List<Notification> notifications = notificationService.getUserNotifications(user.getUserId());
            int unreadCount = notificationService.getUnreadNotificationCount(user.getUserId());

            request.setAttribute("notifications", notifications);
            request.setAttribute("unreadCount", unreadCount);
            request.getRequestDispatcher("/notifications.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading notifications: " + e.getMessage());
            request.getRequestDispatcher("/notifications.jsp").forward(request, response);
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

        String notifIdStr = request.getParameter("notificationId");
        if (notifIdStr != null) {
            try {
                int notifId = Integer.parseInt(notifIdStr);
                notificationService.markAsRead(notifId, user.getUserId());
            } catch (Exception ignored) {}
        }

        doGet(request, response);
    }
}
