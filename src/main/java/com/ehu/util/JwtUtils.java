package com.ehu.util;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.Setter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * @author alan
 * @createtime 18-7-12 下午4:51 * jwo
 */
public class JwtUtils {

    private static SecretKey generalKey() {
        byte[] encodedKey = Base64.getEncoder().encode(Constant.JWT_SECRET.getBytes());
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    /**
     * 生成token
     *
     * @param id        一般传入userId
     * @param claimsMap 用户信息
     * @return token
     */
    public static String createJWT(String id, Map<String, Object> claimsMap) {
        String subject = "group";
        return createJWT(id, subject, Constant.JWT_TTL, claimsMap);
    }

    /**
     * 签发JWT
     *
     * @param id        id
     * @param subject   主体 用户信息
     * @param ttlMillis 过期时间 （单位毫秒）
     * @param claimsMap 用户信息
     * @return token
     */
    public static String createJWT(String id, String subject, long ttlMillis, Map<String, Object> claimsMap) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setIssuer(Constant.ISS)
                .setSubject(subject)
                .setClaims(claimsMap)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS512, generalKey());
        if (ttlMillis >= 0) {
            builder.setExpiration(new Date(nowMillis + ttlMillis));
        }
        return builder.compact();
    }

    /**
     * 验证JWT
     *
     * @param jwtStr 待校验token
     * @return {@link CheckResult}
     */
    public static CheckResult validateJWT(String jwtStr) {
        CheckResult checkResult = new CheckResult();
        Claims claims;
        try {
            claims = parseJWT(jwtStr);
            checkResult.setSuccess(true);
            checkResult.setClaims(claims);
        } catch (ExpiredJwtException e) {
            checkResult.setErrCode(Constant.JWT_ERRCODE_EXPIRE);
            checkResult.setSuccess(false);
        } catch (Exception e) {
            checkResult.setErrCode(Constant.JWT_ERRCODE_FAIL);
            checkResult.setSuccess(false);
        }
        return checkResult;
    }

    /**
     * 解析JWT字符串
     *
     * @param jwt 待解析字符串
     * @return {@link Claims}
     * @throws Exception e
     */
    private static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

    private static class Constant {
        /**
         * jwt
         */
        private static final String JWT_ID = "jwt";
        private static final String ISS = "com.ehoo100";
        private static final String JWT_SECRET = "www.ehoo100.com";
        private static final int JWT_TTL = 60 * 60 * 1000;  //millisecond
        private static final int JWT_REFRESH_INTERVAL = 55 * 60 * 1000;  //millisecond
        private static final int JWT_REFRESH_TTL = 7 * 24 * 60 * 60 * 1000;  //millisecond

        private static final int JWT_ERRCODE_EXPIRE = 1;
        private static final int JWT_ERRCODE_FAIL = 2;
    }

    @Getter
    @Setter
    public static class CheckResult {
        private boolean success;
        private int errCode;
        private Claims claims;
    }
}
