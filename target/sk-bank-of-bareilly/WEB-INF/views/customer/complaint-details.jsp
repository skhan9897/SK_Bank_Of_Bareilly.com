<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Ticket #${complaint.complaintId} | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/customer/complaints" class="btn btn-outline-secondary btn-sm"><i class="fa-solid fa-arrow-left me-1"></i> Back to Complaints</a>
        </div>

        <div class="sk-card p-4 mb-4">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <span class="badge bg-gold text-dark">Ticket #${complaint.complaintId}</span>
                <span class="badge bg-primary">${complaint.status}</span>
            </div>
            <h4 class="fw-bold text-navy">${complaint.subject}</h4>
            <small class="text-muted">Raised on <fmt:formatDate value="${complaint.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></small>
        </div>

        <div class="sk-card p-4 mb-4">
            <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-comments me-2"></i> Conversation Thread</h5>
            <div class="mb-4">
                <c:forEach items="${messages}" var="m">
                    <div class="p-3 mb-3 rounded ${m.senderRole == 'ADMIN' ? 'bg-light border-start border-4 border-warning ms-4' : 'bg-primary bg-opacity-10 border-start border-4 border-primary me-4'}">
                        <div class="d-flex justify-content-between align-items-center mb-1">
                            <strong class="${m.senderRole == 'ADMIN' ? 'text-warning' : 'text-primary'}">${m.senderRole == 'ADMIN' ? 'SK Bank Support' : m.senderName}</strong>
                            <small class="text-muted"><fmt:formatDate value="${m.createdAt}" pattern="dd MMM, hh:mm a"/></small>
                        </div>
                        <p class="mb-0 text-dark">${m.message}</p>
                    </div>
                </c:forEach>
            </div>

            <form action="${pageContext.request.contextPath}/customer/complaints" method="post">
                <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                <input type="hidden" name="action" value="reply">
                <input type="hidden" name="complaintId" value="${complaint.complaintId}">

                <div class="mb-3">
                    <label class="form-label fw-bold small">Add Reply</label>
                    <textarea name="message" rows="3" class="form-control" required placeholder="Type your reply here..."></textarea>
                </div>
                <button type="submit" class="btn btn-gold fw-bold"><i class="fa-solid fa-reply me-1"></i> Send Reply</button>
            </form>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
