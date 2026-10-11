/**
 * SK BANK OF BAREILLY - SMOOTH GLOBAL BANKING LOADER JS
 * Activates smoothly on form submissions and secure payment processing.
 */
(function () {
    'use strict';

    let activeRequests = 0;
    let showTime = 0;
    const MIN_DISPLAY_MS = 300;
    const MAX_SAFETY_TIMEOUT_MS = 20000;
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
                msgEl.textContent = message || 'Processing Secure Banking Request...';
            }

            if (loader) {
                if (!loader.classList.contains('active')) {
                    showTime = Date.now();
                    loader.classList.add('active');
                    loader.setAttribute('aria-hidden', 'false');
                    document.body.classList.add('sk-bank-loading');
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
                }
            }, remainingTime);
        },

        isVisible: function () {
            const loader = getLoaderElement();
            return loader ? loader.classList.contains('active') : false;
        }
    };

    window.SKBankLoader = SKBankLoader;

    // 1. FORM SUBMISSION LOADER
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

        SKBankLoader.show('Processing Secure Banking Request...');
    });

    // 2. FETCH / XHR API INTERCEPTOR (Ignoring background API sync)
    if (window.fetch) {
        const originalFetch = window.fetch;
        window.fetch = function (url, options) {
            const urlStr = typeof url === 'string' ? url : (url && url.url ? url.url : '');
            const isBackground = urlStr.includes('/api/') || (options && (options.noLoader || options.background));

            if (!isBackground) {
                SKBankLoader.show('Processing request...');
            }

            return originalFetch.apply(this, arguments)
                .then(function (response) {
                    if (!isBackground) {
                        SKBankLoader.hide();
                    }
                    return response;
                })
                .catch(function (error) {
                    if (!isBackground) {
                        SKBankLoader.hide();
                    }
                    throw error;
                });
        };
    }

    window.addEventListener('pageshow', function () {
        SKBankLoader.hide(true);
    });

    window.addEventListener('popstate', function () {
        SKBankLoader.hide(true);
    });

})();
