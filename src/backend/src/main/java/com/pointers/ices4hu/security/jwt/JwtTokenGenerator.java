package com.pointers.ices4hu.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtTokenGenerator {

    @Value("${ices4hu.security.secret_key}")
    private String SECRET_KEY;

    @Value("${ices4hu.security.expires_in}")
    private long EXPIRES_IN;

    public String generateJwtToken(Authentication authentication) {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        Date expiresAfter = new Date(new Date().getTime() + EXPIRES_IN * 1000);
        return Jwts.builder()
                .setSubject(jwtUserDetails.getLoginID())
                .setIssuedAt(new Date())
                .setExpiration(expiresAfter)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String extractLoginIDFromJwtToken(String jwtToken) {
        Claims claims = parseClaimsJWS(jwtToken)
                .getBody();

        return claims.getSubject();
    }

    public boolean isTokenValid(String jwtToken) {
        try {
            parseClaimsJWS(jwtToken);
            return !isTokenExpired(jwtToken);
        } catch (Exception e) {
            return false;
        }
    }

    private io.jsonwebtoken.Jws<io.jsonwebtoken.Claims> parseClaimsJWS(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(jwtToken);
    }

    private boolean isTokenExpired(String jwtToken) {
        Date expirationDate = parseClaimsJWS(jwtToken).getBody().getExpiration();
        return new Date().after(expirationDate);
    }



}
