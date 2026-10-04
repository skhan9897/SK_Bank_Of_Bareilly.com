<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Digital Passbook Print | SK BANK OF BAREILLY</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <style>
        body { background: #f8fafc; font-family: 'Inter', sans-serif; color: #1e293b; }
        .passbook-print-container { max-width: 800px; margin: 30px auto; background: #fff; padding: 40px; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,0.1); border: 1px solid #d4a72c; }
        .passbook-header { background: #071f49; color: #fff; padding: 20px; border-radius: 12px; border-bottom: 3px solid #d4a72c; }
        .passbook-title { color: #f4d477; font-weight: 700; }
        .table-label { background: #f1f5f9; font-weight: 600; width: 35%; }
        @media print {
            .no-print { display: none !important; }
            .passbook-print-container { box-shadow: none; border: 1px solid #000; margin: 0; max-width: 100%; width: 100%; }
            body { background: #fff; }
        }
    </style>
</head>
<body>

<div class="container no-print text-center my-3">
    <button onclick="window.print()" class="btn btn-warning fw-bold px-4 me-2"><i class="fa-solid fa-print me-2"></i> Print Passbook</button>
    <a href="${pageContext.request.contextPath}/customer/passbook/pdf?customerId=${passbook.customerId}" class="btn btn-navy text-white fw-bold px-4 me-2" style="background:#071f49"><i class="fa-solid fa-file-pdf me-2"></i> Download PDF</a>
    <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-secondary fw-bold px-4"><i class="fa-solid fa-right-to-bracket me-2"></i> Go To Login</a>
</div>

<div class="passbook-print-container">
    <div class="passbook-header text-center mb-4">
        <img src="${pageContext.request.contextPath}/assets/images/sk-bank-logo-final-transparent.png?v=20261004" alt="SK Bank" width="65" class="mb-2">
        <h3 class="fw-bold mb-0">SK BANK OF BAREILLY</h3>
        <p class="passbook-title mb-0 small">DIGITAL PASSBOOK &amp; ACCOUNT ADVICE</p>
        <span class="badge bg-gold text-dark mt-2 px-3 py-1">TRUST | GROWTH | TOGETHER</span>
    </div>

    <h5 class="fw-bold text-navy mb-3 border-bottom pb-2"><i class="fa-solid fa-user me-2 text-warning"></i> Customer Information</h5>
    <table class="table table-bordered mb-4">
        <tbody>
            <tr><td class="table-label">Customer Name</td><td class="fw-bold">${passbook.customerName}</td></tr>
            <tr><td class="table-label">Customer ID / Number</td><td class="fw-bold text-navy">${passbook.customerNumber}</td></tr>
            <tr><td class="table-label">Date of Birth</td><td>${passbook.dateOfBirth}</td></tr>
            <tr><td class="table-label">Gender</td><td>${passbook.gender}</td></tr>
            <tr><td class="table-label">Mobile (Masked)</td><td>${passbook.maskedMobile}</td></tr>
            <tr><td class="table-label">Email Address</td><td>${passbook.email}</td></tr>
            <tr><td class="table-label">Residential Address</td><td>${passbook.address}, ${passbook.city}, ${passbook.state} - ${passbook.pincode}</td></tr>
        </tbody>
    </table>

    <h5 class="fw-bold text-navy mb-3 border-bottom pb-2"><i class="fa-solid fa-building-columns me-2 text-warning"></i> Account Information</h5>
    <table class="table table-bordered mb-4">
        <tbody>
            <tr><td class="table-label">Account Number</td><td class="fw-bold text-navy">${passbook.maskedAccountNumber}</td></tr>
            <tr><td class="table-label">Account Type</td><td>${passbook.accountType}</td></tr>
            <tr><td class="table-label">Branch Name</td><td>${passbook.branchName} (${passbook.branchCode})</td></tr>
            <tr><td class="table-label">IFSC Code</td><td class="fw-bold">${passbook.ifscCode}</td></tr>
            <tr><td class="table-label">Account Status</td><td><span class="badge bg-success">${passbook.accountStatus}</span></td></tr>
            <tr><td class="table-label">Current Balance</td><td class="fw-bold text-success">₹${passbook.balance}</td></tr>
            <tr><td class="table-label">Available Balance</td><td class="fw-bold text-success">₹${passbook.availableBalance}</td></tr>
        </tbody>
    </table>

    <h5 class="fw-bold text-navy mb-3 border-bottom pb-2"><i class="fa-solid fa-id-card me-2 text-warning"></i> KYC Details</h5>
    <table class="table table-bordered mb-4">
        <tbody>
            <tr><td class="table-label">KYC Status</td><td><span class="badge bg-info text-dark">${passbook.kycStatus}</span></td></tr>
            <tr><td class="table-label">Aadhaar (Masked)</td><td>${passbook.maskedAadhaar}</td></tr>
            <tr><td class="table-label">PAN (Masked)</td><td>${passbook.maskedPan}</td></tr>
        </tbody>
    </table>

    <div class="text-center text-muted small mt-4 pt-3 border-top">
        This is a computer-generated digital passbook issued by SK Bank of Bareilly. No physical signature required.
    </div>
</div>

</body>
</html>
