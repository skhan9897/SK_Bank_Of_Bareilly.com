<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Account Management - Admin SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
            <jsp:param name="active" value="accounts" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-wallet text-warning me-2"></i> All Bank Accounts</h3>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card-custom bg-white p-3">
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Account Number</th>
                                <th>Customer</th>
                                <th>Account Type</th>
                                <th>IFSC Code</th>
                                <th>Balance (₹)</th>
                                <th>Status</th>
                                <th>Created At</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="a" items="${accounts}">
                                <tr>
                                    <td class="fw-bold text-navy">${a.accountNumber}</td>
                                    <td class="fw-semibold">${a.customerName} <small class="text-muted">(${a.customerId})</small></td>
                                    <td><span class="badge badge-navy">${a.accountTypeName}</span></td>
                                    <td class="small">${a.ifscCode}</td>
                                    <td class="fw-bold text-success">₹ <fmt:formatNumber value="${a.balance}" pattern="#,##0.00"/></td>
                                    <td><span class="badge ${a.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${a.status}</span></td>
                                    <td class="small text-muted">${a.createdAt}</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/admin/accounts" method="post" class="d-inline">
                                            <input type="hidden" name="accountId" value="${a.accountId}">
                                            <c:choose>
                                                <c:when test="${a.status == 'ACTIVE'}">
                                                    <input type="hidden" name="action" value="block">
                                                    <button type="submit" class="btn btn-outline-danger btn-sm">Block</button>
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="hidden" name="action" value="activate">
                                                    <button type="submit" class="btn btn-outline-success btn-sm">Activate</button>
                                                </c:otherwise>
                                            </c:choose>
                                        </form>
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
