<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Customer Login | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="sk-card p-4 shadow-lg border-gold">
                <div class="text-center mb-4">
                    <img src="${pageContext.request.contextPath}/assets/images/sk-bank-logo-final-transparent.png?v=20261004" alt="Logo" width="70" class="mb-2" onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/assets/images/sk-bank-logo-transparent.png';">
                    <h4 class="fw-bold text-navy">Customer Login</h4>
                    <p class="text-muted small">Access your SK Bank accounts securely</p>
                </div>

                <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

                <form action="${pageContext.request.contextPath}/login" method="post">
                    <div class="mb-3">
                        <label class="form-label fw-bold small">Username or Customer ID</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light"><i class="fa-solid fa-user text-muted"></i></span>
                            <input type="text" name="username" class="form-control" required placeholder="Enter username">
                        </div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-bold small">Password</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light"><i class="fa-solid fa-lock text-muted"></i></span>
                            <input type="password" name="password" class="form-control" required placeholder="Enter password">
                        </div>
                    </div>

                    <button type="submit" class="btn btn-gold w-100 py-2 fw-bold mb-3"><i class="fa-solid fa-shield-halved me-1"></i> Proceed to OTP</button>
                </form>

                <div class="text-center border-top pt-3 mt-2">
                    <span class="small text-muted">Don't have an account yet? </span>
                    <a href="${pageContext.request.contextPath}/register" class="fw-bold text-decoration-none" style="color: #0B4EA2;">Open Account</a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
