<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Beneficiaries - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="beneficiaries" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-address-book text-warning me-2"></i> Beneficiary Management</h3>
                <button class="btn btn-gold" data-bs-toggle="modal" data-bs-target="#addBeneficiaryModal"><i class="fas fa-plus me-1"></i> Add New Beneficiary</button>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card-custom bg-white p-3">
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Name / Nickname</th>
                                <th>Account Number</th>
                                <th>IFSC Code</th>
                                <th>Bank Name</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty beneficiaries}">
                                    <c:forEach var="b" items="${beneficiaries}">
                                        <tr>
                                            <td class="fw-bold text-navy">${b.name} <c:if test="${not empty b.nickname}"><small class="text-muted">(${b.nickname})</small></c:if></td>
                                            <td>${b.accountNumber}</td>
                                            <td>${b.ifsc}</td>
                                            <td>${b.bankName}</td>
                                            <td><span class="badge bg-success">${b.status}</span></td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/transfer?toAccount=${b.accountNumber}" class="btn btn-gold btn-sm py-1 px-2"><i class="fas fa-paper-plane me-1"></i> Transfer</a>
                                                <form action="${pageContext.request.contextPath}/beneficiaries" method="post" class="d-inline">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="beneficiaryId" value="${b.beneficiaryId}">
                                                    <button type="submit" class="btn btn-outline-danger btn-sm py-1 px-2" onclick="return confirm('Remove beneficiary?')"><i class="fas fa-trash"></i></button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="6" class="text-center py-4 text-muted">No beneficiaries added yet. Click "Add New Beneficiary" above.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- ADD BENEFICIARY MODAL -->
    <div class="modal fade" id="addBeneficiaryModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="card-navy-header">Add New Beneficiary</div>
                <form action="${pageContext.request.contextPath}/beneficiaries" method="post" class="p-4">
                    <input type="hidden" name="action" value="add">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Beneficiary Full Name *</label>
                        <input type="text" name="name" class="form-control" required placeholder="Account Holder Name">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Account Number *</label>
                        <input type="text" name="accountNumber" class="form-control" required placeholder="Enter Receiver Account Number">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">IFSC Code *</label>
                        <input type="text" name="ifsc" class="form-control text-uppercase" required placeholder="e.g. SKB0002401 or HDFC0001234">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Bank Name *</label>
                        <input type="text" name="bankName" class="form-control" required placeholder="e.g. SK Bank of Bareilly / SBI / HDFC">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Nickname (Optional)</label>
                        <input type="text" name="nickname" class="form-control" placeholder="e.g. Landlord / Brother">
                    </div>
                    <div class="d-flex justify-content-end gap-2">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-gold">Save & Activate</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
