<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Customer Management - Admin SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
            <jsp:param name="active" value="customers" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-users text-warning me-2"></i> Customer Directory</h3>
                <form action="${pageContext.request.contextPath}/admin/customers" method="get" class="d-flex gap-2">
                    <input type="text" name="search" class="form-control" placeholder="Search by ID, Name, Mobile..." value="${searchQuery}">
                    <button type="submit" class="btn btn-gold"><i class="fas fa-search"></i></button>
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
                                <th>KYC</th>
                                <th>Registered Date</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${customers}">
                                <tr>
                                    <td class="fw-bold text-navy">${c.customerId}</td>
                                    <td class="fw-bold">${c.fullName}</td>
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
                                        <form action="${pageContext.request.contextPath}/admin/customers" method="post" class="d-inline">
                                            <input type="hidden" name="userId" value="${c.userId}">
                                            <input type="hidden" name="action" value="block">
                                            <button type="submit" class="btn btn-outline-danger btn-sm" onclick="return confirm('Block this customer?')">Block</button>
                                        </form>
                                        <form action="${pageContext.request.contextPath}/admin/customers" method="post" class="d-inline">
                                            <input type="hidden" name="userId" value="${c.userId}">
                                            <input type="hidden" name="action" value="unblock">
                                            <button type="submit" class="btn btn-outline-success btn-sm">Unblock</button>
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
