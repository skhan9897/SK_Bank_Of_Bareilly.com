<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Admin Notifications | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-bell text-primary me-2"></i> System Alerts & Notifications</h3>

        <div class="sk-card p-4">
            <div class="list-group list-group-flush">
                <c:forEach items="${notifications}" var="n">
                    <div class="list-group-item px-0 py-3 border-bottom">
                        <div class="d-flex justify-content-between align-items-center mb-1">
                            <h6 class="fw-bold mb-0 text-navy">${n.title}</h6>
                            <small class="text-muted"><fmt:formatDate value="${n.createdAt}" pattern="dd MMM, hh:mm a"/></small>
                        </div>
                        <p class="mb-0 small text-dark">${n.message}</p>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
