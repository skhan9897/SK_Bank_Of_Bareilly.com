package com.skbank.filter;

import com.skbank.model.AdminRole;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = {"/admin/settings", "/admin/audit-logs"})
public class AdminRoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            AdminRole adminRole = (AdminRole) session.getAttribute("ADMIN_ROLE");
            if (adminRole == AdminRole.SUPER_ADMIN || adminRole == AdminRole.BANK_ADMIN) {
                chain.doFilter(request, response);
                return;
            }
        }

        httpResponse.sendRedirect(httpRequest.getContextPath() + "/admin/dashboard?error=Access Denied: Super Admin privileges required.");
    }

    @Override
    public void destroy() {}
}
