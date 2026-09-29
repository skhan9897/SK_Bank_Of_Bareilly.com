<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Fixed Deposits & Advice Certificate - SK Bank" />
</jsp:include>
<style>
    @media print {
        body * { visibility: hidden; }
        .printable-fd-card, .printable-fd-card * { visibility: visible; }
        .printable-fd-card { position: absolute; left: 0; top: 0; width: 100%; border: none !important; }
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
                <jsp:param name="active" value="fixed-deposits" />
            </jsp:include>
        </div>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom no-print">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-piggy-bank text-warning me-2"></i> Fixed Deposits (FD) Portal</h3>
                <button class="btn btn-gold" data-bs-toggle="modal" data-bs-target="#openFdModal"><i class="fas fa-plus me-1"></i> Book New Fixed Deposit</button>
            </div>

            <div class="no-print">
                <jsp:include page="/WEB-INF/views/common/alerts.jsp" />
            </div>

            <div class="row g-4">
                <c:forEach var="fd" items="${fixedDeposits}">
                    <div class="col-md-6">
                        <div class="card-custom p-4 bg-white shadow-sm border-warning printable-fd-card">
                            <div class="d-flex justify-content-between align-items-center pb-2 border-bottom mb-3">
                                <div class="d-flex align-items-center gap-2">
                                    <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="Logo" style="height: 40px;">
                                    <div>
                                        <h6 class="fw-bold text-navy mb-0">SK Bank Fixed Deposit Receipt</h6>
                                        <small class="text-muted">FD Ref: ${fd.receiptNumber}</small>
                                    </div>
                                </div>
                                <span class="badge bg-success">${fd.status}</span>
                            </div>

                            <div class="row g-2 mb-3">
                                <div class="col-6">
                                    <span class="text-muted small d-block">Principal Amount</span>
                                    <h4 class="fw-bold text-navy mb-0">₹ <fmt:formatNumber value="${fd.depositAmount}" pattern="#,##0.00"/></h4>
                                </div>
                                <div class="col-6 text-end">
                                    <span class="text-muted small d-block">Maturity Value</span>
                                    <h4 class="fw-bold text-success mb-0">₹ <fmt:formatNumber value="${fd.maturityAmount}" pattern="#,##0.00"/></h4>
                                </div>
                            </div>

                            <div class="p-3 bg-light rounded small mb-3">
                                <div class="row g-2">
                                    <div class="col-6">Interest Rate: <strong>${fd.interestRate}% p.a.</strong></div>
                                    <div class="col-6 text-end">Tenure: <strong>${fd.tenureMonths} Months</strong></div>
                                    <div class="col-12">Maturity Date: <strong>${fd.maturityDate}</strong></div>
                                </div>
                            </div>

                            <div class="no-print d-flex justify-content-end">
                                <button onclick="window.print()" class="btn btn-gold btn-sm fw-bold"><i class="fas fa-print me-1"></i> PRINT / SAVE PDF CERTIFICATE</button>
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

    <div class="no-print">
        <jsp:include page="/WEB-INF/views/common/footer.jsp" />
    </div>

</body>
</html>
