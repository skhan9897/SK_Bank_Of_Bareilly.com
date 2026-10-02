<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Confirm Transfer | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="sk-card p-4 text-center shadow-lg border-gold">
                    <div class="text-warning fs-1 mb-2"><i class="fa-solid fa-shield-halved"></i></div>
                    <h4 class="fw-bold text-navy mb-3">Confirm Payment Details</h4>

                    <div class="bg-light p-3 rounded text-start mb-4">
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Recipient:</span>
                            <strong class="text-navy">${transferData.recipientName}</strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Amount:</span>
                            <strong class="text-success fs-5">₹<fmt:formatNumber value="${transferData.amount}" pattern="#,##0.00"/></strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Transfer Mode:</span>
                            <span class="badge bg-primary">${transferData.transferType}</span>
                        </div>
                        <c:if test="${not empty transferData.remarks}">
                            <div class="d-flex justify-content-between">
                                <span class="text-muted">Remarks:</span>
                                <span>${transferData.remarks}</span>
                            </div>
                        </c:if>
                    </div>

                    <form action="${pageContext.request.contextPath}/customer/transfer" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                        <input type="hidden" name="senderAccountId" value="${transferData.senderAccountId}">
                        <input type="hidden" name="receiverAccountId" value="${transferData.receiverAccountId}">
                        <input type="hidden" name="amount" value="${transferData.amount}">
                        <input type="hidden" name="transferType" value="${transferData.transferType}">
                        <input type="hidden" name="remarks" value="${transferData.remarks}">
                        <input type="hidden" name="recipientName" value="${transferData.recipientName}">

                        <button type="submit" class="btn btn-gold w-100 py-3 fw-bold fs-5 mb-2"><i class="fa-solid fa-lock me-2"></i> Confirm & Authorize Transfer</button>
                        <a href="${pageContext.request.contextPath}/customer/send-money" class="btn btn-outline-secondary w-100">Cancel</a>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
