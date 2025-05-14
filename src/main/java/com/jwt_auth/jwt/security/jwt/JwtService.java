package com.jwt_auth.jwt.security.jwt;

import com.jwt_auth.jwt.dto.JwtAuthenticationDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtsecret;

    private static final Logger logger = LogManager.getLogger(JwtService.class);

    //Оба токена не валиды, происходит генерации обоих
    public JwtAuthenticationDto generateAuthToken(String email) {
        JwtAuthenticationDto jwtDTO = new JwtAuthenticationDto();
        jwtDTO.setToken(generateJwtToken(email));
        jwtDTO.setRefreshToken(generateRefreshToken(email));
        return jwtDTO;
    }

    public JwtAuthenticationDto refreshBaseToken(String email, String refreshToken) {
        JwtAuthenticationDto jwtDTO = new JwtAuthenticationDto();
        jwtDTO.setRefreshToken(refreshToken);
        jwtDTO.setToken(generateJwtToken(email));
        return jwtDTO;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
//                .parseEncryptedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
//                    .verifyWith(getSingInKey())
//                    .build()
//                    .parseEncryptedClaims(token)
//                    .getPayload();
                    .verifyWith(getSingInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Expired", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported", e);
        } catch (MalformedJwtException e) {
            logger.error("Malformed", e);
        } catch (SecurityException e) {
            logger.error("Security", e);
        } catch (Exception e) {
            logger.error("Invalid token", e);
        }
        return false;
    }


    //Временный токен (генерируется за счет постоянного)
    private String generateJwtToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusSeconds(60).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSingInKey())
                .compact();
    }
    //Генерация постоянного токена
    private String generateRefreshToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSingInKey())
                .compact();
    }

    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtsecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
