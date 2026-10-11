<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Free CIBIL Credit Score | PhonePe & CRED Bank | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h4 class="fw-bold text-navy mb-1"><i class="fa-solid fa-chart-line text-success me-2"></i>Free CIBIL & Credit Score Report</h4>
                <p class="text-muted small mb-0">Check your live CIBIL score powered by TransUnion CIBIL • 100% Free & No impact on score.</p>
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

        <div class="row g-4">
            <!-- CIBIL SCORE GAUGE METER -->
            <div class="col-lg-5">
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-dark text-white text-center position-relative overflow-hidden">
                    <span class="badge bg-gold text-navy fw-bold mb-3 px-3 py-2 rounded-pill"><i class="fa-solid fa-shield-check me-1"></i> VERIFIED CIBIL SCORE</span>

                    <div class="my-3 position-relative d-inline-block p-4 border border-3 border-success rounded-circle shadow-lg bg-navy" style="width: 210px; height: 210px;">
                        <span class="text-white-50 small d-block">CREDIT HEALTH</span>
                        <h1 class="display-3 fw-bold text-gold my-1 font-monospace">${cibilScore}</h1>
                        <span class="badge bg-success text-white fw-bold px-3 py-1">
                            <c:choose>
                                <c:when test="${cibilScore >= 750}">EXCELLENT</c:when>
                                <c:when test="${cibilScore >= 700}">GOOD</c:when>
                                <c:otherwise>AVERAGE</c:otherwise>
                            </c:choose>
                        </span>
                    </div>

                    <h6 class="fw-bold text-white mt-2 mb-1">${sessionScope.CUSTOMER_NAME}</h6>
                    <p class="text-muted small mb-3">Last Updated: <%= new java.text.SimpleDateFormat("dd MMM yyyy").format(new java.util.Date()) %></p>

                    <div class="d-flex gap-2 justify-content-center">
                        <form action="${pageContext.request.contextPath}/customer/cibil-score" method="POST">
                            <button type="submit" class="btn btn-gold btn-sm rounded-pill fw-bold px-3 shadow">
                                <i class="fa-solid fa-rotate me-1"></i> Refresh Score
                            </button>
                        </form>
                        <button onclick="window.print()" class="btn btn-outline-light btn-sm rounded-pill px-3">
                            <i class="fa-solid fa-print me-1"></i> Print Report
                        </button>
                    </div>
                </div>
            </div>

            <!-- CREDIT FACTORS BREAKDOWN -->
            <div class="col-lg-7">
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-white mb-4">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-list-check me-2 text-primary"></i>Credit Health Impact Factors</h5>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="p-3 bg-light rounded-3 border">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <span class="fw-bold text-navy small"><i class="fa-solid fa-clock text-success me-2"></i>On-Time Payments</span>
                                    <span class="badge bg-success">100%</span>
                                </div>
                                <div class="progress mb-2" style="height: 6px;">
                                    <div class="progress-bar bg-success" role="progressbar" style="width: 100%"></div>
                                </div>
                                <small class="text-muted">High Impact • No missed EMIs</small>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="p-3 bg-light rounded-3 border">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <span class="fw-bold text-navy small"><i class="fa-solid fa-chart-pie text-primary me-2"></i>Credit Utilization</span>
                                    <span class="badge bg-primary">12%</span>
                                </div>
                                <div class="progress mb-2" style="height: 6px;">
                                    <div class="progress-bar bg-primary" role="progressbar" style="width: 12%"></div>
                                </div>
                                <small class="text-muted">High Impact • Below 30% is ideal</small>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="p-3 bg-light rounded-3 border">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <span class="fw-bold text-navy small"><i class="fa-solid fa-calendar-days text-warning me-2"></i>Credit History Age</span>
                                    <span class="badge bg-warning text-dark">3 Yrs 8 Mos</span>
                                </div>
                                <small class="text-muted">Medium Impact • Good history length</small>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="p-3 bg-light rounded-3 border">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <span class="fw-bold text-navy small"><i class="fa-solid fa-magnifying-glass text-info me-2"></i>Hard Enquiries</span>
                                    <span class="badge bg-info">0 Enquiries</span>
                                </div>
                                <small class="text-muted">Low Impact • Excellent profile</small>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- PRE-APPROVED OFFERS BASED ON CIBIL -->
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-white border border-success">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-gift text-warning me-2"></i>Pre-Approved Offers for You</h5>

                    <div class="d-flex align-items-center justify-content-between p-3 bg-success bg-opacity-10 rounded-3 mb-2 border border-success">
                        <div>
                            <h6 class="fw-bold text-navy mb-0">Pre-Approved Personal Loan up to ₹5,000,000</h6>
                            <small class="text-success fw-bold"><i class="fa-solid fa-percent me-1"></i> Interest Rate: 10.5% p.a. • Instant Disbursal</small>
                        </div>
                        <a href="${pageContext.request.contextPath}/customer/loans" class="btn btn-gold btn-sm fw-bold px-3">Apply</a>
                    </div>

                    <div class="d-flex align-items-center justify-content-between p-3 bg-primary bg-opacity-10 rounded-3 border border-primary">
                        <div>
                            <h6 class="fw-bold text-navy mb-0">SK Bank Titanium Credit Card</h6>
                            <small class="text-primary fw-bold"><i class="fa-solid fa-crown me-1"></i> Lifetime Free • ₹150,000 Credit Limit</small>
                        </div>
                        <a href="${pageContext.request.contextPath}/customer/cards" class="btn btn-primary btn-sm fw-bold px-3">Claim</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
