<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="My Profile - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="profile" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-user-cog text-warning me-2"></i> Profile & Security Settings</h3>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row g-4">
                <!-- PROFILE UPDATE -->
                <div class="col-lg-7">
                    <div class="card-custom p-4 bg-white shadow-sm">
                        <h5 class="fw-bold text-navy mb-3"><i class="fas fa-id-card text-warning me-2"></i> Update Personal Profile</h5>
                        <form action="${pageContext.request.contextPath}/profile" method="post">
                            <input type="hidden" name="action" value="updateProfile">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">First Name</label>
                                    <input type="text" name="firstName" class="form-control" value="${sessionScope.customerProfile.firstName}" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Last Name</label>
                                    <input type="text" name="lastName" class="form-control" value="${sessionScope.customerProfile.lastName}" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Mobile Number</label>
                                    <input type="text" name="mobile" class="form-control" value="${sessionScope.customerProfile.mobile}" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label fw-bold">Email Address</label>
                                    <input type="email" name="email" class="form-control" value="${sessionScope.customerProfile.email}" required>
                                </div>
                                <div class="col-12">
                                    <label class="form-label fw-bold">Residential Address</label>
                                    <textarea name="address" class="form-control" rows="2" required>${sessionScope.customerProfile.address}</textarea>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-bold">City</label>
                                    <input type="text" name="city" class="form-control" value="${sessionScope.customerProfile.city}" required>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-bold">State</label>
                                    <input type="text" name="state" class="form-control" value="${sessionScope.customerProfile.state}" required>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label fw-bold">Pincode</label>
                                    <input type="text" name="pincode" class="form-control" value="${sessionScope.customerProfile.pincode}" required>
                                </div>
                                <div class="col-12">
                                    <label class="form-label fw-bold">Occupation</label>
                                    <input type="text" name="occupation" class="form-control" value="${sessionScope.customerProfile.occupation}" required>
                                </div>
                            </div>
                            <button type="submit" class="btn btn-gold mt-4 py-2 px-4 fw-bold"><i class="fas fa-save me-1"></i> Save Changes</button>
                        </form>
                    </div>
                </div>

                <!-- CHANGE PASSWORD -->
                <div class="col-lg-5">
                    <div class="card-custom p-4 bg-white shadow-sm">
                        <h5 class="fw-bold text-navy mb-3"><i class="fas fa-lock text-warning me-2"></i> Change Password</h5>
                        <form action="${pageContext.request.contextPath}/profile" method="post">
                            <input type="hidden" name="action" value="changePassword">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Current Password *</label>
                                <input type="password" name="currentPassword" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">New Password *</label>
                                <input type="password" name="newPassword" class="form-control" required minlength="8">
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Confirm New Password *</label>
                                <input type="password" name="confirmPassword" class="form-control" required minlength="8">
                            </div>
                            <button type="submit" class="btn btn-navy w-100 py-2 fw-bold"><i class="fas fa-key me-1"></i> Update Password</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
