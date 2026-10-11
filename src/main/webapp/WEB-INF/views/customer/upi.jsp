<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="My UPI & 4-Digit PIN | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-qrcode text-primary me-2"></i> UPI Settings & 4-Digit Security PIN</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <c:if test="${not empty param.msg}">
            <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
                <i class="fa-solid fa-circle-check me-2"></i> ${param.msg}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="row g-4">
            <div class="col-lg-6">
                <div class="sk-card p-4 rounded-4 shadow-sm bg-white">
                    <h5 class="fw-bold text-navy mb-3">Auto-Generated Active UPI ID</h5>

                    <div class="bg-light p-3 rounded-3 mb-4 border">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="text-muted small">Registered UPI Handle:</span>
                            <strong class="font-monospace text-primary fs-5">${upi.upiAddress}</strong>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="text-muted small">Status:</span>
                            <span class="badge bg-success px-3 py-1 rounded-pill">${upi.status}</span>
                        </div>
                        <small class="text-muted"><i class="fa-solid fa-circle-check text-success me-1"></i> Linked to Mobile Number</small>
                    </div>

                    <form action="${pageContext.request.contextPath}/customer/upi" method="post" class="p-3 bg-light rounded-3 border">
                        <input type="hidden" name="action" value="setPin">
                        <h6 class="fw-bold text-navy mb-2"><i class="fa-solid fa-key text-warning me-2"></i>Set / Update 4-Digit UPI PIN</h6>
                        <p class="text-muted small mb-3">Mandatory 4-digit PIN required for all payments and transfers.</p>

                        <div class="mb-2">
                            <label class="form-label fw-bold text-muted small">Current PIN (Default: 1234)</label>
                            <input type="password" name="oldPin" maxlength="4" pattern="[0-9]{4}" class="form-control text-center font-monospace fs-5" placeholder="••••">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">New 4-Digit Secret UPI PIN *</label>
                            <input type="password" name="newPin" maxlength="4" pattern="[0-9]{4}" class="form-control text-center font-monospace fs-4 text-primary" required placeholder="••••">
                        </div>

                        <button type="submit" class="btn btn-gold w-100 fw-bold shadow-sm">
                            <i class="fa-solid fa-lock me-1"></i> Save 4-Digit UPI PIN
                        </button>
                    </form>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="sk-card p-4 rounded-4 shadow-sm bg-white">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-shield-halved text-warning me-2"></i> Mandatory 4-Digit PIN Security</h5>
                    <ul class="list-group list-group-flush small">
                        <li class="list-group-item bg-transparent px-0"><i class="fa-solid fa-check-double text-success me-2"></i> <strong>Automatic UPI Handle:</strong> Your UPI handle (e.g. <code>${upi.upiAddress}</code>) is automatically created based on your registered mobile number.</li>
                        <li class="list-group-item bg-transparent px-0"><i class="fa-solid fa-lock text-primary me-2"></i> <strong>Mandatory Verification:</strong> Every payment, transfer, or QR scan requires entering your 4-digit UPI PIN.</li>
                        <li class="list-group-item bg-transparent px-0"><i class="fa-solid fa-key text-warning me-2"></i> <strong>Default PIN:</strong> Initial default PIN for new accounts is <code>1234</code>. Please update your secret 4-digit PIN above.</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
