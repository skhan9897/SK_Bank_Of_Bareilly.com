<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Open Account - SK Bank of Bareilly" />
</jsp:include>
<body class="bg-light">

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-lg-10">
                <div class="card-custom p-4 p-md-5 bg-white shadow-lg">
                    <div class="text-center mb-4">
                        <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="SK Bank Logo" class="img-fluid mb-2" style="max-height: 90px; object-fit: contain;">
                        <h2 class="fw-bold text-navy">Open a Digital Savings Account</h2>
                        <p class="text-muted">Fill in your details below to instantly open your account with SK Bank of Bareilly.</p>
                    </div>

                    <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

                    <form action="${pageContext.request.contextPath}/register" method="post" id="regForm">
                        <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-user-circle me-2 text-warning"></i> Personal Details</h5>
                        <div class="row g-3 mb-4">
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">First Name *</label>
                                <input type="text" name="firstName" class="form-control" required placeholder="e.g. Rajesh">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Last Name *</label>
                                <input type="text" name="lastName" class="form-control" required placeholder="e.g. Kumar">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-semibold">Date of Birth *</label>
                                <input type="date" name="dob" class="form-control" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-semibold">Gender *</label>
                                <select name="gender" class="form-select" required>
                                    <option value="Male">Male</option>
                                    <option value="Female">Female</option>
                                    <option value="Other">Other</option>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-semibold">Occupation *</label>
                                <input type="text" name="occupation" class="form-control" placeholder="e.g. Service / Business" required>
                            </div>
                        </div>

                        <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-address-book me-2 text-warning"></i> Contact & Identity Details</h5>
                        <div class="row g-3 mb-4">
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Mobile Number *</label>
                                <input type="tel" name="mobile" class="form-control" pattern="[6-9]\d{9}" title="Valid 10-digit mobile number" required placeholder="10-digit Mobile Number">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Email Address *</label>
                                <input type="email" name="email" class="form-control" required placeholder="name@example.com">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Aadhaar Number (12 Digits) *</label>
                                <input type="text" name="aadhaar" class="form-control" pattern="\d{12}" required placeholder="123456789012">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">PAN Number (10 Characters) *</label>
                                <input type="text" name="pan" class="form-control text-uppercase" pattern="[A-Za-z]{5}\d{4}[A-Za-z]{1}" required placeholder="ABCDE1234F">
                            </div>
                            <div class="col-12">
                                <label class="form-label fw-semibold">Residential Address *</label>
                                <textarea name="address" class="form-control" rows="2" required placeholder="House/Street/Locality"></textarea>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-semibold">City *</label>
                                <input type="text" name="city" class="form-control" value="Bareilly" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-semibold">State *</label>
                                <input type="text" name="state" class="form-control" value="Uttar Pradesh" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-semibold">Pincode *</label>
                                <input type="text" name="pincode" class="form-control" pattern="\d{6}" required placeholder="243001">
                            </div>
                        </div>

                        <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-lock me-2 text-warning"></i> Internet Banking Credentials</h5>
                        <div class="row g-3 mb-4">
                            <div class="col-md-4">
                                <label class="form-label fw-semibold">Choose Username *</label>
                                <input type="text" name="username" class="form-control" required placeholder="e.g. rajesh123">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-semibold">Password *</label>
                                <input type="password" name="password" id="regPassword" class="form-control" required placeholder="Min. 8 chars, letters, numbers">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-semibold">Confirm Password *</label>
                                <input type="password" name="confirmPassword" id="regConfirmPassword" class="form-control" required placeholder="Re-enter password">
                            </div>
                            <div class="col-12 d-none" id="passwordMatchAlert">
                                <div class="text-danger small"><i class="fas fa-times-circle me-1"></i> Passwords do not match.</div>
                            </div>
                        </div>

                        <div class="form-check mb-4">
                            <input class="form-check-input" type="checkbox" required id="terms">
                            <label class="form-check-label small" for="terms">
                                I agree to the <a href="#">Terms & Conditions</a> of SK Bank of Bareilly and declare that the details provided above are true and correct.
                            </label>
                        </div>

                        <div class="d-grid gap-2 col-md-6 mx-auto">
                            <button type="submit" class="btn btn-gold py-3 fw-bold fs-6"><i class="fas fa-paper-plane me-2"></i> SUBMIT & OPEN ACCOUNT</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
