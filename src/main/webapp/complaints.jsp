<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Support & Complaints - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="complaints" />
        </jsp:include>

        <div class="main-content">
            <div class="d-flex justify-content-between align-items-center mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-headset text-warning me-2"></i> Customer Support & Complaint Portal</h3>
                <button class="btn btn-gold" data-bs-toggle="modal" data-bs-target="#newComplaintModal"><i class="fas fa-edit me-1"></i> Register New Complaint</button>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card-custom bg-white p-3">
                <div class="table-responsive">
                    <table class="table table-hover align-middle table-custom mb-0">
                        <thead>
                            <tr>
                                <th>Complaint ID</th>
                                <th>Subject</th>
                                <th>Category</th>
                                <th>Date Filed</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty complaints}">
                                    <c:forEach var="c" items="${complaints}">
                                        <tr>
                                            <td class="fw-bold text-navy">${c.complaintId}</td>
                                            <td class="fw-semibold">${c.subject}</td>
                                            <td><span class="badge badge-navy">${c.category}</span></td>
                                            <td class="small text-muted">${c.createdAt}</td>
                                            <td>
                                                <span class="badge ${c.status == 'OPEN' ? 'bg-warning' : (c.status == 'RESOLVED' ? 'bg-success' : 'bg-secondary')}">
                                                    ${c.status}
                                                </span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="5" class="text-center py-4 text-muted">No complaints filed. Need help? Click "Register New Complaint" above.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- NEW COMPLAINT MODAL -->
    <div class="modal fade" id="newComplaintModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="card-navy-header">Register Support Ticket / Complaint</div>
                <form action="${pageContext.request.contextPath}/complaints" method="post" class="p-4">
                    <div class="mb-3">
                        <label class="form-label fw-bold">Subject *</label>
                        <input type="text" name="subject" class="form-control" required placeholder="Brief summary of issue">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Category *</label>
                        <select name="category" class="form-select" required>
                            <option value="Transaction Issue">Failed Money Transfer / Transaction Issue</option>
                            <option value="ATM / Card">ATM / Debit Card Issue</option>
                            <option value="Account Balance">Account Balance Discrepancy</option>
                            <option value="Internet Banking">Internet / Mobile Banking Support</option>
                            <option value="Loan / Interest">Loan / Interest Inquiry</option>
                            <option value="Other">General Feedback / Complaint</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Detailed Description *</label>
                        <textarea name="description" class="form-control" rows="4" required placeholder="Provide full details including transaction date/time if applicable..."></textarea>
                    </div>
                    <div class="d-flex justify-content-end gap-2">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-gold">Submit Ticket</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
