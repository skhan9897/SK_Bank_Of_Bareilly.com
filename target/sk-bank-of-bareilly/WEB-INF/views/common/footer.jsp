<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<footer class="bank-footer">
    <div class="container">
        <div class="row g-4">
            <div class="col-md-4">
                <div class="d-flex align-items-center gap-2 mb-3">
                    <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="SK Logo" style="height: 45px;">
                    <h5 class="text-white mb-0 fw-bold">SK Bank of Bareilly</h5>
                </div>
                <p class="small text-white-50">
                    Your trusted financial partner for a brighter tomorrow. Providing secure, simple, and smart personal and commercial banking services in Bareilly and across Uttar Pradesh.
                </p>
            </div>
            <div class="col-md-2">
                <h6 class="text-warning fw-bold mb-3">Quick Links</h6>
                <ul class="list-unstyled small">
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/">Home</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/#services">Services</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/#loans">Loan Portal</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/login">Internet Banking</a></li>
                </ul>
            </div>
            <div class="col-md-3">
                <h6 class="text-warning fw-bold mb-3">Customer Support</h6>
                <ul class="list-unstyled small text-white-50">
                    <li class="mb-2"><i class="fas fa-phone-alt text-warning me-2"></i> Toll Free: 1800-123-4567</li>
                    <li class="mb-2"><i class="fas fa-envelope text-warning me-2"></i> support@skbankofbareilly.example</li>
                    <li class="mb-2"><i class="fas fa-map-marker-alt text-warning me-2"></i> Bareilly Civil Lines, UP - 243001</li>
                </ul>
            </div>
            <div class="col-md-3">
                <h6 class="text-warning fw-bold mb-3">Security & Trust</h6>
                <p class="small text-white-50">256-Bit SSL Encrypted Banking Portal. Fully compliant with RBI banking guidelines and security protocols.</p>
                <div class="d-flex gap-2 text-warning fs-5">
                    <i class="fab fa-facebook"></i>
                    <i class="fab fa-twitter"></i>
                    <i class="fab fa-linkedin"></i>
                    <i class="fab fa-instagram"></i>
                </div>
            </div>
        </div>
        <hr class="border-secondary my-4">
        <div class="row align-items-center small text-white-50">
            <div class="col-md-6 text-center text-md-start">
                © <%= java.time.Year.now().getValue() %> SK Bank of Bareilly. All Rights Reserved.
            </div>
            <div class="col-md-6 text-center text-md-end">
                <a href="#" class="me-3">Privacy Policy</a>
                <a href="#" class="me-3">Terms & Conditions</a>
                <a href="#">Security Statement</a>
            </div>
        </div>
    </div>
</footer>

<!-- Bootstrap 5 JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<!-- Custom JS -->
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
