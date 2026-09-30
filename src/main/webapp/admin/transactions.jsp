<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.time.LocalDate" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Transaction Audit & Counter Receipts - SK Bank Admin" />
</jsp:include>
<style>
    @media print {
        body * { visibility: hidden; }
        #printableAdminReceipt, #printableAdminReceipt * { visibility: visible; }
        #printableAdminReceipt { position: absolute; left: 0; top: 0; width: 100%; border: none !important; }
        .no-print { display: none !important; }
    }
</style>
<body>

    <div class="no-print">
        <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />
    </div>

    <div class="dashboard-container">
        <div class="no-print">
            <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
                <jsp:param name="active" value="transactions" />
            </jsp:include>
        </div>

        <div class="main-content">
            <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom no-print">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-list-alt text-warning me-2"></i> All System Transactions & Counter Receipts</h3>
                <div class="d-flex gap-2">
                    <button onclick="window.print()" class="btn btn-navy"><i class="fas fa-print me-1"></i> Print Audit / Save PDF</button>
                    <button onclick="exportTableToCSV('sk_bank_admin_transactions.csv')" class="btn btn-gold"><i class="fas fa-file-csv me-1"></i> Export CSV</button>
                </div>
            </div>

            <div class="no-print">
                <jsp:include page="/WEB-INF/views/common/alerts.jsp" />
            </div>

            <div class="card-custom bg-white p-4" id="printableAdminReceipt">
                <!-- RECEIPT HEADER -->
                <div class="d-flex justify-content-between align-items-center mb-3 pb-3 border-bottom">
                    <div class="d-flex align-items-center gap-3">
                        <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="Logo" style="height: 50px;">
                        <div>
                            <h4 class="fw-bold text-navy mb-0">SK Bank of Bareilly</h4>
                            <small class="text-muted">Official Counter Transaction Audit Log</small>
                        </div>
                    </div>
                    <div class="text-end small">
                        <div>Officer: <strong>System Admin</strong></div>
                        <div>Date: <strong><%= LocalDate.now() %></strong></div>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Txn Ref</th>
                                <th>Customer Name</th>
                                <th>Account No</th>
                                <th>Date & Time</th>
                                <th>Type</th>
                                <th>Amount (₹)</th>
                                <th>Balance After (₹)</th>
                                <th>Status</th>
                                <th class="no-print">Receipt</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty transactions}">
                                    <c:forEach var="txn" items="${transactions}">
                                        <tr>
                                            <td class="fw-bold text-navy small">${txn.transactionId}</td>
                                            <td class="small fw-bold">${txn.customerName}</td>
                                            <td class="small">${txn.accountNumber}</td>
                                            <td class="small text-muted">${txn.createdAt}</td>
                                            <td>
                                                <span class="badge ${txn.direction == 'CREDIT' or txn.type == 'DEPOSIT' or txn.type == 'INTEREST_CREDIT' ? 'bg-success' : 'bg-primary'}">
                                                    ${txn.type}
                                                </span>
                                            </td>
                                            <td class="fw-bold ${txn.direction == 'CREDIT' or txn.type == 'DEPOSIT' or txn.type == 'INTEREST_CREDIT' ? 'text-success' : 'text-danger'}">
                                                ${txn.direction == 'CREDIT' or txn.type == 'DEPOSIT' or txn.type == 'INTEREST_CREDIT' ? '+' : '-'} ₹ <fmt:formatNumber value="${txn.amount}" pattern="#,##0.00"/>
                                            </td>
                                            <td class="fw-bold text-navy small">₹ <fmt:formatNumber value="${txn.balanceAfter}" pattern="#,##0.00"/></td>
                                            <td><span class="badge bg-success">${txn.status}</span></td>
                                            <td class="no-print">
                                                <button class="btn btn-outline-gold btn-sm" onclick="window.print()"><i class="fas fa-print me-1"></i> Print</button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="9" class="text-center py-4 text-muted">No transactions logged.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="no-print">
        <jsp:include page="/WEB-INF/views/common/footer.jsp" />
    </div>

</body>
</html>
