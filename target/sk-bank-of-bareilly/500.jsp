<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="500 Server Error - SK Bank" />
</jsp:include>
<body class="bg-light">
    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />
    <div class="container py-5 text-center my-5">
        <div class="card-custom p-4 p-md-5 bg-white max-w-50 mx-auto shadow">
            <h1 class="display-1 fw-bold text-danger">500</h1>
            <h3 class="fw-bold text-navy mb-3">Internal Server Error</h3>
            <p class="text-muted mb-4">
                An unexpected technical issue occurred. Our technical team has been notified.
                <c:if test="${not empty pageContext.exception}">
                    <br><small class="text-danger mt-2 d-block">Error Details: ${pageContext.exception.message}</small>
                </c:if>
                <c:if test="${not empty requestScope['javax.servlet.error.message']}">
                    <br><small class="text-danger mt-2 d-block">Message: ${requestScope['javax.servlet.error.message']}</small>
                </c:if>
            </p>
            <a href="${pageContext.request.contextPath}/" class="btn btn-gold"><i class="fas fa-home me-1"></i> Back to Safety</a>
        </div>
    </div>
    <jsp:include page="/WEB-INF/views/common/footer.jsp" />
</body>
</html>
