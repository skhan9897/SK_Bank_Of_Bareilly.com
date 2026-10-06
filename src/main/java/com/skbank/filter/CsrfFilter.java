package com.skbank.filter;

import com.skbank.util.CsrfUtil;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(urlPatterns = {"/*"})
public class CsrfFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Generate token for session if session exists
        if (httpRequest.getSession(false) != null) {
            CsrfUtil.getToken(httpRequest.getSession(false));
        }

        String method = httpRequest.getMethod().toUpperCase();
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Validate token on POST, PUT, DELETE for customer and admin forms
        if (("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) &&
            (path.startsWith("/customer/") || path.startsWith("/admin/"))) {

            // Allow public login, registration, and OTP verification forms without CSRF requirement
            if (path.equals("/admin/login") || path.equals("/login") || path.equals("/register") || path.startsWith("/verify-otp") || path.startsWith("/api/")) {
                chain.doFilter(request, response);
                return;
            }

            if (!CsrfUtil.isValidToken(httpRequest)) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF Token Validation Failed.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
