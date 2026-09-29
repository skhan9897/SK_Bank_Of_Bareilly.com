package com.skbank.filter;

import com.skbank.model.User;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/dashboard", "/accounts", "/transfer", "/beneficiaries", "/transactions",
    "/statements", "/loans", "/fixed-deposits", "/cards", "/payments",
    "/notifications", "/complaints", "/profile", "/kyc",
    "/admin/dashboard", "/admin/customers", "/admin/accounts", "/admin/transactions",
    "/admin/loans", "/admin/fixed-deposits", "/admin/kyc", "/admin/complaints", "/admin/reports"
})
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        User loggedInUser = (session != null) ? (User) session.getAttribute("loggedInUser") : null;

        String uri = httpRequest.getRequestURI();

        if (loggedInUser == null) {
            if (uri.contains("/admin/")) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/login?error=Session expired. Please login as Admin.");
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?error=Please login to access this service.");
            }
            return;
        }

        // Security check: Customer trying to access Admin pages
        if (uri.contains("/admin/") && !"ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/403.jsp");
            return;
        }

        chain.doFilter(request, response);
    }
}
