<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Open New Account | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="sk-card p-4 shadow-lg">
                <div class="text-center mb-4">
                    <img src="${pageContext.request.contextPath}/assets/images/sk-bank-logo-transparent.png" alt="Logo" width="60" class="mb-2">
                    <h3 class="fw-bold text-navy">Open Account Online</h3>
                    <p class="text-muted small">Fill details below to open your digital bank account. Initial balance: <strong>₹0.00</strong></p>
                </div>

                <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

                <form action="${pageContext.request.contextPath}/register" method="post" enctype="multipart/form-data">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold small">Full Name *</label>
                            <input type="text" name="fullName" class="form-control" required placeholder="As per Aadhaar/PAN">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-bold small">Date of Birth *</label>
                            <input type="date" name="dateOfBirth" class="form-control" required>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-bold small">Gender *</label>
                            <select name="gender" class="form-select" required>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                                <option value="OTHER">Other</option>
                            </select>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label fw-bold small">Mobile Number (10 Digits) *</label>
                            <input type="tel" name="mobile" pattern="[6-9][0-9]{9}" class="form-control" required placeholder="e.g. 9876543210">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold small">Email Address *</label>
                            <input type="email" name="email" class="form-control" required placeholder="name@example.com">
                        </div>

                        <div class="col-md-6">
                            <label class="form-label fw-bold small">Aadhaar Number (12 Digits) *</label>
                            <input type="text" name="aadhaarNumber" pattern="[0-9]{12}" class="form-control" required placeholder="12-digit Aadhaar">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold small">PAN Number *</label>
                            <input type="text" name="panNumber" pattern="[A-Z]{5}[0-9]{4}[A-Z]" class="form-control text-uppercase" required placeholder="ABCDE1234F">
                        </div>

                        <div class="col-12">
                            <label class="form-label fw-bold small">Residential Address *</label>
                            <input type="text" name="address" class="form-control" required placeholder="Street address, colony, locality">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold small">City *</label>
                            <input type="text" name="city" class="form-control" required value="Bareilly">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold small">State *</label>
                            <input type="text" name="state" class="form-control" required value="Uttar Pradesh">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold small">Pincode *</label>
                            <input type="text" name="pincode" pattern="[0-9]{6}" class="form-control" required placeholder="243001">
                        </div>

                        <div class="col-md-6">
                            <label class="form-label fw-bold small">Preferred Branch *</label>
                            <select name="branchId" class="form-select" required>
                                <c:forEach items="${branches}" var="b">
                                    <option value="${b.branchId}">${b.branchName} (${b.ifscCode})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold small">Account Type *</label>
                            <select name="accountTypeId" class="form-select" required>
                                <c:forEach items="${accountTypes}" var="at">
                                    <option value="${at.accountTypeId}">${at.typeName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label fw-bold small">Choose Username *</label>
                            <input type="text" name="username" class="form-control" required placeholder="Min 4 characters">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold small">Choose Password *</label>
                            <input type="password" name="password" class="form-control" required placeholder="Min 6 characters">
                        </div>
                    </div>

                    <div class="mt-4">
                        <button type="submit" class="btn btn-gold w-100 py-3 fw-bold fs-5"><i class="fa-solid fa-circle-check me-2"></i> Submit & Open Account</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
