<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Customer Dashboard | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h3 class="fw-bold text-navy mb-1">Welcome back, ${sessionScope.CUSTOMER_NAME}!</h3>
                <p class="text-muted small mb-0">Overview of your bank accounts and financial summary.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/customer/send-money" class="btn btn-gold font-weight-bold"><i class="fa-solid fa-paper-plane me-1"></i> Send Money</a>
            </div>
        </div>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <!-- SUMMARY CARDS -->
        <div class="row g-3 mb-4">
            <div class="col-md-3">
                <div class="sk-card sk-card-stat p-3">
                    <div class="stat-label">Total Balance</div>
                    <div class="stat-value text-primary">₹<fmt:formatNumber value="${stats.totalBalance}" pattern="#,##0.00" /></div>
                    <small class="text-muted">${stats.totalAccountsCount} Linked Account(s)</small>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card sk-card-stat gold p-3">
                    <div class="stat-label">Available Balance</div>
                    <div class="stat-value text-gold">₹<fmt:formatNumber value="${stats.availableBalance}" pattern="#,##0.00" /></div>
                    <small class="text-muted">Instant Withdrawal Limit</small>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card sk-card-stat success p-3">
                    <div class="stat-label">FD Investment</div>
                    <div class="stat-value text-success">₹<fmt:formatNumber value="${stats.fdInvestmentAmount}" pattern="#,##0.00" /></div>
                    <small class="text-muted">Guaranteed Return</small>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card sk-card-stat p-3 border-left-danger" style="border-left-color: #EF4444;">
                    <div class="stat-label">Active Loans</div>
                    <div class="stat-value text-danger">₹<fmt:formatNumber value="${stats.loanOutstandingAmount}" pattern="#,##0.00" /></div>
                    <small class="text-muted">${stats.activeLoansCount} Active Loan(s)</small>
                </div>
            </div>
        </div>

        <!-- ACCOUNTS LIST & RECENT TRANSACTIONS -->
        <div class="row g-4">
            <div class="col-lg-6">
                <div class="sk-card">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="fw-bold text-navy mb-0"><i class="fa-solid fa-wallet text-primary me-2"></i> My Accounts</h5>
                        <a href="${pageContext.request.contextPath}/customer/accounts" class="small text-decoration-none fw-bold">View All</a>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-sk align-middle">
                            <thead>
                                <tr>
                                    <th>Account No</th>
                                    <th>Type</th>
                                    <th>Balance</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${accounts}" var="a">
                                    <tr>
                                        <td class="font-monospace fw-bold">${a.maskedAccountNumber}</td>
                                        <td>${a.accountTypeName}</td>
                                        <td class="fw-bold text-success">₹<fmt:formatNumber value="${a.balance}" pattern="#,##0.00" /></td>
                                        <td><a href="${pageContext.request.contextPath}/customer/account-details?id=${a.accountId}" class="btn btn-sm btn-outline-primary">Details</a></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="sk-card">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="fw-bold text-navy mb-0"><i class="fa-solid fa-clock-rotate-left text-warning me-2"></i> Recent Transactions</h5>
                        <a href="${pageContext.request.contextPath}/customer/transactions" class="small text-decoration-none fw-bold">View History</a>
                    </div>
                    <c:choose>
                        <c:when test="${not empty recentTransactions}">
                            <div class="list-group list-group-flush">
                                <c:forEach items="${recentTransactions}" var="t">
                                    <div class="list-group-item d-flex justify-content-between align-items-center px-0 py-2">
                                        <div>
                                            <div class="fw-bold small">${t.description}</div>
                                            <small class="text-muted"><fmt:formatDate value="${t.createdAt}" pattern="dd MMM yyyy, hh:mm a" /></small>
                                        </div>
                                        <div class="text-end">
                                            <div class="fw-bold ${t.transactionType == 'DEPOSIT' || t.transactionType == 'INTEREST_CREDIT' || t.transactionType == 'REFUND' ? 'text-success' : 'text-danger'}">
                                                ${t.transactionType == 'DEPOSIT' || t.transactionType == 'INTEREST_CREDIT' || t.transactionType == 'REFUND' ? '+' : '-'}₹<fmt:formatNumber value="${t.amount}" pattern="#,##0.00" />
                                            </div>
                                            <span class="badge badge-sk success">SUCCESS</span>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted small my-3 text-center">No recent transactions found.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
