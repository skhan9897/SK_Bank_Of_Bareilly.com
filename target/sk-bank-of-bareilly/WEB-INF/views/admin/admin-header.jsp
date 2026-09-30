<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- MOBILE SIDEBAR OVERLAY BACKDROP FOR ADMIN -->
<div id="sidebarOverlay" class="sidebar-overlay"></div>

<nav class="navbar navbar-expand-lg bank-navbar sticky-top">
    <div class="container-fluid px-2 px-lg-4">
        <!-- MOBILE SIDEBAR HAMBURGER TOGGLE BUTTON -->
        <button id="mobileMenuBtn" class="mobile-menu-btn d-lg-none me-1" type="button" aria-label="Open navigation menu">
            <i class="fas fa-bars"></i>
        </button>

        <a class="navbar-brand d-flex align-items-center gap-2 me-auto" href="${pageContext.request.contextPath}/admin/dashboard">
            <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png"
                 alt="SK Bank Admin" class="bank-logo-img">
            <span class="text-white fw-bold fs-5 bank-title-text">SK Bank Administration</span>
        </a>

        <div class="d-flex align-items-center gap-2 ms-auto">
            <span class="text-warning fw-semibold d-none d-sm-inline"><i class="fas fa-user-shield me-1"></i> Admin Officer</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm"><i class="fas fa-sign-out-alt"></i> Logout</a>
        </div>
    </div>
</nav>
