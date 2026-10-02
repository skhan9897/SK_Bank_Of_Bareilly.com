<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Financial Reports | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-chart-line text-primary me-2"></i> Financial & Transaction Reports</h3>

        <div class="sk-card p-3 mb-4 d-print-none">
            <form action="${pageContext.request.contextPath}/admin/reports" method="get" class="row g-3 align-items-end">
                <div class="col-md-3">
                    <label class="form-label small fw-bold">Report Type</label>
                    <select name="type" class="form-select">
                        <option value="">All Transactions</option>
                        <option value="DEPOSIT" ${reportType == 'DEPOSIT' ? 'selected' : ''}>DEPOSITS</option>
                        <option value="WITHDRAWAL" ${reportType == 'WITHDRAWAL' ? 'selected' : ''}>WITHDRAWALS</option>
                        <option value="TRANSFER" ${reportType == 'TRANSFER' ? 'selected' : ''}>TRANSFERS</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label small fw-bold">From Date</label>
                    <input type="date" name="startDate" class="form-control" value="${startDate}">
                </div>
                <div class="col-md-3">
                    <label class="form-label small fw-bold">To Date</label>
                    <input type="date" name="endDate" class="form-control" value="${endDate}">
                </div>
                <div class="col-md-3">
                    <button type="submit" class="btn btn-gold w-100 fw-bold"><i class="fa-solid fa-gears me-1"></i> Generate Report</button>
                </div>
            </form>
        </div>

        <div class="sk-card p-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold text-navy mb-0">Generated Summary</h5>
                <button onclick="window.print()" class="btn btn-outline-primary btn-sm d-print-none"><i class="fa-solid fa-print me-1"></i> Print Report</button>
            </div>

            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>Ref Number</th>
                            <th>Customer Name</th>
                            <th>Account No</th>
                            <th>Type</th>
                            <th>Amount</th>
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${records}" var="r">
                            <tr>
                                <td class="font-monospace fw-bold">${r.transactionReference}</td>
                                <td>${r.customerName}</td>
                                <td class="font-monospace">${r.accountNumber}</td>
                                <td><span class="badge bg-light text-dark">${r.transactionType}</span></td>
                                <td class="fw-bold">₹<fmt:formatNumber value="${r.amount}" pattern="#,##0.00"/></td>
                                <td class="small"><fmt:formatDate value="${r.createdAt}" pattern="dd MMM yyyy"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
