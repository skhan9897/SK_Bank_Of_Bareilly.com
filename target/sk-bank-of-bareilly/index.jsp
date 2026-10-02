<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="pageTitle" value="SK BANK OF BAREILLY | Banking Made Simple, Secure & Smarter" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<%@ include file="/WEB-INF/views/common/splash.jsp" %>
<%@ include file="/WEB-INF/views/common/navbar.jsp" %>

<!-- HERO SECTION -->
<section id="home" class="py-5 text-white position-relative" style="background: linear-gradient(rgba(7, 31, 73, 0.82), rgba(11, 78, 162, 0.82)), url('${pageContext.request.contextPath}/assets/images/sk-bank-background.png') center/cover no-repeat;">
    <div class="container py-5">
        <div class="row align-items-center g-5">
            <div class="col-lg-6">
                <span class="badge bg-gold text-dark font-weight-bold px-3 py-2 rounded-pill mb-3">TRUST | GROWTH | TOGETHER</span>
                <h1 class="display-4 font-weight-bold fw-bold mb-3">Banking Made Simple, Secure & Smarter</h1>
                <p class="lead mb-4 text-light opacity-90">Experience premium corporate & retail digital banking with SK Bank of Bareilly. Manage deposits, transfers, loans, and UPI seamlessly.</p>
                <div class="d-flex flex-wrap gap-3">
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-gold btn-lg px-4 fw-bold"><i class="fa-solid fa-user-plus me-2"></i> Open Account</a>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-gold btn-lg px-4"><i class="fa-solid fa-right-to-bracket me-2"></i> Customer Login</a>
                </div>
            </div>
            <div class="col-lg-6 text-center">
                <div class="mb-4">
                    <img src="${pageContext.request.contextPath}/assets/images/sk-bank-banner.png" alt="SK Bank Banner" class="img-fluid rounded-4 shadow-lg border border-2 border-gold" style="max-height: 280px; object-fit: cover; width: 100%;">
                </div>
                <div class="sk-card bg-white text-dark shadow-lg border-gold p-4 text-start rounded-4">
                    <h5 class="fw-bold text-navy mb-3"><i class="fa-solid fa-bolt text-warning me-2"></i> Quick Banking Features</h5>
                    <ul class="list-group list-group-flush mb-3">
                        <li class="list-group-item bg-transparent border-0 px-0"><i class="fa-solid fa-circle-check text-success me-2"></i> Instant Account Opening with ₹0.00 Initial Balance</li>
                        <li class="list-group-item bg-transparent border-0 px-0"><i class="fa-solid fa-circle-check text-success me-2"></i> 24/7 UPI & Account Transfers</li>
                        <li class="list-group-item bg-transparent border-0 px-0"><i class="fa-solid fa-circle-check text-success me-2"></i> Fixed Deposits up to 7.75% Interest</li>
                        <li class="list-group-item bg-transparent border-0 px-0"><i class="fa-solid fa-circle-check text-success me-2"></i> Low Interest Personal & Home Loans</li>
                    </ul>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-primary w-100 fw-bold py-2" style="background-color: #0B4EA2;">Get Started Today</a>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- PERSONAL BANKING -->
<section id="personal" class="py-5 bg-white">
    <div class="container py-4">
        <div class="text-center mb-5">
            <h2 class="fw-bold text-navy">Personal Banking Solutions</h2>
            <p class="text-muted">Tailored savings and current accounts designed for everyday convenience.</p>
        </div>
        <div class="row g-4">
            <div class="col-md-4">
                <div class="sk-card text-center h-100">
                    <div class="text-primary fs-1 mb-3"><i class="fa-solid fa-piggy-bank"></i></div>
                    <h5 class="fw-bold mb-2">Savings Account</h5>
                    <p class="text-muted small">High interest rates with 24/7 digital banking access and instant debit card issuing.</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="sk-card text-center h-100">
                    <div class="text-primary fs-1 mb-3"><i class="fa-solid fa-briefcase"></i></div>
                    <h5 class="fw-bold mb-2">Current Account</h5>
                    <p class="text-muted small">Ideal for businesses and traders with high transaction limits and dedicated manager support.</p>
                </div>
            </div>
            <div class="col-md-4">
                <div class="sk-card text-center h-100">
                    <div class="text-primary fs-1 mb-3"><i class="fa-solid fa-heart-pulse"></i></div>
                    <h5 class="fw-bold mb-2">Senior Citizen Account</h5>
                    <p class="text-muted small">Special high-yield savings account for citizens aged 60 and above with zero balance requirements.</p>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- BUSINESS BANKING -->
<section id="business" class="py-5 bg-light">
    <div class="container py-4">
        <div class="row align-items-center g-5">
            <div class="col-lg-6">
                <h2 class="fw-bold text-navy mb-3">Empowering Local Businesses in Bareilly & Beyond</h2>
                <p class="text-muted mb-4">SK Bank of Bareilly provides customized liquidity solutions, business growth loans, and bulk corporate salary management.</p>
                <div class="d-flex mb-3">
                    <div class="me-3 fs-3 text-warning"><i class="fa-solid fa-chart-line"></i></div>
                    <div>
                        <h6 class="fw-bold mb-1">Corporate Salary Accounts</h6>
                        <p class="text-muted small">Zero-balance salary accounts with premium debit cards and free insurance coverage.</p>
                    </div>
                </div>
                <div class="d-flex">
                    <div class="me-3 fs-3 text-warning"><i class="fa-solid fa-hand-holding-hand"></i></div>
                    <div>
                        <h6 class="fw-bold mb-1">Business Expansion Loans</h6>
                        <p class="text-muted small">Working capital financing up to ₹2 Crore with flexible repayment tenures.</p>
                    </div>
                </div>
            </div>
            <div class="col-lg-6">
                <div class="sk-card bg-navy text-white p-4 rounded-4 shadow">
                    <h4 class="fw-bold text-gold mb-3">Why Partner With SK Bank?</h4>
                    <p class="small opacity-90 mb-4">We combine traditional banking trust with state-of-the-art digital infrastructure for effortless transactions.</p>
                    <div class="row text-center g-3">
                        <div class="col-6">
                            <div class="p-3 bg-white bg-opacity-10 rounded">
                                <h3 class="fw-bold text-gold mb-0">100%</h3>
                                <small>Safe & Encrypted</small>
                            </div>
                        </div>
                        <div class="col-6">
                            <div class="p-3 bg-white bg-opacity-10 rounded">
                                <h3 class="fw-bold text-gold mb-0">4 Branches</h3>
                                <small>Bareilly & NCR</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- LOANS -->
<section id="loans" class="py-5 bg-white">
    <div class="container py-4">
        <div class="text-center mb-5">
            <h2 class="fw-bold text-navy">Flexible Loans with Low Interest Rates</h2>
            <p class="text-muted">Fulfill your dreams with quick loan approvals and easy monthly EMIs.</p>
        </div>
        <div class="row g-4">
            <div class="col-md-3">
                <div class="sk-card h-100 text-center">
                    <div class="text-warning fs-2 mb-2"><i class="fa-solid fa-user"></i></div>
                    <h6 class="fw-bold">Personal Loan</h6>
                    <p class="small text-muted mb-2">Interest from 10.50% p.a.</p>
                    <span class="badge bg-light text-dark">Up to ₹10 Lakhs</span>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card h-100 text-center">
                    <div class="text-warning fs-2 mb-2"><i class="fa-solid fa-house"></i></div>
                    <h6 class="fw-bold">Home Loan</h6>
                    <p class="small text-muted mb-2">Interest from 8.25% p.a.</p>
                    <span class="badge bg-light text-dark">Up to ₹1 Crore</span>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card h-100 text-center">
                    <div class="text-warning fs-2 mb-2"><i class="fa-solid fa-car"></i></div>
                    <h6 class="fw-bold">Car / Vehicle Loan</h6>
                    <p class="small text-muted mb-2">Interest from 8.75% p.a.</p>
                    <span class="badge bg-light text-dark">Up to ₹25 Lakhs</span>
                </div>
            </div>
            <div class="col-md-3">
                <div class="sk-card h-100 text-center">
                    <div class="text-warning fs-2 mb-2"><i class="fa-solid fa-graduation-cap"></i></div>
                    <h6 class="fw-bold">Education Loan</h6>
                    <p class="small text-muted mb-2">Interest from 7.90% p.a.</p>
                    <span class="badge bg-light text-dark">Up to ₹50 Lakhs</span>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- FIXED DEPOSITS -->
<section id="fd" class="py-5 bg-navy text-white" style="background-color: #071F49;">
    <div class="container py-4 text-center">
        <h2 class="fw-bold text-gold mb-3">Grow Your Savings with Fixed Deposits</h2>
        <p class="lead opacity-90 max-w-600 mx-auto mb-4">Earn up to 7.75% per annum on guaranteed return fixed deposits. Flexible tenures from 3 months to 10 years.</p>

        <div class="row justify-content-center g-4 my-3">
            <div class="col-md-3">
                <div class="p-4 bg-white bg-opacity-10 rounded-4 border border-gold">
                    <h4 class="text-gold fw-bold mb-1">6.50%</h4>
                    <span class="small">3 – 12 Months</span>
                </div>
            </div>
            <div class="col-md-3">
                <div class="p-4 bg-white bg-opacity-10 rounded-4 border border-gold">
                    <h4 class="text-gold fw-bold mb-1">7.25%</h4>
                    <span class="small">12 – 36 Months</span>
                </div>
            </div>
            <div class="col-md-3">
                <div class="p-4 bg-white bg-opacity-10 rounded-4 border border-gold">
                    <h4 class="text-gold fw-bold mb-1">7.75%</h4>
                    <span class="small">36+ Months</span>
                </div>
            </div>
        </div>

        <a href="${pageContext.request.contextPath}/register" class="btn btn-gold btn-lg mt-3 fw-bold px-5">Open FD Online</a>
    </div>
</section>

<!-- CONTACT SECTION -->
<section id="contact" class="py-5 bg-white">
    <div class="container py-4">
        <div class="row g-5">
            <div class="col-lg-6">
                <h2 class="fw-bold text-navy mb-3">Contact Us</h2>
                <p class="text-muted mb-4">Have questions regarding opening an account, loans, or online banking? Our team in Bareilly is ready to assist you.</p>
                <div class="mb-3">
                    <i class="fa-solid fa-location-dot text-primary me-2 fs-5"></i>
                    <strong>Main Branch:</strong> Civil Lines, Near Cantonment, Bareilly, UP 243001
                </div>
                <div class="mb-3">
                    <i class="fa-solid fa-phone text-primary me-2 fs-5"></i>
                    <strong>Helpline:</strong> 0581-2550001 / Toll Free 1800-123-SKBANK
                </div>
                <div class="mb-3">
                    <i class="fa-solid fa-envelope text-primary me-2 fs-5"></i>
                    <strong>Email:</strong> support@skbank.com
                </div>
            </div>
            <div class="col-lg-6">
                <div class="sk-card bg-light p-4">
                    <h5 class="fw-bold mb-3">Send a Quick Message</h5>
                    <form onsubmit="alert('Thank you for contacting SK Bank! Our support team will call you back shortly.'); return false;">
                        <div class="mb-3">
                            <label class="form-label small fw-bold">Full Name</label>
                            <input type="text" class="form-control" required placeholder="Enter your name">
                        </div>
                        <div class="mb-3">
                            <label class="form-label small fw-bold">Mobile Number</label>
                            <input type="tel" class="form-control" required placeholder="10-digit mobile number">
                        </div>
                        <div class="mb-3">
                            <label class="form-label small fw-bold">Message</label>
                            <textarea class="form-control" rows="3" required placeholder="How can we help you?"></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary w-100 fw-bold" style="background-color: #0B4EA2;">Submit Query</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
