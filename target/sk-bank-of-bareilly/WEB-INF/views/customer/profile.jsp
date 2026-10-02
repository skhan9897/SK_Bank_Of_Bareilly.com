<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="My Profile | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-user text-primary me-2"></i> Profile & Personal Info</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row g-4">
            <div class="col-lg-4">
                <div class="sk-card p-4 text-center">
                    <img src="${pageContext.request.contextPath}/customer/profile-image" alt="Profile" class="rounded-circle border border-3 border-warning mb-3" width="120" height="120">
                    <h5 class="fw-bold text-navy mb-1">${customer.fullName}</h5>
                    <p class="text-muted small mb-3">Customer ID: <strong class="font-monospace">${customer.customerNumber}</strong></p>

                    <form action="${pageContext.request.contextPath}/customer/profile" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                        <input type="hidden" name="action" value="uploadPhoto">

                        <div class="mb-3">
                            <label class="form-label small fw-bold text-muted">Upload New Photo (Max 5MB)</label>
                            <input type="file" name="profileImage" accept="image/png, image/jpeg, image/jpg" class="form-control form-control-sm" required>
                        </div>
                        <button type="submit" class="btn btn-outline-primary btn-sm w-100 fw-bold"><i class="fa-solid fa-upload me-1"></i> Update Photo</button>
                    </form>
                </div>
            </div>

            <div class="col-lg-8">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3">Edit Personal Details</h5>
                    <form action="${pageContext.request.contextPath}/customer/profile" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label fw-bold small">Full Name *</label>
                                <input type="text" name="fullName" class="form-control" required value="${customer.fullName}">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold small">Mobile Number *</label>
                                <input type="tel" name="mobile" pattern="[6-9][0-9]{9}" class="form-control" required value="${customer.mobile}">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold small">Email Address *</label>
                                <input type="email" name="email" class="form-control" required value="${customer.email}">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold small">City *</label>
                                <input type="text" name="city" class="form-control" required value="${customer.city}">
                            </div>
                            <div class="col-12">
                                <label class="form-label fw-bold small">Address *</label>
                                <input type="text" name="address" class="form-control" required value="${customer.address}">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold small">State *</label>
                                <input type="text" name="state" class="form-control" required value="${customer.state}">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold small">Pincode *</label>
                                <input type="text" name="pincode" class="form-control" required value="${customer.pincode}">
                            </div>
                        </div>

                        <button type="submit" class="btn btn-gold fw-bold py-2 mt-4 px-4"><i class="fa-solid fa-save me-1"></i> Save Profile Changes</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
