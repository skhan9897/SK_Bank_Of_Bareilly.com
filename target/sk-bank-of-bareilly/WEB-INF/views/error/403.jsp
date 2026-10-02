<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="403 - Access Denied | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container py-5 text-center">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="sk-card p-5">
                <div class="text-danger fs-1 mb-3"><i class="fa-solid fa-lock"></i></div>
                <h2 class="fw-bold text-navy">403 - Access Denied</h2>
                <p class="text-muted">You do not have permission to access this page or resource.</p>
                <a href="${pageContext.request.contextPath}/" class="btn btn-gold font-weight-bold px-4">Return Home</a>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
