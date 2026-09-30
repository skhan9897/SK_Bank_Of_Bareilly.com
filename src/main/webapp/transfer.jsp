<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="nowDate" class="java.util.Date" />
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Money Transfer & Receipt - SK Bank" />
</jsp:include>
<style>
    @media print {
        body * { visibility: hidden; }
        #txnReceiptCard, #txnReceiptCard * { visibility: visible; }
        #txnReceiptCard { position: absolute; left: 0; top: 0; width: 100%; border: none !important; box-shadow: none !important; }
        .no-print { display: none !important; }
    }
</style>
<body>

    <div class="no-print">
        <jsp:include page="/WEB-INF/views/common/navbar.jsp" />
    </div>

    <div class="dashboard-container">
        <div class="no-print">
            <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
                <jsp:param name="active" value="transfer" />
            </jsp:include>
        </div>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom no-print">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-paper-plane text-warning me-2"></i> Fund Transfer Portal</h3>
            </div>

            <div class="no-print">
                <jsp:include page="/WEB-INF/views/common/alerts.jsp" />
            </div>

            <c:choose>
                <!-- COMPLETED TRANSACTION RECEIPT CARD -->
                <c:when test="${not empty completedTxn}">
                    <div class="card-custom p-4 p-md-5 bg-white shadow-lg border-2 border-success mx-auto" style="max-width: 800px;" id="txnReceiptCard">
                        <!-- RECEIPT HEADER -->
                        <div class="d-flex justify-content-between align-items-center pb-3 mb-4 border-bottom border-2">
                            <div class="d-flex align-items-center gap-3">
                                <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="SK Logo" style="height: 60px; object-fit: contain;">
                                <div>
                                    <h4 class="fw-bold text-navy mb-0">SK Bank of Bareilly</h4>
                                    <small class="text-muted">Official Payment Transfer Advice & Receipt</small>
                                </div>
                            </div>
                            <div class="text-end">
                                <span class="badge bg-success fs-6 mb-1"><i class="fas fa-check-circle me-1"></i> PAYMENT SUCCESS</span>
                                <div class="small text-muted">Date: <strong><fmt:formatDate value="${nowDate}" pattern="yyyy-MM-dd HH:mm:ss" /></strong></div>
                            </div>
                        </div>

                        <!-- RECEIPT AMOUNT BANNER -->
                        <div class="p-3 bg-light rounded text-center mb-4 border">
                            <span class="text-muted small text-uppercase">Amount Transferred</span>
                            <h2 class="fw-bold text-success my-1">₹ <fmt:formatNumber value="${completedTxn.amount}" pattern="#,##0.00"/></h2>
                            <span class="badge badge-navy px-3 py-1">Transfer Mode: ${transferType}</span>
                        </div>

                        <!-- RECEIPT DETAILS TABLE -->
                        <div class="row g-3 mb-4">
                            <div class="col-md-6">
                                <div class="p-3 bg-light rounded border h-100">
                                    <h6 class="fw-bold text-navy border-bottom pb-2 mb-2"><i class="fas fa-arrow-circle-up text-danger me-1"></i> Sender Details</h6>
                                    <div class="small mb-1">Sender Name: <strong>${sessionScope.customerProfile.fullName}</strong></div>
                                    <div class="small mb-1">Sender Account: <strong>${completedTxn.accountNumber}</strong></div>
                                    <div class="small">Remaining Balance: <strong class="text-navy">₹ <fmt:formatNumber value="${completedTxn.balanceAfter}" pattern="#,##0.00"/></strong></div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="p-3 bg-light rounded border h-100">
                                    <h6 class="fw-bold text-navy border-bottom pb-2 mb-2"><i class="fas fa-arrow-circle-down text-success me-1"></i> Beneficiary Details</h6>
                                    <div class="small mb-1">Payee Account: <strong>${receiverAccNo}</strong></div>
                                    <div class="small mb-1">Transaction Ref No: <strong class="text-primary">${completedTxn.transactionId}</strong></div>
                                    <div class="small">Status: <strong class="text-success">SUCCESS / SETTLED</strong></div>
                                </div>
                            </div>
                        </div>

                        <!-- RECEIPT ACTIONS -->
                        <div class="d-flex justify-content-between align-items-center pt-3 border-top no-print">
                            <a href="${pageContext.request.contextPath}/transfer" class="btn btn-outline-navy"><i class="fas fa-redo me-1"></i> New Transfer</a>
                            <div class="d-flex gap-2">
                                <button onclick="window.print()" class="btn btn-gold fw-bold"><i class="fas fa-print me-1"></i> PRINT / SAVE PDF RECEIPT</button>
                                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-navy"><i class="fas fa-home me-1"></i> Dashboard</a>
                            </div>
                        </div>
                    </div>
                </c:when>

                <!-- TRANSFER FORM -->
                <c:otherwise>
                    <div class="row g-4">
                        <div class="col-lg-8">
                            <div class="card-custom p-4 bg-white shadow-sm">
                                <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-exchange-alt me-2 text-warning"></i> Initiate Instant Transfer</h5>

                                <form action="${pageContext.request.contextPath}/transfer" method="post">
                                    <div class="mb-3">
                                        <label class="form-label fw-bold">Select Paying Account *</label>
                                        <select name="senderAccount" class="form-select form-select-lg fs-6" required>
                                            <c:forEach var="acc" items="${accounts}">
                                                <option value="${acc.accountNumber}">${acc.accountTypeName} - ${acc.accountNumber} (Available: ₹<fmt:formatNumber value="${acc.balance}" pattern="#,##0.00"/>)</option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <div class="row g-3 mb-3">
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">Transfer Type *</label>
                                            <select name="transferType" class="form-select" required>
                                                <option value="IMPS">IMPS (Instant 24x7 Settlement)</option>
                                                <option value="NEFT">NEFT (National Electronic Transfer)</option>
                                                <option value="RTGS">RTGS (High Value Transfer)</option>
                                                <option value="INTERNAL">SK Bank Internal Account Transfer</option>
                                            </select>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">Recipient Account Number / UPI *</label>
                                            <input type="text" name="receiverAccount" class="form-control" placeholder="Enter 14-digit Account Number" required>
                                        </div>
                                    </div>

                                    <div class="row g-3 mb-3">
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">Amount to Transfer (₹) *</label>
                                            <input type="number" name="amount" class="form-control form-control-lg fs-6" min="1" max="25000" step="0.01" placeholder="Max ₹25,000 per txn" required>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">Remarks / Purpose</label>
                                            <input type="text" name="remarks" class="form-control" placeholder="e.g. Bill Payment, Rent, Personal">
                                        </div>
                                    </div>

                                    <div class="d-grid mt-4">
                                        <button type="submit" class="btn btn-gold py-3 fw-bold fs-6 shadow-sm"><i class="fas fa-lock me-2"></i> AUTHORIZE & TRANSFER MONEY</button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <div class="col-lg-4">
                            <div class="card-custom p-4 bg-white shadow-sm mb-4">
                                <h6 class="fw-bold text-navy mb-2"><i class="fas fa-shield-alt text-warning me-2"></i> Security Guidelines</h6>
                                <ul class="small text-muted ps-3 mb-0">
                                    <li class="mb-2">Never share your Internet Banking password, OTP, or Card PIN with anyone.</li>
                                    <li class="mb-2">Ensure payee account number is double checked before confirming.</li>
                                    <li class="mb-2">Instant payment receipt is generated upon successful transfer.</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="no-print">
        <jsp:include page="/WEB-INF/views/common/footer.jsp" />
    </div>

</body>
</html>
