<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Account Details | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/customer/accounts" class="btn btn-outline-secondary btn-sm"><i class="fa-solid fa-arrow-left me-1"></i> Back to Accounts</a>
        </div>

        <div class="sk-card p-4 mb-4">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <span class="badge bg-gold text-dark mb-2">${account.accountTypeName}</span>
                    <h3 class="font-monospace fw-bold text-navy mb-1">${account.maskedAccountNumber}</h3>
                    <p class="text-muted small mb-0">Branch: ${account.branchName} | IFSC: ${account.ifscCode}</p>
                </div>
                <div class="col-md-6 text-md-end mt-3 mt-md-0">
                    <small class="text-muted d-block">Available Balance</small>
                    <h2 class="fw-bold text-success">₹<fmt:formatNumber value="${account.availableBalance}" pattern="#,##0.00" /></h2>
                </div>
            </div>
        </div>

        <div class="sk-card p-4">
            <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-clock-rotate-left me-2"></i> Account Transaction History</h5>
            <div class="table-responsive">
                <table class="table table-sk">
                    <thead>
                        <tr>
                            <th>Ref Number</th>
                            <th>Date</th>
                            <th>Description</th>
                            <th>Type</th>
                            <th>Amount</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${transactions}" var="t">
                            <tr>
                                <td class="font-monospace small fw-bold">${t.transactionReference}</td>
                                <td class="small"><fmt:formatDate value="${t.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></td>
                                <td>${t.description}</td>
                                <td><span class="badge bg-light text-dark">${t.transactionType}</span></td>
                                <td class="fw-bold ${t.transactionType == 'DEPOSIT' ? 'text-success' : 'text-danger'}">
                                    ${t.transactionType == 'DEPOSIT' ? '+' : '-'}₹<fmt:formatNumber value="${t.amount}" pattern="#,##0.00"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
