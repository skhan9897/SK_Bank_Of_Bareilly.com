<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Bill Payments - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="payments" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-bolt text-warning me-2"></i> Utility Bill Payments & Recharges</h3>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row g-4">
                <div class="col-lg-7">
                    <div class="card-custom p-4 bg-white shadow-sm">
                        <h5 class="fw-bold text-navy mb-3"><i class="fas fa-receipt text-warning me-2"></i> Pay Bills Online</h5>
                        <form action="${pageContext.request.contextPath}/payments" method="post">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Select Account for Payment *</label>
                                <select name="accountId" class="form-select" required>
                                    <c:forEach var="a" items="${accounts}">
                                        <option value="${a.accountId}">${a.accountTypeName} - ${a.accountNumber} (Available: ₹${a.balance})</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Bill Category *</label>
                                <select name="category" class="form-select" required>
                                    <option value="Electricity">Electricity Bill</option>
                                    <option value="Mobile Recharge">Mobile Prepaid / Postpaid</option>
                                    <option value="Water">Water Bill</option>
                                    <option value="Gas">Piped Gas / LPG Cylinder</option>
                                    <option value="DTH">DTH Recharge</option>
                                    <option value="Internet">Broadband & Internet</option>
                                    <option value="Insurance">Insurance Premium</option>
                                    <option value="Credit Card Bill">Credit Card Bill</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Provider / Biller Name *</label>
                                <input type="text" name="provider" class="form-control" required placeholder="e.g. UPVCL / Airtel / Jio / Adani Gas">
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Consumer / Service / Mobile Number *</label>
                                <input type="text" name="consumerNumber" class="form-control" required placeholder="Enter Consumer ID or Phone Number">
                            </div>
                            <div class="mb-4">
                                <label class="form-label fw-bold">Bill Amount (₹) *</label>
                                <input type="number" name="amount" class="form-control form-control-lg fs-6 fw-bold text-navy" min="1" step="0.01" required placeholder="Enter Bill Amount">
                            </div>
                            <button type="submit" class="btn btn-gold w-100 py-3 fw-bold fs-6"><i class="fas fa-check-circle me-2"></i> PAY BILL NOW</button>
                        </form>
                    </div>
                </div>

                <div class="col-lg-5">
                    <div class="card-custom bg-white p-3">
                        <h6 class="fw-bold text-navy mb-3"><i class="fas fa-history text-warning me-2"></i> Recent Payment History</h6>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle table-custom mb-0 small">
                                <thead>
                                    <tr>
                                        <th>Category</th>
                                        <th>Provider</th>
                                        <th>Amount</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty billPayments}">
                                            <c:forEach var="bp" items="${billPayments}">
                                                <tr>
                                                    <td class="fw-bold text-navy">${bp.billCategory}</td>
                                                    <td>${bp.provider}</td>
                                                    <td class="fw-bold text-danger">₹ <fmt:formatNumber value="${bp.amount}" pattern="#,##0.00"/></td>
                                                    <td><span class="badge bg-success">${bp.status}</span></td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="4" class="text-center py-3 text-muted">No bill payments yet.</td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
