/**
 * SK BANK OF BAREILLY - PERMANENTLY DISABLED GLOBAL LOADER OVERLAY
 * Keeps window.SKBankLoader API available as a safe no-op to prevent JS errors.
 */
(function () {
    'use strict';

    const SKBankLoader = {
        show: function () {
            // Disabled permanently for instant 0ms response
        },
        hide: function () {
            // Disabled permanently
        },
        isVisible: function () {
            return false;
        }
    };

    window.SKBankLoader = SKBankLoader;
})();
