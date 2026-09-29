<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="System Transactions - Admin SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
            <jsp:param name="active" value="transactions" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-exchange-alt text-warning me-2"></i> All System Transactions Audit</h3>
                <button onclick="exportTableToCSV('admin_skbank_transactions.csv')" class="btn btn-gold"><i class="fas fa-file-csv me-1"></i> Export CSV</button>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card-custom bg-white p-3">
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Txn ID</th>
                                <th>Customer</th>
                                <th>Account</th>
                                <th>Type</th>
                                <th>Description</th>
                                <th>Ref No</th>
                                <th>Amount</th>
                                <th>Balance After</th>
                                <th>Status</th>
                                <th>Timestamp</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="t" items="${transactions}">
                                <tr>
                                    <td class="fw-bold text-navy">${t.transactionId}</td>
                                    <td class="fw-semibold">${t.customerName}</td>
                                    <td class="small">${t.accountNumber}</td>
                                    <td><span class="badge badge-navy">${t.type}</span></td>
                                    <td class="small">${t.description}</td>
                                    <td class="small text-muted">${t.referenceNumber}</td>
                                    <td class="fw-bold text-navy">₹ <fmt:formatNumber value="${t.amount}" pattern="#,##0.00"/></td>
                                    <td class="small">₹ <fmt:formatNumber value="${t.balanceAfter}" pattern="#,##0.00"/></td>
                                    <td><span class="badge bg-success">${t.status}</span></td>
                                    <td class="small text-muted">${t.createdAt}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

</body>
</html>
