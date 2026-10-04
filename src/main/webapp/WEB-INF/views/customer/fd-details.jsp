<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="FD Advice | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="sk-card p-4 text-center shadow-lg">
                    <img src="${pageContext.request.contextPath}/assets/images/sk-bank-logo-final-transparent.png?v=20261004" alt="Logo" width="60" class="mb-2" onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/assets/images/sk-bank-logo-transparent.png';">
                    <h4 class="fw-bold text-navy">SK BANK OF BAREILLY</h4>
                    <p class="text-muted small">Fixed Deposit Receipt / Advice</p>

                    <div class="border-top border-bottom py-3 my-3 text-start bg-light p-3 rounded">
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">FD Number:</span>
                            <strong class="font-monospace text-navy">${fd.fdNumber}</strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Principal Amount:</span>
                            <strong class="fs-5 text-primary">₹<fmt:formatNumber value="${fd.principalAmount}" pattern="#,##0.00"/></strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Interest Rate:</span>
                            <strong>${fd.interestRate}% p.a.</strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Tenure:</span>
                            <span>${fd.tenureMonths} Months</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Maturity Amount:</span>
                            <strong class="fs-4 text-success">₹<fmt:formatNumber value="${fd.maturityAmount}" pattern="#,##0.00"/></strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Start Date:</span>
                            <span><fmt:formatDate value="${fd.startDate}" pattern="dd MMM yyyy"/></span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Maturity Date:</span>
                            <span><fmt:formatDate value="${fd.maturityDate}" pattern="dd MMM yyyy"/></span>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span class="text-muted">Status:</span>
                            <span class="badge bg-success">${fd.status}</span>
                        </div>
                    </div>

                    <div class="d-flex gap-2 justify-content-center d-print-none">
                        <button onclick="window.print()" class="btn btn-outline-primary"><i class="fa-solid fa-print me-1"></i> Print Advice</button>
                        <a href="${pageContext.request.contextPath}/customer/fixed-deposits" class="btn btn-gold"><i class="fa-solid fa-arrow-left me-1"></i> Back to FDs</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
