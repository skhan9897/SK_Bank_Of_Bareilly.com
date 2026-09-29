<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="KYC Verification - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="kyc" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-id-card text-warning me-2"></i> KYC Verification Portal</h3>
                <p class="text-muted small">Upload your Aadhaar, PAN, and identity documents to complete mandatory bank KYC verification.</p>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row g-4">
                <div class="col-lg-5">
                    <div class="card-custom p-4 bg-white shadow-sm">
                        <h5 class="fw-bold text-navy mb-3"><i class="fas fa-upload text-warning me-2"></i> Upload KYC Document</h5>
                        <form action="${pageContext.request.contextPath}/kyc" method="post">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Document Type *</label>
                                <select name="documentType" class="form-select" required>
                                    <option value="Aadhaar">Aadhaar Card (Front & Back)</option>
                                    <option value="PAN">PAN Card</option>
                                    <option value="Address Proof">Address Proof (Utility Bill / Passport)</option>
                                    <option value="Photo">Passport Size Photograph</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Document File *</label>
                                <input type="file" class="form-control" required>
                                <input type="hidden" name="filePath" value="uploads/kyc_doc.pdf">
                                <div class="form-text small">Allowed formats: PDF, JPG, PNG (Max 5MB).</div>
                            </div>
                            <button type="submit" class="btn btn-gold w-100 py-2 fw-bold"><i class="fas fa-cloud-upload-alt me-1"></i> Submit Document for Verification</button>
                        </form>
                    </div>
                </div>

                <div class="col-lg-7">
                    <div class="card-custom bg-white p-3">
                        <h6 class="fw-bold text-navy mb-3"><i class="fas fa-file-alt text-warning me-2"></i> Uploaded Documents Status</h6>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle table-custom mb-0 small">
                                <thead>
                                    <tr>
                                        <th>Document Type</th>
                                        <th>Uploaded Date</th>
                                        <th>Status</th>
                                        <th>Remarks</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${not empty kycDocuments}">
                                            <c:forEach var="doc" items="${kycDocuments}">
                                                <tr>
                                                    <td class="fw-bold text-navy">${doc.documentType}</td>
                                                    <td class="text-muted">${doc.uploadedAt}</td>
                                                    <td>
                                                        <span class="badge ${doc.status == 'VERIFIED' ? 'bg-success' : (doc.status == 'REJECTED' ? 'bg-danger' : 'bg-warning')}">
                                                            ${doc.status}
                                                        </span>
                                                    </td>
                                                    <td class="text-muted">${not empty doc.rejectionReason ? doc.rejectionReason : 'Under Bank Review'}</td>
                                                </tr>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <tr>
                                                <td colspan="4" class="text-center py-3 text-muted">No KYC documents uploaded yet.</td>
                                            </tr>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
