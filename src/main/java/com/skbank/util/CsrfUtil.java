package com.skbank.util;

import java.security.SecureRandom;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CsrfUtil {
    public static final String CSRF_SESSION_KEY = "CSRF_TOKEN";
    public static final String CSRF_PARAM_NAME = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String getToken(HttpSession session) {
        if (session == null) return null;
        String token = (String) session.getAttribute(CSRF_SESSION_KEY);
        if (token == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(CSRF_SESSION_KEY, token);
        }
        return token;
    }

    public static boolean isValidToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        String sessionToken = (String) session.getAttribute(CSRF_SESSION_KEY);
        if (sessionToken == null) return false;

        String requestToken = request.getParameter(CSRF_PARAM_NAME);
        if (requestToken == null) {
            requestToken = request.getHeader("X-CSRF-TOKEN");
        }
        return sessionToken.equals(requestToken);
    }
}
