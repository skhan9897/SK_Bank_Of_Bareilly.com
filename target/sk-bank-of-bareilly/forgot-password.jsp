<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Forgot Password - SK Bank of Bareilly" />
</jsp:include>
<body class="bg-light">

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="container py-5 my-5">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="card-custom p-4 p-sm-5 bg-white shadow-lg text-center">
                    <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="SK Bank Logo" class="img-fluid mb-3" style="max-height: 90px; object-fit: contain;">
                    <h3 class="fw-bold text-navy">Password Reset Request</h3>
                    <p class="text-muted small">Enter your registered Email or Customer ID. An OTP link will be sent to your verified mobile/email.</p>

                    <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

                    <form action="${pageContext.request.contextPath}/login" method="get" class="mt-4">
                        <div class="mb-3 text-start">
                            <label class="form-label fw-semibold text-navy">Customer ID / Username / Email</label>
                            <input type="text" class="form-control" required placeholder="Enter ID or Email">
                        </div>
                        <button type="button" onclick="alert('Password reset instructions have been dispatched to your registered contact info.')" class="btn btn-navy w-100 py-2 fw-bold"><i class="fas fa-paper-plane me-2"></i> SEND RESET LINK</button>
                    </form>

                    <div class="mt-4 border-top pt-3">
                        <a href="${pageContext.request.contextPath}/login" class="text-secondary small text-decoration-none"><i class="fas fa-arrow-left me-1"></i> Return to Login</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
