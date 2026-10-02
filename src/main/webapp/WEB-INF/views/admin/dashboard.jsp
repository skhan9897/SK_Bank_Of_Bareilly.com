<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Admin Dashboard | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h3 class="fw-bold text-navy mb-1"><i class="fa-solid fa-gauge text-warning me-2"></i> Bank Operations Dashboard</h3>
                <p class="text-muted small mb-0">System metrics and core banking administration.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/deposits" class="btn btn-gold font-weight-bold"><i class="fa-solid fa-plus-circle me-1"></i> New Deposit</a>
            </div>
        </div>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <!-- STAT CARDS GRID -->
        <div class="row g-3 mb-4">
            <div class="col-md-3">
                <div class="sk-card p-3 border-start border-4 border-primary">
                    <div class="stat-label">Total Customers</div>
                    <div class="stat-value text-primary">${stats.totalCustomers}</div>
                    <small class="text-muted"><a href="${pageContext.request.contextPath}/admin/customers">View Customers &rarr;</a></small>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card p-3 border-start border-4 border-success">
                    <div class="stat-label">Active Accounts</div>
                    <div class="stat-value text-success">${stats.activeAccounts}</div>
                    <small class="text-muted"><a href="${pageContext.request.contextPath}/admin/accounts">Manage Accounts &rarr;</a></small>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card p-3 border-start border-4 border-warning">
                    <div class="stat-label">Total Bank Deposits</div>
                    <div class="stat-value text-warning">₹<fmt:formatNumber value="${stats.totalDeposits}" pattern="#,##0.00"/></div>
                    <small class="text-muted">Aggregated Balances</small>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card p-3 border-start border-4 border-info">
                    <div class="stat-label">Today's Transactions</div>
                    <div class="stat-value text-info">${stats.todaysTransactionsCount}</div>
                    <small class="text-muted"><a href="${pageContext.request.contextPath}/admin/transactions">View Reports &rarr;</a></small>
                </div>
            </div>

            <div class="col-md-3">
                <div class="sk-card p-3 border-start border-4 border-danger">
                    <div class="stat-label">Pending Loan Apps</div>
                    <div class="stat-value text-danger">${stats.pendingLoansCount}</div>
                    <small class="text-muted"><a href="${pageContext.request.contextPath}/admin/loans?status=PENDING">Review Loans &rarr;</a></small>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card p-3 border-start border-4 border-secondary">
                    <div class="stat-label">Active Loans</div>
                    <div class="stat-value text-secondary">${stats.activeLoansCount}</div>
                    <small class="text-muted">Active Disbursed</small>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card p-3 border-start border-4 border-dark">
                    <div class="stat-label">Active FDs</div>
                    <div class="stat-value text-dark">${stats.totalFdsCount}</div>
                    <small class="text-muted"><a href="${pageContext.request.contextPath}/admin/fixed-deposits">View FDs &rarr;</a></small>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card p-3 border-start border-4 border-primary">
                    <div class="stat-label">Pending Complaints</div>
                    <div class="stat-value text-primary">${stats.pendingComplaintsCount}</div>
                    <small class="text-muted"><a href="${pageContext.request.contextPath}/admin/complaints">Support Tickets &rarr;</a></small>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
