<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Loan Details | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/customer/loans" class="btn btn-outline-secondary btn-sm"><i class="fa-solid fa-arrow-left me-1"></i> Back to Loans</a>
        </div>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row g-4 mb-4">
            <div class="col-lg-7">
                <div class="sk-card p-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <span class="badge bg-gold text-dark">${loan.loanTypeName}</span>
                        <span class="badge bg-success">${loan.status}</span>
                    </div>
                    <h3 class="font-monospace fw-bold text-navy mb-3">${loan.loanNumber}</h3>

                    <div class="row bg-light p-3 rounded g-3 mb-3">
                        <div class="col-6">
                            <small class="text-muted d-block">Principal Amount</small>
                            <strong class="fs-5">₹<fmt:formatNumber value="${loan.principalAmount}" pattern="#,##0.00"/></strong>
                        </div>
                        <div class="col-6">
                            <small class="text-muted d-block">Interest Rate</small>
                            <strong class="fs-5">${loan.interestRate}% p.a.</strong>
                        </div>
                        <div class="col-6">
                            <small class="text-muted d-block">Monthly EMI</small>
                            <strong class="fs-4 text-primary">₹<fmt:formatNumber value="${loan.emiAmount}" pattern="#,##0.00"/></strong>
                        </div>
                        <div class="col-6">
                            <small class="text-muted d-block">Outstanding Amount</small>
                            <strong class="fs-4 text-danger">₹<fmt:formatNumber value="${loan.outstandingAmount}" pattern="#,##0.00"/></strong>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-lg-5">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3">Pay Monthly EMI</h5>
                    <c:choose>
                        <c:when test="${loan.status == 'APPROVED' && loan.outstandingAmount > 0}">
                            <form action="${pageContext.request.contextPath}/customer/loan-emi" method="post">
                                <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                <input type="hidden" name="loanId" value="${loan.loanId}">

                                <div class="mb-3">
                                    <label class="form-label fw-bold small">Debit Account *</label>
                                    <select name="accountId" class="form-select" required>
                                        <c:forEach items="${accounts}" var="a">
                                            <option value="${a.accountId}">${a.maskedAccountNumber} (Avail: ₹${a.availableBalance})</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold small">EMI Amount Due</label>
                                    <input type="text" class="form-control fw-bold text-primary" readonly value="₹${loan.emiAmount}">
                                </div>

                                <button type="submit" class="btn btn-gold w-100 fw-bold py-2"><i class="fa-solid fa-credit-card me-1"></i> Pay EMI Now</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted">EMI payment is disabled for this loan state.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="sk-card p-4">
            <h5 class="fw-bold text-navy mb-3">EMI Payment History</h5>
            <div class="table-responsive">
                <table class="table table-sk">
                    <thead>
                        <tr>
                            <th>Payment Ref</th>
                            <th>Date</th>
                            <th>Amount Paid</th>
                            <th>Principal Component</th>
                            <th>Interest Component</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${payments}" var="p">
                            <tr>
                                <td class="font-monospace small fw-bold">${p.paymentReference}</td>
                                <td class="small"><fmt:formatDate value="${p.paymentDate}" pattern="dd MMM yyyy, hh:mm a"/></td>
                                <td class="fw-bold text-success">₹<fmt:formatNumber value="${p.amount}" pattern="#,##0.00"/></td>
                                <td>₹<fmt:formatNumber value="${p.principalComponent}" pattern="#,##0.00"/></td>
                                <td>₹<fmt:formatNumber value="${p.interestComponent}" pattern="#,##0.00"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
