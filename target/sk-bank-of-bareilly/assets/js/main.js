/* =========================================================
   SK BANK OF BAREILLY - MAIN JAVASCRIPT & MOBILE NAVIGATION
   ========================================================= */

document.addEventListener('DOMContentLoaded', function () {
    // Mobile Hamburger Menu & Off-Canvas Sidebar Setup
    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    const sidebar = document.querySelector('.sidebar');
    const sidebarOverlay = document.getElementById('sidebarOverlay');

    if (mobileMenuBtn && sidebar) {
        mobileMenuBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            sidebar.classList.toggle('active');
            if (sidebarOverlay) {
                sidebarOverlay.classList.toggle('active');
            }
        });
    }

    if (sidebarOverlay && sidebar) {
        sidebarOverlay.addEventListener('click', function () {
            sidebar.classList.remove('active');
            sidebarOverlay.classList.remove('active');
        });
    }

    // Auto-close mobile sidebar when clicking a nav link on mobile screens
    if (sidebar) {
        const navLinks = sidebar.querySelectorAll('.nav-link');
        navLinks.forEach(link => {
            link.addEventListener('click', function () {
                if (window.innerWidth < 768) {
                    sidebar.classList.remove('active');
                    if (sidebarOverlay) {
                        sidebarOverlay.classList.remove('active');
                    }
                }
            });
        });
    }
});

// CSV Export Helper Function
function exportTableToCSV(filename) {
    const table = document.querySelector('table');
    if (!table) return;

    let csv = [];
    const rows = table.querySelectorAll('tr');

    for (let i = 0; i < rows.length; i++) {
        let row = [], cols = rows[i].querySelectorAll('td, th');
        for (let j = 0; j < cols.length; j++) {
            // Clean inner text
            let data = cols[j].innerText.replace(/(\r\n|\n|\r)/gm, '').replace(/(\s\s+)/gm, ' ');
            data = data.replace(/"/g, '""');
            row.push('"' + data + '"');
        }
        csv.push(row.join(','));
    }

    // Download CSV file
    const csvFile = new Blob([csv.join('\n')], { type: 'text/csv' });
    const downloadLink = document.createElement('a');
    downloadLink.download = filename;
    downloadLink.href = window.URL.createObjectURL(csvFile);
    downloadLink.style.display = 'none';
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
}

// EMI Calculator Helper Function
function calculateEmi() {
    const amountInput = document.getElementById('emiAmount');
    const rateInput = document.getElementById('emiRate');
    const tenureInput = document.getElementById('emiTenure');

    if (!amountInput || !rateInput || !tenureInput) return;

    const P = parseFloat(amountInput.value) || 0;
    const annualRate = parseFloat(rateInput.value) || 0;
    const N = parseInt(tenureInput.value) || 0;

    if (P <= 0 || annualRate <= 0 || N <= 0) return;

    const R = (annualRate / 12) / 100;
    const emi = (P * R * Math.pow(1 + R, N)) / (Math.pow(1 + R, N) - 1);
    const totalPayable = emi * N;
    const totalInterest = totalPayable - P;

    const emiResultEmi = document.getElementById('emiResultEmi');
    const emiResultInterest = document.getElementById('emiResultInterest');
    const emiResultTotal = document.getElementById('emiResultTotal');

    if (emiResultEmi) emiResultEmi.innerText = '₹ ' + emi.toLocaleString('en-IN', { maximumFractionDigits: 2 });
    if (emiResultInterest) emiResultInterest.innerText = '₹ ' + totalInterest.toLocaleString('en-IN', { maximumFractionDigits: 2 });
    if (emiResultTotal) emiResultTotal.innerText = '₹ ' + totalPayable.toLocaleString('en-IN', { maximumFractionDigits: 2 });
}
