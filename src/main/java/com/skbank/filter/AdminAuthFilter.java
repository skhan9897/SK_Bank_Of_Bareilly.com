package com.skbank.filter;

import com.skbank.model.User;
import com.skbank.model.UserRole;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = {"/admin/*"})
public class AdminAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Security headers
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setHeader("Expires", "0");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Allow public admin login path and assets
        if (path.equals("/admin/login") || path.startsWith("/assets/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        boolean isAuthenticated = false;

        if (session != null) {
            User user = (User) session.getAttribute("AUTHENTICATED_USER");
            UserRole role = (UserRole) session.getAttribute("ROLE");
            if (user != null && role == UserRole.ADMIN) {
                isAuthenticated = true;
            }
        }

        if (isAuthenticated) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/login?error=Admin authentication required.");
        }
    }

    @Override
    public void destroy() {}
}
