<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Send Money | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-paper-plane text-primary me-2"></i> Send Money / Fund Transfer</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row g-4">
            <div class="col-lg-7">
                <div class="sk-card p-4">
                    <form action="${pageContext.request.contextPath}/customer/transfer-confirm" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                        <input type="hidden" name="receiverAccountId" id="receiverAccountIdInput">
                        <input type="hidden" name="recipientName" id="recipientNameInput">

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Select From Account *</label>
                            <select name="senderAccountId" class="form-select form-select-lg" required>
                                <c:forEach items="${accounts}" var="acc">
                                    <option value="${acc.accountId}">${acc.maskedAccountNumber} - ${acc.accountTypeName} (Avail: ₹${acc.availableBalance})</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Transfer Mode *</label>
                            <select name="transferType" id="transferTypeSelect" class="form-select" required>
                                <option value="ACCOUNT">Account Number Transfer</option>
                                <option value="MOBILE">Mobile Number Transfer</option>
                                <option value="UPI">UPI ID Transfer</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Enter Recipient Detail (Account / Mobile / UPI) *</label>
                            <input type="text" id="recipientInput" class="form-control form-control-lg" required placeholder="Type account number, mobile, or UPI handle">
                            <div id="lookupResultBox" class="mt-2 p-3 rounded" style="display:none;"></div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Amount (₹) *</label>
                            <input type="number" step="0.01" min="1" max="500000" name="amount" class="form-control form-control-lg" required placeholder="0.00">
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold small">Remarks / Purpose (Optional)</label>
                            <input type="text" name="remarks" class="form-control" placeholder="e.g. Rent, Gift, Personal">
                        </div>

                        <button type="submit" class="btn btn-gold w-100 py-3 fw-bold fs-5"><i class="fa-solid fa-arrow-right me-2"></i> Review & Proceed</button>
                    </form>
                </div>
            </div>

            <div class="col-lg-5">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-users text-warning me-2"></i> Saved Beneficiaries</h5>
                    <c:choose>
                        <c:when test="${not empty beneficiaries}">
                            <div class="list-group list-group-flush">
                                <c:forEach items="${beneficiaries}" var="b">
                                    <button type="button" class="list-group-item list-group-item-action px-0 py-2 border-bottom" onclick="document.getElementById('recipientInput').value='${b.accountNumber}'; document.getElementById('recipientInput').dispatchEvent(new Event('input'));">
                                        <div class="fw-bold">${b.beneficiaryName} (${b.nickname != null ? b.nickname : 'Saved'})</div>
                                        <small class="text-muted">A/C: ${b.maskedAccountNumber} | ${b.bankName}</small>
                                    </button>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted small">No saved beneficiaries yet. You can add beneficiaries from the Beneficiaries tab.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
