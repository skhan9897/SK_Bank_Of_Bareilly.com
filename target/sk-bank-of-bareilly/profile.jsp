<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="My Profile & Password Reset - SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="profile" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-user-shield text-warning me-2"></i> Profile & Security Settings</h3>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row g-4">
                <!-- READ-ONLY CUSTOMER PROFILE INFO -->
                <div class="col-lg-6">
                    <div class="card-custom p-4 bg-white shadow-sm h-100">
                        <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-id-card text-warning me-2"></i> Account Holder Information</h5>
                        <div class="mb-3">
                            <span class="text-muted small d-block">Customer ID / Username</span>
                            <strong class="badge badge-navy fs-6">${sessionScope.customerProfile.customerId} / ${sessionScope.loggedInUser.username}</strong>
                        </div>
                        <div class="row g-3 mb-3">
                            <div class="col-6">
                                <span class="text-muted small d-block">Full Name</span>
                                <strong class="text-navy">${sessionScope.customerProfile.fullName}</strong>
                            </div>
                            <div class="col-6">
                                <span class="text-muted small d-block">KYC Status</span>
                                <span class="badge bg-success">${sessionScope.customerProfile.kycStatus}</span>
                            </div>
                        </div>
                        <div class="row g-3 mb-3">
                            <div class="col-6">
                                <span class="text-muted small d-block">Registered Mobile</span>
                                <strong>${sessionScope.customerProfile.mobile}</strong>
                            </div>
                            <div class="col-6">
                                <span class="text-muted small d-block">Registered Email</span>
                                <strong>${sessionScope.customerProfile.email}</strong>
                            </div>
                        </div>
                        <div class="row g-3 mb-3">
                            <div class="col-6">
                                <span class="text-muted small d-block">PAN Number</span>
                                <strong>${sessionScope.customerProfile.pan}</strong>
                            </div>
                            <div class="col-6">
                                <span class="text-muted small d-block">Aadhaar Number</span>
                                <strong>${sessionScope.customerProfile.aadhaar}</strong>
                            </div>
                        </div>
                        <div>
                            <span class="text-muted small d-block">Registered Address</span>
                            <strong>${sessionScope.customerProfile.address}, ${sessionScope.customerProfile.city}, ${sessionScope.customerProfile.state} - ${sessionScope.customerProfile.pincode}</strong>
                        </div>
                        <div class="alert alert-info mt-3 py-2 small mb-0">
                            <i class="fas fa-info-circle me-1"></i> Personal details are verified and locked. Contact branch for official updates.
                        </div>
                    </div>
                </div>

                <!-- PASSWORD RESET ONLY FORM -->
                <div class="col-lg-6">
                    <div class="card-custom p-4 bg-white shadow-sm h-100">
                        <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-key text-warning me-2"></i> Reset Internet Banking Password</h5>
                        <form action="${pageContext.request.contextPath}/profile" method="post">
                            <input type="hidden" name="action" value="changePassword">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Current Password *</label>
                                <input type="password" name="currentPassword" class="form-control form-control-lg fs-6" placeholder="Enter current password" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">New Password *</label>
                                <input type="password" name="newPassword" class="form-control form-control-lg fs-6" placeholder="Min. 8 chars, letters, numbers" required minlength="8">
                            </div>
                            <div class="mb-4">
                                <label class="form-label fw-bold">Confirm New Password *</label>
                                <input type="password" name="confirmPassword" class="form-control form-control-lg fs-6" placeholder="Re-enter new password" required minlength="8">
                            </div>
                            <button type="submit" class="btn btn-navy w-100 py-3 fw-bold fs-6 shadow-sm"><i class="fas fa-lock me-2"></i> CONFIRM & UPDATE PASSWORD</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
