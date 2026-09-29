<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Complaints Management - Admin SK Bank" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
            <jsp:param name="active" value="complaints" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-headset text-warning me-2"></i> Customer Support Tickets</h3>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card-custom bg-white p-3">
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Ticket ID</th>
                                <th>Customer Name</th>
                                <th>Subject</th>
                                <th>Category</th>
                                <th>Description</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${complaints}">
                                <tr>
                                    <td class="fw-bold text-navy">${c.complaintId}</td>
                                    <td class="fw-bold">${c.customerName} <small class="text-muted">(${c.customerId})</small></td>
                                    <td class="fw-semibold">${c.subject}</td>
                                    <td><span class="badge badge-navy">${c.category}</span></td>
                                    <td class="small text-muted">${c.description}</td>
                                    <td><span class="badge ${c.status == 'OPEN' ? 'bg-warning' : (c.status == 'RESOLVED' ? 'bg-success' : 'bg-secondary')}">${c.status}</span></td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/admin/complaints" method="post" class="d-inline">
                                            <input type="hidden" name="complaintId" value="${c.complaintId}">
                                            <select name="status" class="form-select form-select-sm d-inline w-auto" onchange="this.form.submit()">
                                                <option value="OPEN" ${c.status == 'OPEN' ? 'selected' : ''}>OPEN</option>
                                                <option value="IN_PROGRESS" ${c.status == 'IN_PROGRESS' ? 'selected' : ''}>IN_PROGRESS</option>
                                                <option value="RESOLVED" ${c.status == 'RESOLVED' ? 'selected' : ''}>RESOLVED</option>
                                                <option value="CLOSED" ${c.status == 'CLOSED' ? 'selected' : ''}>CLOSED</option>
                                            </select>
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
