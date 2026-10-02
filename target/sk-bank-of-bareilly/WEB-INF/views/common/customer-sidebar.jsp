<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="sidebar">
    <div class="px-4 mb-4 text-center">
        <div class="position-relative d-inline-block">
            <img src="${pageContext.request.contextPath}/customer/profile-image" alt="Profile" class="rounded-circle border border-2 border-warning" width="70" height="70">
        </div>
        <h6 class="mt-2 mb-0 text-white font-weight-bold">${sessionScope.CUSTOMER_NAME != null ? sessionScope.CUSTOMER_NAME : 'Customer'}</h6>
        <small class="text-warning">SK Bank Customer</small>
    </div>
    <ul class="sidebar-menu">
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/dashboard" class="sidebar-link ${pageContext.request.requestURI.endsWith('dashboard.jsp') || pageContext.request.requestURI.contains('/dashboard') ? 'active' : ''}">
                <i class="fa-solid fa-house"></i> Dashboard
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/accounts" class="sidebar-link ${pageContext.request.requestURI.contains('/accounts') ? 'active' : ''}">
                <i class="fa-solid fa-wallet"></i> My Accounts
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/send-money" class="sidebar-link ${pageContext.request.requestURI.contains('/send-money') || pageContext.request.requestURI.contains('/transfer') ? 'active' : ''}">
                <i class="fa-solid fa-paper-plane"></i> Send Money
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/withdraw" class="sidebar-link ${pageContext.request.requestURI.contains('/withdraw') ? 'active' : ''}">
                <i class="fa-solid fa-money-bill-transfer"></i> Withdraw
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/beneficiaries" class="sidebar-link ${pageContext.request.requestURI.contains('/beneficiaries') ? 'active' : ''}">
                <i class="fa-solid fa-users"></i> Beneficiaries
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/transactions" class="sidebar-link ${pageContext.request.requestURI.contains('/transactions') ? 'active' : ''}">
                <i class="fa-solid fa-list-check"></i> Transactions
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/statements" class="sidebar-link ${pageContext.request.requestURI.contains('/statements') ? 'active' : ''}">
                <i class="fa-solid fa-file-invoice"></i> Statements
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/upi" class="sidebar-link ${pageContext.request.requestURI.contains('/upi') ? 'active' : ''}">
                <i class="fa-solid fa-qrcode"></i> My UPI
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/fixed-deposits" class="sidebar-link ${pageContext.request.requestURI.contains('/fixed-deposits') ? 'active' : ''}">
                <i class="fa-solid fa-piggy-bank"></i> Fixed Deposits
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/loans" class="sidebar-link ${pageContext.request.requestURI.contains('/loans') || pageContext.request.requestURI.contains('/loan-') ? 'active' : ''}">
                <i class="fa-solid fa-hand-holding-dollar"></i> Loans
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/cards" class="sidebar-link ${pageContext.request.requestURI.contains('/cards') ? 'active' : ''}">
                <i class="fa-solid fa-credit-card"></i> Cards
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/bill-payments" class="sidebar-link ${pageContext.request.requestURI.contains('/bill-payments') ? 'active' : ''}">
                <i class="fa-solid fa-receipt"></i> Bill Payments
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/kyc" class="sidebar-link ${pageContext.request.requestURI.contains('/kyc') ? 'active' : ''}">
                <i class="fa-solid fa-id-card"></i> KYC Details
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/notifications" class="sidebar-link ${pageContext.request.requestURI.contains('/notifications') ? 'active' : ''}">
                <i class="fa-solid fa-bell"></i> Notifications
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/complaints" class="sidebar-link ${pageContext.request.requestURI.contains('/complaints') ? 'active' : ''}">
                <i class="fa-solid fa-headset"></i> Complaints
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/profile" class="sidebar-link ${pageContext.request.requestURI.contains('/profile') ? 'active' : ''}">
                <i class="fa-solid fa-user"></i> Profile
            </a>
        </li>
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/security" class="sidebar-link ${pageContext.request.requestURI.contains('/security') ? 'active' : ''}">
                <i class="fa-solid fa-shield-halved"></i> Security
            </a>
        </li>
        <li class="sidebar-item mt-3">
            <a href="${pageContext.request.contextPath}/logout" class="sidebar-link text-danger">
                <i class="fa-solid fa-right-from-bracket text-danger"></i> Logout
            </a>
        </li>
    </ul>
</div>
