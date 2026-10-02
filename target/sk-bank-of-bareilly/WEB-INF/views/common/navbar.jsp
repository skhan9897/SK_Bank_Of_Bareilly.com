<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-expand-lg navbar-sk">
    <div class="container-fluid">
        <a class="navbar-brand d-flex align-items-center" href="${pageContext.request.contextPath}/">
            <img src="${pageContext.request.contextPath}/assets/images/sk-bank-logo-transparent.png" alt="Logo" width="45" height="45" class="me-2">
            <div>
                <span class="navbar-brand-text">SK BANK OF BAREILLY</span>
                <span class="navbar-brand-tagline">TRUST | GROWTH | TOGETHER</span>
            </div>
        </a>
        <button class="navbar-toggler text-white border-0" type="button" data-bs-toggle="collapse" data-bs-target="#topNav">
            <i class="fa-solid fa-bars fs-4"></i>
        </button>
        <div class="collapse navbar-collapse" id="topNav">
            <ul class="navbar-nav ms-auto align-items-center">
                <li class="nav-item"><a class="nav-link nav-link-sk" href="${pageContext.request.contextPath}/#home">Home</a></li>
                <li class="nav-item"><a class="nav-link nav-link-sk" href="${pageContext.request.contextPath}/#personal">Personal Banking</a></li>
                <li class="nav-item"><a class="nav-link nav-link-sk" href="${pageContext.request.contextPath}/#business">Business Banking</a></li>
                <li class="nav-item"><a class="nav-link nav-link-sk" href="${pageContext.request.contextPath}/#loans">Loans</a></li>
                <li class="nav-item"><a class="nav-link nav-link-sk" href="${pageContext.request.contextPath}/#fd">Fixed Deposit</a></li>
                <li class="nav-item"><a class="nav-link nav-link-sk" href="${pageContext.request.contextPath}/#contact">Contact Us</a></li>
                <c:choose>
                    <c:when test="${not empty sessionScope.AUTHENTICATED_USER}">
                        <c:choose>
                            <c:when test="${sessionScope.ROLE == 'CUSTOMER'}">
                                <li class="nav-item ms-lg-3"><a class="btn btn-gold" href="${pageContext.request.contextPath}/customer/dashboard"><i class="fa-solid fa-gauge me-1"></i> Dashboard</a></li>
                                <li class="nav-item ms-2"><a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/logout">Logout</a></li>
                            </c:when>
                            <c:otherwise>
                                <li class="nav-item ms-lg-3"><a class="btn btn-gold" href="${pageContext.request.contextPath}/admin/dashboard"><i class="fa-solid fa-user-shield me-1"></i> Admin Panel</a></li>
                                <li class="nav-item ms-2"><a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/admin/logout">Logout</a></li>
                            </c:otherwise>
                        </c:choose>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item ms-lg-3"><a class="btn btn-outline-gold me-2" href="${pageContext.request.contextPath}/login"><i class="fa-solid fa-right-to-bracket me-1"></i> Login</a></li>
                        <li class="nav-item"><a class="btn btn-gold" href="${pageContext.request.contextPath}/register"><i class="fa-solid fa-user-plus me-1"></i> Open Account</a></li>
                        <li class="nav-item ms-lg-2"><a class="nav-link nav-link-sk text-warning" href="${pageContext.request.contextPath}/admin/login"><i class="fa-solid fa-lock me-1"></i> Admin</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>
