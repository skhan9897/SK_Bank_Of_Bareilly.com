<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Loan Application Details | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/admin/loans" class="btn btn-outline-secondary btn-sm"><i class="fa-solid fa-arrow-left me-1"></i> Back to Loans</a>
        </div>

        <div class="sk-card p-4 mb-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="badge bg-gold text-dark">${loan.loanTypeName}</span>
                <span class="badge bg-success fs-6">${loan.status}</span>
            </div>
            <h3 class="font-monospace fw-bold text-navy mb-2">${loan.loanNumber}</h3>
            <p class="text-muted small">Applicant: <strong>${loan.customerName}</strong></p>

            <div class="row bg-light p-3 rounded g-3">
                <div class="col-md-3">
                    <small class="text-muted d-block">Principal Amount</small>
                    <strong class="fs-5">₹<fmt:formatNumber value="${loan.principalAmount}" pattern="#,##0.00"/></strong>
                </div>
                <div class="col-md-3">
                    <small class="text-muted d-block">Interest Rate</small>
                    <strong class="fs-5">${loan.interestRate}% p.a.</strong>
                </div>
                <div class="col-md-3">
                    <small class="text-muted d-block">Calculated EMI</small>
                    <strong class="fs-5 text-primary">₹<fmt:formatNumber value="${loan.emiAmount}" pattern="#,##0.00"/></strong>
                </div>
                <div class="col-md-3">
                    <small class="text-muted d-block">Outstanding Amount</small>
                    <strong class="fs-5 text-danger">₹<fmt:formatNumber value="${loan.outstandingAmount}" pattern="#,##0.00"/></strong>
                </div>
            </div>

            <c:if test="${loan.status == 'PENDING'}">
                <div class="d-flex gap-2 mt-4">
                    <a href="${pageContext.request.contextPath}/admin/loans?action=approve&id=${loan.loanId}" class="btn btn-success fw-bold px-4" onclick="return confirm('Approve this loan?');"><i class="fa-solid fa-check me-1"></i> Approve Loan</a>
                    <a href="${pageContext.request.contextPath}/admin/loans?action=reject&id=${loan.loanId}" class="btn btn-danger fw-bold px-4" onclick="return confirm('Reject this loan?');"><i class="fa-solid fa-xmark me-1"></i> Reject Application</a>
                </div>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
