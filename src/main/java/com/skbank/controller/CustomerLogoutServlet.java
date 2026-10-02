package com.skbank.controller;

import com.skbank.model.User;
import com.skbank.util.AuditUtil;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/logout"})
public class CustomerLogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("AUTHENTICATED_USER");
            if (user != null) {
                AuditUtil.logAction(user.getId(), "LOGOUT", "AUTH", "Customer logged out.", request);
            }
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login?msg=You have been successfully logged out.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
