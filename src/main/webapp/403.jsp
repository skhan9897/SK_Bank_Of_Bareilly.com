<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="403 Access Denied - SK Bank" />
</jsp:include>
<body class="bg-light">
    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />
    <div class="container py-5 text-center my-5">
        <div class="card-custom p-5 bg-white max-w-50 mx-auto shadow border-danger border-2">
            <h1 class="display-1 fw-bold text-danger">403</h1>
            <h3 class="fw-bold text-navy mb-3">Access Forbidden</h3>
            <p class="text-muted mb-4">You do not have administrative privileges to access this area.</p>
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-navy"><i class="fas fa-arrow-left me-1"></i> Return to Customer Dashboard</a>
        </div>
    </div>
    <jsp:include page="/WEB-INF/views/common/footer.jsp" />
</body>
</html>
