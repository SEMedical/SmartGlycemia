package edu.tongji.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class Jwt {
    private static final String key = "Tangxiaozhi";

    // 生成token
    public static String generate(Map<String, Object> jwtInfo){
        // 7天
        long expire = 7L * 24 * 60 * 60 * 1000;
        return Jwts.builder()
                .addClaims(jwtInfo)
                .signWith(SignatureAlgorithm.HS256, key)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
    }

    // 解析token
    public static Claims parse(String jwt){
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwt)
                .getBody();
    }
}
