<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Transfer Money - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="transfer" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-paper-plane text-warning me-2"></i> Money Transfer Portal</h3>
                <p class="text-muted small">Fast, secure, 24/7 instant fund transfer via NEFT, RTGS, IMPS & Internal Transfer.</p>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <!-- SUCCESS TRANSACTION RECEIPT MODAL/CARD -->
            <c:if test="${not empty transactionResult}">
                <div class="card-custom p-4 bg-white border border-success border-2 mb-4 shadow">
                    <div class="text-center text-success mb-3">
                        <i class="fas fa-check-circle display-4"></i>
                        <h4 class="fw-bold text-navy mt-2">Transaction Successful!</h4>
                        <p class="text-muted small">Your funds have been transferred successfully.</p>
                    </div>
                    <div class="row g-3 bg-light p-3 rounded small">
                        <div class="col-6">Transaction ID: <strong class="text-navy">${transactionResult.transactionId}</strong></div>
                        <div class="col-6">Reference No: <strong>${transactionResult.referenceNumber}</strong></div>
                        <div class="col-6">Amount Transferred: <strong class="text-success fs-6">₹ <fmt:formatNumber value="${transactionResult.amount}" pattern="#,##0.00"/></strong></div>
                        <div class="col-6">Sender Account: <strong>${transactionResult.accountNumber}</strong></div>
                        <div class="col-12">Updated Available Balance: <strong class="text-navy">₹ <fmt:formatNumber value="${transactionResult.balanceAfter}" pattern="#,##0.00"/></strong></div>
                    </div>
                    <div class="text-center mt-3">
                        <button onclick="window.print()" class="btn btn-outline-gold btn-sm"><i class="fas fa-print me-1"></i> Print Receipt</button>
                    </div>
                </div>
            </c:if>

            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="card-custom p-4 bg-white shadow-sm">
                        <h5 class="fw-bold text-navy mb-3"><i class="fas fa-arrow-circle-right text-warning me-2"></i> Fund Transfer Form</h5>
                        <form action="${pageContext.request.contextPath}/transfer" method="post" id="transferForm">
                            <div class="mb-3">
                                <label class="form-label fw-bold">From Account *</label>
                                <select name="fromAccount" class="form-select form-select-lg fs-6" required>
                                    <c:forEach var="a" items="${accounts}">
                                        <option value="${a.accountNumber}">${a.accountTypeName} - ${a.accountNumber} (Available Bal: ₹${a.balance})</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-bold">Transfer Type *</label>
                                <select name="transferType" class="form-select" required>
                                    <option value="IMPS">IMPS (Instant 24x7 Payment)</option>
                                    <option value="NEFT">NEFT (National Electronic Funds Transfer)</option>
                                    <option value="RTGS">RTGS (Real Time Gross Settlement - High Value)</option>
                                    <option value="INTERNAL">Internal SK Bank Account Transfer</option>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-bold">Select Beneficiary or Enter Account *</label>
                                <input type="text" name="toAccount" list="beneficiaryList" class="form-control form-control-lg fs-6" placeholder="Enter Receiver Account Number or Pick Beneficiary" required>
                                <datalist id="beneficiaryList">
                                    <c:forEach var="b" items="${beneficiaries}">
                                        <option value="${b.accountNumber}">${b.name} - ${b.bankName} (${b.accountNumber})</option>
                                    </c:forEach>
                                </datalist>
                                <div class="form-text small">Don't see beneficiary? <a href="${pageContext.request.contextPath}/beneficiaries" class="text-primary">Add Beneficiary here</a></div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-bold">Amount (₹) *</label>
                                <input type="number" name="amount" class="form-control form-control-lg fs-6 fw-bold text-navy" step="0.01" min="1" required placeholder="Enter Amount in INR">
                            </div>

                            <div class="mb-4">
                                <label class="form-label fw-bold">Remarks / Purpose (Optional)</label>
                                <input type="text" name="remarks" class="form-control" placeholder="e.g. Rent Payment, Family Support">
                            </div>

                            <button type="submit" class="btn btn-gold w-100 py-3 fw-bold fs-6 shadow-sm"><i class="fas fa-lock me-2"></i> PROCEED TO TRANSFER</button>
                        </form>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="card-custom p-4 bg-white mb-4">
                        <h6 class="fw-bold text-navy mb-3"><i class="fas fa-shield-alt text-warning me-2"></i> Transfer Guidelines</h6>
                        <ul class="small text-muted ps-3 mb-0">
                            <li class="mb-2">IMPS transfers are processed 24x7 instantly.</li>
                            <li class="mb-2">Verify receiver account number carefully before confirming transfer.</li>
                            <li class="mb-2">Never share your OTP or Internet Banking password with anyone.</li>
                            <li class="mb-2">Zero charges on internal SK Bank transfers.</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
