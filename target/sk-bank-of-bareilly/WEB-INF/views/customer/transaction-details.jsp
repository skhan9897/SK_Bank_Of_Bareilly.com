<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Transaction Receipt | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="sk-card p-4 text-center shadow-lg">
                    <img src="${pageContext.request.contextPath}/assets/images/sk-bank-logo.svg" alt="Logo" width="60" class="mb-2">
                    <h4 class="fw-bold text-navy">SK BANK OF BAREILLY</h4>
                    <p class="text-muted small">Transaction Receipt</p>

                    <div class="border-top border-bottom py-3 my-3 text-start bg-light p-3 rounded">
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Transaction Reference:</span>
                            <strong class="font-monospace text-navy">${txn.transactionReference}</strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Date & Time:</span>
                            <span><fmt:formatDate value="${txn.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Transaction Type:</span>
                            <span class="badge bg-primary">${txn.transactionType}</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Amount:</span>
                            <strong class="fs-4 text-success">₹<fmt:formatNumber value="${txn.amount}" pattern="#,##0.00"/></strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Balance After:</span>
                            <span>₹<fmt:formatNumber value="${txn.balanceAfter}" pattern="#,##0.00"/></span>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span class="text-muted">Status:</span>
                            <span class="badge bg-success">SUCCESS</span>
                        </div>
                    </div>

                    <div class="d-flex gap-2 justify-content-center d-print-none">
                        <button onclick="window.print()" class="btn btn-outline-primary"><i class="fa-solid fa-print me-1"></i> Print Receipt</button>
                        <a href="${pageContext.request.contextPath}/customer/transactions" class="btn btn-gold"><i class="fa-solid fa-list me-1"></i> Back to History</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
