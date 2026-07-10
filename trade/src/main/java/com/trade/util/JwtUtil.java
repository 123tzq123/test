package com.trade.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    //密钥长度至少256位，密钥字符串不少于32位
    private static final String SECRET = "mysecretkey12345678mysecretkey12345678";
    //过期时间 1天
    private static final long EXPIRATION = 24 * 60 * 60 * 1000L;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 根据用户id生成token
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRATION);
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析token获取用户id
     */
    public Long parseToken(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token);
        String userId = jws.getBody().getSubject();
        return Long.parseLong(userId);
    }
}