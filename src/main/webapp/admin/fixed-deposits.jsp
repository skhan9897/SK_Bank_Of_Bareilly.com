<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Fixed Deposits - Admin SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
            <jsp:param name="active" value="fixed-deposits" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-piggy-bank text-warning me-2"></i> Fixed Deposits Directory</h3>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card-custom bg-white p-3">
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Receipt No</th>
                                <th>Customer Name</th>
                                <th>Source Account</th>
                                <th>Principal (₹)</th>
                                <th>Rate & Tenure</th>
                                <th>Maturity Amount (₹)</th>
                                <th>Maturity Date</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="fd" items="${fixedDeposits}">
                                <tr>
                                    <td class="fw-bold text-navy">${fd.receiptNumber}</td>
                                    <td class="fw-bold">${fd.customerName} <small class="text-muted">(${fd.customerId})</small></td>
                                    <td class="small">${fd.accountNumber}</td>
                                    <td class="fw-bold text-navy">₹ <fmt:formatNumber value="${fd.depositAmount}" pattern="#,##0.00"/></td>
                                    <td class="small">${fd.interestRate}% for ${fd.tenureMonths}m</td>
                                    <td class="fw-bold text-success">₹ <fmt:formatNumber value="${fd.maturityAmount}" pattern="#,##0.00"/></td>
                                    <td class="small text-muted">${fd.maturityDate}</td>
                                    <td><span class="badge bg-success">${fd.status}</span></td>
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
