<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Payment Successful | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="sk-card p-4 text-center shadow-lg border-success" id="receiptCard">
                    <div class="text-success fs-1 mb-2"><i class="fa-solid fa-circle-check"></i></div>
                    <h3 class="fw-bold text-navy">Payment Successful</h3>
                    <p class="text-muted small">Your transaction has been processed securely.</p>

                    <div class="border-top border-bottom py-3 my-3 text-start bg-light p-3 rounded">
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Transaction Reference:</span>
                            <strong class="font-monospace text-navy">${sessionScope.LAST_TRANSFER.referenceNumber}</strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Amount Sent:</span>
                            <strong class="text-success fs-4">₹<fmt:formatNumber value="${sessionScope.LAST_TRANSFER.amount}" pattern="#,##0.00"/></strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Recipient:</span>
                            <strong>${sessionScope.LAST_TRANSFER_DTO.recipientName}</strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Date & Time:</span>
                            <span><fmt:formatDate value="${sessionScope.LAST_TRANSFER.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></span>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span class="text-muted">Status:</span>
                            <span class="badge bg-success">COMPLETED</span>
                        </div>
                    </div>

                    <div class="d-flex gap-2 justify-content-center d-print-none">
                        <button onclick="window.print()" class="btn btn-outline-primary"><i class="fa-solid fa-print me-1"></i> Print Receipt</button>
                        <a href="${pageContext.request.contextPath}/customer/dashboard" class="btn btn-gold fw-bold"><i class="fa-solid fa-house me-1"></i> Dashboard</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
