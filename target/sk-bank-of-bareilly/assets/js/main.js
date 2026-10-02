/* ===============================================================
   SK BANK OF BAREILLY - JARS & AJAX SCRIPTS
   =============================================================== */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Splash Screen Dismissal
    const splash = document.getElementById('splash-screen');
    if (splash) {
        setTimeout(function () {
            splash.classList.add('fade-out');
            setTimeout(function () {
                splash.style.display = 'none';
            }, 500);
        }, 1800);
    }

    // 2. Mobile Sidebar Toggle
    const toggleBtn = document.getElementById('sidebar-toggle');
    const sidebar = document.querySelector('.sidebar');
    if (toggleBtn && sidebar) {
        toggleBtn.addEventListener('click', function () {
            sidebar.classList.toggle('show');
        });
    }

    // 3. Recipient Lookup for Send Money page
    const recipientInput = document.getElementById('recipientInput');
    const transferTypeSelect = document.getElementById('transferTypeSelect');
    const lookupResultBox = document.getElementById('lookupResultBox');

    if (recipientInput && lookupResultBox) {
        let timer;
        recipientInput.addEventListener('input', function () {
            clearTimeout(timer);
            const val = recipientInput.value.trim();
            const type = transferTypeSelect ? transferTypeSelect.value : 'ACCOUNT';

            if (val.length < 3) {
                lookupResultBox.style.display = 'none';
                return;
            }

            timer = setTimeout(function () {
                let url = '';
                if (type === 'MOBILE') {
                    url = contextPath + '/customer/recipient/mobile?mobile=' + encodeURIComponent(val);
                } else if (type === 'UPI') {
                    url = contextPath + '/customer/recipient/upi?upiAddress=' + encodeURIComponent(val);
                } else {
                    url = contextPath + '/customer/recipient/account?accountNumber=' + encodeURIComponent(val);
                }

                fetch(url)
                    .then(response => response.json())
                    .then(data => {
                        lookupResultBox.style.display = 'block';
                        if (data.success) {
                            lookupResultBox.className = 'alert alert-success mt-3';
                            document.getElementById('receiverAccountIdInput').value = data.accountId;
                            document.getElementById('recipientNameInput').value = data.recipientName;

                            let html = '<strong>Recipient Found:</strong> ' + data.recipientName;
                            if (data.bankName) html += '<br><small>Bank: ' + data.bankName + ' (' + data.branchName + ')</small>';
                            if (data.ownAccount) html += '<br><span class="badge bg-warning text-dark mt-1">Your Own Account</span>';

                            lookupResultBox.innerHTML = html;
                        } else {
                            lookupResultBox.className = 'alert alert-danger mt-3';
                            lookupResultBox.innerHTML = '<strong>Lookup Failed:</strong> ' + data.message;
                            document.getElementById('receiverAccountIdInput').value = '';
                        }
                    })
                    .catch(err => {
                        console.error(err);
                    });
            }, 400);
        });
    }
});

// EMI Calculator Function
function calculateEmiClient(principalId, rateId, tenureId, resultEmiId, resultTotalId) {
    const P = parseFloat(document.getElementById(principalId).value);
    const annualRate = parseFloat(document.getElementById(rateId).value);
    const N = parseInt(document.getElementById(tenureId).value);

    if (isNaN(P) || isNaN(annualRate) || isNaN(N) || P <= 0 || annualRate <= 0 || N <= 0) {
        return;
    }

    const r = annualRate / (12 * 100);
    const emi = (P * r * Math.pow(1 + r, N)) / (Math.pow(1 + r, N) - 1);
    const totalPayable = emi * N;

    if (document.getElementById(resultEmiId)) {
        document.getElementById(resultEmiId).innerText = '₹' + emi.toFixed(2);
    }
    if (document.getElementById(resultTotalId)) {
        document.getElementById(resultTotalId).innerText = '₹' + totalPayable.toFixed(2);
    }
}
