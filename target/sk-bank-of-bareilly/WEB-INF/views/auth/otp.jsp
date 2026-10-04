<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Verify OTP | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="sk-card p-4 shadow-lg border-gold text-center">
                <div class="mb-3 text-center">
                    <img src="${pageContext.request.contextPath}/assets/images/sk-bank-logo-final-transparent.png?v=20261004" alt="Logo" width="60" class="mb-2" onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/assets/images/sk-bank-logo-transparent.png';">
                </div>
                <h4 class="fw-bold text-navy mb-1">Two-Factor OTP Verification</h4>
                <p class="text-muted small mb-3">A 6-digit OTP has been dispatched to your registered mobile number.</p>

                <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

                <c:if test="${not empty devOtp}">
                    <div class="alert alert-info py-2 small mb-3">
                        <i class="fa-solid fa-bug me-1"></i> <strong>Dev Mode OTP:</strong> <span class="badge bg-navy text-gold font-monospace fs-6 px-3 py-1">${devOtp}</span>
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/verify-otp" method="post">
                    <input type="hidden" name="purpose" value="${param.purpose}">
                    <div class="mb-4">
                        <input type="text" name="otp" pattern="[0-9]{6}" maxlength="6" class="form-control form-control-lg text-center font-monospace fw-bold fs-3 letter-spacing-3" required placeholder="1 2 3 4 5 6" autofocus>
                    </div>

                    <button type="submit" class="btn btn-gold w-100 py-2 fw-bold mb-3"><i class="fa-solid fa-lock me-1"></i> Verify OTP & Log In</button>
                </form>

                <div class="small text-muted">
                    Didn't receive OTP? <a href="#" onclick="alert('New OTP sent.'); return false;" class="fw-bold text-decoration-none">Resend OTP</a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
