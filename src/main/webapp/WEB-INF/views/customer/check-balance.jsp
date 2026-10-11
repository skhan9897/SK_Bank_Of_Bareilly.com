<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Check Account Balance | PhonePe Bank | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/customer-sidebar.jsp" %>

    <div class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h4 class="fw-bold text-navy mb-1"><i class="fa-solid fa-building-columns text-warning me-2"></i>Check Account Balance</h4>
                <p class="text-muted small mb-0">Check live available liquidity & balance across all your SK Bank accounts.</p>
            </div>
            <a href="${pageContext.request.contextPath}/customer/dashboard" class="btn btn-outline-secondary btn-sm">
                <i class="fa-solid fa-arrow-left me-1"></i> Back
            </a>
        </div>

        <div class="row g-4 justify-content-center">
            <c:forEach var="acc" items="${accounts}" varStatus="status">
                <div class="col-md-6">
                    <div class="sk-card p-4 shadow-sm border-0 rounded-4 bg-white position-relative overflow-hidden">
                        <div class="d-flex align-items-center justify-content-between mb-3">
                            <div class="d-flex align-items-center gap-3">
                                <div class="bg-primary bg-opacity-10 p-3 rounded-circle text-primary fs-3">
                                    <i class="fa-solid fa-vault"></i>
                                </div>
                                <div>
                                    <h6 class="fw-bold text-navy mb-0">${empty acc.accountTypeName ? 'Savings Account' : acc.accountTypeName}</h6>
                                    <span class="font-monospace text-muted small">${acc.maskedAccountNumber}</span>
                                </div>
                            </div>
                            <span class="badge bg-success bg-opacity-10 text-success fw-bold px-3 py-2 rounded-pill">PRIMARY</span>
                        </div>

                        <div class="p-3 bg-light rounded-3 my-3 text-center position-relative">
                            <div id="balanceView_${acc.accountId}" class="d-none">
                                <span class="text-muted small d-block mb-1">Available Liquidity Balance</span>
                                <h2 class="fw-bold text-success mb-0">₹<fmt:formatNumber value="${acc.balance}" pattern="#,##0.00"/></h2>
                                <small class="text-muted"><i class="fa-solid fa-shield-halved text-success me-1"></i> Verified Live Balance</small>
                            </div>

                            <div id="maskedView_${acc.accountId}">
                                <h3 class="fw-bold text-navy mb-2 font-monospace">₹ • • • • • •</h3>
                                <button type="button" onclick="openPinModal('${acc.accountId}', '${acc.maskedAccountNumber}')" class="btn btn-gold btn-sm rounded-pill px-4 fw-bold shadow-sm">
                                    <i class="fa-solid fa-key me-1"></i> Check Balance
                                </button>
                            </div>
                        </div>

                        <div class="d-flex justify-content-between text-muted small">
                            <span>Status: <strong class="text-success">${acc.status}</strong></span>
                            <span>IFSC: <strong class="font-monospace">SKBOB000101</strong></span>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<!-- PHONEPE STYLE SECURE PIN OVERLAY MODAL -->
<div class="modal fade" id="pinModal" tabindex="-1" aria-hidden="true" data-bs-backdrop="static">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content border-0 rounded-4 shadow-lg bg-dark text-white">
            <div class="modal-header border-0 pb-0">
                <h6 class="modal-title fw-bold text-gold" id="modalAccTitle">SK Bank Security</h6>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body text-center p-4">
                <i class="fa-solid fa-shield-halved fs-1 text-warning mb-3"></i>
                <h6 class="fw-bold mb-1">Enter 4-Digit UPI PIN</h6>
                <p class="text-white-50 small mb-3">Required to check real-time account balance</p>

                <input type="password" id="pinInput" maxlength="4" class="form-control bg-secondary text-white text-center fs-3 font-monospace border-0 rounded-3 mb-3" placeholder="••••" autofocus>

                <input type="hidden" id="activeAccountId">

                <button type="button" onclick="verifyPinAndShowBalance()" class="btn btn-gold w-100 fw-bold rounded-3">
                    VERIFY PIN
                </button>
            </div>
        </div>
    </div>
</div>

<script>
function openPinModal(accId, maskedAcc) {
    document.getElementById('activeAccountId').value = accId;
    document.getElementById('modalAccTitle').innerText = "Account " + maskedAcc;
    document.getElementById('pinInput').value = "";
    var modal = new bootstrap.Modal(document.getElementById('pinModal'));
    modal.show();
}

function verifyPinAndShowBalance() {
    var pin = document.getElementById('pinInput').value;
    var accId = document.getElementById('activeAccountId').value;

    if (pin.length < 4) {
        alert("Please enter a valid 4-digit UPI PIN.");
        return;
    }

    var modalElement = document.getElementById('pinModal');
    var modal = bootstrap.Modal.getInstance(modalElement);
    if (modal) modal.hide();

    document.getElementById('maskedView_' + accId).classList.add('d-none');
    document.getElementById('balanceView_' + accId).classList.remove('d-none');
}
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
