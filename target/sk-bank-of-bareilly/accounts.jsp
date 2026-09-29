<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="My Accounts & Net Banking - SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="accounts" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-wallet text-warning me-2"></i> Net Banking Accounts</h3>
                <div class="d-flex gap-2">
                    <button class="btn btn-outline-gold" data-bs-toggle="modal" data-bs-target="#chequeBookModal"><i class="fas fa-book me-1"></i> Request Cheque Book</button>
                    <button class="btn btn-gold" data-bs-toggle="modal" data-bs-target="#newAccountModal"><i class="fas fa-plus me-1"></i> Open Additional Account</button>
                </div>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row g-4">
                <c:forEach var="acc" items="${accounts}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card-custom p-4 bg-white h-100">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="badge badge-navy">${acc.accountTypeName}</span>
                                <span class="badge bg-success">${acc.status}</span>
                            </div>
                            <h4 class="fw-bold text-navy my-2">₹ <fmt:formatNumber value="${acc.balance}" pattern="#,##0.00"/></h4>
                            <div class="small text-muted mb-1"><i class="fas fa-credit-card me-1"></i> Account Number:</div>
                            <div class="fw-bold fs-6 mb-2 text-dark">${acc.accountNumber}</div>
                            <div class="small text-muted">IFSC: <strong>${acc.ifscCode}</strong> | Branch: <strong>${acc.branchName}</strong></div>
                            <hr class="my-3">
                            <div class="d-flex gap-2">
                                <a href="${pageContext.request.contextPath}/statements?accountId=${acc.accountId}" class="btn btn-outline-gold btn-sm flex-fill"><i class="fas fa-file-invoice me-1"></i> Statement</a>
                                <a href="${pageContext.request.contextPath}/transfer" class="btn btn-gold btn-sm flex-fill"><i class="fas fa-paper-plane me-1"></i> Transfer</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

    <!-- REQUEST CHEQUE BOOK MODAL -->
    <div class="modal fade" id="chequeBookModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="card-navy-header"><i class="fas fa-book text-warning me-2"></i> Request Cheque Book</div>
                <form action="${pageContext.request.contextPath}/accounts" method="post" class="p-4">
                    <input type="hidden" name="action" value="requestChequeBook">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Select Account *</label>
                        <select name="accountNumber" class="form-select" required>
                            <c:forEach var="a" items="${accounts}">
                                <option value="${a.accountNumber}">${a.accountTypeName} - ${a.accountNumber}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Cheque Book Leaves *</label>
                        <select name="leaves" class="form-select" required>
                            <option value="25">25 Leaves Booklet</option>
                            <option value="50">50 Leaves Booklet</option>
                            <option value="100">100 Leaves Booklet</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Delivery Address</label>
                        <input type="text" class="form-control" value="${sessionScope.customerProfile.address}" readonly>
                        <div class="form-text small">Will be delivered to registered customer address.</div>
                    </div>
                    <div class="d-flex justify-content-end gap-2">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-gold">Confirm Request</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- NEW ACCOUNT MODAL -->
    <div class="modal fade" id="newAccountModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="card-navy-header">Open Additional Account</div>
                <form action="${pageContext.request.contextPath}/accounts" method="post" class="p-4">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Select Account Type</label>
                        <select name="typeId" class="form-select" required>
                            <c:forEach var="type" items="${accountTypes}">
                                <option value="${type.typeId}">${type.typeName} (Min. Bal: ₹${type.minBalance})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Initial Deposit Amount (₹)</label>
                        <input type="number" name="initialDeposit" class="form-control" value="1000" min="500" required>
                    </div>
                    <div class="d-flex justify-content-end gap-2">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-gold">Confirm & Open Account</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
