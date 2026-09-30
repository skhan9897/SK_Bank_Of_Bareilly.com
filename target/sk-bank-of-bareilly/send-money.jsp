<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Send Money & Automatic Payee Search - SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="transfer" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-paper-plane text-warning me-2"></i> Send Money (Instant Payee Search)</h3>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="card-custom p-4 bg-white shadow-sm">
                        <!-- TABS FOR RECIPIENT SEARCH MODE -->
                        <ul class="nav nav-pills nav-justified mb-4" id="sendMoneyTabs">
                            <li class="nav-item">
                                <button class="nav-link active fw-bold" id="mobileTab" data-bs-toggle="pill" data-bs-target="#mobilePanel" type="button">
                                    <i class="fas fa-mobile-alt me-1"></i> Mobile Number
                                </button>
                            </li>
                            <li class="nav-item">
                                <button class="nav-link fw-bold" id="accountTab" data-bs-toggle="pill" data-bs-target="#accountPanel" type="button">
                                    <i class="fas fa-university me-1"></i> Account Number
                                </button>
                            </li>
                            <li class="nav-item">
                                <button class="nav-link fw-bold" id="upiTab" data-bs-toggle="pill" data-bs-target="#upiPanel" type="button">
                                    <i class="fas fa-qrcode me-1"></i> UPI ID
                                </button>
                            </li>
                        </ul>

                        <div class="tab-content" id="sendMoneyTabContent">
                            <!-- PANEL 1: MOBILE SEARCH -->
                            <div class="tab-pane fade show active" id="mobilePanel">
                                <div class="mb-3">
                                    <label class="form-label fw-bold">Enter Recipient 10-Digit Mobile Number *</label>
                                    <div class="input-group input-group-lg">
                                        <span class="input-group-text bg-navy text-warning"><i class="fas fa-phone"></i></span>
                                        <input type="tel" id="mobileInput" class="form-control fs-6" maxlength="10" placeholder="e.g. 9876543210" oninput="searchRecipientByMobile(this.value)">
                                    </div>
                                    <div class="form-text small">Search triggers automatically when 10 digits are entered.</div>
                                </div>
                            </div>

                            <!-- PANEL 2: ACCOUNT NUMBER SEARCH -->
                            <div class="tab-pane fade" id="accountPanel">
                                <div class="mb-3">
                                    <label class="form-label fw-bold">Enter Recipient Account Number *</label>
                                    <div class="input-group input-group-lg">
                                        <span class="input-group-text bg-navy text-warning"><i class="fas fa-university"></i></span>
                                        <input type="text" id="accountInput" class="form-control fs-6" placeholder="e.g. SKB24010000001" oninput="searchRecipientByAccount(this.value)">
                                    </div>
                                    <div class="form-text small">Automatic database search starts as you type.</div>
                                </div>
                            </div>

                            <!-- PANEL 3: UPI ID SEARCH -->
                            <div class="tab-pane fade" id="upiPanel">
                                <div class="mb-3">
                                    <label class="form-label fw-bold">Enter Recipient UPI ID *</label>
                                    <div class="input-group input-group-lg">
                                        <span class="input-group-text bg-navy text-warning"><i class="fas fa-at"></i></span>
                                        <input type="text" id="upiInput" class="form-control fs-6" placeholder="e.g. rahul@skbank" oninput="searchRecipientByUpi(this.value)">
                                    </div>
                                    <div class="form-text small">Format: username@skbank</div>
                                </div>
                            </div>
                        </div>

                        <!-- AUTOMATIC RECIPIENT DISPLAY CARD -->
                        <div id="recipientResultCard" class="d-none mt-4 p-4 rounded bg-light border-2 border-primary">
                            <div class="d-flex justify-content-between align-items-center border-bottom pb-2 mb-3">
                                <span class="badge bg-success fs-6"><i class="fas fa-check-circle me-1"></i> RECIPIENT FOUND ✓</span>
                                <span class="badge badge-navy" id="resStatus">ACTIVE</span>
                            </div>

                            <div class="row g-3 mb-3">
                                <div class="col-md-6">
                                    <span class="text-muted small d-block">Recipient Account Holder</span>
                                    <h5 class="fw-bold text-navy my-1" id="resName">Rahul Sharma</h5>
                                    <div class="small text-muted" id="resBank">SK Bank of Bareilly</div>
                                </div>
                                <div class="col-md-6 text-md-end">
                                    <span class="text-muted small d-block">UPI ID / Account</span>
                                    <strong class="text-primary d-block" id="resUpi">rahul@skbank</strong>
                                    <strong class="text-dark d-block" id="resAcc">XXXXXX1234</strong>
                                </div>
                            </div>

                            <!-- PAYMENT TRANSFER FORM -->
                            <form action="${pageContext.request.contextPath}/transfer" method="post" class="mt-4 pt-3 border-top">
                                <input type="hidden" name="senderAccount" value="${accounts[0].accountNumber}">
                                <input type="hidden" name="receiverAccount" id="formReceiverAcc">
                                <input type="hidden" name="transferType" value="IMPS">

                                <div class="row g-3 mb-3">
                                    <div class="col-md-6">
                                        <label class="form-label fw-bold">Transfer Amount (₹) *</label>
                                        <input type="number" name="amount" class="form-control form-control-lg fs-6" min="1" max="25000" step="0.01" required placeholder="Max ₹25,000 per txn">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-bold">Remarks (Max 250 chars)</label>
                                        <input type="text" name="remarks" class="form-control form-control-lg fs-6" maxlength="250" placeholder="e.g. Rent / Family / Fees">
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-gold w-100 py-3 fw-bold fs-6 shadow-sm"><i class="fas fa-paper-plane me-2"></i> CONFIRM & PAY NOW</button>
                            </form>
                        </div>

                        <!-- SEARCH ERROR ALERT -->
                        <div id="recipientErrorCard" class="d-none mt-4 alert alert-danger py-3 text-center mb-0">
                            <i class="fas fa-exclamation-circle me-1"></i> <span id="errorMsg">Recipient not found.</span>
                        </div>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="card-custom p-4 bg-white shadow-sm mb-4">
                        <h6 class="fw-bold text-navy mb-2"><i class="fas fa-info-circle text-warning me-2"></i> How Search Works</h6>
                        <ul class="small text-muted ps-3 mb-0">
                            <li class="mb-2">Enter mobile, account number, or UPI ID.</li>
                            <li class="mb-2">System automatically retrieves the verified account holder name from the database.</li>
                            <li class="mb-2">You do NOT need to manually type payee name.</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        let searchTimeout;

        function searchRecipientByMobile(val) {
            clearTimeout(searchTimeout);
            if (val.length === 10) {
                searchTimeout = setTimeout(() => {
                    fetch('${pageContext.request.contextPath}/customer/recipient/mobile?mobile=' + encodeURIComponent(val))
                        .then(r => r.json())
                        .then(data => displayRecipient(data));
                }, 300);
            } else {
                hideCards();
            }
        }

        function searchRecipientByAccount(val) {
            clearTimeout(searchTimeout);
            if (val.length >= 6) {
                searchTimeout = setTimeout(() => {
                    fetch('${pageContext.request.contextPath}/customer/recipient/account?accountNumber=' + encodeURIComponent(val))
                        .then(r => r.json())
                        .then(data => displayRecipient(data));
                }, 400);
            } else {
                hideCards();
            }
        }

        function searchRecipientByUpi(val) {
            clearTimeout(searchTimeout);
            if (val.includes('@')) {
                searchTimeout = setTimeout(() => {
                    fetch('${pageContext.request.contextPath}/customer/recipient/upi?upiId=' + encodeURIComponent(val))
                        .then(r => r.json())
                        .then(data => displayRecipient(data));
                }, 400);
            } else {
                hideCards();
            }
        }

        function displayRecipient(data) {
            const card = document.getElementById('recipientResultCard');
            const errCard = document.getElementById('recipientErrorCard');

            if (data && data.found) {
                document.getElementById('resName').innerText = data.customerName;
                document.getElementById('resUpi').innerText = data.upiId;
                document.getElementById('resAcc').innerText = data.maskedAccountNumber;
                document.getElementById('resBank').innerText = data.bankName + ' (' + data.branchName + ')';
                document.getElementById('resStatus').innerText = data.status;
                document.getElementById('formReceiverAcc').value = data.accountNumber;

                card.classList.remove('d-none');
                errCard.classList.add('d-none');
            } else {
                document.getElementById('errorMsg').innerText = data.message || 'Recipient not found in SK Bank database.';
                card.classList.add('d-none');
                errCard.classList.remove('d-none');
            }
        }

        function hideCards() {
            document.getElementById('recipientResultCard').classList.add('d-none');
            document.getElementById('recipientErrorCard').classList.add('d-none');
        }
    </script>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
