package com.mras.common.util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {

    private RequestUtil() {}

    public static String getClientIp(HttpServletRequest req) {
        // If later you put Nginx/Proxy, this helps
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }

    public static String getUserAgent(HttpServletRequest req) {
        String ua = req.getHeader("User-Agent");
        return (ua == null) ? null : ua;
    }
}
