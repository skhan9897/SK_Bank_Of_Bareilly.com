<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Loans & EMI - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="loans" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-hand-holding-usd text-warning me-2"></i> Loans & EMI Portal</h3>
                <button class="btn btn-gold" data-bs-toggle="modal" data-bs-target="#applyLoanModal"><i class="fas fa-plus me-1"></i> Apply New Loan</button>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row g-4 mb-4">
                <c:forEach var="l" items="${loans}">
                    <div class="col-md-6 col-lg-4">
                        <div class="card-custom p-4 bg-white h-100">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span class="badge badge-navy">${l.loanType}</span>
                                <span class="badge ${l.status == 'APPROVED' ? 'bg-success' : 'bg-warning'}">${l.status}</span>
                            </div>
                            <h4 class="fw-bold text-navy my-2">₹ <fmt:formatNumber value="${l.principalAmount}" pattern="#,##0.00"/></h4>
                            <div class="small text-muted mb-1">Monthly EMI: <strong class="text-dark">₹ <fmt:formatNumber value="${l.monthlyEmi}" pattern="#,##0.00"/></strong></div>
                            <div class="small text-muted mb-1">Interest Rate: <strong>${l.interestRate}% p.a.</strong></div>
                            <div class="small text-muted mb-1">Tenure: <strong>${l.tenureMonths} Months</strong></div>
                            <div class="small text-muted">Outstanding: <strong class="text-danger">₹ <fmt:formatNumber value="${l.outstandingAmount}" pattern="#,##0.00"/></strong></div>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- EMI CALCULATOR CARD -->
            <div class="card-custom p-4 bg-white shadow-sm">
                <h4 class="fw-bold text-navy mb-3"><i class="fas fa-calculator text-warning me-2"></i> EMI Calculator</h4>
                <div class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label fw-bold">Loan Amount (₹)</label>
                        <input type="number" id="emiAmount" class="form-control" value="200000" oninput="calculateEmi()">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-bold">Interest Rate (% p.a.)</label>
                        <input type="number" id="emiRate" class="form-control" value="10.5" step="0.1" oninput="calculateEmi()">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-bold">Tenure (Months)</label>
                        <input type="number" id="emiTenure" class="form-control" value="24" oninput="calculateEmi()">
                    </div>
                </div>
                <div class="p-3 bg-light rounded text-center mt-3">
                    <div class="row">
                        <div class="col-md-4">Monthly EMI: <strong id="emiResultEmi" class="text-navy fs-5">₹ 9,276.00</strong></div>
                        <div class="col-md-4">Total Interest: <strong id="emiResultInterest" class="text-danger">₹ 22,624.00</strong></div>
                        <div class="col-md-4">Total Payable: <strong id="emiResultTotal" class="text-success">₹ 2,22,624.00</strong></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- APPLY LOAN MODAL -->
    <div class="modal fade" id="applyLoanModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="card-navy-header">Apply for a Loan</div>
                <form action="${pageContext.request.contextPath}/loans" method="post" class="p-4">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Loan Type *</label>
                        <select name="loanType" class="form-select" required>
                            <option value="Personal Loan">Personal Loan (10.5% p.a.)</option>
                            <option value="Home Loan">Home Loan (8.5% p.a.)</option>
                            <option value="Car Loan">Car Loan (9.25% p.a.)</option>
                            <option value="Education Loan">Education Loan (7.5% p.a.)</option>
                            <option value="Business Loan">Business Loan (11.0% p.a.)</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Loan Amount (₹) *</label>
                        <input type="number" name="amount" class="form-control" min="10000" required placeholder="e.g. 200000">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Tenure (Months) *</label>
                        <input type="number" name="tenureMonths" class="form-control" min="6" max="360" value="24" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Monthly Income (₹) *</label>
                        <input type="number" name="monthlyIncome" class="form-control" required placeholder="e.g. 50000">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Employment Type *</label>
                        <select name="employmentType" class="form-select" required>
                            <option value="Salaried">Salaried Employee</option>
                            <option value="Self-Employed">Self-Employed / Business</option>
                            <option value="Professional">Doctor / Lawyer / CA</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Purpose *</label>
                        <input type="text" name="purpose" class="form-control" required placeholder="e.g. Home Renovation, Education">
                    </div>
                    <div class="d-flex justify-content-end gap-2">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-gold">Submit Loan Application</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
