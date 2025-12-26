package com.aqtilink.activity_service.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() == null
                ? null
                : SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof Jwt jwt)) {
            throw new RuntimeException("No authenticated user found");
        }

        // Prefer common claims, fallback to subject
        String rawId = firstNonEmpty(
                jwt.getClaimAsString("user_id"),
                jwt.getClaimAsString("uid"),
                jwt.getSubject(),
                jwt.getClaimAsString("sub")
        );

        if (rawId == null || rawId.isEmpty()) {
            throw new RuntimeException("Authenticated user id not present in token");
        }

        return rawId;
    }

    private static String firstNonEmpty(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.isEmpty()) return v;
        }
        return null;
    }
}
