package com.example.chattest.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private final long validityInMilliseconds = 3600000; // 1시간

    @PostConstruct
    protected void init() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256).toString();
    }

    public String createToken(String userId, String role) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("role", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
        } catch (io.jsonwebtoken.SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    public String getUserIdFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String getRoleFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("role", String.class);
    }
}
