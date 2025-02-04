package com.qvl.gethomeweb.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Claim;

import java.util.Calendar;
import java.util.Map;

public class JwtUtil {
    public static String getToken(Map<String, String> map, String secret) {
        JWTCreator.Builder builder = JWT.create();
        map.forEach(builder::withClaim);
        Calendar instance = Calendar.getInstance();
//        一天過期
        instance.add(Calendar.DATE, 1);
        builder.withExpiresAt(instance.getTime());
        return builder.sign(Algorithm.HMAC256(secret));
    }
//    解密
    public static DecodedJWT verifyToken(String token, String key) {
        return JWT.require(Algorithm.HMAC256(key)).build().verify(token);
    }

    public static Map<String, Claim> getTokenInfo(String token, String secret) {
        return JWT.require(Algorithm.HMAC256(secret)).build().verify(token).getClaims();
    }
}
