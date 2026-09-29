<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Transaction History - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="transactions" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-exchange-alt text-warning me-2"></i> All Account Transactions</h3>
                <button onclick="exportTableToCSV('sk_bank_transactions.csv')" class="btn btn-outline-gold"><i class="fas fa-file-csv me-1"></i> Export CSV</button>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card-custom bg-white p-3">
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Txn ID</th>
                                <th>Account No</th>
                                <th>Date & Time</th>
                                <th>Type</th>
                                <th>Description</th>
                                <th>Ref No</th>
                                <th>Amount</th>
                                <th>Balance After</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty transactions}">
                                    <c:forEach var="txn" items="${transactions}">
                                        <tr>
                                            <td class="fw-bold text-navy">${txn.transactionId}</td>
                                            <td class="small fw-semibold">${txn.accountNumber}</td>
                                            <td class="small text-muted">${txn.createdAt}</td>
                                            <td>
                                                <span class="badge ${txn.type == 'DEPOSIT' or txn.type == 'INTEREST_CREDIT' ? 'bg-success' : 'bg-primary'}">
                                                    ${txn.type}
                                                </span>
                                            </td>
                                            <td class="small">${txn.description}</td>
                                            <td class="small text-muted">${txn.referenceNumber}</td>
                                            <td class="fw-bold ${txn.type == 'DEPOSIT' or txn.type == 'INTEREST_CREDIT' ? 'text-success' : 'text-danger'}">
                                                ${txn.type == 'DEPOSIT' or txn.type == 'INTEREST_CREDIT' ? '+' : '-'} ₹ <fmt:formatNumber value="${txn.amount}" pattern="#,##0.00"/>
                                            </td>
                                            <td class="fw-bold text-navy small">₹ <fmt:formatNumber value="${txn.balanceAfter}" pattern="#,##0.00"/></td>
                                            <td><span class="badge bg-success">${txn.status}</span></td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="9" class="text-center py-4 text-muted">No transactions found.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
