<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="sidebar">
    <div class="px-4 mb-4 text-center">
        <div class="text-warning fs-1 mb-1"><i class="fa-solid fa-user-shield"></i></div>
        <h6 class="mb-0 text-white font-weight-bold">${sessionScope.ADMIN_NAME != null ? sessionScope.ADMIN_NAME : 'Admin'}</h6>
        <span class="badge bg-gold text-dark mt-1">${sessionScope.ADMIN_ROLE != null ? sessionScope.ADMIN_ROLE : 'ADMIN'}</span>
    </div>
    <ul class="sidebar-menu">
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="sidebar-link ${pageContext.request.requestURI.contains('/dashboard') ? 'active' : ''}">
                <i class="fa-solid fa-gauge"></i> Dashboard
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/customers" class="sidebar-link ${pageContext.request.requestURI.contains('/customers') || pageContext.request.requestURI.contains('/customer-') ? 'active' : ''}">
                <i class="fa-solid fa-users"></i> Customers
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/accounts" class="sidebar-link ${pageContext.request.requestURI.contains('/accounts') || pageContext.request.requestURI.contains('/account-') ? 'active' : ''}">
                <i class="fa-solid fa-wallet"></i> Accounts
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/deposits" class="sidebar-link ${pageContext.request.requestURI.contains('/deposits') ? 'active' : ''}">
                <i class="fa-solid fa-circle-plus"></i> Cash Deposit
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/withdrawals" class="sidebar-link ${pageContext.request.requestURI.contains('/withdrawals') ? 'active' : ''}">
                <i class="fa-solid fa-circle-minus"></i> Withdrawal
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/transactions" class="sidebar-link ${pageContext.request.requestURI.contains('/transactions') ? 'active' : ''}">
                <i class="fa-solid fa-list-check"></i> Transactions
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/transfers" class="sidebar-link ${pageContext.request.requestURI.contains('/transfers') ? 'active' : ''}">
                <i class="fa-solid fa-arrow-right-arrow-left"></i> Transfers
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/loans" class="sidebar-link ${pageContext.request.requestURI.contains('/loans') || pageContext.request.requestURI.contains('/loan-') ? 'active' : ''}">
                <i class="fa-solid fa-hand-holding-dollar"></i> Loans
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/fixed-deposits" class="sidebar-link ${pageContext.request.requestURI.contains('/fixed-deposits') ? 'active' : ''}">
                <i class="fa-solid fa-piggy-bank"></i> Fixed Deposits
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/cards" class="sidebar-link ${pageContext.request.requestURI.contains('/cards') ? 'active' : ''}">
                <i class="fa-solid fa-credit-card"></i> Cards
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/bill-payments" class="sidebar-link ${pageContext.request.requestURI.contains('/bill-payments') ? 'active' : ''}">
                <i class="fa-solid fa-receipt"></i> Bill Payments
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/complaints" class="sidebar-link ${pageContext.request.requestURI.contains('/complaints') ? 'active' : ''}">
                <i class="fa-solid fa-headset"></i> Complaints
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/reports" class="sidebar-link ${pageContext.request.requestURI.contains('/reports') ? 'active' : ''}">
                <i class="fa-solid fa-chart-line"></i> Reports
            </a>
        </li>
        <c:if test="${sessionScope.ADMIN_ROLE == 'SUPER_ADMIN' || sessionScope.ADMIN_ROLE == 'BANK_ADMIN'}">
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/audit-logs" class="sidebar-link ${pageContext.request.requestURI.contains('/audit-logs') ? 'active' : ''}">
                    <i class="fa-solid fa-shield-halved"></i> Audit Logs
                </a>
            </li>
            <li class="sidebar-item">
                <a href="${pageContext.request.contextPath}/admin/settings" class="sidebar-link ${pageContext.request.requestURI.contains('/settings') ? 'active' : ''}">
                    <i class="fa-solid fa-gears"></i> System Settings
                </a>
            </li>
        </c:if>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/admin/profile" class="sidebar-link ${pageContext.request.requestURI.contains('/profile') ? 'active' : ''}">
                <i class="fa-solid fa-user-gear"></i> Admin Profile
            </a>
        </li>
        <li class="sidebar-item mt-3">
            <a href="${pageContext.request.contextPath}/admin/logout" class="sidebar-link text-danger">
                <i class="fa-solid fa-right-from-bracket text-danger"></i> Logout
            </a>
        </li>
    </ul>
</div>
