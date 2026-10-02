package com.skbank.filter;

import com.skbank.model.User;
import com.skbank.model.UserRole;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = {"/customer/*"})
public class CustomerAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Prevent browser caching sensitive banking pages
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setHeader("Expires", "0");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        HttpSession session = httpRequest.getSession(false);
        boolean isAuthenticated = false;

        if (session != null) {
            User user = (User) session.getAttribute("AUTHENTICATED_USER");
            UserRole role = (UserRole) session.getAttribute("ROLE");
            if (user != null && role == UserRole.CUSTOMER) {
                isAuthenticated = true;
            }
        }

        if (isAuthenticated) {
            chain.doFilter(request, response);
        } else {
            String requestURI = httpRequest.getRequestURI();
            if (httpRequest.getQueryString() != null) {
                requestURI += "?" + httpRequest.getQueryString();
            }
            if (session != null) {
                session.setAttribute("REDIRECT_AFTER_LOGIN", requestURI);
            }
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?error=Session expired. Please log in.");
        }
    }

    @Override
    public void destroy() {}
}
