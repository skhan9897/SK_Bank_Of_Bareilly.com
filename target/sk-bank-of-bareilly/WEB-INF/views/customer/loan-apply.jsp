<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Apply Loan | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/customer/loans" class="btn btn-outline-secondary btn-sm"><i class="fa-solid fa-arrow-left me-1"></i> Back to Loans</a>
        </div>

        <div class="row g-4">
            <div class="col-lg-6">
                <div class="sk-card p-4">
                    <h4 class="fw-bold text-navy mb-3">Apply for a New Loan</h4>

                    <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

                    <form action="${pageContext.request.contextPath}/customer/loan-apply" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Select Loan Category *</label>
                            <select name="loanTypeId" id="loanTypeSelect" class="form-select" required>
                                <c:forEach items="${loanTypes}" var="lt">
                                    <option value="${lt.loanTypeId}">${lt.loanName} (${lt.interestRate}% p.a.)</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Required Loan Amount (₹) *</label>
                            <input type="number" step="1000" min="10000" name="principalAmount" id="calcPrincipal" class="form-control" required placeholder="e.g. 100000">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Tenure (Months) *</label>
                            <input type="number" min="6" max="240" name="tenureMonths" id="calcTenure" class="form-control" required placeholder="e.g. 24">
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold small">Interest Rate (% p.a.)</label>
                            <input type="text" id="calcRate" class="form-control bg-light" readonly value="10.50">
                        </div>

                        <button type="submit" class="btn btn-gold w-100 py-3 fw-bold fs-5"><i class="fa-solid fa-paper-plane me-2"></i> Submit Application</button>
                    </form>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="sk-card p-4 bg-navy text-white" style="background-color: #071F49;">
                    <h5 class="fw-bold text-gold mb-3"><i class="fa-solid fa-calculator me-2"></i> Instant EMI Calculator</h5>
                    <p class="small opacity-90 mb-4">Calculate your estimated monthly installment instantly before applying.</p>

                    <div class="p-3 bg-white bg-opacity-10 rounded mb-3">
                        <small class="text-gold d-block fw-bold">Estimated Monthly EMI</small>
                        <h2 class="fw-bold mb-0 text-white" id="calcResultEmi">₹0.00</h2>
                    </div>

                    <div class="p-3 bg-white bg-opacity-10 rounded">
                        <small class="text-gold d-block fw-bold">Total Amount Payable</small>
                        <h4 class="fw-bold mb-0 text-white" id="calcResultTotal">₹0.00</h4>
                    </div>

                    <button type="button" class="btn btn-outline-gold w-100 mt-4 fw-bold" onclick="calculateEmiClient('calcPrincipal', 'calcRate', 'calcTenure', 'calcResultEmi', 'calcResultTotal')">
                        <i class="fa-solid fa-arrows-rotate me-1"></i> Recalculate EMI
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
