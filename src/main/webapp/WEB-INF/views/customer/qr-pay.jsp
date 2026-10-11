<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Scan & Pay | PhonePe Payment Bank | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h4 class="fw-bold text-navy mb-1"><i class="fa-solid fa-qrcode text-warning me-2"></i>Scan & Pay (UPI QR Bank)</h4>
                <p class="text-muted small mb-0">Scan any UPI QR Code or display your personal QR code to receive money instantly.</p>
            </div>
            <a href="${pageContext.request.contextPath}/customer/dashboard" class="btn btn-outline-secondary btn-sm">
                <i class="fa-solid fa-arrow-left me-1"></i> Back
            </a>
        </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                <i class="fa-solid fa-circle-exclamation me-2"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="row g-4">
            <!-- LEFT COLUMN: MY PERSONAL QR CODE -->
            <div class="col-lg-5">
                <div class="sk-card p-4 text-center shadow-sm border-0 rounded-4 bg-white">
                    <div class="d-flex align-items-center justify-content-center gap-2 mb-3">
                        <img src="${pageContext.request.contextPath}/assets/images/sk_bank_app_logo.png" alt="SK Bank" width="36" height="36" onerror="this.src='https://cdn-icons-png.flaticon.com/512/2830/2830284.png'">
                        <span class="fw-bold text-navy fs-5">SK BANK OF BAREILLY</span>
                    </div>

                    <p class="text-muted small mb-1">Scan with any UPI App to Pay Me</p>
                    <c:set var="primaryUpi" value="${not empty upiAccounts ? upiAccounts[0].upiAddress : 'skbank@upi'}" />

                    <!-- DYNAMIC QR CODE DISPLAY -->
                    <div class="p-3 bg-light rounded-4 d-inline-block border my-2 position-relative shadow-sm">
                        <img id="qrImage" src="https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=upi://pay?pa=${primaryUpi}&pn=${sessionScope.CUSTOMER_NAME}&cu=INR"
                             alt="UPI QR Code" class="img-fluid rounded" style="max-width: 200px;">
                    </div>

                    <h6 class="fw-bold text-navy mt-2 mb-0">${sessionScope.CUSTOMER_NAME}</h6>
                    <span class="badge bg-primary px-3 py-2 rounded-pill mt-1 font-monospace fs-6">
                        <i class="fa-solid fa-at me-1"></i>${primaryUpi}
                    </span>

                    <div class="d-flex gap-2 justify-content-center mt-4">
                        <button onclick="window.print()" class="btn btn-outline-primary btn-sm rounded-pill px-3">
                            <i class="fa-solid fa-download me-1"></i> Save QR
                        </button>
                        <a href="${pageContext.request.contextPath}/customer/upi" class="btn btn-gold btn-sm rounded-pill px-3 fw-bold">
                            <i class="fa-solid fa-gear me-1"></i> Manage UPI
                        </a>
                    </div>
                </div>
            </div>

            <!-- RIGHT COLUMN: SCAN & PAY FORM -->
            <div class="col-lg-7">
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-white">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-camera me-2 text-primary"></i>Pay via QR or UPI ID</h5>

                    <!-- QR IMAGE UPLOAD & CAMERA SCANNER BUTTON -->
                    <div class="p-3 mb-4 bg-light border border-dashed rounded-3 text-center">
                        <i class="fa-solid fa-qrcode fs-1 text-primary mb-2"></i>
                        <h6 class="fw-bold text-navy">Upload / Scan QR Code Image</h6>
                        <p class="text-muted small mb-2">Upload any PhonePe, Paytm, Google Pay, or BHIM QR code image</p>
                        <input type="file" id="qrFileInput" accept="image/*" class="d-none" onchange="parseUploadedQr(this)">
                        <button type="button" onclick="document.getElementById('qrFileInput').click()" class="btn btn-primary btn-sm rounded-pill px-4">
                            <i class="fa-solid fa-upload me-1"></i> Choose QR Image
                        </button>
                    </div>

                    <!-- PAYMENT FORM -->
                    <form action="${pageContext.request.contextPath}/customer/qr-pay" method="POST" id="qrPayForm">
                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Select Payment Account *</label>
                            <select name="senderAccountId" class="form-select rounded-3" required>
                                <c:forEach var="acc" items="${accounts}">
                                    <option value="${acc.accountId}">
                                        ${acc.maskedAccountNumber} (${empty acc.accountTypeName ? 'Savings Account' : acc.accountTypeName}) - Available: ₹<fmt:formatNumber value="${acc.balance}" pattern="#,##0.00"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Recipient UPI ID / Handle / Account *</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="fa-solid fa-user-check text-primary"></i></span>
                                <input type="text" name="recipientDetail" id="recipientDetailInput" class="form-control rounded-end-3"
                                       placeholder="e.g. 9897001122@skbank or merchant@upi" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Amount (₹) *</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light fw-bold text-success">₹</span>
                                <input type="number" step="0.01" min="1.00" name="amount" id="amountInput" class="form-control rounded-end-3 fs-4 fw-bold text-success" placeholder="0.00" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Note / Remarks (Optional)</label>
                            <input type="text" name="remarks" class="form-control rounded-3" placeholder="e.g. Dinner, Shopping, Grocery">
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold text-muted small">Enter 4-Digit UPI PIN *</label>
                            <input type="password" name="upiPin" maxlength="4" class="form-control rounded-3 text-center fs-4 font-monospace letter-spacing-2" placeholder="••••" required>
                        </div>

                        <button type="submit" class="btn btn-gold btn-lg w-100 fw-bold rounded-3 shadow">
                            <i class="fa-solid fa-lock me-2"></i>Pay Now via PhonePe UPI
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
function parseUploadedQr(input) {
    if (input.files && input.files[0]) {
        // Mock QR decoder extracting recipient handle
        var demoHandles = ['merchant@skbank', 'store@upi', 'bareilly_mart@skbank', '9897001122@skbank'];
        var randomHandle = demoHandles[Math.floor(Math.random() * demoHandles.length)];
        document.getElementById('recipientDetailInput').value = randomHandle;
        alert("QR Code scanned successfully! Recipient: " + randomHandle);
    }
}
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
