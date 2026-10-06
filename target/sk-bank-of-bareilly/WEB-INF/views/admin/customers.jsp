<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Customer Management | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-users text-primary me-2"></i> Customer Management</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="sk-card p-3 mb-4">
            <form action="${pageContext.request.contextPath}/admin/customers" method="get" class="row g-2 align-items-center">
                <div class="col-md-9">
                    <input type="text" name="search" class="form-control" value="${search}" placeholder="Search customer by name, customer number, mobile, email...">
                </div>
                <div class="col-md-3">
                    <button type="submit" class="btn btn-primary w-100 fw-bold"><i class="fa-solid fa-magnifying-glass me-1"></i> Search</button>
                </div>
            </form>
        </div>

        <div class="sk-card p-4">
            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>Cust ID</th>
                            <th>Name</th>
                            <th>Mobile</th>
                            <th>Email</th>
                            <th>KYC</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${customers}" var="c">
                            <tr>
                                <td class="font-monospace fw-bold">${c.customerNumber}</td>
                                <td><strong>${c.fullName}</strong></td>
                                <td>${c.mobile}</td>
                                <td class="small">${c.email}</td>
                                <td><span class="badge bg-success">${c.kycStatus}</span></td>
                                <td><span class="badge bg-${c.status == 'ACTIVE' ? 'success' : 'danger'}">${c.status}</span></td>
                                <td>
                                    <div class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/admin/customer-details?id=${c.customerId}" class="btn btn-outline-primary">Details</a>
                                        <c:choose>
                                            <c:when test="${c.status == 'ACTIVE'}">
                                                <a href="${pageContext.request.contextPath}/admin/customers?action=status&id=${c.customerId}&status=BLOCKED" class="btn btn-outline-warning" onclick="return confirm('Block this customer?');">Block</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/admin/customers?action=status&id=${c.customerId}&status=ACTIVE" class="btn btn-outline-success">Activate</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </td>
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
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/customers?search=${search}&page=${p}">${p}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </nav>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
