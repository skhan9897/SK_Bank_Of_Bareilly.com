<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Login - SK Bank of Bareilly" />
</jsp:include>
<body>

    <div class="container-fluid p-0 overflow-hidden">
        <div class="row g-0 min-vh-100">
            <!-- LEFT SPLIT SCREEN (BRAND BACKGROUND) -->
            <div class="col-lg-7 d-none d-lg-block login-bg position-relative">
                <div class="position-absolute top-0 start-0 w-100 h-100 d-flex flex-column justify-content-between p-5 text-white">
                    <div class="d-flex align-items-center gap-3">
                        <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="Logo" style="height: 55px; object-fit: contain;">
                        <h4 class="fw-bold mb-0 text-white">SK Bank of Bareilly</h4>
                    </div>
                    <div class="my-auto">
                        <span class="badge badge-gold fs-6 mb-3 px-3 py-2">Secure Internet Banking</span>
                        <h1 class="display-4 fw-bold mb-3">Empowering Your Financial Future</h1>
                        <p class="fs-5 text-white-50 max-w-75">
                            Bank anytime, anywhere with SK Bank of Bareilly's encrypted online portal. Transfer funds, pay bills, manage loans, and track investments with ease.
                        </p>
                    </div>
                    <div class="small text-white-50">
                        <i class="fas fa-shield-alt me-1 text-warning"></i> Protected by 256-bit SSL encryption.
                    </div>
                </div>
            </div>

            <!-- RIGHT SPLIT SCREEN (LOGIN FORM) -->
            <div class="col-lg-5 d-flex align-items-center justify-content-center bg-white p-4 p-sm-5">
                <div class="w-100" style="max-width: 420px;">
                    <div class="text-center mb-4">
                        <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="SK Bank Logo" class="img-fluid mb-3" style="max-height: 110px; object-fit: contain;">
                        <h3 class="fw-bold text-navy mb-1">Welcome Back</h3>
                        <p class="text-muted small">Login to your SK Bank account</p>
                    </div>

                    <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

                    <form action="${pageContext.request.contextPath}/login" method="post" class="mt-4">
                        <div class="mb-3">
                            <label class="form-label fw-semibold text-navy"><i class="fas fa-user me-1 text-warning"></i> Customer ID / Username</label>
                            <input type="text" name="username" class="form-control form-control-lg fs-6" placeholder="Enter Customer ID or Username" required autofocus>
                        </div>

                        <div class="mb-3">
                            <div class="d-flex justify-content-between align-items-center">
                                <label class="form-label fw-semibold text-navy mb-0"><i class="fas fa-key me-1 text-warning"></i> Password</label>
                                <a href="${pageContext.request.contextPath}/forgot-password.jsp" class="small text-decoration-none text-primary">Forgot Password?</a>
                            </div>
                            <input type="password" name="password" class="form-control form-control-lg fs-6 mt-1" placeholder="Enter Password" required>
                        </div>

                        <div class="mb-4 form-check">
                            <input type="checkbox" class="form-check-input" id="rememberMe">
                            <label class="form-check-label small text-muted" for="rememberMe">Remember me on this device</label>
                        </div>

                        <button type="submit" class="btn btn-navy w-100 py-3 fw-bold fs-6 shadow-sm"><i class="fas fa-lock me-2"></i> LOGIN SECURELY</button>
                    </form>

                    <div class="mt-4 pt-3 text-center border-top">
                        <p class="text-muted small mb-0">Don't have an account yet?</p>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-gold btn-sm w-100 mt-2 py-2"><i class="fas fa-user-plus me-1"></i> Open a New Bank Account</a>
                    </div>

                    <!-- PROMINENT ADMIN LOGIN LINK -->
                    <div class="mt-3 p-2 bg-light rounded text-center border">
                        <a href="${pageContext.request.contextPath}/admin/login" class="text-navy fw-bold small text-decoration-none">
                            <i class="fas fa-user-shield text-warning me-1"></i> Are you a Bank Officer? <strong>Admin Login Portal →</strong>
                        </a>
                    </div>

                    <div class="mt-3 text-center">
                        <a href="${pageContext.request.contextPath}/" class="text-secondary small text-decoration-none"><i class="fas fa-arrow-left me-1"></i> Back to Homepage</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

</body>
</html>
