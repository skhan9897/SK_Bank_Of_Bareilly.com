<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Manage Cards - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="cards" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-credit-card text-warning me-2"></i> Debit & Credit Cards</h3>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="row g-4">
                <c:forEach var="card" items="${cards}">
                    <div class="col-lg-6">
                        <div class="bank-debit-card mb-3">
                            <div class="d-flex justify-content-between align-items-center">
                                <span class="fw-bold fs-5 text-warning">SK BANK OF BAREILLY</span>
                                <span class="badge ${card.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">${card.status}</span>
                            </div>
                            <div class="chip my-2"></div>
                            <div class="card-num">${card.maskedCardNumber}</div>
                            <div class="d-flex justify-content-between align-items-end mt-2 small">
                                <div>
                                    <div class="text-white-50" style="font-size: 0.7rem;">CARD HOLDER</div>
                                    <div class="fw-bold text-uppercase">${card.cardHolderName}</div>
                                </div>
                                <div class="text-end">
                                    <div class="text-white-50" style="font-size: 0.7rem;">EXPIRES</div>
                                    <div class="fw-bold">${card.expiryDate}</div>
                                </div>
                            </div>
                        </div>

                        <div class="card-custom p-3 bg-white d-flex gap-2">
                            <form action="${pageContext.request.contextPath}/cards" method="post" class="flex-fill">
                                <input type="hidden" name="action" value="toggleBlock">
                                <input type="hidden" name="cardId" value="${card.cardId}">
                                <input type="hidden" name="currentStatus" value="${card.status}">
                                <button type="submit" class="btn ${card.status == 'ACTIVE' ? 'btn-outline-danger' : 'btn-success'} w-100 btn-sm">
                                    <i class="fas fa-ban me-1"></i> ${card.status == 'ACTIVE' ? 'Block Card' : 'Unblock Card'}
                                </button>
                            </form>

                            <button class="btn btn-navy btn-sm flex-fill" data-bs-toggle="modal" data-bs-target="#changePinModal${card.cardId}">
                                <i class="fas fa-key me-1"></i> Set / Change PIN
                            </button>
                        </div>

                        <!-- CHANGE PIN MODAL -->
                        <div class="modal fade" id="changePinModal${card.cardId}" tabindex="-1">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="card-navy-header">Change Card PIN</div>
                                    <form action="${pageContext.request.contextPath}/cards" method="post" class="p-4">
                                        <input type="hidden" name="action" value="changePin">
                                        <input type="hidden" name="cardId" value="${card.cardId}">
                                        <div class="mb-3">
                                            <label class="form-label fw-bold">Enter New 4-Digit PIN *</label>
                                            <input type="password" name="newPin" class="form-control" maxlength="4" pattern="\d{4}" required placeholder="4-Digit Number">
                                        </div>
                                        <div class="d-flex justify-content-end gap-2">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                            <button type="submit" class="btn btn-gold">Update PIN</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
