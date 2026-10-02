<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Bill Payments | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-receipt text-primary me-2"></i> Utility Bill Payments</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row g-4">
            <div class="col-lg-5">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3">Pay Bill</h5>
                    <form action="${pageContext.request.contextPath}/customer/bill-payments" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Pay From Account *</label>
                            <select name="accountId" class="form-select" required>
                                <c:forEach items="${accounts}" var="a">
                                    <option value="${a.accountId}">${a.maskedAccountNumber} - Avail: ₹${a.availableBalance}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Biller Type *</label>
                            <select name="billerType" class="form-select" required>
                                <option value="ELECTRICITY">Electricity Bill</option>
                                <option value="WATER">Water Bill</option>
                                <option value="GAS">Piped Gas / Cylinder</option>
                                <option value="MOBILE">Mobile Postpaid / Recharge</option>
                                <option value="DTH">DTH Recharge</option>
                                <option value="INTERNET">Broadband / Internet</option>
                                <option value="INSURANCE">Insurance Premium</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Biller Name *</label>
                            <input type="text" name="billerName" class="form-control" required placeholder="e.g. UPVCL, Airtel, Indane Gas">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Consumer / Connection Number *</label>
                            <input type="text" name="consumerNumber" class="form-control" required placeholder="Consumer ID or Account No">
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold small">Bill Amount (₹) *</label>
                            <input type="number" step="0.01" min="1" name="amount" class="form-control" required placeholder="0.00">
                        </div>

                        <button type="submit" class="btn btn-gold w-100 fw-bold py-2"><i class="fa-solid fa-bolt me-1"></i> Pay Bill Now</button>
                    </form>
                </div>
            </div>

            <div class="col-lg-7">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3">Bill Payment History</h5>
                    <div class="table-responsive">
                        <table class="table table-sk align-middle">
                            <thead>
                                <tr>
                                    <th>Biller</th>
                                    <th>Consumer No</th>
                                    <th>Amount</th>
                                    <th>Date</th>
                                    <th>Ref</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${history}" var="bp">
                                    <tr>
                                        <td>
                                            <div class="fw-bold">${bp.billerName}</div>
                                            <small class="text-muted">${bp.billerType}</small>
                                        </td>
                                        <td class="font-monospace">${bp.consumerNumber}</td>
                                        <td class="fw-bold text-primary">₹<fmt:formatNumber value="${bp.amount}" pattern="#,##0.00"/></td>
                                        <td class="small"><fmt:formatDate value="${bp.createdAt}" pattern="dd MMM yyyy"/></td>
                                        <td class="font-monospace small">${bp.paymentReference}</td>
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
