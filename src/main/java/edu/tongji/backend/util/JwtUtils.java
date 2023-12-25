package edu.tongji.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtils {
    private static final String signKey = "wwjasdf";

    // 生成token
    public static String generateJwt(Map<String, Object> jwtInfo){
        // 7天
        long expire = 7L * 24 * 60 * 60 * 1000;
        return Jwts.builder()
                .addClaims(jwtInfo)
                .signWith(SignatureAlgorithm.HS256, signKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
    }

    // 解析token
    public static Claims parseJWT(String jwt){
        return Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();
    }
}
