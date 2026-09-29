<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Notifications - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="dashboard-container">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp">
            <jsp:param name="active" value="notifications" />
        </jsp:include>

        <div class="main-content">
            <div class="mb-4 pb-2 border-bottom">
                <h3 class="fw-bold text-navy mb-0"><i class="fas fa-bell text-warning me-2"></i> Notifications & Alerts</h3>
            </div>

            <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

            <div class="card-custom bg-white p-4">
                <c:choose>
                    <c:when test="${not empty notifications}">
                        <div class="list-group list-group-flush">
                            <c:forEach var="n" items="${notifications}">
                                <div class="list-group-item py-3 ${!n.read ? 'bg-light border-start border-warning border-4' : ''}">
                                    <div class="d-flex w-100 justify-content-between align-items-center">
                                        <h6 class="mb-1 fw-bold text-navy"><i class="fas fa-info-circle text-warning me-2"></i> ${n.title}</h6>
                                        <small class="text-muted">${n.createdAt}</small>
                                    </div>
                                    <p class="mb-1 small text-secondary">${n.message}</p>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-5 text-muted">
                            <i class="fas fa-bell-slash display-4 mb-3"></i>
                            <p>No notifications available right now.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
