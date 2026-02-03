package com.app.backend.util;

import com.app.backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.refresh_time}")
    private int refreshTime;

    @Value("${jwt.access_time}")
    private int accessTime;

    @Value("${jwt.reset_time}")
    private int resetTime;

    private Key key;

    @PostConstruct
    public void init(){
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user,int expiryTime){
        return Jwts
                .builder()
                .setSubject(user.getEmail())
                .claim("id",user.getId())
                .claim("role",user.getRole())
                .signWith(key)
                .setExpiration(new Date(System.currentTimeMillis()+expiryTime))
                .setIssuedAt(new Date())
                .compact();
    }

    public String getAccessToken(User user){
        return generateToken(user,accessTime);
    }

    public String getRefreshToken(User user){
        return generateToken(user,refreshTime);
    }

    public String generateResetToken(User user){
        return generateToken(user,resetTime);
    }
    public Claims extractClaim(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractSubject(String token){
        return extractClaim(token).getSubject();
    }

    public Date extractExpiration(String token){
        return extractClaim(token).getExpiration();
    }

    public boolean validateToken(String token){
        try {
            return extractExpiration(token).after(new Date()) && extractSubject(token)!=null;
        } catch (Exception e){
            return false;
        }
    }
}
