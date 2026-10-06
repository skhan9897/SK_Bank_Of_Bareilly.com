<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Admin Portal Login | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="sk-card p-4 shadow-lg border-danger">
                <div class="text-center mb-4">
                    <img src="${pageContext.request.contextPath}/assets/images/sk-bank-logo-final-transparent.png?v=20261004" alt="Logo" width="60" class="mb-2" onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/assets/images/sk-bank-logo-transparent.png';">
                    <h4 class="fw-bold text-navy">Admin Portal Login</h4>
                    <p class="text-muted small">Authorized SK Bank Staff Only</p>
                </div>

                <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

                <form action="${pageContext.request.contextPath}/admin/login" method="post">
                    <div class="mb-3">
                        <label class="form-label fw-bold small">Admin Username</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light"><i class="fa-solid fa-user-shield text-muted"></i></span>
                            <input type="text" name="username" class="form-control" required placeholder="Enter admin username">
                        </div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-bold small">Password</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light"><i class="fa-solid fa-key text-muted"></i></span>
                            <input type="password" name="password" class="form-control" required placeholder="Enter admin password">
                        </div>
                    </div>

                    <button type="submit" class="btn btn-navy w-100 py-2 fw-bold text-white mb-3" style="background-color: #071F49;"><i class="fa-solid fa-shield-halved me-1"></i> Authorize Login</button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
