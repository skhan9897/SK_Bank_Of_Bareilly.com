/**
 * SK BANK OF BAREILLY - GLOBAL PROCESSING LOADER API
 * Handles fullscreen loader overlay, form submissions, navigation links, and AJAX interceptors.
 */
(function () {
    'use strict';

    let activeRequests = 0;
    let showTime = 0;
    const MIN_DISPLAY_MS = 400; // Prevent flickering on fast requests
    const MAX_SAFETY_TIMEOUT_MS = 30000; // 30s auto-dismiss safety timeout
    let safetyTimer = null;
    let hideTimer = null;

    function getLoaderElement() {
        return document.getElementById('bankProcessingLoader');
    }

    function getMessageElement() {
        return document.getElementById('bankLoaderMessage');
    }

    const SKBankLoader = {
        /**
         * Displays the global loader overlay with optional custom message.
         * @param {string} [message] - Message to display (default: "Processing...")
         */
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
                    document.body.style.overflow = 'hidden';
                }
            }

            // Safety timeout to prevent loader getting stuck indefinitely
            if (safetyTimer) clearTimeout(safetyTimer);
            safetyTimer = setTimeout(function () {
                SKBankLoader.hide(true);
            }, MAX_SAFETY_TIMEOUT_MS);
        },

        /**
         * Hides the global loader overlay.
         * @param {boolean} [force=false] - If true, resets active requests counter immediately
         */
        hide: function (force) {
            if (force) {
                activeRequests = 0;
            } else if (activeRequests > 0) {
                activeRequests--;
            }

            if (activeRequests > 0 && !force) {
                return; // Other requests still active
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
                    document.body.style.overflow = '';
                    activeRequests = 0;
                    // Reset form processing flags
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

        /**
         * Returns true if loader is currently visible.
         * @returns {boolean}
         */
        isVisible: function () {
            const loader = getLoaderElement();
            return loader ? loader.classList.contains('active') : false;
        }
    };

    // Expose Global API
    window.SKBankLoader = SKBankLoader;

    // Determine context-appropriate message for form actions
    function getContextualMessage(form) {
        const action = (form.getAttribute('action') || '').toLowerCase();
        const formId = (form.getAttribute('id') || '').toLowerCase();
        const buttonText = (form.querySelector('button[type="submit"], input[type="submit"]')?.textContent || '').toLowerCase();

        if (action.includes('login') || formId.includes('login') || buttonText.includes('login')) {
            return 'Signing you in...';
        }
        if (action.includes('register') || formId.includes('register') || buttonText.includes('register') || buttonText.includes('open account')) {
            return 'Creating your bank account...';
        }
        if (action.includes('transfer') || buttonText.includes('transfer') || buttonText.includes('send')) {
            return 'Processing your transfer...';
        }
        if (action.includes('withdraw') || buttonText.includes('withdraw')) {
            return 'Processing withdrawal...';
        }
        if (action.includes('deposit') || buttonText.includes('deposit')) {
            return 'Processing deposit...';
        }
        if (action.includes('upi') || buttonText.includes('upi')) {
            return 'Processing UPI request...';
        }
        if (action.includes('fd') || buttonText.includes('fixed deposit')) {
            return 'Creating your fixed deposit...';
        }
        if (action.includes('loan') || buttonText.includes('loan')) {
            return 'Submitting loan application...';
        }
        if (action.includes('bill') || buttonText.includes('bill') || buttonText.includes('recharge')) {
            return 'Processing bill payment...';
        }
        if (action.includes('statement') || buttonText.includes('statement')) {
            return 'Generating statement...';
        }
        if (action.includes('profile') || buttonText.includes('profile')) {
            return 'Updating profile...';
        }
        if (action.includes('security') || buttonText.includes('password')) {
            return 'Updating security settings...';
        }
        return 'Processing your request...';
    }

    // 1. FORM SUBMISSION INTEGRATION
    document.addEventListener('submit', function (event) {
        const form = event.target;
        if (!form || form.tagName !== 'FORM') return;

        // Respect HTML5 / Bootstrap validation
        if (typeof form.checkValidity === 'function' && !form.checkValidity()) {
            return; // Let browser validation errors display
        }

        // Prevent duplicate submissions
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
                submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-2"></i> Please wait...';
            }
        }

        const msg = getContextualMessage(form);
        SKBankLoader.show(msg);
    });

    // 2. INTERNAL NAVIGATION LINKS INTEGRATION
    document.addEventListener('click', function (event) {
        const link = event.target.closest('a');
        if (!link) return;

        const href = link.getAttribute('href');
        if (!href) return;

        // Skip non-navigational links
        if (href.startsWith('javascript:') ||
            href.startsWith('#') ||
            href.startsWith('mailto:') ||
            href.startsWith('tel:') ||
            link.getAttribute('data-bs-toggle') ||
            link.getAttribute('data-toggle') ||
            link.hasAttribute('download') ||
            link.getAttribute('target') === '_blank') {
            return;
        }

        // Check if external link
        try {
            const url = new URL(link.href, window.location.origin);
            if (url.origin !== window.location.origin) return;
        } catch (e) {
            return;
        }

        const linkText = link.textContent.trim().toLowerCase();
        let msg = 'Loading...';

        if (linkText.includes('logout') || href.includes('logout')) {
            msg = 'Signing you out...';
        } else if (linkText.includes('dashboard') || href.includes('dashboard')) {
            msg = 'Loading dashboard...';
        } else if (linkText.includes('account') || href.includes('account')) {
            msg = 'Loading account details...';
        } else if (linkText.includes('statement') || href.includes('statement')) {
            msg = 'Preparing statement...';
        }

        SKBankLoader.show(msg);
    });

    // 3. FETCH API INTERCEPTOR
    if (window.fetch) {
        const originalFetch = window.fetch;
        window.fetch = function () {
            SKBankLoader.show('Processing request...');
            return originalFetch.apply(this, arguments)
                .then(function (response) {
                    SKBankLoader.hide();
                    return response;
                })
                .catch(function (error) {
                    SKBankLoader.hide();
                    throw error;
                });
        };
    }

    // 4. XHR / AJAX INTERCEPTOR
    if (window.XMLHttpRequest) {
        const originalOpen = XMLHttpRequest.prototype.open;
        const originalSend = XMLHttpRequest.prototype.send;

        XMLHttpRequest.prototype.open = function () {
            this._bankLoaderTrack = true;
            return originalOpen.apply(this, arguments);
        };

        XMLHttpRequest.prototype.send = function () {
            if (this._bankLoaderTrack) {
                SKBankLoader.show('Processing request...');
                this.addEventListener('readystatechange', function () {
                    if (this.readyState === 4) {
                        SKBankLoader.hide();
                    }
                });
            }
            return originalSend.apply(this, arguments);
        };
    }

    // 5. PAGE LIFECYCLE & BFCACHE RESET
    window.addEventListener('pageshow', function (event) {
        // Reset loader when page is restored from back/forward cache
        SKBankLoader.hide(true);
    });

    window.addEventListener('popstate', function () {
        SKBankLoader.hide(true);
    });

})();
