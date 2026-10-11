/**
 * SK BANK OF BAREILLY - PREMIUM GLOBAL BANKING LOADER JS
 * Provides window.SKBankLoader API for explicit forms and payment processing.
 */
(function () {
    'use strict';

    let activeRequests = 0;
    let showTime = 0;
    const MIN_DISPLAY_MS = 200;
    const MAX_SAFETY_TIMEOUT_MS = 15000;
    let safetyTimer = null;
    let hideTimer = null;

    function getLoaderElement() {
        return document.getElementById('skBankGlobalLoader');
    }

    function getMessageElement() {
        return document.getElementById('skLoaderMessage');
    }

    const SKBankLoader = {
        show: function (message) {
            activeRequests++;
            const loader = getLoaderElement();
            const msgEl = getMessageElement();

            if (msgEl) {
                msgEl.textContent = message || 'Processing your request...';
            }

            if (loader) {
                if (!loader.classList.contains('active')) {
                    showTime = Date.now();
                    loader.classList.add('active');
                    loader.setAttribute('aria-hidden', 'false');
                }
            }

            if (safetyTimer) clearTimeout(safetyTimer);
            safetyTimer = setTimeout(function () {
                SKBankLoader.hide(true);
            }, MAX_SAFETY_TIMEOUT_MS);
        },

        hide: function (force) {
            if (force) {
                activeRequests = 0;
            } else if (activeRequests > 0) {
                activeRequests--;
            }

            if (activeRequests > 0 && !force) {
                return;
            }

            const loader = getLoaderElement();
            if (!loader) return;

            const elapsedTime = Date.now() - showTime;
            const remainingTime = Math.max(0, MIN_DISPLAY_MS - elapsedTime);

            if (hideTimer) clearTimeout(hideTimer);
            if (safetyTimer) clearTimeout(safetyTimer);

            hideTimer = setTimeout(function () {
                if (activeRequests <= 0 || force) {
                    loader.classList.remove('active');
                    loader.setAttribute('aria-hidden', 'true');
                    document.body.classList.remove('sk-bank-loading');
                    activeRequests = 0;

                    document.querySelectorAll('form[data-processing="true"]').forEach(function (form) {
                        form.dataset.processing = 'false';
                        const submitBtn = form.querySelector('button[type="submit"], input[type="submit"]');
                        if (submitBtn) {
                            submitBtn.disabled = false;
                        }
                    });
                }
            }, remainingTime);
        },

        isVisible: function () {
            const loader = getLoaderElement();
            return loader ? loader.classList.contains('active') : false;
        }
    };

    window.SKBankLoader = SKBankLoader;

    // 1. FORM SUBMISSIONS ONLY (e.g. Transfers, Payments, Login)
    document.addEventListener('submit', function (event) {
        const form = event.target;
        if (!form || form.tagName !== 'FORM') return;

        if (typeof form.checkValidity === 'function' && !form.checkValidity()) {
            return;
        }

        if (form.dataset.processing === 'true') {
            event.preventDefault();
            return;
        }

        form.dataset.processing = 'true';

        const submitBtn = form.querySelector('button[type="submit"], input[type="submit"]');
        if (submitBtn) {
            submitBtn.disabled = true;
            if (submitBtn.tagName === 'BUTTON') {
                submitBtn.dataset.originalHtml = submitBtn.innerHTML;
                submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-2"></i> Processing...';
            }
        }
    });

    // 2. PAGE LIFECYCLE RESET
    window.addEventListener('pageshow', function () {
        SKBankLoader.hide(true);
    });

    window.addEventListener('popstate', function () {
        SKBankLoader.hide(true);
    });

})();
