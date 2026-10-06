<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="pageTitle" value="Admin Cash Deposit | SK BANK OF BAREILLY" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<!-- Include html2pdf library for PDF download -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>

<style>
    @media print {
        body * {
            visibility: hidden;
        }
        #printableReceiptCard, #printableReceiptCard * {
            visibility: visible;
        }
        #printableReceiptCard {
            position: absolute;
            left: 5%;
            top: 5%;
            width: 90% !important;
            box-shadow: none !important;
            border: 2px solid #198754 !important;
        }
    }
</style>

<div class="dashboard-wrapper">
    <%@ include file="/WEB-INF/views/common/admin-sidebar.jsp" %>

    <div class="main-content">
        <h3 class="fw-bold text-navy mb-3"><i class="fa-solid fa-circle-plus text-success me-2"></i> Counter Cash Deposit</h3>

        <%@ include file="/WEB-INF/views/common/alerts.jsp" %>

        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-7">

                <c:choose>
                    <c:when test="${depositSuccess}">
                        <!-- 1. LOADING SPINNER (2 SECONDS) -->
                        <div id="depositLoadingSpinner" class="sk-card p-5 text-center shadow">
                            <div class="spinner-border text-success mb-3" style="width: 4rem; height: 4rem;" role="status"></div>
                            <h4 class="fw-bold text-navy mb-2">Processing Counter Cash Deposit...</h4>
                            <p class="text-muted">Generating official deposit receipt, please wait...</p>
                        </div>

                        <!-- 2. OFFICIAL CASH DEPOSIT RECEIPT CARD (SHOWN AFTER 2 SECONDS) -->
                        <div id="depositReceiptContainer" class="d-none">
                            <div class="sk-card p-4 shadow-lg border-top border-4 border-success mb-4 bg-white" id="printableReceiptCard" style="background-color: #ffffff; box-sizing: border-box; width: 100%; max-width: 720px; margin: 0 auto;">
                                <div class="text-center border-bottom pb-3 mb-3">
                                    <img src="${pageContext.request.contextPath}/assets/images/sk-bank-logo-final-transparent.png?v=20261004" alt="Logo" width="70" class="mb-2" onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/assets/images/sk-bank-logo-transparent.png';">
                                    <h4 class="fw-bold text-navy mb-0">SK BANK OF BAREILLY</h4>
                                    <p class="text-muted small mb-2">Official Counter Cash Deposit Voucher / Receipt</p>
                                    <span class="badge bg-success px-4 py-2 fs-6"><i class="fa-solid fa-circle-check me-1"></i> CASH DEPOSIT SUCCESSFUL</span>
                                </div>

                                <div class="row g-3 small">
                                    <div class="col-6">
                                        <span class="text-muted d-block">Transaction Reference:</span>
                                        <strong class="font-monospace text-navy fs-6">${txn.transactionReference}</strong>
                                    </div>
                                    <div class="col-6 text-end">
                                        <span class="text-muted d-block">Date & Time:</span>
                                        <strong class="text-navy"><fmt:formatDate value="${txn.createdAt}" pattern="dd MMM yyyy, hh:mm a"/></strong>
                                    </div>
                                    <div class="col-6">
                                        <span class="text-muted d-block">Customer Name:</span>
                                        <strong class="text-navy fs-6">${empty customer.fullName ? 'Bank Customer' : customer.fullName}</strong>
                                    </div>
                                    <div class="col-6 text-end">
                                        <span class="text-muted d-block">Account Number:</span>
                                        <strong class="font-monospace text-navy fs-6">${account.accountNumber}</strong>
                                    </div>
                                    <div class="col-6">
                                        <span class="text-muted d-block">Depositor / Remarks:</span>
                                        <strong class="text-navy">${empty txn.description ? 'Counter Cash Deposit' : txn.description}</strong>
                                    </div>
                                    <div class="col-6 text-end">
                                        <span class="text-muted d-block">Transaction Type:</span>
                                        <span class="badge bg-success px-3 py-1">DEPOSIT</span>
                                    </div>
                                </div>

                                <hr class="my-3">

                                <div class="row align-items-center bg-light p-3 rounded border my-3">
                                    <div class="col-6">
                                        <span class="text-muted small d-block">Deposited Amount</span>
                                        <h3 class="fw-bold text-success mb-0">₹<fmt:formatNumber value="${txn.amount}" pattern="#,##0.00"/></h3>
                                    </div>
                                    <div class="col-6 text-end">
                                        <span class="text-muted small d-block">Updated Account Balance</span>
                                        <h4 class="fw-bold text-navy mb-0">₹<fmt:formatNumber value="${account.availableBalance}" pattern="#,##0.00"/></h4>
                                    </div>
                                </div>

                                <div class="mt-4 pt-3 border-top text-center text-muted small" style="font-size: 0.8rem; line-height: 1.4;">
                                    <p class="mb-1">This is a computer generated official banking receipt and does not require a physical signature.</p>
                                    <p class="mb-0 text-navy fw-bold">SK BANK OF BAREILLY | Trust • Growth • Together</p>
                                </div>
                            </div>

                            <!-- ACTION BUTTONS: PRINT & PDF -->
                            <div class="d-flex gap-2">
                                <button type="button" onclick="printReceipt()" class="btn btn-navy flex-fill py-2.5 fw-bold" style="background-color: #071F49; color: #fff;">
                                    <i class="fa-solid fa-print me-1"></i> Print Receipt
                                </button>
                                <button type="button" onclick="downloadPdfReceipt()" class="btn btn-gold flex-fill py-2.5 fw-bold">
                                    <i class="fa-solid fa-file-pdf me-1"></i> Download PDF
                                </button>
                                <a href="${pageContext.request.contextPath}/admin/deposits" class="btn btn-outline-secondary py-2.5 fw-bold">
                                    <i class="fa-solid fa-plus me-1"></i> New Deposit
                                </a>
                            </div>
                        </div>

                        <script>
                            // 2-Second Loading Animation Delay
                            setTimeout(function() {
                                var spinner = document.getElementById('depositLoadingSpinner');
                                var receipt = document.getElementById('depositReceiptContainer');
                                if (spinner && receipt) {
                                    spinner.classList.add('d-none');
                                    receipt.classList.remove('d-none');
                                }
                            }, 2000);

                            function printReceipt() {
                                window.print();
                            }

                            function downloadPdfReceipt() {
                                var element = document.getElementById('printableReceiptCard');
                                var opt = {
                                  margin:       [0.3, 0.3, 0.3, 0.3],
                                  filename:     'SK_Bank_Deposit_Receipt_' + '${txn.transactionReference}' + '.pdf',
                                  image:        { type: 'jpeg', quality: 0.98 },
                                  html2canvas:  { scale: 2, useCORS: true, scrollY: 0 },
                                  jsPDF:        { unit: 'in', format: 'a4', orientation: 'portrait' },
                                  pagebreak:    { mode: ['avoid-all', 'css', 'legacy'] }
                                };
                                html2pdf().set(opt).from(element).save();
                            }
                        </script>
                    </c:when>

                    <c:otherwise>
                        <!-- DEPOSIT FORM -->
                        <div class="sk-card p-4 shadow">
                            <form action="${pageContext.request.contextPath}/admin/deposits" method="post">
                                <input type="hidden" name="csrfToken" value="${sessionScope.CSRF_TOKEN}">

                                <!-- Dropdown Select Customer Account -->
                                <div class="mb-3">
                                    <label class="form-label fw-bold small">Select Customer Account (From List)</label>
                                    <select class="form-select form-select-lg mb-2" onchange="if(this.value){ document.getElementById('accountIdentifierInput').value = this.value; }">
                                        <option value="">-- Select Customer Account from List --</option>
                                        <c:forEach items="${accounts}" var="acc">
                                            <option value="${acc.accountNumber}">
                                                ${empty acc.customerName ? 'Customer' : acc.customerName} | Acc: ${acc.accountNumber} (Bal: ₹<fmt:formatNumber value="${acc.availableBalance}" pattern="#,##0.00"/>)
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <!-- Direct Search Input -->
                                <div class="mb-3">
                                    <label class="form-label fw-bold small">OR Enter Account No / Mobile No / Customer No *</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light"><i class="fa-solid fa-magnifying-glass text-muted"></i></span>
                                        <input type="text" name="accountIdentifier" id="accountIdentifierInput" class="form-control form-control-lg font-monospace fw-bold" required placeholder="e.g. SK1029384756 or Mobile 9876543210">
                                    </div>
                                    <small class="text-muted">Enter Account Number, Customer Mobile Number, or Customer Number.</small>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold small">Deposit Amount (₹) *</label>
                                    <input type="number" step="0.01" min="1" name="amount" class="form-control form-control-lg fw-bold text-success" required placeholder="0.00">
                                </div>

                                <div class="mb-4">
                                    <label class="form-label fw-bold small">Remarks / Depositor Name</label>
                                    <input type="text" name="remarks" class="form-control" placeholder="Counter Cash Deposit">
                                </div>

                                <button type="submit" class="btn btn-gold w-100 py-3 fw-bold fs-5"><i class="fa-solid fa-circle-check me-2"></i> Authorize & Credit Cash Deposit</button>
                            </form>
                        </div>
                    </c:otherwise>
                </c:choose>

            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
