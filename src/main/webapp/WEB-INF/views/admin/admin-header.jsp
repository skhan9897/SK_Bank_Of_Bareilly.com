<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar navbar-expand-lg bank-navbar sticky-top">
    <div class="container-fluid px-lg-4">
        <a class="navbar-brand d-flex align-items-center gap-2" href="${pageContext.request.contextPath}/admin/dashboard">
            <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png"
                 alt="SK Bank Admin" class="bank-logo-img">
            <span class="text-white fw-bold fs-5">SK Bank Administration</span>
        </a>

        <div class="d-flex align-items-center gap-3 ms-auto">
            <span class="text-warning fw-semibold"><i class="fas fa-user-shield me-1"></i> Welcome, Administrator</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm"><i class="fas fa-sign-out-alt"></i> Logout</a>
        </div>
    </div>
</nav>
