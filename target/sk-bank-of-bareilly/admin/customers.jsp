<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Customer Directory & Control - Admin SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
            <jsp:param name="active" value="customers" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-users text-warning me-2"></i> Customer Directory & Account Control</h3>
                <form action="${pageContext.request.contextPath}/admin/customers" method="get" class="d-flex gap-2">
                    <input type="text" name="search" class="form-control" placeholder="Search by ID, Name, Mobile..." value="${searchQuery}">
                    <button type="submit" class="btn btn-gold"><i class="fas fa-search"></i> Search</button>
                </form>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card-custom bg-white p-3">
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Customer ID</th>
                                <th>Full Name</th>
                                <th>Mobile & Email</th>
                                <th>PAN & Aadhaar</th>
                                <th>KYC Status</th>
                                <th>Registered Date</th>
                                <th>Control Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${customers}">
                                <tr>
                                    <td class="fw-bold text-navy">
                                        <a href="${pageContext.request.contextPath}/admin/customers?id=${c.customerId}" class="text-navy text-decoration-none fw-bold">
                                            <i class="fas fa-user-circle me-1"></i> ${c.customerId}
                                        </a>
                                    </td>
                                    <td class="fw-bold">
                                        <a href="${pageContext.request.contextPath}/admin/customers?id=${c.customerId}" class="text-dark text-decoration-none">
                                            ${c.fullName}
                                        </a>
                                    </td>
                                    <td class="small">
                                        <div><i class="fas fa-phone text-warning me-1"></i> ${c.mobile}</div>
                                        <div class="text-muted"><i class="fas fa-envelope text-warning me-1"></i> ${c.email}</div>
                                    </td>
                                    <td class="small">
                                        <div>PAN: <strong>${c.pan}</strong></div>
                                        <div class="text-muted">Aadhaar: ${c.aadhaar}</div>
                                    </td>
                                    <td><span class="badge ${c.kycStatus == 'VERIFIED' ? 'bg-success' : 'bg-warning'}">${c.kycStatus}</span></td>
                                    <td class="small text-muted">${c.createdAt}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/customers?id=${c.customerId}" class="btn btn-gold btn-sm fw-bold">
                                            <i class="fas fa-user-cog me-1"></i> Manage Account
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
