<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="SK Bank of Bareilly - Official Banking Portal" />
</jsp:include>
<body>

    <!-- PREMIUM CORPORATE BANKING SPLASH SCREEN -->
    <div id="splashScreen" class="splash-screen">
        <div class="splash-background"></div>

        <div class="splash-content">
            <div class="logo-wrapper">
                <img src="${pageContext.request.contextPath}/images/sk-bank-logo-transparent.png"
                     alt="SK Bank of Bareilly"
                     class="splash-logo"
                     onerror="this.style.display='none'; document.getElementById('logoFallback').style.display='block';">
                <h1 id="logoFallback" class="display-3 fw-bold text-warning" style="display: none;">SK</h1>
            </div>

            <h1 class="bank-name">SK BANK OF BAREILLY</h1>
            <p class="bank-tagline">TRUST | GROWTH | TOGETHER</p>

            <div class="gold-line"></div>

            <div class="loading-indicator">
                <span></span>
                <span></span>
                <span></span>
            </div>
        </div>

        <div class="security-text">
            Secure • Simple • Trusted
        </div>
    </div>

    <!-- Splash Screen Script -->
    <script src="${pageContext.request.contextPath}/assets/js/splash.js"></script>

</body>
</html>
