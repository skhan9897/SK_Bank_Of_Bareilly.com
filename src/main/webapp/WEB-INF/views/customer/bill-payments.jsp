<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Recharge & Bill Payments | PhonePe & Paytm Bank | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h4 class="fw-bold text-navy mb-1"><i class="fa-solid fa-receipt text-primary me-2"></i>Recharge & Utility Bill Payments</h4>
                <p class="text-muted small mb-0">Pay bills, recharge mobile/DTH, and manage utilities instantly using SK Bank UPI.</p>
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

        <!-- PHONEPE / PAYTM STYLE RECHARGE & BILL CATEGORIES GRID -->
        <div class="sk-card p-4 rounded-4 shadow-sm border-0 bg-white mb-4">
            <h6 class="fw-bold text-navy mb-3"><i class="fa-solid fa-border-all text-warning me-2"></i>Recharge & Pay Bills Categories</h6>

            <div class="row g-3 text-center">
                <div class="col-4 col-md-2">
                    <button type="button" onclick="selectCategory('MOBILE', 'Jio / Airtel / Vi Mobile')" class="btn btn-light w-100 p-3 rounded-4 border shadow-sm h-100">
                        <i class="fa-solid fa-mobile-screen fs-2 text-primary mb-2 d-block"></i>
                        <span class="small fw-bold text-navy d-block">Mobile</span>
                    </button>
                </div>
                <div class="col-4 col-md-2">
                    <button type="button" onclick="selectCategory('ELECTRICITY', 'UPPCL / Electricity Board')" class="btn btn-light w-100 p-3 rounded-4 border shadow-sm h-100">
                        <i class="fa-solid fa-bolt fs-2 text-warning mb-2 d-block"></i>
                        <span class="small fw-bold text-navy d-block">Electricity</span>
                    </button>
                </div>
                <div class="col-4 col-md-2">
                    <button type="button" onclick="selectCategory('DTH', 'Tata Play / Airtel Digital')" class="btn btn-light w-100 p-3 rounded-4 border shadow-sm h-100">
                        <i class="fa-solid fa-tv fs-2 text-danger mb-2 d-block"></i>
                        <span class="small fw-bold text-navy d-block">DTH</span>
                    </button>
                </div>
                <div class="col-4 col-md-2">
                    <button type="button" onclick="selectCategory('INTERNET', 'JioFiber / Broadband')" class="btn btn-light w-100 p-3 rounded-4 border shadow-sm h-100">
                        <i class="fa-solid fa-wifi fs-2 text-success mb-2 d-block"></i>
                        <span class="small fw-bold text-navy d-block">Broadband</span>
                    </button>
                </div>
                <div class="col-4 col-md-2">
                    <button type="button" onclick="selectCategory('GAS', 'Indane / Bharat Gas')" class="btn btn-light w-100 p-3 rounded-4 border shadow-sm h-100">
                        <i class="fa-solid fa-fire-flame-simple fs-2 text-info mb-2 d-block"></i>
                        <span class="small fw-bold text-navy d-block">LPG Gas</span>
                    </button>
                </div>
                <div class="col-4 col-md-2">
                    <button type="button" onclick="selectCategory('INSURANCE', 'LIC / Insurance Premium')" class="btn btn-light w-100 p-3 rounded-4 border shadow-sm h-100">
                        <i class="fa-solid fa-shield-heart fs-2 text-primary mb-2 d-block"></i>
                        <span class="small fw-bold text-navy d-block">Insurance</span>
                    </button>
                </div>
            </div>
        </div>

        <div class="row g-4">
            <!-- PAYMENT FORM -->
            <div class="col-lg-5">
                <div class="sk-card p-4 rounded-4 shadow-sm border-0 bg-white">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-credit-card me-2 text-primary"></i>Pay Bill / Recharge</h5>
                    <form action="${pageContext.request.contextPath}/customer/bill-payments" method="post" id="billForm">

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Pay From Account *</label>
                            <select name="accountId" class="form-select rounded-3" required>
                                <c:forEach items="${accounts}" var="a">
                                    <option value="${a.accountId}">${a.maskedAccountNumber} - Available: ₹<fmt:formatNumber value="${a.availableBalance}" pattern="#,##0.00"/></option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Biller / Category *</label>
                            <select name="billerType" id="billerTypeSelect" class="form-select rounded-3" required>
                                <option value="ELECTRICITY">Electricity Bill</option>
                                <option value="MOBILE">Mobile Postpaid / Recharge</option>
                                <option value="DTH">DTH Recharge</option>
                                <option value="INTERNET">Broadband / Internet</option>
                                <option value="GAS">Piped Gas / LPG Cylinder</option>
                                <option value="WATER">Water Bill</option>
                                <option value="INSURANCE">Insurance Premium</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Biller Name / Operator *</label>
                            <input type="text" name="billerName" id="billerNameInput" class="form-control rounded-3" required placeholder="e.g. UPPCL, Jio, Airtel, Indane">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Consumer / Mobile / Account No *</label>
                            <input type="text" name="consumerNumber" class="form-control rounded-3" required placeholder="Enter Consumer ID / Mobile Number">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold text-muted small">Bill Amount (₹) *</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light fw-bold text-success">₹</span>
                                <input type="number" step="0.01" min="1" name="amount" class="form-control rounded-end-3 fs-4 fw-bold text-success" required placeholder="0.00">
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold text-muted small">Enter 4-Digit UPI PIN *</label>
                            <input type="password" name="upiPin" maxlength="4" pattern="[0-9]{4}" class="form-control rounded-3 text-center fs-4 font-monospace" required placeholder="••••">
                        </div>

                        <button type="submit" class="btn btn-gold btn-lg w-100 fw-bold rounded-3 shadow">
                            <i class="fa-solid fa-bolt me-2"></i>Pay Bill via PhonePe UPI
                        </button>
                    </form>
                </div>
            </div>

            <!-- PAYMENT HISTORY TABLE -->
            <div class="col-lg-7">
                <div class="sk-card p-4 rounded-4 shadow-sm border-0 bg-white">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-clock-rotate-left me-2 text-warning"></i>Bill Payment History</h5>
                    <div class="table-responsive">
                        <table class="table table-sk align-middle">
                            <thead>
                                <tr>
                                    <th>Biller</th>
                                    <th>Consumer No</th>
                                    <th>Amount</th>
                                    <th>Date</th>
                                    <th>Ref</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${billPayments}" var="bp">
                                    <tr>
                                        <td>
                                            <div class="fw-bold text-navy">${bp.billerName}</div>
                                            <small class="badge bg-light text-primary border">${bp.billerType}</small>
                                        </td>
                                        <td class="font-monospace fw-bold">${bp.consumerNumber}</td>
                                        <td class="fw-bold text-success">₹<fmt:formatNumber value="${bp.amount}" pattern="#,##0.00"/></td>
                                        <td class="small text-muted"><fmt:formatDate value="${bp.createdAt}" pattern="dd MMM yyyy"/></td>
                                        <td class="font-monospace small">${bp.paymentReference}</td>
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

<script>
function selectCategory(type, defaultBiller) {
    document.getElementById('billerTypeSelect').value = type;
    document.getElementById('billerNameInput').value = defaultBiller;
    document.getElementById('billForm').scrollIntoView({ behavior: 'smooth' });
}
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
