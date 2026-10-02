<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="System Settings | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-gears text-primary me-2"></i> System Configuration & Limits</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="sk-card p-4">
                    <form action="${pageContext.request.contextPath}/admin/settings" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label fw-bold small">Max Single Transfer Amount (₹)</label>
                                <input type="number" step="0.01" name="MAX_TRANSFER_AMOUNT" class="form-control" required value="500000.00">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold small">Daily Aggregated Transfer Limit (₹)</label>
                                <input type="number" step="0.01" name="DAILY_TRANSFER_LIMIT" class="form-control" required value="1000000.00">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label fw-bold small">Max Single Withdrawal Amount (₹)</label>
                                <input type="number" step="0.01" name="MAX_WITHDRAWAL_AMOUNT" class="form-control" required value="50000.00">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold small">Daily Aggregated Withdrawal Limit (₹)</label>
                                <input type="number" step="0.01" name="DAILY_WITHDRAWAL_LIMIT" class="form-control" required value="100000.00">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label fw-bold small">Max UPI Transaction Amount (₹)</label>
                                <input type="number" step="0.01" name="MAX_UPI_TRANSACTION_AMOUNT" class="form-control" required value="100000.00">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold small">OTP Expiry (Minutes)</label>
                                <input type="number" name="OTP_EXPIRY_MINUTES" class="form-control" required value="5">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label fw-bold small">Session Timeout (Minutes)</label>
                                <input type="number" name="SESSION_TIMEOUT_MINUTES" class="form-control" required value="30">
                            </div>
                        </div>

                        <button type="submit" class="btn btn-gold fw-bold py-2 mt-4 px-4"><i class="fa-solid fa-save me-1"></i> Save System Limits</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
