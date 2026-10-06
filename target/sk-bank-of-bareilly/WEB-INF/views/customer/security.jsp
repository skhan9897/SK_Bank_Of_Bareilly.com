<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Account Security | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-shield-halved text-primary me-2"></i> Password & Security Settings</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3">Change Password</h5>
                    <form action="${pageContext.request.contextPath}/customer/security" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Current Password *</label>
                            <input type="password" name="currentPassword" class="form-control" required placeholder="Enter current password">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">New Password *</label>
                            <input type="password" name="newPassword" class="form-control" required minlength="6" placeholder="Min 6 characters">
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold small">Confirm New Password *</label>
                            <input type="password" name="confirmPassword" class="form-control" required minlength="6" placeholder="Re-type new password">
                        </div>

                        <button type="submit" class="btn btn-gold w-100 fw-bold py-2"><i class="fa-solid fa-key me-1"></i> Update Password</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
