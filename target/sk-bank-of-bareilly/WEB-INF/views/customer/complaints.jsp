<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Help & Support Tickets | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-headset text-primary me-2"></i> Complaints & Helpdesk</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row g-4">
            <div class="col-lg-5">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3">Create New Ticket</h5>
                    <form action="${pageContext.request.contextPath}/customer/complaints" method="post">
                        <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Subject / Issue Title *</label>
                            <input type="text" name="subject" class="form-control" required placeholder="e.g. Failed Transfer, Card issue">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold small">Priority Level *</label>
                            <select name="priority" class="form-select" required>
                                <option value="LOW">Low</option>
                                <option value="MEDIUM" selected>Medium</option>
                                <option value="HIGH">High</option>
                                <option value="URGENT">Urgent</option>
                            </select>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold small">Detailed Description *</label>
                            <textarea name="description" rows="4" class="form-control" required placeholder="Explain your issue in detail..."></textarea>
                        </div>

                        <button type="submit" class="btn btn-gold w-100 fw-bold py-2"><i class="fa-solid fa-paper-plane me-1"></i> Raise Ticket</button>
                    </form>
                </div>
            </div>

            <div class="col-lg-7">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3">My Support Tickets</h5>
                    <div class="table-responsive">
                        <table class="table table-sk align-middle">
                            <thead>
                                <tr>
                                    <th>Ticket ID</th>
                                    <th>Subject</th>
                                    <th>Priority</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${complaints}" var="c">
                                    <tr>
                                        <td>#${c.complaintId}</td>
                                        <td><div class="fw-bold">${c.subject}</div></td>
                                        <td><span class="badge bg-light text-dark">${c.priority}</span></td>
                                        <td>
                                            <span class="badge bg-${c.status == 'RESOLVED' ? 'success' : 'warning text-dark'}">${c.status}</span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/customer/complaints?id=${c.complaintId}" class="btn btn-sm btn-outline-primary">View & Reply</a>
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
