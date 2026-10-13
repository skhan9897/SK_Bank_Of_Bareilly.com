<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Bareilly Bank of India | BBIN Portal | SK BANK OF INDIA" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h4 class="fw-bold text-navy mb-1"><i class="fa-solid fa-building-columns text-success me-2"></i>Bareilly Bank of India (BBIN) Gateway</h4>
                <p class="text-muted small mb-0">Official Participating Member Bank • Inter-Bank Clearing & Settlement Network.</p>
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
            <!-- INFO CARD -->
            <div class="col-lg-5">
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-dark text-white text-center position-relative overflow-hidden mb-4">
                    <span class="badge bg-success text-white fw-bold mb-2">SCHEDULED COMMERCIAL BANK</span>
                    <h5 class="fw-bold text-gold mb-1">Bareilly Bank of India</h5>
                    <p class="small text-white-50 mb-3">IFSC Prefix: <strong class="text-white font-monospace">BBIN0000001</strong></p>

                    <div class="p-3 bg-navy rounded-3 text-start border border-secondary">
                        <div class="d-flex justify-content-between mb-1">
                            <span class="text-white-50 small">Bank Code:</span>
                            <strong class="font-monospace text-gold">BBIN</strong>
                        </div>
                        <div class="d-flex justify-content-between mb-1">
                            <span class="text-white-50 small">Clearing Status:</span>
                            <strong class="text-success">ACTIVE &amp; VERIFIED</strong>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span class="text-white-50 small">UPI Handle Suffix:</span>
                            <strong class="font-monospace text-warning">@bareillybank</strong>
                        </div>
                    </div>
                </div>
            </div>

            <!-- TRANSFER FORM -->
            <div class="col-lg-7">
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-white">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-paper-plane me-2 text-success"></i>Transfer via Bareilly Bank of India</h5>

                    <form action="${pageContext.request.contextPath}/customer/bareilly-bank" method="POST">
                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Select Source Account *</label>
                            <select name="senderAccountId" class="form-select rounded-3" required>
                                <c:forEach items="${accounts}" var="a">
                                    <option value="${a.accountId}">${a.maskedAccountNumber} - Available: ₹<fmt:formatNumber value="${a.availableBalance}" pattern="#,##0.00"/></option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Recipient Account / Mobile / UPI ID (@bareillybank) *</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="fa-solid fa-at text-success"></i></span>
                                <input type="text" name="recipientDetail" class="form-control rounded-end-3" required placeholder="e.g. 9897001122@bareillybank or Account No">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Amount (₹) *</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light fw-bold text-success">₹</span>
                                <input type="number" step="0.01" min="1.00" name="amount" class="form-control rounded-end-3 fs-4 fw-bold text-success" required placeholder="0.00">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Remarks (Optional)</label>
                            <input type="text" name="remarks" class="form-control rounded-3" placeholder="e.g. BBIN Secure Transfer">
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold text-muted small">Enter 4-Digit UPI PIN *</label>
                            <input type="password" name="upiPin" maxlength="4" pattern="[0-9]{4}" class="form-control rounded-3 text-center fs-4 font-monospace" required placeholder="••••">
                        </div>

                        <button type="submit" class="btn btn-gold btn-lg w-100 fw-bold rounded-3 shadow">
                            <i class="fa-solid fa-shield-halved me-2"></i>Send via Bareilly Bank of India
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
