package com.skbank.controller.customer;

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
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/customer/notifications"})
public class NotificationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final NotificationService notificationService = new NotificationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute("USER_ID");

        String action = request.getParameter("action");
        if ("markAllRead".equalsIgnoreCase(action)) {
            try {
                notificationService.markAllAsRead(userId);
                response.sendRedirect(request.getContextPath() + "/customer/notifications");
                return;
            } catch (Exception ignored) {}
        }

        try {
            List<Notification> notifications = notificationService.getUserNotifications(userId, 50);
            request.setAttribute("notifications", notifications);
            request.getRequestDispatcher("/WEB-INF/views/customer/notifications.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error loading notifications: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/customer/notifications.jsp").forward(request, response);
        }
    }
}
