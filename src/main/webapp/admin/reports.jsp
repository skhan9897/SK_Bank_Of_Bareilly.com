<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Reports & Analytics - Admin SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
            <jsp:param name="active" value="reports" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-file-alt text-warning me-2"></i> Bank Reports & Analytics</h3>
                <button onclick="window.print()" class="btn btn-navy"><i class="fas fa-print me-1"></i> Print Master Summary</button>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row g-4">
                <div class="col-md-6">
                    <div class="card-custom p-4 bg-white h-100">
                        <h5 class="fw-bold text-navy mb-3"><i class="fas fa-chart-pie text-warning me-2"></i> Financial Capital Overview</h5>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                Total Active Bank Accounts
                                <span class="badge badge-navy fs-6">${totalAccounts}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                Total Customer Deposits Balance
                                <span class="fw-bold text-success fs-6">₹ <fmt:formatNumber value="${totalDeposits}" pattern="#,##0.00"/></span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                Total Fixed Deposits Booked Volume
                                <span class="fw-bold text-primary fs-6">₹ <fmt:formatNumber value="${totalFDVolume}" pattern="#,##0.00"/></span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                Total Active Loans Outstanding
                                <span class="fw-bold text-danger fs-6">₹ <fmt:formatNumber value="${totalLoansOutstanding}" pattern="#,##0.00"/></span>
                            </li>
                        </ul>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="card-custom p-4 bg-white h-100">
                        <h5 class="fw-bold text-navy mb-3"><i class="fas fa-layer-group text-warning me-2"></i> System Health & Metrics</h5>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                Total Customer Base
                                <span class="badge badge-gold fs-6">${totalCustomers}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                Today's Total Transaction Volume
                                <span class="fw-bold text-navy fs-6">₹ <fmt:formatNumber value="${todayVolume}" pattern="#,##0.00"/></span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                Security & Audit Logging
                                <span class="badge bg-success fs-6">ACTIVE</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center py-3">
                                Database Transaction Integrity
                                <span class="badge bg-success fs-6">ACID Compliant</span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

</body>
</html>
