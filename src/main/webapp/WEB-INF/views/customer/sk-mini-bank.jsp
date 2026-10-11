<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="SK Mini Bank Payment Portal | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h4 class="fw-bold text-navy mb-1"><i class="fa-solid fa-building-columns text-warning me-2"></i>SK Mini Bank Payment Portal & Terminal</h4>
                <p class="text-muted small mb-0">Direct inter-bank transfers and live banking terminal integration with SK Mini Bank (skminibank-9o8v.onrender.com).</p>
            </div>
            <a href="${pageContext.request.contextPath}/customer/dashboard" class="btn btn-outline-secondary btn-sm">
                <i class="fa-solid fa-arrow-left me-1"></i> Back
            </a>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                <i class="fa-solid fa-circle-exclamation me-2"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="row g-4 mb-4">
            <!-- PAYMENT FORM -->
            <div class="col-lg-6">
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-white">
                    <div class="d-flex align-items-center gap-2 mb-3">
                        <i class="fa-solid fa-paper-plane fs-3 text-warning"></i>
                        <div>
                            <h5 class="fw-bold text-navy mb-0">Send Money to SK Mini Bank</h5>
                            <small class="text-muted">Instant Inter-Bank Transfer via SK Mini Bank Network</small>
                        </div>
                    </div>

                    <form action="${pageContext.request.contextPath}/customer/sk-mini-bank" method="POST">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Debit From Account *</label>
                            <select name="senderAccountId" class="form-select rounded-3" required>
                                <c:forEach items="${accounts}" var="a">
                                    <option value="${a.accountId}">${a.maskedAccountNumber} - Available: ₹<fmt:formatNumber value="${a.availableBalance}" pattern="#,##0.00"/></option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">SK Mini Bank Account No / UPI ID *</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="fa-solid fa-at text-warning"></i></span>
                                <input type="text" name="miniBankDetail" class="form-control rounded-end-3" required placeholder="e.g. 9897001122@skminibank or Account No">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Transfer Amount (₹) *</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light fw-bold text-success">₹</span>
                                <input type="number" step="0.01" min="1.00" name="amount" class="form-control rounded-end-3 fs-4 fw-bold text-success" required placeholder="0.00">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Remarks / Purpose (Optional)</label>
                            <input type="text" name="remarks" class="form-control rounded-3" placeholder="e.g. SK Mini Bank Deposit">
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold text-muted small">Enter 4-Digit UPI PIN *</label>
                            <input type="password" name="upiPin" maxlength="4" pattern="[0-9]{4}" class="form-control rounded-3 text-center fs-4 font-monospace" required placeholder="••••">
                        </div>

                        <button type="submit" class="btn btn-gold btn-lg w-100 fw-bold rounded-3 shadow">
                            <i class="fa-solid fa-shield-halved me-2"></i>Pay to SK Mini Bank Now
                        </button>
                    </form>
                </div>
            </div>

            <!-- EMBEDDED LIVE SK MINI BANK TERMINAL -->
            <div class="col-lg-6">
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-white">
                    <div class="d-flex align-items-center justify-content-between mb-3">
                        <h5 class="fw-bold text-navy mb-0"><i class="fa-solid fa-globe text-primary me-2"></i>Live SK Mini Bank Terminal</h5>
                        <a href="https://skminibank-9o8v.onrender.com" target="_blank" class="btn btn-outline-primary btn-sm rounded-pill">
                            <i class="fa-solid fa-arrow-up-right-from-square me-1"></i> Open Terminal
                        </a>
                    </div>

                    <div class="ratio ratio-16x9 rounded-3 overflow-hidden border shadow-sm" style="min-height: 380px;">
                        <iframe src="https://skminibank-9o8v.onrender.com" title="SK Mini Bank Terminal" allowfullscreen></iframe>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
