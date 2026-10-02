<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Fixed Deposits | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-piggy-bank text-primary me-2"></i> Fixed Deposits (FD)</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row g-4">
            <div class="col-lg-5">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3">Open New Fixed Deposit</h5>
                    <form action="${pageContext.request.contextPath}/customer/fixed-deposits" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Debit From Account *</label>
                            <select name="accountId" class="form-select" required>
                                <c:forEach items="${accounts}" var="a">
                                    <option value="${a.accountId}">${a.maskedAccountNumber} - Avail: ₹${a.availableBalance}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Principal Deposit Amount (₹) *</label>
                            <input type="number" step="0.01" min="1000" name="principalAmount" class="form-control" required placeholder="Min ₹1,000">
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold small">Tenure (Months) *</label>
                            <select name="tenureMonths" class="form-select" required>
                                <option value="6">6 Months (6.50% p.a.)</option>
                                <option value="12">12 Months (7.25% p.a.)</option>
                                <option value="24">24 Months (7.25% p.a.)</option>
                                <option value="36">36 Months (7.75% p.a.)</option>
                                <option value="60">60 Months (7.75% p.a.)</option>
                            </select>
                        </div>

                        <button type="submit" class="btn btn-gold w-100 fw-bold py-2"><i class="fa-solid fa-lock me-1"></i> Open FD Instantly</button>
                    </form>
                </div>
            </div>

            <div class="col-lg-7">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3">My Fixed Deposits</h5>
                    <div class="table-responsive">
                        <table class="table table-sk align-middle">
                            <thead>
                                <tr>
                                    <th>FD Number</th>
                                    <th>Principal</th>
                                    <th>Interest</th>
                                    <th>Maturity Amount</th>
                                    <th>Maturity Date</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${fds}" var="f">
                                    <tr>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/customer/fd-details?id=${f.fdId}" class="font-monospace fw-bold text-decoration-none">
                                                ${f.fdNumber}
                                            </a>
                                        </td>
                                        <td class="fw-bold">₹<fmt:formatNumber value="${f.principalAmount}" pattern="#,##0.00"/></td>
                                        <td>${f.interestRate}%</td>
                                        <td class="fw-bold text-success">₹<fmt:formatNumber value="${f.maturityAmount}" pattern="#,##0.00"/></td>
                                        <td class="small"><fmt:formatDate value="${f.maturityDate}" pattern="dd MMM yyyy"/></td>
                                        <td><span class="badge bg-success">${f.status}</span></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
