<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="All Issued Cards | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-credit-card text-primary me-2"></i> All Issued Debit & Credit Cards</h3>

        <div class="sk-card p-4">
            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>Card Number</th>
                            <th>Customer</th>
                            <th>Account No</th>
                            <th>Type</th>
                            <th>Expiry</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${cards}" var="cd">
                            <tr>
                                <td class="font-monospace fw-bold">${cd.maskedCardNumber}</td>
                                <td><strong>${cd.customerName}</strong></td>
                                <td class="font-monospace">${cd.accountNumber}</td>
                                <td><span class="badge bg-navy text-gold font-monospace">${cd.cardType}</span></td>
                                <td>${cd.expiryDate}</td>
                                <td><span class="badge bg-${cd.cardStatus == 'ACTIVE' ? 'success' : 'danger'}">${cd.cardStatus}</span></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
