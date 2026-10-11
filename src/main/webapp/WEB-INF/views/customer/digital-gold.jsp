<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="24K Digital Gold Vault | PhonePe Wealth | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h4 class="fw-bold text-navy mb-1"><i class="fa-solid fa-coins text-warning me-2"></i>24K Digital Gold Vault</h4>
                <p class="text-muted small mb-0">Buy, store, and sell 24K 99.9% pure digital gold backed by MMTC-PAMP 100% insured vaults.</p>
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

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                <i class="fa-solid fa-circle-exclamation me-2"></i> ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="row g-4">
            <!-- LIVE GOLD PRICE & VAULT CARD -->
            <div class="col-lg-5">
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-dark text-white text-center position-relative overflow-hidden mb-4">
                    <span class="badge bg-gold text-navy fw-bold mb-2">24K 99.9% PURE GOLD</span>
                    <h6 class="text-white-50 mb-1">Live Market Buy Price</h6>
                    <h2 class="fw-bold text-gold mb-0">₹7,250.00 <span class="fs-6 text-white-50">/ gram</span></h2>
                    <small class="text-success"><i class="fa-solid fa-chart-line me-1"></i> +0.45% Today • Insured Locker</small>
                </div>

                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-white text-center border border-warning">
                    <i class="fa-solid fa-vault fs-1 text-warning mb-2"></i>
                    <h6 class="fw-bold text-navy mb-1">My Digital Gold Locker</h6>
                    <h3 class="fw-bold text-navy mb-0">1.250 <small class="fs-6 text-muted">Grams</small></h3>
                    <p class="text-success fw-bold small mt-1 mb-0">Vault Market Value: ₹9,062.50</p>
                </div>
            </div>

            <!-- BUY / SELL GOLD FORM -->
            <div class="col-lg-7">
                <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-white">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-cart-shopping me-2 text-warning"></i>Buy or Sell Digital Gold</h5>

                    <form action="${pageContext.request.contextPath}/customer/digital-gold" method="POST">
                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Select Transaction Action *</label>
                            <select name="action" class="form-select rounded-3" required>
                                <option value="BUY">BUY 24K DIGITAL GOLD</option>
                                <option value="SELL">SELL GOLD (Instantly Credit Bank Account)</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Linked Bank Account *</label>
                            <select name="accountId" class="form-select rounded-3" required>
                                <c:forEach var="acc" items="${accounts}">
                                    <option value="${acc.accountId}">
                                        ${acc.maskedAccountNumber} (${empty acc.accountTypeName ? 'Savings Account' : acc.accountTypeName}) - Available: ₹<fmt:formatNumber value="${acc.balance}" pattern="#,##0.00"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Amount in Rupees (₹) *</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light text-warning fw-bold">₹</span>
                                <input type="number" step="0.01" min="10.00" name="amount" id="goldAmount" class="form-control rounded-end-3 fs-4 fw-bold text-navy" placeholder="e.g. 500.00" value="500.00" required>
                            </div>
                        </div>

                        <div class="d-flex gap-2 mb-4">
                            <button type="button" onclick="document.getElementById('goldAmount').value='100.00'" class="btn btn-outline-warning btn-sm rounded-pill px-3">+ ₹100</button>
                            <button type="button" onclick="document.getElementById('goldAmount').value='500.00'" class="btn btn-outline-warning btn-sm rounded-pill px-3">+ ₹500</button>
                            <button type="button" onclick="document.getElementById('goldAmount').value='1000.00'" class="btn btn-outline-warning btn-sm rounded-pill px-3">+ ₹1,000</button>
                            <button type="button" onclick="document.getElementById('goldAmount').value='5000.00'" class="btn btn-outline-warning btn-sm rounded-pill px-3">+ ₹5,000</button>
                        </div>

                        <button type="submit" class="btn btn-gold btn-lg w-100 fw-bold rounded-3 shadow">
                            <i class="fa-solid fa-coins me-2"></i>Proceed with Gold Transaction
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
