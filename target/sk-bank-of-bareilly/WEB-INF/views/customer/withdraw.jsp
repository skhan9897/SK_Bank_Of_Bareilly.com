<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Withdraw Money | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-money-bill-transfer text-primary me-2"></i> Withdraw Funds</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="sk-card p-4">
                    <form action="${pageContext.request.contextPath}/customer/withdraw" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Select Account *</label>
                            <select name="accountId" class="form-select form-select-lg" required>
                                <c:forEach items="${accounts}" var="acc">
                                    <option value="${acc.accountId}">${acc.maskedAccountNumber} - Avail: ₹${acc.availableBalance}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Withdrawal Amount (₹) *</label>
                            <input type="number" step="0.01" min="1" max="50000" name="amount" class="form-control form-control-lg" required placeholder="0.00">
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold small">Description / Purpose</label>
                            <input type="text" name="description" class="form-control" placeholder="Internal Demo Withdrawal">
                        </div>

                        <button type="submit" class="btn btn-gold w-100 py-3 fw-bold fs-5"><i class="fa-solid fa-money-bill-wave me-2"></i> Authorize Withdrawal</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
