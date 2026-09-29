/* =========================================================
   SK BANK OF BAREILLY - JAVASCRIPT & AJAX HELPERS
   ========================================================= */

document.addEventListener('DOMContentLoaded', function () {
    console.log("SK Bank of Bareilly Portal Initialized.");

    // Password Match Validation in Register
    const passInput = document.getElementById('regPassword');
    const confirmInput = document.getElementById('regConfirmPassword');
    const matchAlert = document.getElementById('passwordMatchAlert');

    if (passInput && confirmInput && matchAlert) {
        confirmInput.addEventListener('input', function () {
            if (passInput.value !== confirmInput.value) {
                matchAlert.classList.remove('d-none');
            } else {
                matchAlert.classList.add('d-none');
            }
        });
    }
});

// EMI Calculator Function
function calculateEmi() {
    const principal = parseFloat(document.getElementById('emiAmount').value) || 0;
    const rate = parseFloat(document.getElementById('emiRate').value) || 0;
    const months = parseInt(document.getElementById('emiTenure').value) || 0;

    if (principal <= 0 || rate <= 0 || months <= 0) {
        document.getElementById('emiResultEmi').innerText = '₹ 0.00';
        document.getElementById('emiResultTotal').innerText = '₹ 0.00';
        document.getElementById('emiResultInterest').innerText = '₹ 0.00';
        return;
    }

    const monthlyRate = rate / (12 * 100);
    const emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, months)) / (Math.pow(1 + monthlyRate, months) - 1);
    const totalPayment = emi * months;
    const totalInterest = totalPayment - principal;

    document.getElementById('emiResultEmi').innerText = '₹ ' + emi.toLocaleString('en-IN', { maximumFractionDigits: 2 });
    document.getElementById('emiResultTotal').innerText = '₹ ' + totalPayment.toLocaleString('en-IN', { maximumFractionDigits: 2 });
    document.getElementById('emiResultInterest').innerText = '₹ ' + totalInterest.toLocaleString('en-IN', { maximumFractionDigits: 2 });
}

// Fixed Deposit Calculator
function calculateFD() {
    const amount = parseFloat(document.getElementById('fdAmount').value) || 0;
    const months = parseInt(document.getElementById('fdTenure').value) || 12;

    let rate = 6.0;
    if (months >= 12 && months < 24) rate = 6.75;
    else if (months >= 24 && months < 36) rate = 7.10;
    else if (months >= 36) rate = 7.50;

    const years = months / 12.0;
    const maturity = amount * Math.pow((1 + (rate / 100.0) / 4.0), 4.0 * years);
    const interest = maturity - amount;

    if (document.getElementById('fdRateDisplay')) {
        document.getElementById('fdRateDisplay').innerText = rate.toFixed(2) + ' %';
    }
    if (document.getElementById('fdMaturityDisplay')) {
        document.getElementById('fdMaturityDisplay').innerText = '₹ ' + maturity.toLocaleString('en-IN', { maximumFractionDigits: 2 });
    }
    if (document.getElementById('fdInterestDisplay')) {
        document.getElementById('fdInterestDisplay').innerText = '₹ ' + interest.toLocaleString('en-IN', { maximumFractionDigits: 2 });
    }
}

// Print Bank Statement
function printStatement() {
    window.print();
}

// Export Table to CSV
function exportTableToCSV(filename) {
    const table = document.querySelector(".table-custom");
    if (!table) return;

    let csv = [];
    const rows = table.querySelectorAll("tr");

    for (let i = 0; i < rows.length; i++) {
        let row = [], cols = rows[i].querySelectorAll("td, th");
        for (let j = 0; j < cols.length; j++) {
            row.push('"' + cols[j].innerText.trim() + '"');
        }
        csv.push(row.join(","));
    }

    const csvFile = new Blob([csv.join("\n")], { type: "text/csv" });
    const downloadLink = document.createElement("a");
    downloadLink.download = filename;
    downloadLink.href = window.URL.createObjectURL(csvFile);
    downloadLink.style.display = "none";
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
}
