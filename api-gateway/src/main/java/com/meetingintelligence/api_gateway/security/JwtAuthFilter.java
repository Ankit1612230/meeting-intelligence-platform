package com.meetingintelligence.api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final SecretKey key;

    public JwtAuthFilter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        // Browser preflight and login/register need no token
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())
                || req.getRequestURI().startsWith("/auth-service/api/auth/")) {
            chain.doFilter(new IdentityRequest(req, null, null), res);
            return;
        }

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            unauthorized(res);
            return;
        }

        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(header.substring(7)).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            unauthorized(res);
            return;
        }

        chain.doFilter(new IdentityRequest(req, claims.getSubject(),
                claims.get("role", String.class)), res);
    }

    private void unauthorized(HttpServletResponse res) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter().write("{\"status\":401,\"detail\":\"Invalid or missing token\"}");
    }

    // Removes any X-User-* headers the client sent, then adds the verified ones
    private static class IdentityRequest extends HttpServletRequestWrapper {

        private final Map<String, String> identity = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        IdentityRequest(HttpServletRequest req, String email, String role) {
            super(req);
            if (email != null) {
                identity.put("X-User-Email", email);
                identity.put("X-User-Role", role);
            }
        }

        private boolean isIdentityHeader(String name) {
            return name.equalsIgnoreCase("X-User-Email") || name.equalsIgnoreCase("X-User-Role");
        }

        @Override
        public String getHeader(String name) {
            return isIdentityHeader(name) ? identity.get(name) : super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (!isIdentityHeader(name)) return super.getHeaders(name);
            String value = identity.get(name);
            return value == null ? Collections.emptyEnumeration()
                    : Collections.enumeration(List.of(value));
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new LinkedHashSet<>();
            for (String n : Collections.list(super.getHeaderNames())) {
                if (!isIdentityHeader(n)) names.add(n);
            }
            names.addAll(identity.keySet());
            return Collections.enumeration(names);
        }
    }
}