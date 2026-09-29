<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Customer Dashboard - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <!-- SIDEBAR -->
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="dashboard" />
        </jsp:include>

        <!-- MAIN CONTENT AREA -->
        <div class="main-content">
            <!-- WELCOME HEADER -->
            <div class="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-3 border-bottom">
                <div>
                    <h3 class="fw-bold text-navy mb-1">Welcome back, ${sessionScope.customerProfile.fullName}!</h3>
                    <p class="text-muted small mb-0">Customer ID: <span class="badge badge-navy">${sessionScope.customerProfile.customerId}</span> | KYC Status: <span class="badge bg-success">${sessionScope.customerProfile.kycStatus}</span></p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/transfer" class="btn btn-gold fw-bold shadow-sm me-2"><i class="fas fa-paper-plane me-1"></i> Quick Transfer</a>
                    <a href="${pageContext.request.contextPath}/statements" class="btn btn-navy"><i class="fas fa-download me-1"></i> Statement</a>
                </div>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <!-- SUMMARY STAT CARDS -->
            <div class="row g-4 mb-4">
                <div class="col-md-4">
                    <div class="card-balance">
                        <div class="small text-warning text-uppercase fw-bold mb-1">Total Available Balance</div>
                        <div class="display-6 fw-bold">₹ <fmt:formatNumber value="${totalBalance != null ? totalBalance : 0.00}" pattern="#,##0.00"/></div>
                        <div class="small text-white-50 mt-2"><i class="fas fa-university me-1"></i> Across ${accounts.size()} Active Accounts</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card-custom p-4 h-100 bg-white">
                        <div class="d-flex align-items-center justify-content-between mb-2">
                            <span class="text-muted small fw-bold">LOAN OUTSTANDING</span>
                            <i class="fas fa-hand-holding-usd text-warning fs-4"></i>
                        </div>
                        <div class="h3 fw-bold text-navy mb-1">
                            <c:set var="totalLoan" value="0.00" />
                            <c:forEach var="l" items="${loans}">
                                <c:if test="${l.status == 'APPROVED' or l.status == 'ACTIVE'}">
                                    <c:set var="totalLoan" value="${totalLoan + l.outstandingAmount}" />
                                </c:if>
                            </c:forEach>
                            ₹ <fmt:formatNumber value="${totalLoan}" pattern="#,##0.00"/>
                        </div>
                        <div class="small text-muted"><a href="${pageContext.request.contextPath}/loans" class="text-decoration-none">View ${loans.size()} Loan Accounts →</a></div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card-custom p-4 h-100 bg-white">
                        <div class="d-flex align-items-center justify-content-between mb-2">
                            <span class="text-muted small fw-bold">DEBIT / CREDIT CARDS</span>
                            <i class="fas fa-credit-card text-warning fs-4"></i>
                        </div>
                        <div class="h5 fw-bold text-navy mb-1">
                            <c:choose>
                                <c:when test="${not empty cards}">
                                    ${cards[0].maskedCardNumber}
                                </c:when>
                                <c:otherwise>Active SKB Debit Card</c:otherwise>
                            </c:choose>
                        </div>
                        <div class="small text-success mt-2"><i class="fas fa-check-circle me-1"></i> Status: Active & Secured</div>
                    </div>
                </div>
            </div>

            <!-- QUICK ACTIONS GRID -->
            <div class="card-custom p-4 bg-white mb-4">
                <h5 class="fw-bold text-navy mb-3"><i class="fas fa-bolt text-warning me-2"></i> Quick Banking Services</h5>
                <div class="row g-3 text-center">
                    <div class="col-6 col-md-3 col-lg-2">
                        <a href="${pageContext.request.contextPath}/transfer" class="p-3 d-block card-custom text-decoration-none text-navy">
                            <i class="fas fa-exchange-alt fs-2 text-primary mb-2"></i>
                            <div class="fw-bold small">Transfer Money</div>
                        </a>
                    </div>
                    <div class="col-6 col-md-3 col-lg-2">
                        <a href="${pageContext.request.contextPath}/beneficiaries" class="p-3 d-block card-custom text-decoration-none text-navy">
                            <i class="fas fa-user-plus fs-2 text-warning mb-2"></i>
                            <div class="fw-bold small">Beneficiaries</div>
                        </a>
                    </div>
                    <div class="col-6 col-md-3 col-lg-2">
                        <a href="${pageContext.request.contextPath}/payments" class="p-3 d-block card-custom text-decoration-none text-navy">
                            <i class="fas fa-file-invoice fs-2 text-success mb-2"></i>
                            <div class="fw-bold small">Pay Bills</div>
                        </a>
                    </div>
                    <div class="col-6 col-md-3 col-lg-2">
                        <a href="${pageContext.request.contextPath}/fixed-deposits" class="p-3 d-block card-custom text-decoration-none text-navy">
                            <i class="fas fa-piggy-bank fs-2 text-danger mb-2"></i>
                            <div class="fw-bold small">Open FD</div>
                        </a>
                    </div>
                    <div class="col-6 col-md-3 col-lg-2">
                        <a href="${pageContext.request.contextPath}/loans" class="p-3 d-block card-custom text-decoration-none text-navy">
                            <i class="fas fa-hand-holding-usd fs-2 text-info mb-2"></i>
                            <div class="fw-bold small">Apply Loan</div>
                        </a>
                    </div>
                    <div class="col-6 col-md-3 col-lg-2">
                        <a href="${pageContext.request.contextPath}/statements" class="p-3 d-block card-custom text-decoration-none text-navy">
                            <i class="fas fa-file-alt fs-2 text-secondary mb-2"></i>
                            <div class="fw-bold small">Statements</div>
                        </a>
                    </div>
                </div>
            </div>

            <!-- RECENT TRANSACTIONS TABLE -->
            <div class="card-custom bg-white">
                <div class="card-navy-header d-flex justify-content-between align-items-center">
                    <span><i class="fas fa-history me-2 text-warning"></i> Recent Account Activity</span>
                    <a href="${pageContext.request.contextPath}/transactions" class="btn btn-gold btn-sm py-1 px-3">View All Transactions</a>
                </div>
                <div class="table-responsive p-3">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Txn ID</th>
                                <th>Date & Time</th>
                                <th>Type</th>
                                <th>Description</th>
                                <th>Amount</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty recentTransactions}">
                                    <c:forEach var="txn" items="${recentTransactions}">
                                        <tr>
                                            <td class="fw-bold text-navy">${txn.transactionId}</td>
                                            <td class="small text-muted">${txn.createdAt}</td>
                                            <td>
                                                <span class="badge ${txn.type == 'DEPOSIT' or txn.type == 'INTEREST_CREDIT' ? 'bg-success' : 'bg-primary'}">
                                                    ${txn.type}
                                                </span>
                                            </td>
                                            <td class="small">${txn.description}</td>
                                            <td class="fw-bold ${txn.type == 'DEPOSIT' or txn.type == 'INTEREST_CREDIT' ? 'text-success' : 'text-danger'}">
                                                ${txn.type == 'DEPOSIT' or txn.type == 'INTEREST_CREDIT' ? '+' : '-'} ₹ <fmt:formatNumber value="${txn.amount}" pattern="#,##0.00"/>
                                            </td>
                                            <td><span class="badge bg-success">${txn.status}</span></td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="6" class="text-center py-4 text-muted">No recent transactions found.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
