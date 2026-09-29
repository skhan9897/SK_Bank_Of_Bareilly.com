<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="sidebar">
    <div class="px-3 mb-3 text-center">
        <h6 class="text-warning fw-bold mb-0"><i class="fas fa-university me-1"></i> Admin Panel</h6>
        <small class="text-white-50">Management Portal</small>
    </div>
    <hr class="border-secondary mx-3">
    <nav class="nav flex-column">
        <a class="nav-link ${param.active == 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="fas fa-chart-line text-warning"></i> Control Panel
        </a>
        <a class="nav-link ${param.active == 'customers' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/customers">
            <i class="fas fa-users text-warning"></i> Customer Directory
        </a>
        <a class="nav-link ${param.active == 'accounts' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/accounts">
            <i class="fas fa-wallet text-warning"></i> Account Management
        </a>
        <a class="nav-link ${param.active == 'transactions' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/transactions">
            <i class="fas fa-exchange-alt text-warning"></i> All Transactions
        </a>
        <a class="nav-link ${param.active == 'loans' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/loans">
            <i class="fas fa-hand-holding-usd text-warning"></i> Loan Applications
        </a>
        <a class="nav-link ${param.active == 'fixed-deposits' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/fixed-deposits">
            <i class="fas fa-piggy-bank text-warning"></i> Fixed Deposits
        </a>
        <a class="nav-link ${param.active == 'kyc' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/kyc">
            <i class="fas fa-id-card text-warning"></i> KYC Approvals
        </a>
        <a class="nav-link ${param.active == 'complaints' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/complaints">
            <i class="fas fa-headset text-warning"></i> Customer Complaints
        </a>
        <a class="nav-link ${param.active == 'reports' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/reports">
            <i class="fas fa-file-alt text-warning"></i> Reports & Analytics
        </a>
        <a class="nav-link text-danger mt-4" href="${pageContext.request.contextPath}/logout">
            <i class="fas fa-sign-out-alt"></i> Logout Admin
        </a>
    </nav>
</div>
