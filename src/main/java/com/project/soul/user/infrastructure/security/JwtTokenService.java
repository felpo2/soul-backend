package com.project.soul.user.infrastructure.security;

import com.project.soul.user.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtTokenService {

    // Lê a chave secreta do application.properties ou usa uma default segura
    @Value("${api.security.token.secret:sua_chave_secreta_super_longa_e_segura_aqui_123456}")
    private String secret;

    // 7200000ms = 2h. ou seja: o TOKEN de sessão dura 2h
    private final long expirationMillis = 7200000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id", user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateTokenAndGetSubject(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject(); // Retorna o email se o token for valido
        } catch (Exception e) {
            return null; // Token expirado ou errado
        }
    }
}

