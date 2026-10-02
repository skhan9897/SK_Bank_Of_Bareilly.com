<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="My Loans | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h3 class="fw-bold text-navy mb-0"><i class="fa-solid fa-hand-holding-dollar text-primary me-2"></i> My Loans</h3>
            <a href="${pageContext.request.contextPath}/customer/loan-apply" class="btn btn-gold font-weight-bold"><i class="fa-solid fa-plus me-1"></i> Apply for Loan</a>
        </div>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="sk-card p-4">
            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>Loan Number</th>
                            <th>Type</th>
                            <th>Principal</th>
                            <th>Monthly EMI</th>
                            <th>Outstanding</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${loans}" var="l">
                            <tr>
                                <td><span class="font-monospace fw-bold text-navy">${l.loanNumber}</span></td>
                                <td>${l.loanTypeName}</td>
                                <td class="fw-bold">₹<fmt:formatNumber value="${l.principalAmount}" pattern="#,##0.00"/></td>
                                <td class="fw-bold text-primary">₹<fmt:formatNumber value="${l.emiAmount}" pattern="#,##0.00"/></td>
                                <td class="fw-bold text-danger">₹<fmt:formatNumber value="${l.outstandingAmount}" pattern="#,##0.00"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${l.status == 'APPROVED'}"><span class="badge bg-success">APPROVED</span></c:when>
                                        <c:when test="${l.status == 'PENDING'}"><span class="badge bg-warning text-dark">PENDING</span></c:when>
                                        <c:when test="${l.status == 'REJECTED'}"><span class="badge bg-danger">REJECTED</span></c:when>
                                        <c:otherwise><span class="badge bg-secondary">${l.status}</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/customer/loan-details?id=${l.loanId}" class="btn btn-sm btn-outline-primary">Details / Pay EMI</a>
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
