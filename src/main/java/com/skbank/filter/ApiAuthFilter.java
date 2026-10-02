package com.skbank.filter;

import com.google.gson.Gson;
import com.skbank.dao.CustomerDAO;
import com.skbank.dao.UserDAO;
import com.skbank.dao.impl.CustomerDAOImpl;
import com.skbank.dao.impl.UserDAOImpl;
import com.skbank.dto.ApiResponse;
import com.skbank.model.Customer;
import com.skbank.model.User;
import com.skbank.util.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = {"/api/*"})
public class ApiAuthFilter implements Filter {

    private final Gson gson = new Gson();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Bypassed public API endpoints
        if (path.equals("/api/customer/auth/login") ||
            path.equals("/api/customer/auth/verify-otp") ||
            path.equals("/api/customer/register")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write(gson.toJson(ApiResponse.error("Authentication token required", "AUTH_REQUIRED")));
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            User user = findUserByToken(token);
            if (user == null) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write(gson.toJson(ApiResponse.error("Invalid or expired session token", "SESSION_EXPIRED")));
                return;
            }

            Customer customer = customerDAO.findByUserId(user.getId());

            request.setAttribute("API_USER", user);
            request.setAttribute("API_USER_ID", user.getId());
            if (customer != null) {
                request.setAttribute("API_CUSTOMER", customer);
                request.setAttribute("API_CUSTOMER_ID", customer.getCustomerId());
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpResponse.getWriter().write(gson.toJson(ApiResponse.error("API Auth Error: " + e.getMessage(), "SERVER_ERROR")));
        }
    }

    private User findUserByToken(String token) throws Exception {
        String sql = "SELECT * FROM users WHERE auth_token = ? AND (token_expiry IS NULL OR token_expiry > NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getLong("id"));
                    u.setUsername(rs.getString("username"));
                    u.setRole(com.skbank.model.UserRole.valueOf(rs.getString("role")));
                    u.setStatus(com.skbank.model.UserStatus.valueOf(rs.getString("status")));
                    return u;
                }
            }
        }
        return null;
    }

    @Override
    public void destroy() {}
}
