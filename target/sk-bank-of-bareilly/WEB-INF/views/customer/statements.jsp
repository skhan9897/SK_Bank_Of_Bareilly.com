<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Account Statements | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-file-invoice text-primary me-2"></i> Generate Account Statement</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="sk-card p-4 mb-4 d-print-none">
            <form action="${pageContext.request.contextPath}/customer/statements" method="get" class="row g-3 align-items-end">
                <div class="col-md-4">
                    <label class="form-label fw-bold small">Select Account *</label>
                    <select name="accountId" class="form-select" required>
                        <c:forEach items="${accounts}" var="a">
                            <option value="${a.accountId}">${a.maskedAccountNumber} - ${a.accountTypeName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-bold small">From Date *</label>
                    <input type="date" name="startDate" class="form-control" required value="${startDate}">
                </div>
                <div class="col-md-3">
                    <label class="form-label fw-bold small">To Date *</label>
                    <input type="date" name="endDate" class="form-control" required value="${endDate}">
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-gold w-100 fw-bold"><i class="fa-solid fa-gears me-1"></i> Generate</button>
                </div>
            </form>
        </div>

        <c:if test="${not empty selectedAccount}">
            <div class="sk-card p-4">
                <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-3">
                    <div>
                        <h4 class="fw-bold text-navy mb-1">SK BANK OF BAREILLY</h4>
                        <p class="text-muted small mb-0">Official Account Statement (${startDate} to ${endDate})</p>
                    </div>
                    <div class="d-print-none">
                        <button onclick="window.print()" class="btn btn-outline-primary"><i class="fa-solid fa-print me-1"></i> Print / Download</button>
                    </div>
                </div>

                <div class="row bg-light p-3 rounded mb-4">
                    <div class="col-md-3">
                        <small class="text-muted d-block">Account Holder</small>
                        <strong>${sessionScope.CUSTOMER_NAME}</strong>
                    </div>
                    <div class="col-md-3">
                        <small class="text-muted d-block">Account Number</small>
                        <strong class="font-monospace">${selectedAccount.maskedAccountNumber}</strong>
                    </div>
                    <div class="col-md-3">
                        <small class="text-muted d-block">Total Credits</small>
                        <strong class="text-success">₹<fmt:formatNumber value="${totalCredits}" pattern="#,##0.00"/></strong>
                    </div>
                    <div class="col-md-3">
                        <small class="text-muted d-block">Total Debits</small>
                        <strong class="text-danger">₹<fmt:formatNumber value="${totalDebits}" pattern="#,##0.00"/></strong>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="table table-sk">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Ref Number</th>
                                <th>Description</th>
                                <th>Type</th>
                                <th>Amount</th>
                                <th>Balance After</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${statementTxns}" var="st">
                                <tr>
                                    <td class="small"><fmt:formatDate value="${st.createdAt}" pattern="dd/MM/yyyy"/></td>
                                    <td class="font-monospace small">${st.transactionReference}</td>
                                    <td>${st.description}</td>
                                    <td><span class="badge bg-light text-dark">${st.transactionType}</span></td>
                                    <td class="fw-bold ${st.transactionType == 'DEPOSIT' || st.transactionType == 'INTEREST_CREDIT' || st.transactionType == 'REFUND' ? 'text-success' : 'text-danger'}">
                                        ${st.transactionType == 'DEPOSIT' || st.transactionType == 'INTEREST_CREDIT' || st.transactionType == 'REFUND' ? '+' : '-'}₹<fmt:formatNumber value="${st.amount}" pattern="#,##0.00"/>
                                    </td>
                                    <td>₹<fmt:formatNumber value="${st.balanceAfter}" pattern="#,##0.00"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:if>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
