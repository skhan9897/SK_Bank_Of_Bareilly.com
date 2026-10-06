<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Transfers Log | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-arrow-right-arrow-left text-primary me-2"></i> All System Transfers</h3>

        <div class="sk-card p-4">
            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>Ref Number</th>
                            <th>Sender Account</th>
                            <th>Amount</th>
                            <th>Description</th>
                            <th>Date & Time</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${transfers}" var="tr">
                            <tr>
                                <td class="font-monospace fw-bold">${tr.transactionReference}</td>
                                <td class="font-monospace">${tr.accountNumber}</td>
                                <td class="fw-bold text-primary">₹<fmt:formatNumber value="${tr.amount}" pattern="#,##0.00"/></td>
                                <td>${tr.description}</td>
                                <td class="small"><fmt:formatDate value="${tr.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
