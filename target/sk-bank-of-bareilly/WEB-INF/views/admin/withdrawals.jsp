<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Admin Cash Withdrawal | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-circle-minus text-danger me-2"></i> Counter Cash Withdrawal</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="sk-card p-4">
                    <form action="${pageContext.request.contextPath}/admin/withdrawals" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Target Account ID *</label>
                            <input type="number" name="accountId" class="form-control form-control-lg" required placeholder="Enter account ID">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Withdrawal Amount (₹) *</label>
                            <input type="number" step="0.01" min="1" name="amount" class="form-control form-control-lg" required placeholder="0.00">
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold small">Reason / Customer Consent Verification *</label>
                            <input type="text" name="reason" class="form-control" required placeholder="Counter Cash Withdrawal Signed">
                        </div>

                        <button type="submit" class="btn btn-navy text-white w-100 py-3 fw-bold fs-5" style="background-color: #071F49;"><i class="fa-solid fa-circle-check me-2"></i> Authorize Cash Withdrawal</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
