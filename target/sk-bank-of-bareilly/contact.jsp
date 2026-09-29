<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Contact Us - SK Bank of Bareilly" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <div class="container py-5">
        <div class="text-center mb-5">
            <h6 class="text-warning fw-bold text-uppercase">Get In Touch</h6>
            <h2 class="fw-bold text-navy">We Are Always Here to Help You</h2>
            <div class="mx-auto" style="width: 80px; height: 4px; background: var(--grad-gold); border-radius: 2px;"></div>
        </div>

        <jsp:include page="/WEB-INF/views/common/alerts.jsp" />

        <div class="row g-4">
            <div class="col-lg-5">
                <div class="card-custom p-4 bg-white shadow-sm h-100">
                    <h5 class="fw-bold text-navy mb-4"><i class="fas fa-university text-warning me-2"></i> SK Bank of Bareilly Head Office</h5>
                    <ul class="list-unstyled mb-4">
                        <li class="mb-3 d-flex gap-3 align-items-start">
                            <i class="fas fa-map-marker-alt text-warning fs-5 mt-1"></i>
                            <div>
                                <strong>Main Branch Address:</strong>
                                <div class="text-muted small">124 Civil Lines, Near Railway Station, Bareilly, Uttar Pradesh - 243001</div>
                            </div>
                        </li>
                        <li class="mb-3 d-flex gap-3 align-items-start">
                            <i class="fas fa-phone-alt text-warning fs-5 mt-1"></i>
                            <div>
                                <strong>Toll-Free Customer Care:</strong>
                                <div class="text-muted small">1800-123-4567 (24x7 Support)</div>
                            </div>
                        </li>
                        <li class="mb-3 d-flex gap-3 align-items-start">
                            <i class="fas fa-envelope text-warning fs-5 mt-1"></i>
                            <div>
                                <strong>Support Email:</strong>
                                <div class="text-muted small">support@skbankofbareilly.example</div>
                            </div>
                        </li>
                    </ul>
                </div>
            </div>

            <div class="col-lg-7">
                <div class="card-custom p-4 bg-white shadow-sm">
                    <h5 class="fw-bold text-navy mb-3"><i class="fas fa-paper-plane text-warning me-2"></i> Send Us a Message</h5>
                    <form action="${pageContext.request.contextPath}/contact" method="post">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label fw-bold">Full Name *</label>
                                <input type="text" name="name" class="form-control" required placeholder="Your Name">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold">Email Address *</label>
                                <input type="email" name="email" class="form-control" required placeholder="name@example.com">
                            </div>
                            <div class="col-12">
                                <label class="form-label fw-bold">Message *</label>
                                <textarea name="message" class="form-control" rows="4" required placeholder="How can we assist you today?"></textarea>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-gold mt-4 py-2 px-4 fw-bold"><i class="fas fa-paper-plane me-2"></i> Submit Inquiry</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
