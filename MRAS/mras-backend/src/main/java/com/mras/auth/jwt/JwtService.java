package com.mras.auth.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mras.common.enums.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey key;
    private final int expMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expMinutes:120}") int expMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expMinutes = expMinutes;
    }

    public String generateToken(String userId, String email, Set<Role> roles) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expMinutes * 60L);

        return Jwts.builder()
                .subject(userId)
                .issuer("mras")
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("email", email)
                .claim("roles", roles.stream().map(Enum::name).collect(Collectors.toList()))
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            Claims c = parseClaims(token);
            return c.getExpiration() != null && c.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
