<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Admin Portal Login - SK Bank" />
</jsp:include>
<body class="login-bg d-flex align-items-center justify-content-center min-vh-100">

    <div class="container d-flex align-items-center justify-content-center py-5">
        <div class="login-card-theme p-4 p-sm-5 shadow-lg w-100" style="max-width: 440px;">
            <div class="text-center mb-4">
                <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="SK Bank Logo" class="img-fluid mb-2" style="max-height: 100px; object-fit: contain;">
                <h3 class="fw-bold text-white mb-0">SK Bank Admin Portal</h3>
                <small class="text-gold"><i class="fas fa-shield-alt me-1"></i> Authorized Personnel Only</small>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <form action="${pageContext.request.contextPath}/admin/login" method="post" class="mt-3">
                <div class="mb-3">
                    <label class="form-label fw-bold"><i class="fas fa-user-shield text-gold me-1"></i> Admin Username</label>
                    <input type="text" name="username" class="form-control form-control-lg fs-6" placeholder="admin" required autofocus>
                </div>
                <div class="mb-4">
                    <label class="form-label fw-bold"><i class="fas fa-lock text-gold me-1"></i> Password</label>
                    <input type="password" name="password" class="form-control form-control-lg fs-6" placeholder="Admin Password" required>
                </div>
                <button type="submit" class="btn btn-bank-gold w-100 py-3 fw-bold fs-6 shadow"><i class="fas fa-key me-2"></i> LOGIN AS ADMINISTRATOR</button>
            </form>

            <div class="mt-4 pt-3 text-center border-top border-secondary">
                <a href="${pageContext.request.contextPath}/login" class="small text-white-50 text-decoration-none"><i class="fas fa-arrow-left me-1"></i> Return to Customer Login</a>
            </div>
        </div>
    </div>

</body>
</html>
