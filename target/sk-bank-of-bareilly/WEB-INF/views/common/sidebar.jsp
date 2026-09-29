<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="sidebar">
    <div class="px-3 mb-3 text-center">
        <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="SK Logo" class="bank-logo-img-lg mb-2">
        <h6 class="text-white mb-0 fw-bold">SK Bank Portal</h6>
        <small class="text-warning">Bareilly Main Branch</small>
    </div>
    <hr class="border-secondary mx-3">
    <nav class="nav flex-column">
        <a class="nav-link ${param.active == 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/dashboard">
            <i class="fas fa-th-large text-warning"></i> Dashboard
        </a>
        <a class="nav-link ${param.active == 'accounts' ? 'active' : ''}" href="${pageContext.request.contextPath}/accounts">
            <i class="fas fa-wallet text-warning"></i> My Accounts
        </a>
        <a class="nav-link ${param.active == 'transfer' ? 'active' : ''}" href="${pageContext.request.contextPath}/transfer">
            <i class="fas fa-paper-plane text-warning"></i> Transfer Money
        </a>
        <a class="nav-link ${param.active == 'beneficiaries' ? 'active' : ''}" href="${pageContext.request.contextPath}/beneficiaries">
            <i class="fas fa-address-book text-warning"></i> Beneficiaries
        </a>
        <a class="nav-link ${param.active == 'transactions' ? 'active' : ''}" href="${pageContext.request.contextPath}/transactions">
            <i class="fas fa-exchange-alt text-warning"></i> Transactions
        </a>
        <a class="nav-link ${param.active == 'statements' ? 'active' : ''}" href="${pageContext.request.contextPath}/statements">
            <i class="fas fa-file-invoice-dollar text-warning"></i> Statements
        </a>
        <a class="nav-link ${param.active == 'loans' ? 'active' : ''}" href="${pageContext.request.contextPath}/loans">
            <i class="fas fa-hand-holding-usd text-warning"></i> Loans & EMI
        </a>
        <a class="nav-link ${param.active == 'fixed-deposits' ? 'active' : ''}" href="${pageContext.request.contextPath}/fixed-deposits">
            <i class="fas fa-piggy-bank text-warning"></i> Fixed Deposits
        </a>
        <a class="nav-link ${param.active == 'cards' ? 'active' : ''}" href="${pageContext.request.contextPath}/cards">
            <i class="fas fa-credit-card text-warning"></i> Cards
        </a>
        <a class="nav-link ${param.active == 'payments' ? 'active' : ''}" href="${pageContext.request.contextPath}/payments">
            <i class="fas fa-bolt text-warning"></i> Bill Payments
        </a>
        <a class="nav-link ${param.active == 'kyc' ? 'active' : ''}" href="${pageContext.request.contextPath}/kyc">
            <i class="fas fa-id-card text-warning"></i> KYC Verification
        </a>
        <a class="nav-link ${param.active == 'notifications' ? 'active' : ''}" href="${pageContext.request.contextPath}/notifications">
            <i class="fas fa-bell text-warning"></i> Notifications
        </a>
        <a class="nav-link ${param.active == 'complaints' ? 'active' : ''}" href="${pageContext.request.contextPath}/complaints">
            <i class="fas fa-headset text-warning"></i> Support & Complaints
        </a>
        <a class="nav-link ${param.active == 'profile' ? 'active' : ''}" href="${pageContext.request.contextPath}/profile">
            <i class="fas fa-user-cog text-warning"></i> My Profile
        </a>
        <a class="nav-link text-danger mt-3" href="${pageContext.request.contextPath}/logout">
            <i class="fas fa-sign-out-alt"></i> Logout
        </a>
    </nav>
</div>
