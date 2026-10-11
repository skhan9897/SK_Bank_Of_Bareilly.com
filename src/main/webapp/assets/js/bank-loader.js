/**
 * SK BANK OF BAREILLY - PERMANENTLY DISABLED GLOBAL LOADER OVERLAY
 * Keeps window.SKBankLoader API available as a safe no-op.
 */
(function () {
    'use strict';

    const SKBankLoader = {
        show: function () {},
        hide: function () {},
        isVisible: function () { return false; }
    };

    window.SKBankLoader = SKBankLoader;
})();
