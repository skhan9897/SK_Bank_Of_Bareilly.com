<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.time.LocalDateTime" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Authorized Counter Deposit - Admin SK Bank" />
</jsp:include>
<style>
    @media print {
        body * { visibility: hidden; }
        #adminDepositReceipt, #adminDepositReceipt * { visibility: visible; }
        #adminDepositReceipt { position: absolute; left: 0; top: 0; width: 100%; border: none !important; box-shadow: none !important; }
        .no-print { display: none !important; }
    }
</style>
<body>

    <div class="no-print">
        <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />
    </div>

    <div class="dashboard-container">
        <div class="no-print">
            <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
                <jsp:param name="active" value="transactions" />
            </jsp:include>
        </div>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom no-print">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-cash-register text-warning me-2"></i> Authorized Counter Cash Deposit</h3>
            </div>

            <div class="no-print">
                <jsp:include page="/WEB-INF/views/common/alerts.jsp" />
            </div>

            <!-- COMPLETED DEPOSIT RECEIPT ADVICE -->
            <c:choose>
                <c:when test="${not empty completedDeposit}">
                    <div class="card-custom p-4 p-md-5 bg-white shadow-lg border-2 border-success mx-auto" style="max-width: 800px;" id="adminDepositReceipt">
                        <div class="d-flex justify-content-between align-items-center pb-3 mb-4 border-bottom border-2">
                            <div class="d-flex align-items-center gap-3">
                                <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="Logo" style="height: 60px; object-fit: contain;">
                                <div>
                                    <h4 class="fw-bold text-navy mb-0">SK Bank of Bareilly</h4>
                                    <small class="text-muted">Official Branch Counter Deposit Advice & Receipt</small>
                                </div>
                            </div>
                            <div class="text-end">
                                <span class="badge bg-success fs-6 mb-1"><i class="fas fa-check-circle me-1"></i> CASH DEPOSITED</span>
                                <div class="small text-muted">Date: <strong><%= LocalDateTime.now() %></strong></div>
                            </div>
                        </div>

                        <div class="p-3 bg-light rounded text-center mb-4 border">
                            <span class="text-muted small text-uppercase">Total Cash Deposited</span>
                            <h2 class="fw-bold text-success my-1">₹ <fmt:formatNumber value="${completedDeposit.amount}" pattern="#,##0.00"/></h2>
                            <span class="badge badge-navy px-3 py-1">Receipt Ref: ${completedDeposit.transactionId}</span>
                        </div>

                        <div class="row g-3 mb-4">
                            <div class="col-md-6">
                                <div class="p-3 bg-light rounded border h-100">
                                    <h6 class="fw-bold text-navy border-bottom pb-2 mb-2">Customer Details</h6>
                                    <div class="small mb-1">Name: <strong>${depositCustomer.fullName}</strong></div>
                                    <div class="small mb-1">Customer ID: <strong>${depositCustomer.customerId}</strong></div>
                                    <div class="small">Mobile: <strong>${depositCustomer.mobile}</strong></div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="p-3 bg-light rounded border h-100">
                                    <h6 class="fw-bold text-navy border-bottom pb-2 mb-2">Account Balance After Deposit</h6>
                                    <div class="small mb-1">Account Number: <strong>${depositAccount.accountNumber}</strong></div>
                                    <div class="small mb-1">Previous Balance: ₹ <fmt:formatNumber value="${completedDeposit.balanceBefore}" pattern="#,##0.00"/></div>
                                    <div class="small fw-bold text-success fs-6">New Balance: ₹ <fmt:formatNumber value="${completedDeposit.balanceAfter}" pattern="#,##0.00"/></div>
                                </div>
                            </div>
                        </div>

                        <div class="d-flex justify-content-between align-items-center pt-3 border-top no-print">
                            <a href="${pageContext.request.contextPath}/admin/deposit" class="btn btn-outline-navy"><i class="fas fa-redo me-1"></i> New Deposit</a>
                            <button onclick="window.print()" class="btn btn-gold fw-bold"><i class="fas fa-print me-1"></i> PRINT DEPOSIT RECEIPT (PDF)</button>
                        </div>
                    </div>
                </c:when>

                <!-- SEARCH & DEPOSIT FORM -->
                <c:otherwise>
                    <div class="card-custom p-4 bg-white shadow-sm mb-4 no-print">
                        <h5 class="fw-bold text-navy mb-3"><i class="fas fa-search me-2 text-warning"></i> Search Customer for Counter Deposit</h5>
                        <form action="${pageContext.request.contextPath}/admin/deposit" method="get" class="row g-3">
                            <div class="col-md-9">
                                <input type="text" name="search" class="form-control form-control-lg fs-6" placeholder="Enter Customer ID, Account Number, or Mobile Number..." value="${param.search}" required>
                            </div>
                            <div class="col-md-3 d-grid">
                                <button type="submit" class="btn btn-gold py-2 fw-bold"><i class="fas fa-search me-1"></i> Search Account</button>
                            </div>
                        </form>
                    </div>

                    <c:if test="${not empty selectedCustomer}">
                        <div class="card-custom p-4 bg-white shadow-sm border-2 border-primary no-print">
                            <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-user-check me-2 text-warning"></i> Deposit Funds for ${selectedCustomer.fullName}</h5>

                            <form action="${pageContext.request.contextPath}/admin/deposit" method="post">
                                <div class="row g-3 mb-3">
                                    <div class="col-md-6">
                                        <label class="form-label fw-bold">Select Target Account *</label>
                                        <select name="accountId" class="form-select form-select-lg fs-6" required>
                                            <c:forEach var="acc" items="${customerAccounts}">
                                                <option value="${acc.accountId}">${acc.accountTypeName} - ${acc.accountNumber} (Current Balance: ₹<fmt:formatNumber value="${acc.balance}" pattern="#,##0.00"/>)</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-bold">Cash Deposit Amount (₹) *</label>
                                        <input type="number" name="amount" class="form-control form-control-lg fs-6" min="1" step="0.01" placeholder="Enter amount to deposit" required>
                                    </div>
                                </div>

                                <div class="mb-4">
                                    <label class="form-label fw-bold">Deposit Remarks / Counter Receipt Note</label>
                                    <input type="text" name="remarks" class="form-control" placeholder="e.g. Branch Counter Cash Deposit / Authorized Payout">
                                </div>

                                <button type="submit" class="btn btn-navy py-3 px-4 fw-bold fs-6 shadow-sm"><i class="fas fa-check-circle me-2"></i> PROCESS COUNTER CASH DEPOSIT</button>
                            </form>
                        </div>
                    </c:if>
                </c:otherwise>
            </c:choose>

        </div>
    </div>

    <div class="no-print">
        <jsp:include page="/WEB-INF/views/common/footer.jsp" />
    </div>

</body>
</html>
