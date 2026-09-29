<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Account Statement - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="statements" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-file-invoice-dollar text-warning me-2"></i> Account Statements</h3>
                <div class="d-flex gap-2">
                    <button onclick="printStatement()" class="btn btn-navy"><i class="fas fa-print me-1"></i> Print Statement</button>
                    <button onclick="exportTableToCSV('sk_bank_statement.csv')" class="btn btn-gold"><i class="fas fa-file-csv me-1"></i> Export CSV</button>
                </div>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <!-- FILTER FORM -->
            <div class="card-custom p-4 bg-white mb-4">
                <form action="${pageContext.request.contextPath}/statements" method="get" class="row g-3 align-items-end">
                    <div class="col-md-5">
                        <label class="form-label fw-bold">Select Account</label>
                        <select name="accountId" class="form-select">
                            <c:forEach var="a" items="${accounts}">
                                <option value="${a.accountId}" ${selectedAccount != null and selectedAccount.accountId == a.accountId ? 'selected' : ''}>${a.accountTypeName} - ${a.accountNumber} (₹${a.balance})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-bold">Period Filter</label>
                        <select name="period" class="form-select">
                            <option value="Today" ${selectedPeriod == 'Today' ? 'selected' : ''}>Today</option>
                            <option value="7 Days" ${selectedPeriod == '7 Days' ? 'selected' : ''}>Last 7 Days</option>
                            <option value="30 Days" ${selectedPeriod == '30 Days' ? 'selected' : ''}>Last 30 Days</option>
                            <option value="3 Months" ${selectedPeriod == '3 Months' ? 'selected' : ''}>Last 3 Months</option>
                            <option value="6 Months" ${selectedPeriod == '6 Months' ? 'selected' : ''}>Last 6 Months</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <button type="submit" class="btn btn-navy w-100 py-2"><i class="fas fa-filter me-1"></i> Generate Statement</button>
                    </div>
                </form>
            </div>

            <!-- STATEMENT REPORT SHEET -->
            <c:if test="${not empty selectedAccount}">
                <div class="card-custom bg-white p-4 shadow-sm" id="printableStatement">
                    <div class="d-flex justify-content-between align-items-center mb-4 pb-3 border-bottom">
                        <div class="d-flex align-items-center gap-3">
                            <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="Logo" style="height: 50px;">
                            <div>
                                <h4 class="fw-bold text-navy mb-0">SK Bank of Bareilly</h4>
                                <small class="text-muted">Statement of Account for Period: ${selectedPeriod}</small>
                            </div>
                        </div>
                        <div class="text-end small">
                            <div>Account No: <strong>${selectedAccount.accountNumber}</strong></div>
                            <div>IFSC: <strong>${selectedAccount.ifscCode}</strong></div>
                            <div>Current Balance: <strong class="text-success">₹ <fmt:formatNumber value="${selectedAccount.balance}" pattern="#,##0.00"/></strong></div>
                        </div>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-bordered table-striped align-middle table-custom mb-0">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Txn ID</th>
                                    <th>Description</th>
                                    <th>Ref No</th>
                                    <th class="text-danger">Debit (₹)</th>
                                    <th class="text-success">Credit (₹)</th>
                                    <th>Balance (₹)</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty statementTransactions}">
                                        <c:forEach var="t" items="${statementTransactions}">
                                            <tr>
                                                <td class="small text-muted">${t.createdAt}</td>
                                                <td class="fw-bold small text-navy">${t.transactionId}</td>
                                                <td class="small">${t.description}</td>
                                                <td class="small text-muted">${t.referenceNumber}</td>
                                                <td class="fw-bold text-danger">
                                                    <c:if test="${t.type != 'DEPOSIT' and t.type != 'INTEREST_CREDIT'}">
                                                        <fmt:formatNumber value="${t.amount}" pattern="#,##0.00"/>
                                                    </c:if>
                                                </td>
                                                <td class="fw-bold text-success">
                                                    <c:if test="${t.type == 'DEPOSIT' or t.type == 'INTEREST_CREDIT'}">
                                                        <fmt:formatNumber value="${t.amount}" pattern="#,##0.00"/>
                                                    </c:if>
                                                </td>
                                                <td class="fw-bold small">₹ <fmt:formatNumber value="${t.balanceAfter}" pattern="#,##0.00"/></td>
                                                <td><span class="badge bg-success">${t.status}</span></td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="8" class="text-center py-4 text-muted">No transactions recorded for the selected period.</td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </c:if>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
