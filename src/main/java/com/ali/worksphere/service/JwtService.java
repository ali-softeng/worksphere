package com.ali.worksphere.service;

import com.ali.worksphere.entity.Employee;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    private SecretKey getSigningKey(){
       byte[] keyBytes =  Base64.getDecoder().decode(secret);
       return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Employee employee){
       return Jwts.builder()
                .subject(employee.getEmail())
                .claim("id", employee.getId())
                .claim("role", employee.getRole())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(){
        SecureRandom random = new SecureRandom();
        byte[]  randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().
                withoutPadding().encodeToString(randomBytes);

     }

    public String extractEmail(String token){

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
