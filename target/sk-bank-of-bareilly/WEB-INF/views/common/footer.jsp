<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<footer class="bg-dark text-white pt-5 pb-3 border-top border-warning mt-auto">
    <div class="container">
        <div class="row g-4">
            <div class="col-lg-4 col-md-6">
                <div class="d-flex align-items-center mb-3">
                    <img src="${pageContext.request.contextPath}/assets/images/sk-bank-logo-transparent.png" alt="Logo" width="40" height="40" class="me-2">
                    <span class="h5 mb-0 font-weight-bold text-white">SK BANK OF BAREILLY</span>
                </div>
                <p class="text-muted small">Providing secure, reliable, and modern corporate & retail banking services. Committed to growth and trust.</p>
                <div class="text-warning font-weight-bold small">TRUST | GROWTH | TOGETHER</div>
            </div>
            <div class="col-lg-2 col-md-6">
                <h6 class="text-gold font-weight-bold mb-3">Quick Links</h6>
                <ul class="list-unstyled small text-muted">
                    <li><a href="${pageContext.request.contextPath}/#personal" class="text-white-50 text-decoration-none">Personal Banking</a></li>
                    <li><a href="${pageContext.request.contextPath}/#business" class="text-white-50 text-decoration-none">Business Banking</a></li>
                    <li><a href="${pageContext.request.contextPath}/#loans" class="text-white-50 text-decoration-none">Loans & EMIs</a></li>
                    <li><a href="${pageContext.request.contextPath}/#fd" class="text-white-50 text-decoration-none">Fixed Deposits</a></li>
                </ul>
            </div>
            <div class="col-lg-3 col-md-6">
                <h6 class="text-gold font-weight-bold mb-3">Security & Trust</h6>
                <ul class="list-unstyled small text-muted">
                    <li><i class="fa-solid fa-lock text-warning me-2"></i> 256-bit Encryption</li>
                    <li><i class="fa-solid fa-shield-halved text-warning me-2"></i> Role-Based Access</li>
                    <li><i class="fa-solid fa-key text-warning me-2"></i> BCrypt Password Hashing</li>
                    <li><i class="fa-solid fa-user-check text-warning me-2"></i> OTP Authentication</li>
                </ul>
            </div>
            <div class="col-lg-3 col-md-6">
                <h6 class="text-gold font-weight-bold mb-3">Head Office</h6>
                <p class="small text-muted mb-1"><i class="fa-solid fa-location-dot me-2 text-warning"></i> Main Branch, Civil Lines, Bareilly, UP - 243001</p>
                <p class="small text-muted mb-1"><i class="fa-solid fa-phone me-2 text-warning"></i> Toll Free: 1800-123-SKBANK</p>
                <p class="small text-muted"><i class="fa-solid fa-envelope me-2 text-warning"></i> support@skbank.com</p>
            </div>
        </div>
        <hr class="border-secondary my-4">
        <div class="row align-items-center">
            <div class="col-md-6 text-center text-md-start small text-muted">
                &copy; <%= java.time.Year.now().getValue() %> SK BANK OF BAREILLY. All Rights Reserved.
            </div>
            <div class="col-md-6 text-center text-md-end small text-muted">
                Developed cleanly with Pure Servlet + JSP + JDBC + MySQL 8.
            </div>
        </div>
    </div>
</footer>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<!-- Custom Application JS -->
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
