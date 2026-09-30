<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="KYC Verification - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="kyc" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-id-card text-warning me-2"></i> KYC Verification</h3>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row justify-content-center">
                <div class="col-lg-8">
                    <div class="card-custom p-4 p-md-5 bg-white shadow-lg border-2 border-warning">

                        <!-- HEADER & LOGO -->
                        <div class="text-center mb-4">
                            <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="SK Bank Logo" class="img-fluid mb-2" style="max-height: 80px; object-fit: contain;">
                            <h2 class="fw-bold text-navy">KYC Verification</h2>
                            <p class="text-muted small">Verify your Aadhaar and PAN numbers securely with SK Bank of Bareilly.</p>

                            <!-- KYC STATUS BADGE -->
                            <div class="mt-2">
                                <c:choose>
                                    <c:when test="${kyc.kycStatus == 'VERIFIED' or sessionScope.customerProfile.kycStatus == 'VERIFIED'}">
                                        <span class="badge bg-success fs-6 px-3 py-2"><i class="fas fa-check-circle me-1"></i> ✓ KYC VERIFIED</span>
                                    </c:when>
                                    <c:when test="${kyc.kycStatus == 'REJECTED' or sessionScope.customerProfile.kycStatus == 'REJECTED'}">
                                        <span class="badge bg-danger fs-6 px-3 py-2"><i class="fas fa-times-circle me-1"></i> KYC REJECTED</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-warning text-dark fs-6 px-3 py-2"><i class="fas fa-clock me-1"></i> KYC PENDING</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- STATE 1: VERIFIED STATE -->
                        <c:choose>
                            <c:when test="${kyc.kycStatus == 'VERIFIED' or sessionScope.customerProfile.kycStatus == 'VERIFIED'}">
                                <div class="p-4 bg-light rounded border border-success mb-4 text-center">
                                    <div class="display-4 text-success mb-2"><i class="fas fa-check-circle"></i></div>
                                    <h4 class="fw-bold text-success mb-3">✓ KYC VERIFIED</h4>

                                    <div class="row g-3 text-start max-w-md mx-auto small">
                                        <div class="col-6 text-muted">Aadhaar Number:</div>
                                        <div class="col-6 fw-bold text-navy">${not empty kyc.aadhaarMasked ? kyc.aadhaarMasked : 'XXXX XXXX 9012'}</div>

                                        <div class="col-6 text-muted">PAN Number:</div>
                                        <div class="col-6 fw-bold text-navy">${not empty kyc.panMasked ? kyc.panMasked : 'ABCDE****F'}</div>

                                        <div class="col-6 text-muted">Verification Date:</div>
                                        <div class="col-6 fw-bold">${not empty kyc.verifiedAt ? kyc.verifiedAt : '30 Sep 2026'}</div>

                                        <div class="col-6 text-muted">Verification Reference:</div>
                                        <div class="col-6 fw-bold text-primary">${not empty kyc.verificationReference ? kyc.verificationReference : 'SKKYC202600001'}</div>
                                    </div>
                                </div>
                                <div class="alert alert-success text-center py-2 small mb-0">
                                    <i class="fas fa-shield-alt me-1"></i> Your identity is verified and locked. Re-verification is disabled.
                                </div>
                            </c:when>

                            <!-- STATE 2: REJECTED STATE -->
                            <c:when test="${kyc.kycStatus == 'REJECTED' or sessionScope.customerProfile.kycStatus == 'REJECTED'}">
                                <div class="p-4 bg-light rounded border border-danger mb-4 text-center">
                                    <div class="display-4 text-danger mb-2"><i class="fas fa-exclamation-triangle"></i></div>
                                    <h4 class="fw-bold text-danger mb-2">KYC Verification Failed</h4>
                                    <p class="text-muted small mb-3">Reason: <strong>${not empty kyc.rejectionReason ? kyc.rejectionReason : 'Aadhaar or PAN format verification failed.'}</strong></p>

                                    <a href="${pageContext.request.contextPath}/kyc?retry=true" class="btn btn-gold fw-bold"><i class="fas fa-redo me-1"></i> RE-VERIFY KYC</a>
                                </div>
                            </c:when>

                            <!-- STATE 3: PENDING STATE (FORM) -->
                            <c:otherwise>
                                <form action="${pageContext.request.contextPath}/kyc" method="post" id="kycForm">
                                    <div class="mb-4">
                                        <label class="form-label fw-bold text-navy">Aadhaar Number (12 Numeric Digits) *</label>
                                        <div class="input-group input-group-lg">
                                            <span class="input-group-text bg-navy text-warning"><i class="fas fa-id-card"></i></span>
                                            <input type="text" name="aadhaarNumber" class="form-control fs-6" pattern="\d{12}" maxlength="12" placeholder="123456789012" required>
                                        </div>
                                        <div class="form-text small">Enter your 12-digit Aadhaar number without spaces.</div>
                                    </div>

                                    <div class="mb-4">
                                        <label class="form-label fw-bold text-navy">PAN Number (10 Characters) *</label>
                                        <div class="input-group input-group-lg">
                                            <span class="input-group-text bg-navy text-warning"><i class="fas fa-address-card"></i></span>
                                            <input type="text" name="panNumber" class="form-control text-uppercase fs-6" pattern="[A-Za-z]{5}\d{4}[A-Za-z]{1}" maxlength="10" placeholder="ABCDE1234F" required>
                                        </div>
                                        <div class="form-text small">Format: 5 letters, 4 numbers, 1 letter (e.g. ABCDE1234F).</div>
                                    </div>

                                    <div class="d-grid mb-4">
                                        <button type="submit" class="btn btn-gold py-3 fw-bold fs-6 shadow-sm"><i class="fas fa-shield-alt me-2"></i> VERIFY KYC</button>
                                    </div>

                                    <div class="p-3 bg-light rounded text-center small text-muted">
                                        <i class="fas fa-lock text-warning me-1"></i> Your KYC information is securely processed. Never share your banking password, PIN or OTP.
                                    </div>
                                </form>
                            </c:otherwise>
                        </c:choose>

                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
