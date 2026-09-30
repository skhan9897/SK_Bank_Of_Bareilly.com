<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="KYC Verification Directory - Admin SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
            <jsp:param name="active" value="kyc" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-id-card text-warning me-2"></i> Number-Based KYC Directory</h3>
                <span class="badge badge-gold px-3 py-2 fs-6">Aadhaar (12 Digits) + PAN (10 Chars)</span>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card-custom bg-white p-3">
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Customer ID</th>
                                <th>Aadhaar Number (Masked)</th>
                                <th>PAN Number (Masked)</th>
                                <th>Verification Ref</th>
                                <th>KYC Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty allKyc}">
                                    <c:forEach var="k" items="${allKyc}">
                                        <tr>
                                            <td class="fw-bold text-navy">
                                                <a href="${pageContext.request.contextPath}/admin/customers?id=${k.customerId}" class="text-navy text-decoration-none">
                                                    <i class="fas fa-user-circle me-1"></i> ${k.customerId}
                                                </a>
                                            </td>
                                            <td class="fw-bold small">${k.aadhaarMasked}</td>
                                            <td class="fw-bold small text-primary">${k.panMasked}</td>
                                            <td class="small text-muted">${k.verificationReference}</td>
                                            <td>
                                                <span class="badge ${k.kycStatus == 'VERIFIED' ? 'bg-success' : (k.kycStatus == 'REJECTED' ? 'bg-danger' : 'bg-warning')}">
                                                    ${k.kycStatus}
                                                </span>
                                            </td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/admin/kyc" method="post" class="d-inline">
                                                    <input type="hidden" name="customerId" value="${k.customerId}">
                                                    <input type="hidden" name="action" value="approve">
                                                    <button type="submit" class="btn btn-success btn-sm"><i class="fas fa-check"></i> Approve</button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/admin/kyc" method="post" class="d-inline">
                                                    <input type="hidden" name="customerId" value="${k.customerId}">
                                                    <input type="hidden" name="action" value="reject">
                                                    <input type="hidden" name="reason" value="Identity details mismatch">
                                                    <button type="submit" class="btn btn-outline-danger btn-sm"><i class="fas fa-times"></i> Reject</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="6" class="text-center py-4 text-muted">No KYC records submitted yet.</td>
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
