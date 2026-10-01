package com.skbank.filter;

import com.skbank.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/customer/*",
    "/dashboard",
    "/accounts",
    "/transfer",
    "/statements",
    "/transactions",
    "/loans",
    "/fixed-deposits",
    "/cards",
    "/payments",
    "/kyc",
    "/notifications",
    "/complaints",
    "/profile",
    "/upi"
})
public class CustomerAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        // Bypass filter for public registration & login URLs
        if (uri.endsWith("/customer/register") || uri.endsWith("/customer/login") ||
            uri.endsWith("/register") || uri.endsWith("/login") ||
            uri.contains("/assets/") || uri.contains("/images/") || uri.contains("/uploads/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
