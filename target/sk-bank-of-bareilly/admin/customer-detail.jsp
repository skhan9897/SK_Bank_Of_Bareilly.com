<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Customer Full Control Console - SK Bank Admin" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/admin/admin-header.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/admin/admin-sidebar.jsp">
            <jsp:param name="active" value="customers" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <div class="d-flex align-items-center gap-3">
                    <a href="${pageContext.request.contextPath}/admin/customers" class="btn btn-outline-navy btn-sm"><i class="fas fa-arrow-left me-1"></i> Back to List</a>
                    <h3 class="fw-bold text-navy mb-0"><i class="fas fa-user-shield text-warning me-2"></i> Administrative Control Console</h3>
                </div>
                <div>
                    <span class="badge ${selectedUser.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'} fs-6 me-2">Account: ${selectedUser.status}</span>
                    <span class="badge badge-gold fs-6">KYC: ${selectedCustomer.kycStatus}</span>
                </div>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <!-- ADMIN ACTION TOOLBAR (EXCLUSIVE TO ADMIN) -->
            <div class="card-custom p-3 bg-white shadow-sm mb-4 border-2 border-primary">
                <div class="d-flex flex-wrap align-items-center justify-content-between gap-3">
                    <div class="fw-bold text-navy"><i class="fas fa-sliders-h text-warning me-2"></i> Admin Authorizations & Controls:</div>
                    <div class="d-flex flex-wrap gap-2">
                        <!-- Toggle Status -->
                        <form action="${pageContext.request.contextPath}/admin/customers" method="post" class="d-inline">
                            <input type="hidden" name="action" value="updateUserStatus">
                            <input type="hidden" name="customerId" value="${selectedCustomer.customerId}">
                            <input type="hidden" name="status" value="${selectedUser.status == 'ACTIVE' ? 'BLOCKED' : 'ACTIVE'}">
                            <button type="submit" class="btn ${selectedUser.status == 'ACTIVE' ? 'btn-danger' : 'btn-success'} btn-sm fw-bold">
                                <i class="fas fa-ban me-1"></i> ${selectedUser.status == 'ACTIVE' ? 'Block Customer' : 'Unblock Customer'}
                            </button>
                        </form>

                        <!-- Toggle KYC -->
                        <form action="${pageContext.request.contextPath}/admin/customers" method="post" class="d-inline">
                            <input type="hidden" name="action" value="updateKycStatus">
                            <input type="hidden" name="customerId" value="${selectedCustomer.customerId}">
                            <input type="hidden" name="kycStatus" value="${selectedCustomer.kycStatus == 'VERIFIED' ? 'REJECTED' : 'VERIFIED'}">
                            <button type="submit" class="btn btn-outline-navy btn-sm fw-bold">
                                <i class="fas fa-id-card me-1"></i> ${selectedCustomer.kycStatus == 'VERIFIED' ? 'Mark KYC Rejected' : 'Approve KYC'}
                            </button>
                        </form>

                        <!-- Adjust Balance Modal Button -->
                        <button class="btn btn-gold btn-sm fw-bold" data-bs-toggle="modal" data-bs-target="#adminBalanceModal">
                            <i class="fas fa-coins me-1"></i> Deposit / Adjust Funds
                        </button>

                        <!-- Reset Password Modal Button -->
                        <button class="btn btn-navy btn-sm fw-bold" data-bs-toggle="modal" data-bs-target="#adminResetPasswordModal">
                            <i class="fas fa-key me-1"></i> Reset Password
                        </button>

                        <!-- Issue Card Button -->
                        <form action="${pageContext.request.contextPath}/admin/customers" method="post" class="d-inline">
                            <input type="hidden" name="action" value="adminIssueCard">
                            <input type="hidden" name="customerId" value="${selectedCustomer.customerId}">
                            <input type="hidden" name="accountId" value="${customerAccounts[0].accountId}">
                            <button type="submit" class="btn btn-outline-gold btn-sm fw-bold">
                                <i class="fas fa-credit-card me-1"></i> Issue Debit Card
                            </button>
                        </form>
                    </div>
                </div>
            </div>

            <!-- CUSTOMER DETAILS & ACCOUNTS -->
            <div class="row g-4 mb-4">
                <!-- CUSTOMER PROFILE CARD -->
                <div class="col-lg-5">
                    <div class="card-custom p-4 bg-white shadow-sm h-100">
                        <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-id-card text-warning me-2"></i> Customer Demographics</h5>
                        <div class="mb-3">
                            <span class="text-muted small d-block">Customer ID / Username</span>
                            <strong class="badge badge-navy fs-6">${selectedCustomer.customerId} / ${selectedUser.username}</strong>
                        </div>
                        <div class="mb-2">Full Name: <strong class="text-navy">${selectedCustomer.fullName}</strong></div>
                        <div class="mb-2">Mobile Number: <strong>${selectedCustomer.mobile}</strong></div>
                        <div class="mb-2">Email Address: <strong>${selectedCustomer.email}</strong></div>
                        <div class="mb-2">Aadhaar: <strong>${selectedCustomer.aadhaar}</strong></div>
                        <div class="mb-2">PAN: <strong>${selectedCustomer.pan}</strong></div>
                        <div class="mb-2">Gender / DOB: <strong>${selectedCustomer.gender} / ${selectedCustomer.dob}</strong></div>
                        <div>Address: <strong>${selectedCustomer.address}, ${selectedCustomer.city}, ${selectedCustomer.state} - ${selectedCustomer.pincode}</strong></div>
                    </div>
                </div>

                <!-- ACCOUNTS & CARDS CARD -->
                <div class="col-lg-7">
                    <div class="card-custom p-4 bg-white shadow-sm h-100">
                        <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-wallet text-warning me-2"></i> Bank Accounts & Balance</h5>
                        <div class="row g-3 mb-4">
                            <c:forEach var="acc" items="${customerAccounts}">
                                <div class="col-md-6">
                                    <div class="p-3 bg-light rounded border">
                                        <div class="d-flex justify-content-between align-items-center mb-1">
                                            <span class="badge badge-navy">${acc.accountTypeName}</span>
                                            <span class="badge bg-success">${acc.status}</span>
                                        </div>
                                        <h4 class="fw-bold text-navy my-1">₹ <fmt:formatNumber value="${acc.balance}" pattern="#,##0.00"/></h4>
                                        <div class="small text-muted">Acc No: <strong>${acc.accountNumber}</strong></div>
                                        <div class="small text-muted">IFSC: <strong>${acc.ifscCode}</strong></div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <h6 class="fw-bold text-navy border-bottom pb-2 mb-2"><i class="fas fa-credit-card text-warning me-2"></i> Issued Debit & Credit Cards</h6>
                        <div class="table-responsive">
                            <table class="table table-sm table-hover align-middle">
                                <thead>
                                    <tr>
                                        <th>Card Number</th>
                                        <th>Type</th>
                                        <th>Expiry</th>
                                        <th>Status</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="card" items="${customerCards}">
                                        <tr>
                                            <td class="fw-bold text-navy small">${card.maskedCardNumber}</td>
                                            <td><span class="badge badge-gold">${card.cardType}</span></td>
                                            <td class="small">${card.expiryDate}</td>
                                            <td><span class="badge ${card.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${card.status}</span></td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/admin/customers" method="post">
                                                    <input type="hidden" name="action" value="adminToggleCardBlock">
                                                    <input type="hidden" name="customerId" value="${selectedCustomer.customerId}">
                                                    <input type="hidden" name="cardId" value="${card.cardId}">
                                                    <input type="hidden" name="currentStatus" value="${card.status}">
                                                    <button type="submit" class="btn btn-outline-danger btn-sm py-0"><i class="fas fa-ban"></i> Toggle</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <!-- CUSTOMER TRANSACTION AUDIT LOG -->
            <div class="card-custom p-4 bg-white shadow-sm">
                <h5 class="fw-bold text-navy border-bottom pb-2 mb-3"><i class="fas fa-list-alt text-warning me-2"></i> Customer Recent Transaction Audit Log</h5>
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Txn Ref</th>
                                <th>Account No</th>
                                <th>Date & Time</th>
                                <th>Type</th>
                                <th>Description</th>
                                <th>Amount (₹)</th>
                                <th>Balance After (₹)</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="txn" items="${customerTransactions}">
                                <tr>
                                    <td class="fw-bold text-navy small">${txn.transactionId}</td>
                                    <td class="small">${txn.accountNumber}</td>
                                    <td class="small text-muted">${txn.createdAt}</td>
                                    <td>
                                        <span class="badge ${txn.direction == 'CREDIT' or txn.type == 'DEPOSIT' ? 'bg-success' : 'bg-primary'}">${txn.type}</span>
                                    </td>
                                    <td class="small">${txn.description}</td>
                                    <td class="fw-bold ${txn.direction == 'CREDIT' or txn.type == 'DEPOSIT' ? 'text-success' : 'text-danger'}">
                                        ${txn.direction == 'CREDIT' or txn.type == 'DEPOSIT' ? '+' : '-'} ₹ <fmt:formatNumber value="${txn.amount}" pattern="#,##0.00"/>
                                    </td>
                                    <td class="fw-bold text-navy small">₹ <fmt:formatNumber value="${txn.balanceAfter}" pattern="#,##0.00"/></td>
                                    <td><span class="badge bg-success">${txn.status}</span></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- ADMIN ADJUST BALANCE MODAL -->
    <div class="modal fade" id="adminBalanceModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="card-navy-header"><i class="fas fa-coins text-warning me-2"></i> Admin Fund Deposit / Adjustment</div>
                <form action="${pageContext.request.contextPath}/admin/customers" method="post" class="p-4">
                    <input type="hidden" name="action" value="adminAdjustBalance">
                    <input type="hidden" name="customerId" value="${selectedCustomer.customerId}">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Select Account *</label>
                        <select name="accountId" class="form-select" required>
                            <c:forEach var="a" items="${customerAccounts}">
                                <option value="${a.accountId}">${a.accountTypeName} - ${a.accountNumber} (Bal: ₹${a.balance})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Adjustment Type *</label>
                        <select name="type" class="form-select" required>
                            <option value="CREDIT">Deposit / Credit (+)</option>
                            <option value="DEBIT">Deduct / Debit (-)</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Amount (₹) *</label>
                        <input type="number" name="amount" class="form-control" min="1" step="0.01" required placeholder="Enter amount">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Audit Remark *</label>
                        <input type="text" name="remark" class="form-control" required placeholder="e.g. Counter Deposit / Admin Interest Adjustment">
                    </div>
                    <div class="d-flex justify-content-end gap-2">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-gold">Confirm Adjustment</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- ADMIN RESET PASSWORD MODAL -->
    <div class="modal fade" id="adminResetPasswordModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="card-navy-header"><i class="fas fa-key text-warning me-2"></i> Admin Customer Password Reset</div>
                <form action="${pageContext.request.contextPath}/admin/customers" method="post" class="p-4">
                    <input type="hidden" name="action" value="adminResetPassword">
                    <input type="hidden" name="customerId" value="${selectedCustomer.customerId}">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Customer Username</label>
                        <input type="text" class="form-control" value="${selectedUser.username}" readonly>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Enter New Password *</label>
                        <input type="password" name="newPassword" class="form-control" required minlength="8" placeholder="Min. 8 characters">
                    </div>
                    <div class="d-flex justify-content-end gap-2">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-navy">Set New Password</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
