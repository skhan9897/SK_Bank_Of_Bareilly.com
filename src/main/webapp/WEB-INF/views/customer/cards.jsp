<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="My Cards | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-credit-card text-primary me-2"></i> Debit & Credit Cards</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row g-4">
            <c:forEach items="${cards}" var="card">
                <div class="col-md-6">
                    <div class="banking-card ${card.cardType == 'CREDIT' ? 'credit' : ''} mb-3">
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="fw-bold text-warning fs-5">SK BANK OF BAREILLY</span>
                            <span class="badge bg-gold text-dark font-monospace">${card.cardType}</span>
                        </div>
                        <div class="card-chip"></div>
                        <div class="card-number-text text-center my-2">${card.maskedCardNumber}</div>
                        <div class="d-flex justify-content-between align-items-end">
                            <div>
                                <small class="text-white-50 d-block">CARD HOLDER</small>
                                <span class="fw-bold text-uppercase">${sessionScope.CUSTOMER_NAME}</span>
                            </div>
                            <div>
                                <small class="text-white-50 d-block">EXPIRES</small>
                                <span class="fw-bold font-monospace">${card.expiryDate}</span>
                            </div>
                        </div>
                    </div>

                    <div class="sk-card p-3 d-flex justify-content-between align-items-center">
                        <div>
                            <small class="text-muted d-block">Card Status</small>
                            <span class="badge bg-${card.cardStatus == 'ACTIVE' ? 'success' : 'danger'}">${card.cardStatus}</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/customer/cards?action=toggle&id=${card.cardId}" class="btn btn-sm btn-outline-${card.cardStatus == 'ACTIVE' ? 'danger' : 'success'} fw-bold" onclick="return confirm('Change card status?');">
                            ${card.cardStatus == 'ACTIVE' ? 'Block Card' : 'Unblock Card'}
                        </a>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
