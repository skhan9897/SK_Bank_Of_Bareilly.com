<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="My Accounts | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-wallet text-primary me-2"></i> My Accounts</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row g-4">
            <c:forEach items="${accounts}" var="a">
                <div class="col-md-6">
                    <div class="sk-card p-4">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <span class="badge bg-primary text-white font-monospace">${empty a.accountTypeName ? 'Savings Account' : a.accountTypeName}</span>
                            <span class="badge bg-success text-white">${a.status}</span>
                        </div>
                        <h4 class="font-monospace fw-bold text-navy mb-2">${a.maskedAccountNumber}</h4>
                        <div class="row my-3 bg-light p-3 rounded">
                            <div class="col-6">
                                <small class="text-muted d-block">Current Balance</small>
                                <strong class="fs-5 text-success">₹<fmt:formatNumber value="${a.balance}" pattern="#,##0.00" /></strong>
                            </div>
                            <div class="col-6">
                                <small class="text-muted d-block">Available Balance</small>
                                <strong class="fs-5 text-primary">₹<fmt:formatNumber value="${a.availableBalance}" pattern="#,##0.00" /></strong>
                            </div>
                        </div>
                        <div class="small text-muted mb-3">
                            <div><i class="fa-solid fa-building me-1"></i> Branch: <strong>${empty a.branchName ? 'Main Branch Bareilly' : a.branchName}</strong></div>
                            <div><i class="fa-solid fa-code me-1"></i> IFSC: <strong>${empty a.ifscCode ? 'SKBK0000001' : a.ifscCode}</strong></div>
                        </div>
                        <div class="d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/customer/account-details?id=${a.accountId}" class="btn btn-outline-primary btn-sm w-50">View Details</a>
                            <a href="${pageContext.request.contextPath}/customer/send-money" class="btn btn-gold btn-sm w-50">Transfer Money</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
