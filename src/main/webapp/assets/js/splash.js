/* =========================================================
   SK BANK OF BAREILLY - SPLASH SCREEN CONTROLLER & ROUTING
   ========================================================= */

document.addEventListener('DOMContentLoaded', function () {
    const splashScreen = document.getElementById('splashScreen');
    if (!splashScreen) return;

    // Minimum splash animation duration = 2000ms
    const MIN_SPLASH_TIME = 2000;
    const startTime = Date.now();

    // Check session status via lightweight auth check endpoint
    fetch(window.location.origin + window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) + '/auth-check')
        .then(response => response.json())
        .then(data => {
            handleNavigation(data);
        })
        .catch(err => {
            console.log('Splash Session Check Fallback:', err);
            handleNavigation({ authenticated: false, role: 'NONE' });
        });

    function handleNavigation(authData) {
        const elapsedTime = Date.now() - startTime;
        const remainingTime = Math.max(0, MIN_SPLASH_TIME - elapsedTime);

        setTimeout(() => {
            // Add fade-out class
            splashScreen.classList.add('splash-hide');

            // Redirect after 500ms fade-out
            setTimeout(() => {
                const contextPath = window.location.origin + window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));

                if (authData && authData.authenticated) {
                    if (authData.role === 'ADMIN') {
                        window.location.href = contextPath + '/admin/dashboard';
                    } else {
                        window.location.href = contextPath + '/dashboard';
                    }
                } else {
                    window.location.href = contextPath + '/login';
                }
            }, 500);

        }, remainingTime);
    }
});
