package com.flowfund.engine.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {
    // 🛡️ Standardized Secret Key for Persistent Identity Handshakes
    private final String SECRET = "v3ry_s3cr3t_fl0w_fund_s3cur1ty_k3y_2026_premium_os";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private final long expiration = 86400000; // 24h

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null; // Identity establishes failure
        }
    }

    public boolean validateToken(String token, String email) {
        String extracted = extractEmail(token);
        return extracted != null && extracted.equals(email);
    }
}
