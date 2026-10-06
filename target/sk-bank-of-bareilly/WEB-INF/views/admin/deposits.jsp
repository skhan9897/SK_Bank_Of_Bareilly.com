<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Admin Cash Deposit | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-circle-plus text-success me-2"></i> Counter Cash Deposit</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row justify-content-center">
            <div class="col-md-7">
                <div class="sk-card p-4 shadow">
                    <form action="${pageContext.request.contextPath}/admin/deposits" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                        <!-- Dropdown Select Customer Account -->
                        <div class="mb-3">
                            <label class="form-label fw-bold small">Select Customer Account (From List)</label>
                            <select class="form-select form-select-lg mb-2" onchange="if(this.value){ document.getElementById('accountIdentifierInput').value = this.value; }">
                                <option value="">-- Select Customer Account from List --</option>
                                <c:forEach items="${accounts}" var="acc">
                                    <option value="${acc.accountNumber}">
                                        ${empty acc.customerName ? 'Customer' : acc.customerName} | Acc: ${acc.accountNumber} (Bal: ₹<fmt:formatNumber value="${acc.availableBalance}" pattern="#,##0.00"/>)
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Direct Search Input -->
                        <div class="mb-3">
                            <label class="form-label fw-bold small">OR Enter Account No / Mobile No / Customer No *</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="fa-solid fa-magnifying-glass text-muted"></i></span>
                                <input type="text" name="accountIdentifier" id="accountIdentifierInput" class="form-control form-control-lg font-monospace fw-bold" required placeholder="e.g. SK1029384756 or Mobile 9876543210">
                            </div>
                            <small class="text-muted">Enter Account Number, Customer Mobile Number, or Customer Number.</small>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Deposit Amount (₹) *</label>
                            <input type="number" step="0.01" min="1" name="amount" class="form-control form-control-lg fw-bold text-success" required placeholder="0.00">
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold small">Remarks / Depositor Name</label>
                            <input type="text" name="remarks" class="form-control" placeholder="Counter Cash Deposit">
                        </div>

                        <button type="submit" class="btn btn-gold w-100 py-3 fw-bold fs-5"><i class="fa-solid fa-circle-check me-2"></i> Authorize & Credit Cash Deposit</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
