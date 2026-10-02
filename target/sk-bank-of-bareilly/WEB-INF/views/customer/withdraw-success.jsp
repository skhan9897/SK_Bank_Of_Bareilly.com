<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Withdrawal Successful | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="sk-card p-4 text-center shadow-lg border-success">
                    <div class="text-success fs-1 mb-2"><i class="fa-solid fa-circle-check"></i></div>
                    <h3 class="fw-bold text-navy">Withdrawal Successful</h3>
                    <p class="text-muted small">Your account balance has been updated.</p>

                    <div class="border-top border-bottom py-3 my-3 text-start bg-light p-3 rounded">
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Transaction Ref:</span>
                            <strong class="font-monospace text-navy">${sessionScope.LAST_WITHDRAWAL_TXN.transactionReference}</strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Amount Debited:</span>
                            <strong class="text-danger fs-4">₹<fmt:formatNumber value="${sessionScope.LAST_WITHDRAWAL_TXN.amount}" pattern="#,##0.00"/></strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">New Balance:</span>
                            <strong class="text-success">₹<fmt:formatNumber value="${sessionScope.LAST_WITHDRAWAL_TXN.balanceAfter}" pattern="#,##0.00"/></strong>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span class="text-muted">Date & Time:</span>
                            <span><fmt:formatDate value="${sessionScope.LAST_WITHDRAWAL_TXN.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></span>
                        </div>
                    </div>

                    <a href="${pageContext.request.contextPath}/customer/dashboard" class="btn btn-gold fw-bold px-4"><i class="fa-solid fa-house me-1"></i> Dashboard</a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
