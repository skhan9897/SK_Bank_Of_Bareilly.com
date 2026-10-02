<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="Manage Beneficiaries | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-users text-primary me-2"></i> Manage Beneficiaries</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row g-4">
            <div class="col-lg-5">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3">Add New Beneficiary</h5>
                    <form action="${pageContext.request.contextPath}/customer/beneficiaries" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Account Number *</label>
                            <input type="text" name="accountNumber" class="form-control" required placeholder="Enter account number">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold small">Beneficiary Name</label>
                            <input type="text" name="beneficiaryName" class="form-control" placeholder="Auto-fetched for SK Bank">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold small">IFSC Code</label>
                            <input type="text" name="ifscCode" class="form-control text-uppercase" placeholder="SKBK0000001">
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold small">Bank Name</label>
                            <input type="text" name="bankName" class="form-control" value="SK BANK OF BAREILLY">
                        </div>
                        <div class="mb-4">
                            <label class="form-label fw-bold small">Nickname / Label</label>
                            <input type="text" name="nickname" class="form-control" placeholder="e.g. Brother, Landlord">
                        </div>

                        <button type="submit" class="btn btn-gold w-100 fw-bold py-2"><i class="fa-solid fa-plus me-1"></i> Add Beneficiary</button>
                    </form>
                </div>
            </div>

            <div class="col-lg-7">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3">Saved Beneficiaries</h5>
                    <div class="table-responsive">
                        <table class="table table-sk align-middle">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Account No</th>
                                    <th>Bank / IFSC</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${beneficiaries}" var="b">
                                    <tr>
                                        <td>
                                            <div class="fw-bold">${b.beneficiaryName}</div>
                                            <small class="text-muted">${b.nickname}</small>
                                        </td>
                                        <td class="font-monospace">${b.maskedAccountNumber}</td>
                                        <td><small>${b.bankName}<br>IFSC: ${b.ifscCode}</small></td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/customer/beneficiaries?action=delete&id=${b.beneficiaryId}" class="btn btn-sm btn-outline-danger" onclick="return confirm('Delete this beneficiary?');"><i class="fa-solid fa-trash"></i></a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
