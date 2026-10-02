<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Ticket #${complaint.complaintId} Admin Reply | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <div class="mb-3">
            <a href="${pageContext.request.contextPath}/admin/complaints" class="btn btn-outline-secondary btn-sm"><i class="fa-solid fa-arrow-left me-1"></i> Back to Complaints</a>
        </div>

        <div class="sk-card p-4 mb-4">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <div>
                    <span class="badge bg-gold text-dark me-2">Ticket #${complaint.complaintId}</span>
                    <strong class="text-navy">${complaint.customerName}</strong>
                </div>
                <form action="${pageContext.request.contextPath}/admin/complaints" method="post" class="d-flex align-items-center gap-2">
                    <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                    <input type="hidden" name="action" value="status">
                    <input type="hidden" name="complaintId" value="${complaint.complaintId}">

                    <select name="status" class="form-select form-select-sm" style="width: auto;">
                        <option value="OPEN" ${complaint.status == 'OPEN' ? 'selected' : ''}>OPEN</option>
                        <option value="IN_PROGRESS" ${complaint.status == 'IN_PROGRESS' ? 'selected' : ''}>IN_PROGRESS</option>
                        <option value="RESOLVED" ${complaint.status == 'RESOLVED' ? 'selected' : ''}>RESOLVED</option>
                        <option value="CLOSED" ${complaint.status == 'CLOSED' ? 'selected' : ''}>CLOSED</option>
                    </select>
                    <button type="submit" class="btn btn-sm btn-primary">Update Status</button>
                </form>
            </div>
            <h4 class="fw-bold text-navy">${complaint.subject}</h4>
        </div>

        <div class="sk-card p-4 mb-4">
            <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-comments me-2"></i> Message History</h5>
            <div class="mb-4">
                <c:forEach items="${messages}" var="m">
                    <div class="p-3 mb-3 rounded ${m.senderRole == 'ADMIN' ? 'bg-warning bg-opacity-10 border-start border-4 border-warning me-4' : 'bg-light border-start border-4 border-primary ms-4'}">
                        <div class="d-flex justify-content-between align-items-center mb-1">
                            <strong class="${m.senderRole == 'ADMIN' ? 'text-navy' : 'text-primary'}">${m.senderRole == 'ADMIN' ? 'Staff (' + m.senderName + ')' : m.senderName}</strong>
                            <small class="text-muted"><fmt:formatDate value="${m.createdAt}" pattern="dd MMM, hh:mm a"/></small>
                        </div>
                        <p class="mb-0 text-dark">${m.message}</p>
                    </div>
                </c:forEach>
            </div>

            <form action="${pageContext.request.contextPath}/admin/complaints" method="post">
                <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                <input type="hidden" name="action" value="reply">
                <input type="hidden" name="complaintId" value="${complaint.complaintId}">

                <div class="mb-3">
                    <label class="form-label fw-bold small">Admin Response</label>
                    <textarea name="message" rows="3" class="form-control" required placeholder="Type staff response..."></textarea>
                </div>
                <button type="submit" class="btn btn-navy text-white fw-bold" style="background-color: #071F49;"><i class="fa-solid fa-reply me-1"></i> Send Official Reply</button>
            </form>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
