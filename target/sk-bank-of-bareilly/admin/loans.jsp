<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Loan Management - Admin SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
            <jsp:param name="active" value="loans" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-hand-holding-usd text-warning me-2"></i> Loan Applications & Approvals</h3>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card-custom bg-white p-3">
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Loan ID</th>
                                <th>Applicant Name</th>
                                <th>Loan Type</th>
                                <th>Amount</th>
                                <th>Rate & Tenure</th>
                                <th>Monthly EMI</th>
                                <th>Income & Employment</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="l" items="${loans}">
                                <tr>
                                    <td class="fw-bold text-navy">SKL-${l.loanId}</td>
                                    <td class="fw-bold">${l.customerName} <small class="text-muted">(${l.customerId})</small></td>
                                    <td><span class="badge badge-navy">${l.loanType}</span></td>
                                    <td class="fw-bold text-success">₹ <fmt:formatNumber value="${l.principalAmount}" pattern="#,##0.00"/></td>
                                    <td class="small">${l.interestRate}% for ${l.tenureMonths}m</td>
                                    <td class="fw-bold small">₹ <fmt:formatNumber value="${l.monthlyEmi}" pattern="#,##0.00"/></td>
                                    <td class="small">${l.employmentType} (₹<fmt:formatNumber value="${l.monthlyIncome}" pattern="#,##0"/>/m)</td>
                                    <td><span class="badge ${l.status == 'APPROVED' ? 'bg-success' : (l.status == 'REJECTED' ? 'bg-danger' : 'bg-warning')}">${l.status}</span></td>
                                    <td>
                                        <c:if test="${l.status == 'APPLIED' or l.status == 'UNDER_REVIEW'}">
                                            <form action="${pageContext.request.contextPath}/admin/loans" method="post" class="d-inline">
                                                <input type="hidden" name="loanId" value="${l.loanId}">
                                                <input type="hidden" name="action" value="approve">
                                                <button type="submit" class="btn btn-success btn-sm"><i class="fas fa-check"></i> Approve</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/admin/loans" method="post" class="d-inline">
                                                <input type="hidden" name="loanId" value="${l.loanId}">
                                                <input type="hidden" name="action" value="reject">
                                                <button type="submit" class="btn btn-outline-danger btn-sm"><i class="fas fa-times"></i> Reject</button>
                                            </form>
                                        </c:if>
                                    </td>
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
