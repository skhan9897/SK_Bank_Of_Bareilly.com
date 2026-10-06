<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Bill Payment Audit | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-receipt text-primary me-2"></i> All Bill Payments</h3>

        <div class="sk-card p-4">
            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>Ref Reference</th>
                            <th>Biller Name</th>
                            <th>Category</th>
                            <th>Consumer No</th>
                            <th>Amount</th>
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${bills}" var="bp">
                            <tr>
                                <td class="font-monospace fw-bold">${bp.paymentReference}</td>
                                <td><strong>${bp.billerName}</strong></td>
                                <td><span class="badge bg-light text-dark">${bp.billerType}</span></td>
                                <td class="font-monospace">${bp.consumerNumber}</td>
                                <td class="fw-bold text-primary">₹<fmt:formatNumber value="${bp.amount}" pattern="#,##0.00"/></td>
                                <td class="small"><fmt:formatDate value="${bp.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
