<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Helpdesk Tickets | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-headset text-primary me-2"></i> All Customer Support Tickets</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="sk-card p-4">
            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>Ticket ID</th>
                            <th>Customer Name</th>
                            <th>Subject</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${complaints}" var="c">
                            <tr>
                                <td>#${c.complaintId}</td>
                                <td><strong>${c.customerName}</strong></td>
                                <td>${c.subject}</td>
                                <td><span class="badge bg-light text-dark">${c.priority}</span></td>
                                <td><span class="badge bg-${c.status == 'RESOLVED' ? 'success' : 'warning text-dark'}">${c.status}</span></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/complaints?id=${c.complaintId}" class="btn btn-sm btn-outline-primary">Manage & Reply</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
