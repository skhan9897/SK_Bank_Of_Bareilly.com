<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="PhonePe Rewards & Scratch Cards | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h4 class="fw-bold text-navy mb-1"><i class="fa-solid fa-gift text-warning me-2"></i>PhonePe Rewards & Scratch Cards</h4>
                <p class="text-muted small mb-0">Scratch your reward cards to win real cashback credited straight into your bank account.</p>
            </div>
            <a href="${pageContext.request.contextPath}/customer/dashboard" class="btn btn-outline-secondary btn-sm">
                <i class="fa-solid fa-arrow-left me-1"></i> Back
            </a>
        </div>

        <c:if test="${not empty param.msg}">
            <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
                <i class="fa-solid fa-circle-check me-2"></i> ${param.msg}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="row g-4 justify-content-center">
            <!-- SCRATCH CARD 1 -->
            <div class="col-md-4">
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-gradient-gold text-navy text-center position-relative overflow-hidden">
                    <div class="badge bg-navy text-gold fw-bold mb-2">UNLOCKED REWARD</div>
                    <h5 class="fw-bold mb-1">Transfer Cashback</h5>
                    <p class="small text-navy opacity-75 mb-3">Earned on recent UPI transfer</p>

                    <div class="p-4 bg-white rounded-4 shadow-sm position-relative d-inline-block border w-100" style="min-height: 140px;">
                        <h2 class="fw-bold text-success mb-1">₹15.00</h2>
                        <span class="badge bg-success bg-opacity-10 text-success fw-bold">Cashback Won!</span>

                        <form action="${pageContext.request.contextPath}/customer/rewards" method="POST" class="mt-3">
                            <input type="hidden" name="rewardAmount" value="15.00">
                            <button type="submit" class="btn btn-gold btn-sm rounded-pill fw-bold px-4 shadow">
                                <i class="fa-solid fa-hand-holding-dollar me-1"></i> Claim to Bank
                            </button>
                        </form>
                    </div>
                </div>
            </div>

            <!-- SCRATCH CARD 2 -->
            <div class="col-md-4">
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-primary text-white text-center position-relative overflow-hidden">
                    <div class="badge bg-warning text-dark fw-bold mb-2">HOT REWARD</div>
                    <h5 class="fw-bold mb-1">Bill Payment Bonus</h5>
                    <p class="small text-white-50 mb-3">Earned on Utility bill payment</p>

                    <div class="p-4 bg-white text-dark rounded-4 shadow-sm position-relative d-inline-block border w-100" style="min-height: 140px;">
                        <h2 class="fw-bold text-success mb-1">₹25.00</h2>
                        <span class="badge bg-success bg-opacity-10 text-success fw-bold">Cashback Won!</span>

                        <form action="${pageContext.request.contextPath}/customer/rewards" method="POST" class="mt-3">
                            <input type="hidden" name="rewardAmount" value="25.00">
                            <button type="submit" class="btn btn-primary btn-sm rounded-pill fw-bold px-4 shadow">
                                <i class="fa-solid fa-hand-holding-dollar me-1"></i> Claim to Bank
                            </button>
                        </form>
                    </div>
                </div>
            </div>

            <!-- SCRATCH CARD 3 -->
            <div class="col-md-4">
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-dark text-gold text-center position-relative overflow-hidden">
                    <div class="badge bg-gold text-navy fw-bold mb-2">BUMPER REWARD</div>
                    <h5 class="fw-bold mb-1">Super Bumper Scratch</h5>
                    <p class="small text-white-50 mb-3">Earned on UPI Registration</p>

                    <div class="p-4 bg-white text-dark rounded-4 shadow-sm position-relative d-inline-block border w-100" style="min-height: 140px;">
                        <h2 class="fw-bold text-success mb-1">₹50.00</h2>
                        <span class="badge bg-success bg-opacity-10 text-success fw-bold">Cashback Won!</span>

                        <form action="${pageContext.request.contextPath}/customer/rewards" method="POST" class="mt-3">
                            <input type="hidden" name="rewardAmount" value="50.00">
                            <button type="submit" class="btn btn-gold btn-sm rounded-pill fw-bold px-4 shadow">
                                <i class="fa-solid fa-hand-holding-dollar me-1"></i> Claim to Bank
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
