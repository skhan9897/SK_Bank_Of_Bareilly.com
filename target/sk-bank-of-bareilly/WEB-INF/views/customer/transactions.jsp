<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Transactions History | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-list-check text-primary me-2"></i> Transaction History</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="sk-card p-3 mb-4">
            <form action="${pageContext.request.contextPath}/customer/transactions" method="get" class="row g-3 align-items-end">
                <div class="col-md-3">
                    <label class="form-label small fw-bold">Select Account</label>
                    <select name="accountId" class="form-select">
                        <c:forEach items="${accounts}" var="a">
                            <option value="${a.accountId}" ${a.accountId == selectedAccountId ? 'selected' : ''}>${a.maskedAccountNumber}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label small fw-bold">Transaction Type</label>
                    <select name="type" class="form-select">
                        <option value="">All Types</option>
                        <option value="DEPOSIT" ${typeFilter == 'DEPOSIT' ? 'selected' : ''}>DEPOSIT</option>
                        <option value="WITHDRAWAL" ${typeFilter == 'WITHDRAWAL' ? 'selected' : ''}>WITHDRAWAL</option>
                        <option value="TRANSFER" ${typeFilter == 'TRANSFER' ? 'selected' : ''}>TRANSFER</option>
                        <option value="BILL_PAYMENT" ${typeFilter == 'BILL_PAYMENT' ? 'selected' : ''}>BILL PAYMENT</option>
                        <option value="LOAN_EMI" ${typeFilter == 'LOAN_EMI' ? 'selected' : ''}>LOAN EMI</option>
                        <option value="FD_INVESTMENT" ${typeFilter == 'FD_INVESTMENT' ? 'selected' : ''}>FD INVESTMENT</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label small fw-bold">Start Date</label>
                    <input type="date" name="startDate" class="form-control" value="${startDate}">
                </div>
                <div class="col-md-3">
                    <label class="form-label small fw-bold">End Date</label>
                    <input type="date" name="endDate" class="form-control" value="${endDate}">
                </div>
                <div class="col-md-1">
                    <button type="submit" class="btn btn-primary w-100"><i class="fa-solid fa-filter"></i></button>
                </div>
            </form>
        </div>

        <div class="sk-card p-4">
            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>Ref Number</th>
                            <th>Date & Time</th>
                            <th>Description</th>
                            <th>Type</th>
                            <th>Amount</th>
                            <th>Balance After</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${transactions}" var="t">
                            <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/customer/transaction-details?ref=${t.transactionReference}" class="font-monospace fw-bold text-decoration-none">
                                        ${t.transactionReference}
                                    </a>
                                </td>
                                <td class="small"><fmt:formatDate value="${t.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></td>
                                <td>${t.description}</td>
                                <td><span class="badge bg-light text-dark">${t.transactionType}</span></td>
                                <td class="fw-bold ${t.transactionType == 'DEPOSIT' || t.transactionType == 'INTEREST_CREDIT' || t.transactionType == 'REFUND' ? 'text-success' : 'text-danger'}">
                                    ${t.transactionType == 'DEPOSIT' || t.transactionType == 'INTEREST_CREDIT' || t.transactionType == 'REFUND' ? '+' : '-'}₹<fmt:formatNumber value="${t.amount}" pattern="#,##0.00"/>
                                </td>
                                <td>₹<fmt:formatNumber value="${t.balanceAfter}" pattern="#,##0.00"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <nav class="mt-4">
                    <ul class="pagination justify-content-center">
                        <c:forEach begin="1" end="${totalPages}" var="p">
                            <li class="page-item ${p == currentPage ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/customer/transactions?accountId=${selectedAccountId}&type=${typeFilter}&startDate=${startDate}&endDate=${endDate}&page=${p}">${p}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </nav>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
