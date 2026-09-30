<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="now" class="java.util.Date" />
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Open Account & Digital Passbook - SK Bank" />
</jsp:include>
<style>
    @media print {
        body * { visibility: hidden; }
        #digitalPassbookReceipt, #digitalPassbookReceipt * { visibility: visible; }
        #digitalPassbookReceipt { position: absolute; left: 0; top: 0; width: 100%; border: none !important; box-shadow: none !important; }
        .no-print { display: none !important; }
    }
</style>
<body class="bg-light">

    <div class="no-print">
        <jsp:include page="/WEB-INF/views/common/navbar.jsp" />
    </div>

    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-lg-10">

                <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

                <!-- DIGITAL PASSBOOK & VIRTUAL DEBIT CARD RECEIPT CARD -->
                <c:choose>
                    <c:when test="${not empty registeredCustomer}">
                        <div class="card-custom p-4 p-md-5 bg-white shadow-lg border-2 border-warning" id="digitalPassbookReceipt">
                            <!-- PASSBOOK HEADER -->
                            <div class="d-flex flex-wrap justify-content-between align-items-center pb-3 mb-4 border-bottom border-warning border-2">
                                <div class="d-flex align-items-center gap-3">
                                    <img src="${pageContext.request.contextPath}/${registeredCustomer.profilePhoto}" alt="Customer Photo" class="profile-photo" style="width: 65px; height: 60px; border-radius: 50%; object-fit: cover; border: 2px solid #D4A72C;">
                                    <div>
                                        <h3 class="fw-bold text-navy mb-0">SK Bank of Bareilly</h3>
                                        <p class="text-muted small mb-0">Head Office: 124 Civil Lines, Station Road, Bareilly, UP - 243001</p>
                                        <span class="badge badge-gold px-2 py-1 mt-1"><i class="fas fa-shield-alt me-1"></i> Official Digital Passbook & Account Receipt</span>
                                    </div>
                                </div>
                                <div class="text-end mt-2 mt-sm-0">
                                    <span class="badge bg-success fs-6 mb-1"><i class="fas fa-check-circle me-1"></i> ACCOUNT ACTIVE</span>
                                    <div class="small text-muted">Issue Date: <strong><fmt:formatDate value="${now}" pattern="yyyy-MM-dd" /></strong></div>
                                </div>
                            </div>

                            <!-- PASSBOOK & VIRTUAL DEBIT CARD BODY -->
                            <div class="row g-4 mb-4">
                                <!-- ACCOUNT DETAILS CARD -->
                                <div class="col-md-6">
                                    <div class="p-3 bg-light rounded border h-100">
                                        <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-wallet text-warning me-2"></i> Account Details</h5>
                                        <div class="mb-2">Account Number: <strong class="text-navy fs-5 d-block">${registeredAccount.accountNumber}</strong></div>
                                        <div class="mb-2">Customer ID: <strong class="badge badge-navy">${registeredCustomer.customerId}</strong></div>
                                        <div class="mb-2">Account Type: <strong>${registeredAccount.accountTypeName}</strong></div>
                                        <div class="mb-2">IFSC Code: <strong class="text-navy">${registeredAccount.ifscCode}</strong></div>
                                        <div class="mb-2">Branch: <strong>${registeredAccount.branchName}</strong></div>
                                        <div>Initial Balance: <strong class="text-success fs-5">₹ 0.00</strong></div>
                                    </div>
                                </div>

                                <!-- INSTANT VIRTUAL DEBIT CARD -->
                                <div class="col-md-6">
                                    <div class="p-3 bg-light rounded border h-100">
                                        <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-credit-card text-warning me-2"></i> Issued Virtual Debit Card</h5>

                                        <c:choose>
                                            <c:when test="${not empty registeredCard}">
                                                <!-- REALISTIC VIRTUAL CARD PREVIEW -->
                                                <div class="bank-debit-card mb-2 shadow">
                                                    <div class="d-flex justify-content-between align-items-center">
                                                        <span class="fw-bold fs-6 text-warning">SK BANK OF BAREILLY</span>
                                                        <span class="badge bg-success">ACTIVE DEBIT</span>
                                                    </div>
                                                    <div class="chip my-1"></div>
                                                    <div class="card-num fs-5">${registeredCard.maskedCardNumber}</div>
                                                    <div class="d-flex justify-content-between align-items-end mt-1 small">
                                                        <div>
                                                            <div class="text-white-50" style="font-size: 0.65rem;">CARD HOLDER</div>
                                                            <div class="fw-bold text-uppercase" style="font-size: 0.85rem;">${registeredCard.cardHolderName}</div>
                                                        </div>
                                                        <div class="text-end">
                                                            <div class="text-white-50" style="font-size: 0.65rem;">EXPIRES</div>
                                                            <div class="fw-bold" style="font-size: 0.85rem;">${registeredCard.expiryDate}</div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="small text-muted text-center"><i class="fas fa-lock text-warning me-1"></i> Default PIN: <code>1234</code> (Change PIN anytime in Cards section)</div>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="p-3 bg-white text-center rounded border">
                                                    <i class="fas fa-credit-card text-warning fs-3 mb-2"></i>
                                                    <p class="small text-muted mb-0">Virtual Debit Card issued. Access card controls in your dashboard.</p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <!-- CUSTOMER DETAILS CARD -->
                                <div class="col-12">
                                    <div class="p-3 bg-light rounded border">
                                        <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-user-circle text-warning me-2"></i> Account Holder Profile</h5>
                                        <div class="row g-2 small">
                                            <div class="col-md-4">Account Holder: <strong>${registeredCustomer.fullName}</strong></div>
                                            <div class="col-md-4">Internet Banking Username: <strong class="text-primary">${registeredUsername}</strong></div>
                                            <div class="col-md-4">Mobile Number: <strong>${registeredCustomer.mobile}</strong></div>
                                            <div class="col-md-4">Email Address: <strong>${registeredCustomer.email}</strong></div>
                                            <div class="col-md-4">PAN Number: <strong>${registeredCustomer.pan}</strong></div>
                                            <div class="col-md-4">Aadhaar Number: <strong>${registeredCustomer.aadhaar}</strong></div>
                                            <div class="col-12">Address: <strong>${registeredCustomer.address}, ${registeredCustomer.city}, ${registeredCustomer.state} - ${registeredCustomer.pincode}</strong></div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- PASSBOOK FOOTER & ACTIONS -->
                            <div class="p-3 bg-navy text-white rounded d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
                                <div class="small">
                                    <i class="fas fa-info-circle text-warning me-1"></i> Your account has been opened with initial balance ₹0.00. Please deposit funds through authorized bank/admin process to start transacting.
                                </div>
                                <div class="no-print d-flex gap-2">
                                    <button onclick="window.print()" class="btn btn-gold fw-bold shadow-sm"><i class="fas fa-print me-1"></i> PRINT PASSBOOK & RECEIPT</button>
                                    <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-light"><i class="fas fa-sign-in-alt me-1"></i> PROCEED TO LOGIN</a>
                                </div>
                            </div>

                            <div class="text-center text-muted small border-top pt-3">
                                SK Bank of Bareilly — RBI Regulated Commercial Bank. 24x7 Customer Care: 1800-123-4567 | support@skbankofbareilly.example
                            </div>
                        </div>
                    </c:when>

                    <!-- REGISTRATION FORM -->
                    <c:otherwise>
                        <div class="card-custom p-4 p-md-5 bg-white shadow-lg">
                            <div class="text-center mb-4">
                                <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="SK Bank Logo" class="img-fluid mb-2" style="max-height: 90px; object-fit: contain;">
                                <h2 class="fw-bold text-navy">Open a Digital Bank Account</h2>
                                <p class="text-muted">Fill in your details below to instantly open your account with SK Bank of Bareilly.</p>
                            </div>

                            <form action="${pageContext.request.contextPath}/register" method="post" enctype="multipart/form-data" id="regForm">
                                <!-- SECTION 1: ACCOUNT TYPE & BRANCH SELECTION -->
                                <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-university me-2 text-warning"></i> Select Account Type & Branch</h5>
                                <div class="row g-3 mb-4">
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Select Account Type *</label>
                                        <select name="accountTypeId" class="form-select form-select-lg fs-6" required>
                                            <c:forEach var="type" items="${accountTypes}">
                                                <option value="${type.typeId}">${type.typeName} - ${type.description}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Select Home Branch *</label>
                                        <select name="branchId" class="form-select form-select-lg fs-6" required>
                                            <c:forEach var="b" items="${branches}">
                                                <option value="${b.branchId}">${b.branchName} (${b.city}) - IFSC: ${b.ifscCode}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>

                                <!-- SECTION 2: PERSONAL DETAILS & OPTIONAL PROFILE PHOTO -->
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
                                    <!-- OPTIONAL PROFILE PHOTO UPLOAD -->
                                    <div class="col-12">
                                        <label class="form-label fw-semibold">Profile Photo (Optional)</label>
                                        <input type="file" name="profilePhoto" id="profilePhotoInput" class="form-control" accept=".jpg,.jpeg,.png,image/jpeg,image/png" onchange="validateProfilePhoto(this)">
                                        <div class="form-text small">Profile photo is optional. JPG, JPEG or PNG only. Maximum file size: 5 MB.</div>
                                    </div>
                                </div>

                                <!-- SECTION 3: CONTACT & IDENTITY DETAILS -->
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

                                <!-- SECTION 4: INTERNET BANKING CREDENTIALS -->
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
                    </c:otherwise>
                </c:choose>

            </div>
        </div>
    </div>

    <script>
        function validateProfilePhoto(input) {
            if (input.files && input.files[0]) {
                const file = input.files[0];
                const maxSize = 5 * 1024 * 1024; // 5 MB
                const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png'];

                if (file.size > maxSize) {
                    alert("Profile photo must be less than or equal to 5 MB.");
                    input.value = "";
                    return;
                }

                if (!allowedTypes.includes(file.type.toLowerCase())) {
                    alert("Invalid file type. Only JPG, JPEG, and PNG images are allowed.");
                    input.value = "";
                    return;
                }
            }
        }
    </script>

    <div class="no-print">
        <jsp:include page="/WEB-INF/views/common/footer.jsp" />
    </div>

</body>
</html>
