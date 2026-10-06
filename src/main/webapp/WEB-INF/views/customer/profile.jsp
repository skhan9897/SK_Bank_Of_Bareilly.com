<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="My Profile | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-address-card text-primary me-2"></i> Profile & Account Overview</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row g-4 mb-4">
            <!-- LEFT COLUMN: Profile Avatar & Quick IDs -->
            <div class="col-lg-4">
                <div class="sk-card p-4 text-center mb-4">
                    <img src="${pageContext.request.contextPath}/customer/profile-image" alt="Profile" class="rounded-circle border border-3 border-warning mb-3 shadow" width="120" height="120">
                    <h5 class="fw-bold text-navy mb-1">${customer.fullName}</h5>
                    <p class="text-muted small mb-2"><i class="fa-solid fa-id-badge text-gold me-1"></i> Customer Number: <strong class="font-monospace text-navy">${customer.customerNumber}</strong></p>
                    <p class="text-muted small mb-3"><i class="fa-solid fa-hashtag text-secondary me-1"></i> Internal Customer ID: <strong class="font-monospace">${customer.customerId}</strong></p>

                    <div class="d-flex justify-content-center gap-2 mb-3">
                        <span class="badge bg-success px-3 py-2"><i class="fa-solid fa-circle-check me-1"></i> KYC ${customer.kycStatus}</span>
                        <span class="badge bg-primary px-3 py-2"><i class="fa-solid fa-shield me-1"></i> ${customer.status}</span>
                    </div>

                    <form action="${pageContext.request.contextPath}/customer/profile" method="post" enctype="multipart/form-data" class="mt-3 border-top pt-3">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                        <input type="hidden" name="action" value="uploadPhoto">

                        <div class="mb-3">
                            <label class="form-label small fw-bold text-muted">Upload Profile Photo (Max 5MB)</label>
                            <input type="file" name="profileImage" accept="image/png, image/jpeg, image/jpg" class="form-control form-control-sm" required>
                        </div>
                        <button type="submit" class="btn btn-outline-primary btn-sm w-100 fw-bold"><i class="fa-solid fa-upload me-1"></i> Update Profile Photo</button>
                    </form>
                </div>

                <!-- KYC & Identity Information -->
                <div class="sk-card p-4">
                    <h6 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fa-solid fa-id-card text-gold me-2"></i> KYC & Identity Details</h6>

                    <div class="mb-2">
                        <span class="text-muted small d-block">Aadhaar Number:</span>
                        <strong class="font-monospace text-navy">${customer.maskedAadhaar}</strong>
                    </div>

                    <div class="mb-2">
                        <span class="text-muted small d-block">PAN Card Number:</span>
                        <strong class="font-monospace text-navy">${customer.maskedPan}</strong>
                    </div>

                    <div class="mb-2">
                        <span class="text-muted small d-block">Date of Birth:</span>
                        <strong class="text-navy">${customer.dateOfBirth}</strong>
                    </div>

                    <div>
                        <span class="text-muted small d-block">Gender:</span>
                        <strong class="text-navy">${customer.gender}</strong>
                    </div>
                </div>
            </div>

            <!-- RIGHT COLUMN: Banking Account Details & Editable Profile -->
            <div class="col-lg-8">
                <!-- Primary Bank Account Overview Card -->
                <div class="sk-card p-4 mb-4 border-start border-4 border-warning">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-building-columns text-primary me-2"></i> Linked Bank Account Details</h5>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <span class="text-muted small d-block">Primary Account Number:</span>
                            <h5 class="fw-bold text-navy font-monospace mb-0">${passbook.accountNumber}</h5>
                        </div>
                        <div class="col-md-6">
                            <span class="text-muted small d-block">Account Type:</span>
                            <strong class="badge bg-light text-navy border fs-6 px-3 py-1">${passbook.accountType}</strong>
                        </div>
                        <div class="col-md-6">
                            <span class="text-muted small d-block">Available Balance:</span>
                            <h4 class="fw-bold text-success mb-0">₹<fmt:formatNumber value="${passbook.availableBalance}" pattern="#,##0.00"/></h4>
                        </div>
                        <div class="col-md-6">
                            <span class="text-muted small d-block">Account Status:</span>
                            <span class="badge bg-success">${passbook.accountStatus}</span>
                        </div>
                        <div class="col-md-6">
                            <span class="text-muted small d-block">Home Branch Name:</span>
                            <strong class="text-navy">${passbook.branchName}</strong>
                        </div>
                        <div class="col-md-6">
                            <span class="text-muted small d-block">IFSC Code:</span>
                            <strong class="font-monospace text-navy">${passbook.ifscCode}</strong>
                        </div>
                        <div class="col-md-6">
                            <span class="text-muted small d-block">Branch Code:</span>
                            <strong class="font-monospace text-navy">${passbook.branchCode}</strong>
                        </div>
                        <div class="col-md-6">
                            <span class="text-muted small d-block">Account Opening Date:</span>
                            <strong class="text-navy">${passbook.openingDate}</strong>
                        </div>
                    </div>
                </div>

                <!-- Editable Personal & Contact Details -->
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-user-pen text-primary me-2"></i> Contact & Address Information</h5>
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
