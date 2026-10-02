<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Loan Applications | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-hand-holding-dollar text-primary me-2"></i> Loan Approval & Management</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="sk-card p-4">
            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>Loan Number</th>
                            <th>Customer</th>
                            <th>Type</th>
                            <th>Amount</th>
                            <th>Tenure</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${loans}" var="l">
                            <tr>
                                <td class="font-monospace fw-bold">${l.loanNumber}</td>
                                <td><strong>${l.customerName}</strong></td>
                                <td>${l.loanTypeName}</td>
                                <td class="fw-bold">₹<fmt:formatNumber value="${l.principalAmount}" pattern="#,##0.00"/></td>
                                <td>${l.tenureMonths} mos</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${l.status == 'APPROVED'}"><span class="badge bg-success">APPROVED</span></c:when>
                                        <c:when test="${l.status == 'PENDING'}"><span class="badge bg-warning text-dark">PENDING</span></c:when>
                                        <c:when test="${l.status == 'REJECTED'}"><span class="badge bg-danger">REJECTED</span></c:when>
                                        <c:otherwise><span class="badge bg-secondary">${l.status}</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/admin/loan-details?id=${l.loanId}" class="btn btn-outline-primary">View</a>
                                        <c:if test="${l.status == 'PENDING'}">
                                            <a href="${pageContext.request.contextPath}/admin/loans?action=approve&id=${l.loanId}" class="btn btn-outline-success" onclick="return confirm('Approve this loan application?');">Approve</a>
                                            <a href="${pageContext.request.contextPath}/admin/loans?action=reject&id=${l.loanId}" class="btn btn-outline-danger" onclick="return confirm('Reject this loan application?');">Reject</a>
                                        </c:if>
                                    </div>
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
