<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="My UPI | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-qrcode text-primary me-2"></i> UPI Settings & Handles</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row g-4">
            <div class="col-lg-6">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3">Active UPI Details</h5>
                    <c:choose>
                        <c:when test="${not empty upi}">
                            <div class="bg-light p-3 rounded mb-4 border">
                                <div class="d-flex justify-content-between mb-2">
                                    <span class="text-muted">UPI Handle:</span>
                                    <strong class="font-monospace text-primary fs-5">${upi.upiAddress}</strong>
                                </div>
                                <div class="d-flex justify-content-between mb-2">
                                    <span class="text-muted">Linked Account:</span>
                                    <strong class="font-monospace">${upi.accountNumber}</strong>
                                </div>
                                <div class="d-flex justify-content-between">
                                    <span class="text-muted">UPI Status:</span>
                                    <span class="badge bg-success">${upi.status}</span>
                                </div>
                            </div>

                            <form action="${pageContext.request.contextPath}/customer/upi" method="post" class="mb-3">
                                <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                <input type="hidden" name="action" value="changePin">
                                <h6 class="fw-bold mb-3">Change UPI PIN</h6>
                                <div class="mb-2">
                                    <input type="password" name="oldPin" maxlength="6" pattern="[0-9]{4,6}" class="form-control" required placeholder="Current UPI PIN">
                                </div>
                                <div class="mb-3">
                                    <input type="password" name="newPin" maxlength="6" pattern="[0-9]{4,6}" class="form-control" required placeholder="New UPI PIN (4 or 6 digits)">
                                </div>
                                <button type="submit" class="btn btn-primary btn-sm fw-bold">Update PIN</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-3">You don't have an active UPI ID created yet.</p>
                            <form action="${pageContext.request.contextPath}/customer/upi" method="post">
                                <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">
                                <input type="hidden" name="action" value="create">

                                <div class="mb-3">
                                    <label class="form-label fw-bold small">Link Account *</label>
                                    <select name="accountId" class="form-select" required>
                                        <c:forEach items="${accounts}" var="a">
                                            <option value="${a.accountId}">${a.maskedAccountNumber} - ${a.accountTypeName}</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold small">Desired UPI Address *</label>
                                    <div class="input-group">
                                        <input type="text" name="upiAddress" class="form-control" required placeholder="username@skbank">
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold small">Set 4-Digit UPI PIN *</label>
                                    <input type="password" name="upiPin" maxlength="6" pattern="[0-9]{4,6}" class="form-control" required placeholder="Set Secret PIN">
                                </div>

                                <button type="submit" class="btn btn-gold w-100 fw-bold py-2"><i class="fa-solid fa-plus me-1"></i> Create UPI Handle</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="sk-card p-4">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-shield-cat text-warning me-2"></i> UPI Safety Tips</h5>
                    <ul class="list-group list-group-flush small">
                        <li class="list-group-item bg-transparent px-0"><i class="fa-solid fa-lock text-success me-2"></i> Never share your UPI PIN with anyone over call or message.</li>
                        <li class="list-group-item bg-transparent px-0"><i class="fa-solid fa-circle-info text-primary me-2"></i> You only enter your UPI PIN to SEND money, never to receive money.</li>
                        <li class="list-group-item bg-transparent px-0"><i class="fa-solid fa-key text-warning me-2"></i> SK Bank hashes UPI PINs securely using BCrypt algorithms.</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
