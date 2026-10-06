<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="System Audit Logs | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-shield-halved text-primary me-2"></i> Security & Operation Audit Logs</h3>

        <div class="sk-card p-4">
            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>User</th>
                            <th>Action</th>
                            <th>Module</th>
                            <th>Description</th>
                            <th>IP Address</th>
                            <th>Timestamp</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${auditLogs}" var="al">
                            <tr>
                                <td>#${al.auditId}</td>
                                <td><strong>${al.username != null ? al.username : 'SYSTEM'}</strong></td>
                                <td><span class="badge bg-navy text-gold font-monospace">${al.action}</span></td>
                                <td><span class="badge bg-light text-dark">${al.module}</span></td>
                                <td class="small">${al.description}</td>
                                <td class="font-monospace small">${al.ipAddress}</td>
                                <td class="small"><fmt:formatDate value="${al.createdAt}" pattern="dd MMM yyyy, hh:mm:ss a"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <c:if test="${totalPages > 1}">
                <nav class="mt-4">
                    <ul class="pagination justify-content-center">
                        <c:forEach begin="1" end="${totalPages}" var="p">
                            <li class="page-item ${p == currentPage ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/audit-logs?page=${p}">${p}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </nav>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
