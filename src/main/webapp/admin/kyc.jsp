<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="KYC Approvals - Admin SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
            <jsp:param name="active" value="kyc" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-id-card text-warning me-2"></i> Pending KYC Approvals</h3>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card-custom bg-white p-3">
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Customer Name</th>
                                <th>Customer ID</th>
                                <th>Document Type</th>
                                <th>Uploaded At</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty pendingKyc}">
                                    <c:forEach var="k" items="${pendingKyc}">
                                        <tr>
                                            <td class="fw-bold text-navy">${k.customerName}</td>
                                            <td class="fw-semibold">${k.customerId}</td>
                                            <td><span class="badge badge-navy">${k.documentType}</span></td>
                                            <td class="small text-muted">${k.uploadedAt}</td>
                                            <td><span class="badge bg-warning">${k.status}</span></td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/admin/kyc" method="post" class="d-inline">
                                                    <input type="hidden" name="kycId" value="${k.kycId}">
                                                    <input type="hidden" name="customerId" value="${k.customerId}">
                                                    <input type="hidden" name="action" value="approve">
                                                    <button type="submit" class="btn btn-success btn-sm"><i class="fas fa-check"></i> Approve KYC</button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/admin/kyc" method="post" class="d-inline">
                                                    <input type="hidden" name="kycId" value="${k.kycId}">
                                                    <input type="hidden" name="customerId" value="${k.customerId}">
                                                    <input type="hidden" name="action" value="reject">
                                                    <input type="hidden" name="reason" value="Document unclear or invalid">
                                                    <button type="submit" class="btn btn-outline-danger btn-sm"><i class="fas fa-times"></i> Reject</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="6" class="text-center py-4 text-muted">No pending KYC approvals. All customer accounts are up to date!</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

</body>
</html>
