package com.epam.esm.webservice.security;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@Log4j2
public class JwtProvider {

    private static final long VALIDITY_IN_MILLISECONDS = 60 * 1000 * 15; // 15 minutes
    private static final String JWT_SECRET = "secret-key-for-encryption";
    private static final String AUTHORITY_KEY = "authority";

    private static final String ROLE_KEY = "role";

    public String generateToken(String login, String role) {
        Claims claims = Jwts.claims().setSubject(login);
        claims.put(ROLE_KEY, new SimpleGrantedAuthority(role));
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + VALIDITY_IN_MILLISECONDS))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired");
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt");
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt");
        } catch (SignatureException sEx) {
            log.error("Invalid signature");
        } catch (Exception e) {
            log.error("invalid token");
        }
        return false;
    }

    public String getLogin(String token) {
        Claims claims = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public GrantedAuthority getRole(String token) {
        Map<String, String> claims = (Map<String, String>) Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody()
                .get(ROLE_KEY);
        return new SimpleGrantedAuthority(claims.get(AUTHORITY_KEY));
    }
}
