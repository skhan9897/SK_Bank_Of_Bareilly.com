<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="SK Bank of Bareilly - Official Portal" />
</jsp:include>
<body>

    <jsp:include page="/WEB-INF/views/common/navbar.jsp" />

    <!-- HERO SECTION -->
    <section class="hero-section text-center text-lg-start">
        <div class="container py-5">
            <div class="row align-items-center g-5">
                <div class="col-lg-7">
                    <span class="badge badge-gold mb-3 fs-6 px-3 py-2"><i class="fas fa-shield-alt me-1"></i> Bareilly's Most Trusted Bank</span>
                    <h1 class="display-4 fw-extrabold text-white mb-3">Your Financial Partner for a Brighter Tomorrow</h1>
                    <p class="lead text-white-50 mb-4 fs-5">
                        Secure, Simple and Smart Banking with SK Bank of Bareilly. Access seamless internet banking, instant money transfers, competitive loan interest rates, and high-yield fixed deposits.
                    </p>
                    <div class="d-flex flex-wrap gap-3">
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-gold btn-lg"><i class="fas fa-user-plus me-2"></i> OPEN ACCOUNT</a>
                        <a href="#services" class="btn btn-outline-gold btn-lg"><i class="fas fa-compass me-2"></i> EXPLORE SERVICES</a>
                    </div>
                </div>
                <div class="col-lg-5 text-center">
                    <div class="card-custom p-4 bg-white text-dark shadow-lg">
                        <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png" alt="SK Bank Logo" class="img-fluid mb-3" style="max-height: 120px; object-fit: contain;">
                        <h4 class="fw-bold text-navy">Internet Banking Portal</h4>
                        <p class="text-muted small">Login securely to manage your savings, loans, and credit cards.</p>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-navy w-100 py-2 fs-6"><i class="fas fa-lock me-2"></i> LOGIN TO INTERNET BANKING</a>
                        <div class="mt-3 text-center small">
                            <a href="${pageContext.request.contextPath}/admin/login" class="text-secondary text-decoration-none"><i class="fas fa-user-shield me-1"></i> Admin Portal Login</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- KEY SERVICES -->
    <section id="services" class="py-5 bg-light">
        <div class="container py-4">
            <div class="text-center mb-5">
                <h6 class="text-warning fw-bold text-uppercase tracking-wider">What We Offer</h6>
                <h2 class="fw-bold text-navy">Comprehensive Digital Banking Solutions</h2>
                <div class="mx-auto" style="width: 80px; height: 4px; background: var(--grad-gold); border-radius: 2px;"></div>
            </div>

            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card-custom p-4 text-center h-100">
                        <div class="fs-1 text-warning mb-3"><i class="fas fa-piggy-bank"></i></div>
                        <h5 class="fw-bold text-navy">Savings Account</h5>
                        <p class="text-muted small">Earn up to 4.5% p.a. interest on your savings with zero maintenance fees and instant mobile banking access.</p>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-gold btn-sm mt-auto">Open Account</a>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card-custom p-4 text-center h-100">
                        <div class="fs-1 text-warning mb-3"><i class="fas fa-briefcase"></i></div>
                        <h5 class="fw-bold text-navy">Business & Current Account</h5>
                        <p class="text-muted small">Tailored for entrepreneurs and enterprises with unlimited daily transactions, bulk payouts, and POS integration.</p>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-gold btn-sm mt-auto">Learn More</a>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card-custom p-4 text-center h-100">
                        <div class="fs-1 text-warning mb-3"><i class="fas fa-chart-line"></i></div>
                        <h5 class="fw-bold text-navy">Fixed Deposits</h5>
                        <p class="text-muted small">Lock in high returns up to 7.5% p.a. with flexible tenure options ranging from 6 months to 5 years.</p>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-gold btn-sm mt-auto">Book FD Online</a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- EMI CALCULATOR SECTION -->
    <section id="loans" class="py-5 bg-white border-top border-bottom">
        <div class="container">
            <div class="row align-items-center g-5">
                <div class="col-lg-6">
                    <span class="badge badge-gold mb-2">Instant Approval</span>
                    <h2 class="fw-bold text-navy">Personal & Home Loans Made Simple</h2>
                    <p class="text-muted">Apply online with minimal documentation, competitive interest rates starting at 8.5% p.a., and quick disbursement into your account.</p>
                    <ul class="list-unstyled">
                        <li class="mb-2"><i class="fas fa-check-circle text-success me-2"></i> Personal Loans up to ₹15 Lakhs</li>
                        <li class="mb-2"><i class="fas fa-check-circle text-success me-2"></i> Home Loans with tenure up to 30 Years</li>
                        <li class="mb-2"><i class="fas fa-check-circle text-success me-2"></i> Low processing fees & transparent terms</li>
                    </ul>
                </div>
                <div class="col-lg-6">
                    <div class="card-custom p-4 shadow">
                        <h4 class="fw-bold text-navy mb-3"><i class="fas fa-calculator text-warning me-2"></i> Loan EMI Calculator</h4>
                        <div class="mb-3">
                            <label class="form-label small fw-bold">Loan Amount (₹)</label>
                            <input type="number" id="emiAmount" class="form-control" value="500000" oninput="calculateEmi()">
                        </div>
                        <div class="row g-3 mb-3">
                            <div class="col-6">
                                <label class="form-label small fw-bold">Interest Rate (% p.a.)</label>
                                <input type="number" id="emiRate" class="form-control" value="9.5" step="0.1" oninput="calculateEmi()">
                            </div>
                            <div class="col-6">
                                <label class="form-label small fw-bold">Tenure (Months)</label>
                                <input type="number" id="emiTenure" class="form-control" value="36" oninput="calculateEmi()">
                            </div>
                        </div>
                        <div class="p-3 bg-light rounded text-center">
                            <div class="small text-muted mb-1">Estimated Monthly EMI</div>
                            <div class="h3 fw-bold text-navy mb-3" id="emiResultEmi">₹ 16,016.32</div>
                            <div class="row small">
                                <div class="col-6 text-start">Total Interest Payable: <strong id="emiResultInterest">₹ 76,587.41</strong></div>
                                <div class="col-6 text-end">Total Amount: <strong id="emiResultTotal">₹ 5,76,587.41</strong></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <jsp:include page="/WEB-INF/views/common/footer.jsp" />

</body>
</html>
