package com.pointers.ices4hu.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtUtils {
    @Value("${ices4hu.security.token_prefix}")
    private String TOKEN_PREFIX;

    public String fetchJwtTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = fetchRequestHeaderFromRequest(request, "Authorization");
        return fetchJwtTokenFromRequestHeader(authorizationHeader);
    }

    private String fetchJwtTokenFromRequestHeader(String header) {
        if (StringUtils.hasText(header) && header.startsWith(TOKEN_PREFIX + " ")) {
            return header.substring(TOKEN_PREFIX.length() + 1);
        }
        return null;
    }

    private String fetchRequestHeaderFromRequest(HttpServletRequest request, String headerKey) {
        return request.getHeader(headerKey);
    }

}
