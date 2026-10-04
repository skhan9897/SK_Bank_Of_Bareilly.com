<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Open New Account | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="register-page-bg">
    <div class="glass-registration-card">
        <!-- HEADER -->
        <div class="registration-header">
            <img src="${pageContext.request.contextPath}/assets/images/sk-bank-logo-transparent.png"
                 alt="SK Bank Logo"
                 class="registration-logo"
                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/assets/images/sk-bank-logo.png';">
            <h2 class="fw-bold mb-1 text-white">Open Account Online</h2>
            <p class="text-gold-light mb-2 small fw-semibold">Secure Digital Banking Registration</p>
            <div class="d-inline-block bg-white text-navy px-3 py-1 rounded-pill fw-bold small shadow-sm mb-2">
                Initial Account Balance: <span class="text-success fw-extrabold">₹0.00</span>
            </div>
            <div class="small text-white-50"><i class="fa-solid fa-lock text-warning me-1"></i> 256-Bit Encrypted Secure Registration</div>
        </div>

        <div class="p-4 p-md-5">
            <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

            <!-- STEP INDICATOR BAR -->
            <div class="step-indicator-wrapper">
                <div class="step-item active" id="stepIndicator1">
                    <div class="step-circle">01</div>
                    <div class="step-title">Personal</div>
                </div>
                <div class="step-item" id="stepIndicator2">
                    <div class="step-circle">02</div>
                    <div class="step-title">Contact</div>
                </div>
                <div class="step-item" id="stepIndicator3">
                    <div class="step-circle">03</div>
                    <div class="step-title">KYC</div>
                </div>
                <div class="step-item" id="stepIndicator4">
                    <div class="step-circle">04</div>
                    <div class="step-title">Account</div>
                </div>
                <div class="step-item" id="stepIndicator5">
                    <div class="step-circle">05</div>
                    <div class="step-title">Review</div>
                </div>
            </div>

            <!-- REGISTRATION MULTIPART FORM -->
            <form id="registrationForm" action="${pageContext.request.contextPath}/register" method="post" enctype="multipart/form-data" novalidate>

                <!-- SECTION 1: PERSONAL DETAILS -->
                <div class="registration-section" id="section1">
                    <h5 class="fw-bold text-navy mb-3 border-bottom pb-2"><i class="fa-solid fa-user text-warning me-2"></i> Step 1: Personal Details</h5>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold small text-navy">Full Name *</label>
                            <input type="text" name="fullName" id="regFullName" class="form-control form-control-lg" required placeholder="As per Aadhaar / PAN">
                            <div class="invalid-feedback">Please enter your full name as per Aadhaar/PAN.</div>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-bold small text-navy">Date of Birth *</label>
                            <input type="date" name="dateOfBirth" id="regDob" class="form-control form-control-lg" required max="<%= java.time.LocalDate.now().toString() %>">
                            <div class="invalid-feedback">Please select a valid date of birth.</div>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label fw-bold small text-navy">Gender *</label>
                            <select name="gender" id="regGender" class="form-select form-select-lg" required>
                                <option value="MALE">Male</option>
                                <option value="FEMALE">Female</option>
                                <option value="OTHER">Other</option>
                            </select>
                        </div>
                    </div>
                    <div class="d-flex justify-content-end mt-4">
                        <button type="button" class="btn btn-gold btn-lg px-4" onclick="nextSection(1, 2)">Continue to Contact <i class="fa-solid fa-arrow-right ms-2"></i></button>
                    </div>
                </div>

                <!-- SECTION 2: CONTACT & ADDRESS -->
                <div class="registration-section d-none" id="section2">
                    <h5 class="fw-bold text-navy mb-3 border-bottom pb-2"><i class="fa-solid fa-address-book text-warning me-2"></i> Step 2: Contact & Address Details</h5>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold small text-navy">Mobile Number (10 Digits) *</label>
                            <div class="input-group input-group-lg">
                                <span class="input-group-text bg-light fw-bold">+91</span>
                                <input type="tel" name="mobile" id="regMobile" pattern="[6-9][0-9]{9}" maxlength="10" class="form-control" required placeholder="9876543210" oninput="this.value = this.value.replace(/[^\d]/g, '')">
                            </div>
                            <div class="invalid-feedback">Enter a valid 10-digit mobile number starting with 6-9.</div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold small text-navy">Email Address *</label>
                            <input type="email" name="email" id="regEmail" class="form-control form-control-lg" required placeholder="name@example.com">
                            <div class="invalid-feedback">Please enter a valid email address.</div>
                        </div>
                        <div class="col-12">
                            <label class="form-label fw-bold small text-navy">Residential Address *</label>
                            <input type="text" name="address" id="regAddress" class="form-control form-control-lg" required placeholder="Flat / House No., Colony, Street Locality">
                            <div class="invalid-feedback">Please enter your residential address.</div>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold small text-navy">City *</label>
                            <input type="text" name="city" id="regCity" class="form-control form-control-lg" required value="Bareilly">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold small text-navy">State *</label>
                            <input type="text" name="state" id="regState" class="form-control form-control-lg" required value="Uttar Pradesh">
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold small text-navy">Pincode *</label>
                            <input type="text" name="pincode" id="regPincode" pattern="[0-9]{6}" maxlength="6" class="form-control form-control-lg" required placeholder="243001" oninput="this.value = this.value.replace(/[^\d]/g, '')">
                            <div class="invalid-feedback">Enter 6-digit PIN code.</div>
                        </div>
                    </div>
                    <div class="d-flex justify-content-between mt-4">
                        <button type="button" class="btn btn-outline-secondary btn-lg px-4" onclick="prevSection(2, 1)"><i class="fa-solid fa-arrow-left me-2"></i> Back</button>
                        <button type="button" class="btn btn-gold btn-lg px-4" onclick="nextSection(2, 3)">Continue to KYC <i class="fa-solid fa-arrow-right ms-2"></i></button>
                    </div>
                </div>

                <!-- SECTION 3: KYC DETAILS & PROFILE PHOTO -->
                <div class="registration-section d-none" id="section3">
                    <h5 class="fw-bold text-navy mb-3 border-bottom pb-2"><i class="fa-solid fa-id-card text-warning me-2"></i> Step 3: KYC & Profile Photo</h5>
                    <div class="row g-3 align-items-center">
                        <div class="col-md-6">
                            <label class="form-label fw-bold small text-navy">Aadhaar Number (12 Digits) *</label>
                            <input type="text" name="aadhaarNumber" id="regAadhaar" pattern="[0-9]{12}" maxlength="12" class="form-control form-control-lg" required placeholder="12-digit Aadhaar number" oninput="this.value = this.value.replace(/[^\d]/g, '')">
                            <div class="invalid-feedback">Please enter a valid 12-digit Aadhaar number.</div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold small text-navy">PAN Number *</label>
                            <input type="text" name="panNumber" id="regPan" pattern="[A-Za-z]{5}[0-9]{4}[A-Za-z]" maxlength="10" class="form-control form-control-lg text-uppercase" required placeholder="ABCDE1234F" oninput="this.value = this.value.toUpperCase()">
                            <div class="invalid-feedback">Please enter a valid 10-character PAN number (e.g. ABCDE1234F).</div>
                        </div>

                        <div class="col-md-8">
                            <label class="form-label fw-bold small text-navy">Optional Profile Photo (Max 5 MB)</label>
                            <input type="file" name="profileImage" id="profileImageInput" accept="image/jpeg,image/png,image/jpg" class="form-control form-control-lg">
                            <div class="form-text text-muted small">Supported formats: JPG, JPEG, PNG. Maximum size: 5 MB.</div>
                        </div>
                        <div class="col-md-4 text-center">
                            <div class="profile-avatar-wrapper">
                                <img id="profileImagePreview" src="${pageContext.request.contextPath}/assets/images/default-avatar.svg" alt="Preview" class="profile-avatar-img">
                            </div>
                            <span class="small text-muted d-block mt-1">Photo Preview</span>
                        </div>
                    </div>
                    <div class="d-flex justify-content-between mt-4">
                        <button type="button" class="btn btn-outline-secondary btn-lg px-4" onclick="prevSection(3, 2)"><i class="fa-solid fa-arrow-left me-2"></i> Back</button>
                        <button type="button" class="btn btn-gold btn-lg px-4" onclick="nextSection(3, 4)">Continue to Account <i class="fa-solid fa-arrow-right ms-2"></i></button>
                    </div>
                </div>

                <!-- SECTION 4: ACCOUNT & SECURITY -->
                <div class="registration-section d-none" id="section4">
                    <h5 class="fw-bold text-navy mb-3 border-bottom pb-2"><i class="fa-solid fa-shield-halved text-warning me-2"></i> Step 4: Account & Credentials</h5>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold small text-navy">Preferred Branch *</label>
                            <select name="branchId" id="regBranchId" class="form-select form-select-lg" required>
                                <c:choose>
                                    <c:when test="${not empty branches}">
                                        <c:forEach items="${branches}" var="b">
                                            <option value="${b.branchId}" data-ifsc="${b.ifscCode}">${b.branchName} (${b.ifscCode})</option>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="1" data-ifsc="SKBK0000001">Main Branch Bareilly (SKBK0000001)</option>
                                        <option value="2" data-ifsc="SKBK0000002">Izzatnagar Branch (SKBK0000002)</option>
                                        <option value="3" data-ifsc="SKBK0000003">Rajendra Nagar Branch (SKBK0000003)</option>
                                        <option value="4" data-ifsc="SKBK0000004">Noida Cyber Branch (SKBK0000004)</option>
                                    </c:otherwise>
                                </c:choose>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold small text-navy">Account Type *</label>
                            <select name="accountTypeId" id="regAccountTypeId" class="form-select form-select-lg" required>
                                <c:choose>
                                    <c:when test="${not empty accountTypes}">
                                        <c:forEach items="${accountTypes}" var="at">
                                            <option value="${at.accountTypeId}">${at.typeName}</option>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="1">Savings Account</option>
                                        <option value="2">Current Account</option>
                                        <option value="3">Corporate Salary Account</option>
                                        <option value="4">Basic Savings Account (BSBD)</option>
                                        <option value="5">Senior Citizen Savings Account</option>
                                    </c:otherwise>
                                </c:choose>
                            </select>
                        </div>

                        <div class="col-md-4">
                            <label class="form-label fw-bold small text-navy">Username (Auto-Generated) *</label>
                            <input type="text" name="username" id="regUsername" class="form-control form-control-lg bg-light" readonly placeholder="Auto-generated (e.g. sajid4767)">
                            <div class="form-text text-muted small">Generated automatically from Name &amp; Mobile.</div>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold small text-navy">Password *</label>
                            <input type="password" name="password" id="regPassword" class="form-control form-control-lg" required placeholder="Min 6 characters">
                            <div class="invalid-feedback">Password must be at least 6 characters.</div>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label fw-bold small text-navy">Confirm Password *</label>
                            <input type="password" id="regConfirmPassword" class="form-control form-control-lg" required placeholder="Re-enter password">
                            <div class="invalid-feedback">Passwords do not match.</div>
                        </div>

                        <div class="col-12 mt-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="termsCheck" required>
                                <label class="form-check-label small text-muted" for="termsCheck">
                                    I agree to the <a href="#" class="text-navy fw-bold text-decoration-underline">Terms & Conditions</a> and <a href="#" class="text-navy fw-bold text-decoration-underline">Digital Banking Privacy Policy</a> of SK Bank of Bareilly.
                                </label>
                                <div class="invalid-feedback">You must accept terms to proceed.</div>
                            </div>
                        </div>
                    </div>

                    <div class="d-flex justify-content-between mt-4">
                        <button type="button" class="btn btn-outline-secondary btn-lg px-4" onclick="prevSection(4, 3)"><i class="fa-solid fa-arrow-left me-2"></i> Back</button>
                        <button type="button" class="btn btn-gold btn-lg px-4" onclick="prepareReviewSection()">Review &amp; Verify <i class="fa-solid fa-arrow-right ms-2"></i></button>
                    </div>
                </div>

                <!-- SECTION 5: REVIEW & SUBMIT -->
                <div class="registration-section d-none" id="section5">
                    <h5 class="fw-bold text-navy mb-3 border-bottom pb-2"><i class="fa-solid fa-magnifying-glass text-warning me-2"></i> Step 5: Review & Confirm Submission</h5>

                    <div class="sk-card bg-light border-gold p-4 mb-4">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <span class="text-muted small d-block">Full Name</span>
                                <strong id="revFullName" class="text-navy h6"></strong>
                            </div>
                            <div class="col-md-3">
                                <span class="text-muted small d-block">Date of Birth</span>
                                <strong id="revDob" class="text-navy"></strong>
                            </div>
                            <div class="col-md-3">
                                <span class="text-muted small d-block">Gender</span>
                                <strong id="revGender" class="text-navy"></strong>
                            </div>

                            <div class="col-md-6">
                                <span class="text-muted small d-block">Mobile Number</span>
                                <strong id="revMobile" class="text-navy"></strong>
                            </div>
                            <div class="col-md-6">
                                <span class="text-muted small d-block">Email Address</span>
                                <strong id="revEmail" class="text-navy"></strong>
                            </div>

                            <div class="col-md-6">
                                <span class="text-muted small d-block">Aadhaar (Masked)</span>
                                <strong id="revAadhaar" class="text-navy font-monospace"></strong>
                            </div>
                            <div class="col-md-6">
                                <span class="text-muted small d-block">PAN (Masked)</span>
                                <strong id="revPan" class="text-navy font-monospace"></strong>
                            </div>

                            <div class="col-md-6">
                                <span class="text-muted small d-block">Preferred Branch</span>
                                <strong id="revBranch" class="text-navy"></strong>
                            </div>
                            <div class="col-md-6">
                                <span class="text-muted small d-block">Account Type</span>
                                <strong id="revAccountType" class="text-navy"></strong>
                            </div>

                            <div class="col-md-6">
                                <span class="text-muted small d-block">Auto-Generated Username</span>
                                <strong id="revUsername" class="text-navy"></strong>
                            </div>
                            <div class="col-md-6">
                                <span class="text-muted small d-block">Initial Balance</span>
                                <strong class="text-success fw-bold">₹0.00</strong>
                            </div>
                        </div>
                    </div>

                    <div class="d-flex justify-content-between align-items-center mt-4">
                        <button type="button" class="btn btn-outline-secondary btn-lg px-4" onclick="prevSection(5, 4)"><i class="fa-solid fa-arrow-left me-2"></i> Edit Details</button>
                        <button type="submit" id="btnFinalSubmit" class="btn btn-gold btn-lg px-5 fw-bold shadow"><i class="fa-solid fa-paper-plane me-2"></i> OPEN ACCOUNT NOW</button>
                    </div>
                </div>

            </form>
        </div>
    </div>
</div>

<script>
    // Real-time Auto Username Generator
    function updateAutoUsername() {
        const name = document.getElementById('regFullName')?.value || '';
        const mobile = document.getElementById('regMobile')?.value || '';

        let base = name.replace(/[^a-zA-Z]/g, '').toLowerCase();
        if (base.length > 8) base = base.substring(0, 8);
        if (!base) base = 'skb';

        let suffix = mobile.length >= 4 ? mobile.slice(-4) : '1234';

        const autoUser = base + suffix;
        const userField = document.getElementById('regUsername');
        if (userField) {
            userField.value = autoUser;
        }
    }

    document.getElementById('regFullName')?.addEventListener('input', updateAutoUsername);
    document.getElementById('regMobile')?.addEventListener('input', updateAutoUsername);

    // Image Preview & Validation (Max 5 MB)
    document.getElementById('profileImageInput')?.addEventListener('change', function (e) {
        const file = e.target.files[0];
        if (file) {
            if (file.size > 5 * 1024 * 1024) {
                alert('Profile image size exceeds maximum 5 MB limit. Please select a smaller photo.');
                e.target.value = '';
                return;
            }
            const reader = new FileReader();
            reader.onload = function (evt) {
                document.getElementById('profileImagePreview').src = evt.target.result;
            };
            reader.readAsDataURL(file);
        }
    });

    // Step Navigation Handlers
    function showSection(sectionNumber) {
        for (let i = 1; i <= 5; i++) {
            const sec = document.getElementById('section' + i);
            const ind = document.getElementById('stepIndicator' + i);
            if (sec) {
                if (i === sectionNumber) {
                    sec.classList.remove('d-none');
                } else {
                    sec.classList.add('d-none');
                }
            }
            if (ind) {
                if (i === sectionNumber) {
                    ind.classList.add('active');
                    ind.classList.remove('completed');
                } else if (i < sectionNumber) {
                    ind.classList.remove('active');
                    ind.classList.add('completed');
                } else {
                    ind.classList.remove('active', 'completed');
                }
            }
        }
        window.scrollTo({ top: 100, behavior: 'smooth' });
    }

    function validateSection(sectionNum) {
        const section = document.getElementById('section' + sectionNum);
        if (!section) return true;

        let valid = true;
        const inputs = section.querySelectorAll('input[required], select[required]');

        inputs.forEach(input => {
            if (!input.checkValidity()) {
                input.classList.add('is-invalid');
                valid = false;
            } else {
                input.classList.remove('is-invalid');
            }
        });

        // Special password match check for section 4
        if (sectionNum === 4) {
            const pwd = document.getElementById('regPassword');
            const confirmPwd = document.getElementById('regConfirmPassword');
            if (pwd && confirmPwd && pwd.value !== confirmPwd.value) {
                confirmPwd.classList.add('is-invalid');
                valid = false;
            }
        }

        return valid;
    }

    function nextSection(current, next) {
        updateAutoUsername();
        if (validateSection(current)) {
            showSection(next);
        }
    }

    function prevSection(current, prev) {
        showSection(prev);
    }

    function prepareReviewSection() {
        updateAutoUsername();
        if (!validateSection(4)) return;

        // Populate Review Elements
        document.getElementById('revFullName').textContent = document.getElementById('regFullName').value || '-';
        document.getElementById('revDob').textContent = document.getElementById('regDob').value || '-';
        document.getElementById('revGender').textContent = document.getElementById('regGender').value || '-';
        document.getElementById('revMobile').textContent = document.getElementById('regMobile').value || '-';
        document.getElementById('revEmail').textContent = document.getElementById('regEmail').value || '-';

        const aadhaarVal = document.getElementById('regAadhaar').value || '';
        document.getElementById('revAadhaar').textContent = aadhaarVal.length >= 4 ? 'XXXX XXXX ' + aadhaarVal.slice(-4) : 'XXXX XXXX XXXX';

        const panVal = document.getElementById('regPan').value || '';
        document.getElementById('revPan').textContent = panVal.length === 10 ? panVal.substring(0, 2) + '*****' + panVal.substring(7) : '*****';

        const branchSelect = document.getElementById('regBranchId');
        document.getElementById('revBranch').textContent = branchSelect ? branchSelect.options[branchSelect.selectedIndex].text : '-';

        const accountTypeSelect = document.getElementById('regAccountTypeId');
        document.getElementById('revAccountType').textContent = accountTypeSelect ? accountTypeSelect.options[accountTypeSelect.selectedIndex].text : '-';

        document.getElementById('revUsername').textContent = document.getElementById('regUsername').value || '-';

        showSection(5);
    }
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
