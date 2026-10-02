<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="All Fixed Deposits | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-piggy-bank text-primary me-2"></i> All Fixed Deposits</h3>

        <div class="sk-card p-4">
            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>FD Number</th>
                            <th>Customer</th>
                            <th>Principal</th>
                            <th>Rate</th>
                            <th>Maturity Amount</th>
                            <th>Maturity Date</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${fds}" var="f">
                            <tr>
                                <td class="font-monospace fw-bold">${f.fdNumber}</td>
                                <td><strong>${f.customerName}</strong></td>
                                <td class="fw-bold">₹<fmt:formatNumber value="${f.principalAmount}" pattern="#,##0.00"/></td>
                                <td>${f.interestRate}%</td>
                                <td class="fw-bold text-success">₹<fmt:formatNumber value="${f.maturityAmount}" pattern="#,##0.00"/></td>
                                <td class="small"><fmt:formatDate value="${f.maturityDate}" pattern="dd MMM yyyy"/></td>
                                <td><span class="badge bg-success">${f.status}</span></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
