<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="All Transactions | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-list-check text-primary me-2"></i> System Transactions Audit</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="sk-card p-3 mb-4">
            <form action="${pageContext.request.contextPath}/admin/transactions" method="get" class="row g-2 align-items-center">
                <div class="col-md-5">
                    <input type="text" name="search" class="form-control" value="${search}" placeholder="Search ref, account number, customer...">
                </div>
                <div class="col-md-3">
                    <select name="type" class="form-select">
                        <option value="">All Types</option>
                        <option value="DEPOSIT" ${typeFilter == 'DEPOSIT' ? 'selected' : ''}>DEPOSIT</option>
                        <option value="WITHDRAWAL" ${typeFilter == 'WITHDRAWAL' ? 'selected' : ''}>WITHDRAWAL</option>
                        <option value="TRANSFER" ${typeFilter == 'TRANSFER' ? 'selected' : ''}>TRANSFER</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <button type="submit" class="btn btn-primary w-100 fw-bold"><i class="fa-solid fa-filter me-1"></i> Filter</button>
                </div>
            </form>
        </div>

        <div class="sk-card p-4">
            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>Ref Number</th>
                            <th>Customer / Account</th>
                            <th>Type</th>
                            <th>Amount</th>
                            <th>Balance After</th>
                            <th>Date & Time</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${transactions}" var="t">
                            <tr>
                                <td class="font-monospace fw-bold">${t.transactionReference}</td>
                                <td>
                                    <div><strong>${t.customerName}</strong></div>
                                    <small class="font-monospace text-muted">${t.accountNumber}</small>
                                </td>
                                <td><span class="badge bg-light text-dark">${t.transactionType}</span></td>
                                <td class="fw-bold ${t.transactionType == 'DEPOSIT' ? 'text-success' : 'text-danger'}">
                                    ${t.transactionType == 'DEPOSIT' ? '+' : '-'}₹<fmt:formatNumber value="${t.amount}" pattern="#,##0.00"/>
                                </td>
                                <td>₹<fmt:formatNumber value="${t.balanceAfter}" pattern="#,##0.00"/></td>
                                <td class="small"><fmt:formatDate value="${t.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></td>
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
                                <a class="page-link" href="${pageContext.request.contextPath}/admin/transactions?search=${search}&type=${typeFilter}&page=${p}">${p}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </nav>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
