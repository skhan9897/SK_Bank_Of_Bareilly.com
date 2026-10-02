<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="KYC Details | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-id-card text-primary me-2"></i> KYC Verification Details</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row justify-content-center">
            <div class="col-md-7">
                <div class="sk-card p-4">
                    <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-3">
                        <div>
                            <h5 class="fw-bold text-navy mb-0">Customer Identity Verification</h5>
                            <small class="text-muted">Aadhaar & PAN Identification Record</small>
                        </div>
                        <span class="badge bg-success p-2"><i class="fa-solid fa-circle-check me-1"></i> VERIFIED</span>
                    </div>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label text-muted small fw-bold">Customer Name</label>
                            <div class="fw-bold fs-6">${customer.fullName}</div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-muted small fw-bold">Customer Number</label>
                            <div class="font-monospace fw-bold fs-6 text-primary">${customer.customerNumber}</div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-muted small fw-bold">Aadhaar Card (Masked)</label>
                            <div class="font-monospace fw-bold fs-6">${kyc.maskedAadhaar}</div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label text-muted small fw-bold">PAN Card (Masked)</label>
                            <div class="font-monospace fw-bold fs-6">${kyc.maskedPan}</div>
                        </div>
                    </div>

                    <div class="alert alert-info mt-4 small">
                        <i class="fa-solid fa-shield-halved me-1"></i> Your identity is cryptographically bound to your registered profile. For official updates, please visit any SK Bank branch.
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
