<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar navbar-expand-lg bank-navbar sticky-top">
    <div class="container-fluid px-lg-4">
        <a class="navbar-brand d-flex align-items-center gap-2" href="${pageContext.request.contextPath}/">
            <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png"
                 alt="SK Bank of Bareilly" class="bank-logo-img">
            <span class="text-white fw-bold fs-5 d-none d-sm-inline">SK Bank of Bareilly</span>
        </a>

        <button class="navbar-toggler text-white border-white" type="button" data-bs-toggle="collapse" data-bs-target="#bankNav">
            <i class="fas fa-bars fs-4"></i>
        </button>

        <div class="collapse navbar-collapse" id="bankNav">
            <ul class="navbar-nav mx-auto mb-2 mb-lg-0">
                <li class="nav-item"><a class="nav-link bank-nav-link" href="${pageContext.request.contextPath}/">Home</a></li>
                <li class="nav-item"><a class="nav-link bank-nav-link" href="${pageContext.request.contextPath}/#services">Personal Banking</a></li>
                <li class="nav-item"><a class="nav-link bank-nav-link" href="${pageContext.request.contextPath}/#services">Business Banking</a></li>
                <li class="nav-item"><a class="nav-link bank-nav-link" href="${pageContext.request.contextPath}/#loans">Loans</a></li>
                <li class="nav-item"><a class="nav-link bank-nav-link" href="${pageContext.request.contextPath}/#deposits">Fixed Deposit</a></li>
                <li class="nav-item"><a class="nav-link bank-nav-link" href="${pageContext.request.contextPath}/contact">Contact Us</a></li>
            </ul>

            <div class="d-flex align-items-center gap-2">
                <c:choose>
                    <c:when test="${not empty sessionScope.loggedInUser}">
                        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-gold btn-sm"><i class="fas fa-chart-line me-1"></i> Dashboard</a>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm"><i class="fas fa-sign-out-alt"></i> Logout</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-gold btn-sm px-3"><i class="fas fa-lock me-1"></i> Login</a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-gold btn-sm px-3"><i class="fas fa-user-plus me-1"></i> Open Account</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>
