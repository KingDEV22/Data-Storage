package com.data.organization.configration.jwt;

import java.time.LocalDate;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.data.organization.model.AppUserDetails;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class JwtUtil {
    private String email;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public String generateJwtToken(Authentication authentication, SecretKey secretKey, Date expiry) {
        AppUserDetails userPrincipal = (AppUserDetails) authentication.getPrincipal();
        logger.info("token genererated.");
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .claim("authority", userPrincipal.getAuthorities())
                .setIssuedAt(java.sql.Date.valueOf(LocalDate.now()))
                .setExpiration(expiry)
                .signWith(secretKey)
                .compact();

    }

    public String getUserNameFromJwtToken(String token, SecretKey secretKey) {
        logger.info("username generated from token.");
        email = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        return email;
    }

    public String getEmail() {
        return email;
    }

    public boolean validateJwtToken(String authToken, SecretKey secretKey) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            logger.info("token validated.");
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
