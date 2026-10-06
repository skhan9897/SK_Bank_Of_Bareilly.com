<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Customer Profile | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/admin/customers" class="btn btn-outline-secondary btn-sm"><i class="fa-solid fa-arrow-left me-1"></i> Back to Customers</a>
        </div>

        <div class="sk-card p-4 mb-4">
            <div class="row align-items-center">
                <div class="col-md-2 text-center">
                    <img src="${pageContext.request.contextPath}/admin/profile-image?id=${customer.customerId}" alt="Profile" class="rounded-circle border border-3 border-warning" width="100" height="100">
                </div>
                <div class="col-md-7">
                    <span class="badge bg-gold text-dark mb-1">Customer ID: ${customer.customerNumber}</span>
                    <h3 class="fw-bold text-navy mb-1">${customer.fullName}</h3>
                    <p class="text-muted small mb-0"><i class="fa-solid fa-phone me-1"></i> ${customer.mobile} | <i class="fa-solid fa-envelope me-1"></i> ${customer.email}</p>
                    <p class="text-muted small mb-0"><i class="fa-solid fa-location-dot me-1"></i> ${customer.address}, ${customer.city}, ${customer.state} - ${customer.pincode}</p>
                </div>
                <div class="col-md-3 text-end">
                    <span class="badge bg-success fs-6">${customer.status}</span>
                </div>
            </div>
        </div>

        <div class="sk-card p-4">
            <h5 class="fw-bold text-navy mb-3">Linked Accounts</h5>
            <div class="table-responsive">
                <table class="table table-sk align-middle">
                    <thead>
                        <tr>
                            <th>Account No</th>
                            <th>Type</th>
                            <th>Balance</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${accounts}" var="a">
                            <tr>
                                <td class="font-monospace fw-bold">${a.maskedAccountNumber}</td>
                                <td>${a.accountTypeName}</td>
                                <td class="fw-bold text-success">₹<fmt:formatNumber value="${a.balance}" pattern="#,##0.00"/></td>
                                <td><span class="badge bg-success">${a.status}</span></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/account-details?id=${a.accountId}" class="btn btn-sm btn-outline-primary">View Account</a>
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
