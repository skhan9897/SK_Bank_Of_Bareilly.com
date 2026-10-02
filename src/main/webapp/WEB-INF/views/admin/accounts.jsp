<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Account Management | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-wallet text-primary me-2"></i> Account Management</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="sk-card p-3 mb-4">
            <form action="${pageContext.request.contextPath}/admin/accounts" method="get" class="row g-2 align-items-center">
                <div class="col-md-9">
                    <input type="text" name="search" class="form-control" value="${search}" placeholder="Search by account number or customer name...">
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
                            <th>Account No</th>
                            <th>Customer Name</th>
                            <th>Type</th>
                            <th>Balance</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${accounts}" var="a">
                            <tr>
                                <td class="font-monospace fw-bold">${a.maskedAccountNumber}</td>
                                <td><strong>${a.customerName}</strong></td>
                                <td>${a.accountTypeName}</td>
                                <td class="fw-bold text-success">₹<fmt:formatNumber value="${a.balance}" pattern="#,##0.00"/></td>
                                <td><span class="badge bg-${a.status == 'ACTIVE' ? 'success' : 'danger'}">${a.status}</span></td>
                                <td>
                                    <div class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/admin/account-details?id=${a.accountId}" class="btn btn-outline-primary">Details</a>
                                        <c:choose>
                                            <c:when test="${a.status == 'ACTIVE'}">
                                                <a href="${pageContext.request.contextPath}/admin/accounts?action=status&id=${a.accountId}&status=BLOCKED" class="btn btn-outline-warning" onclick="return confirm('Block this account?');">Block</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/admin/accounts?action=status&id=${a.accountId}&status=ACTIVE" class="btn btn-outline-success">Activate</a>
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
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/accounts?search=${search}&page=${p}">${p}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </nav>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
