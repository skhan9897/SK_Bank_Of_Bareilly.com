/**
 * SK BANK OF BAREILLY - GLOBAL PROCESSING LOADER API
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
        return document.getElementById('bankProcessingLoader');
    }

    function getMessageElement() {
        return document.getElementById('bankLoaderMessage');
    }

    const SKBankLoader = {
        show: function (message) {
            activeRequests++;
            const loader = getLoaderElement();
            const msgEl = getMessageElement();

            if (msgEl) {
                msgEl.textContent = message || 'Processing...';
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

    // PAGE LIFECYCLE RESET
    window.addEventListener('pageshow', function () {
        SKBankLoader.hide(true);
    });

    window.addEventListener('popstate', function () {
        SKBankLoader.hide(true);
    });

})();
