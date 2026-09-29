<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Admin Dashboard - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
            <jsp:param name="active" value="dashboard" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-chart-line text-warning me-2"></i> Bank Administration Control Center</h3>
                <p class="text-muted small">Real-time overview of customers, deposits, active loans, and system transactions.</p>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <!-- METRICS CARDS -->
            <div class="row g-4 mb-4">
                <div class="col-md-4 col-lg-2">
                    <div class="card-custom p-3 bg-white text-center h-100">
                        <div class="text-muted small fw-bold">TOTAL CUSTOMERS</div>
                        <div class="h3 fw-bold text-navy my-2">${totalCustomers}</div>
                    </div>
                </div>
                <div class="col-md-4 col-lg-2">
                    <div class="card-custom p-3 bg-white text-center h-100">
                        <div class="text-muted small fw-bold">BANK ACCOUNTS</div>
                        <div class="h3 fw-bold text-navy my-2">${totalAccounts}</div>
                    </div>
                </div>
                <div class="col-md-4 col-lg-3">
                    <div class="card-custom p-3 bg-white text-center h-100">
                        <div class="text-muted small fw-bold">TOTAL DEPOSITS (IN DEPOSIT)</div>
                        <div class="h4 fw-bold text-success my-2">₹ <fmt:formatNumber value="${totalDeposits}" pattern="#,##0.00"/></div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-3">
                    <div class="card-custom p-3 bg-white text-center h-100">
                        <div class="text-muted small fw-bold">ACTIVE LOANS OUTSTANDING</div>
                        <div class="h4 fw-bold text-danger my-2">₹ <fmt:formatNumber value="${activeLoans}" pattern="#,##0.00"/></div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-2">
                    <div class="card-custom p-3 bg-white text-center h-100">
                        <div class="text-muted small fw-bold">TODAY TXNS</div>
                        <div class="h3 fw-bold text-navy my-2">${todayTxnCount}</div>
                    </div>
                </div>
            </div>

            <!-- RECENT TRANSACTIONS TABLE -->
            <div class="card-custom bg-white p-3">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="fw-bold text-navy mb-0"><i class="fas fa-history text-warning me-2"></i> System Recent Transactions</h5>
                    <a href="${pageContext.request.contextPath}/admin/transactions" class="btn btn-navy btn-sm">View All</a>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Txn ID</th>
                                <th>Customer</th>
                                <th>Account</th>
                                <th>Type</th>
                                <th>Amount</th>
                                <th>Status</th>
                                <th>Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="t" items="${recentTransactions}">
                                <tr>
                                    <td class="fw-bold text-navy">${t.transactionId}</td>
                                    <td class="fw-semibold">${t.customerName}</td>
                                    <td class="small">${t.accountNumber}</td>
                                    <td><span class="badge badge-navy">${t.type}</span></td>
                                    <td class="fw-bold text-navy">₹ <fmt:formatNumber value="${t.amount}" pattern="#,##0.00"/></td>
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
