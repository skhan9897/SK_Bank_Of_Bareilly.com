<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Fixed Deposits - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="fixed-deposits" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-piggy-bank text-warning me-2"></i> Fixed Deposits (FD)</h3>
                <button class="btn btn-gold" data-bs-toggle="modal" data-bs-target="#openFdModal"><i class="fas fa-plus me-1"></i> Book New Fixed Deposit</button>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row g-4">
                <c:forEach var="fd" items="${fixedDeposits}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card-custom p-4 bg-white h-100 shadow-sm border-warning">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="badge badge-gold">Receipt: ${fd.receiptNumber}</span>
                                <span class="badge bg-success">${fd.status}</span>
                            </div>
                            <h4 class="fw-bold text-navy my-2">₹ <fmt:formatNumber value="${fd.depositAmount}" pattern="#,##0.00"/></h4>
                            <div class="small text-muted mb-1">Interest Rate: <strong class="text-dark">${fd.interestRate}% p.a.</strong></div>
                            <div class="small text-muted mb-1">Tenure: <strong>${fd.tenureMonths} Months</strong></div>
                            <div class="small text-muted mb-1">Maturity Date: <strong>${fd.maturityDate}</strong></div>
                            <hr class="my-2">
                            <div class="d-flex justify-content-between align-items-center">
                                <span class="small text-muted">Maturity Value:</span>
                                <span class="fw-bold text-success fs-6">₹ <fmt:formatNumber value="${fd.maturityAmount}" pattern="#,##0.00"/></span>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

    <!-- BOOK FD MODAL -->
    <div class="modal fade" id="openFdModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="card-navy-header">Book Fixed Deposit</div>
                <form action="${pageContext.request.contextPath}/fixed-deposits" method="post" class="p-4">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Select Funding Account *</label>
                        <select name="accountId" class="form-select" required>
                            <c:forEach var="a" items="${accounts}">
                                <option value="${a.accountId}">${a.accountTypeName} - ${a.accountNumber} (Available: ₹${a.balance})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Deposit Amount (₹) *</label>
                        <input type="number" name="amount" id="fdAmount" class="form-control" min="1000" value="50000" oninput="calculateFD()" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Select Tenure *</label>
                        <select name="tenureMonths" id="fdTenure" class="form-select" onchange="calculateFD()" required>
                            <option value="6">6 Months (6.00% p.a.)</option>
                            <option value="12" selected>1 Year / 12 Months (6.75% p.a.)</option>
                            <option value="24">2 Years / 24 Months (7.10% p.a.)</option>
                            <option value="36">3 Years / 36 Months (7.50% p.a.)</option>
                            <option value="60">5 Years / 60 Months (7.50% p.a.)</option>
                        </select>
                    </div>
                    <div class="p-3 bg-light rounded mb-3 small">
                        <div class="d-flex justify-content-between mb-1">
                            <span>Applicable Interest Rate:</span>
                            <strong id="fdRateDisplay">6.75 %</strong>
                        </div>
                        <div class="d-flex justify-content-between">
                            <span>Estimated Maturity Value:</span>
                            <strong id="fdMaturityDisplay" class="text-success fs-6">₹ 53,375.00</strong>
                        </div>
                    </div>
                    <div class="d-flex justify-content-end gap-2">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-gold">Confirm & Book FD</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
