package com.example.demo.utils;


import com.example.demo.common.token.TokenValidateEnum;
import com.example.demo.dto.UserLoginDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author : ssk
 * @date : 2020/3/3
 * <p>
 * Token工具类
 */
public class TokenUtil {

    /**
     * Token生成者
     */
    private static final String ISS = "demo/login";

    /**
     * 秘钥
     */
    private static final String SECRET = "ad2fdfa225bca02c7fb1204eba7ba41c314a9cac";

    /**
     * 生成的密钥
     */
    private static final Key SIGN_KEY = generalSecretKey(SignatureAlgorithm.HS256, SECRET);


    /**
     * 生成Token
     *
     * @param userLoginDTO 用户登录DTO
     * @param validTime    有效时间
     * @return Token
     */
    public static String generateToken(UserLoginDTO userLoginDTO, long validTime) {
        // 设置jwt头
        final Map<String, Object> header = new HashMap<>(4, 1);
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Date nowDate = new Date();
        long nowMillis = nowDate.getTime();
        Date expireDate = null;
        if (validTime > 0) {
            expireDate = new Date(nowMillis + validTime);
        } else {
            // 默认15分钟
            expireDate = new Date(nowMillis + 15 * 60 * 1000);
        }

        // 构建jwt
        JwtBuilder builder = Jwts.builder()
                .setHeader(header)
                .setIssuer(ISS)
                .setIssuedAt(nowDate)
                .setSubject(userLoginDTO.getUserId().toString())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, SIGN_KEY);

        // 压缩jwt
        String compactJws = builder.compact();

        return compactJws;
    }

    /**
     * 验证token
     *
     * @param compactJws 已生成的token
     * @return 自定义的返回响应结构
     */
    public static String validateToken(String compactJws) {
        String resultFormat;
        try {
            Claims claims = parseJWT(compactJws);
            // 验证通过,返回值为“ok”
            resultFormat = "ok";
            // 验证不通过
        } catch (ExpiredJwtException e) {
            throw new RuntimeException(TokenValidateEnum.EXPIRE.toString() + "---" + TokenValidateEnum.EXPIRE.getDesc());
        } catch (SignatureException e) {
            throw new RuntimeException(TokenValidateEnum.NO_VALID.toString() + "---" + TokenValidateEnum.NO_VALID.getDesc());
        } catch (Exception e) {
            throw new RuntimeException(TokenValidateEnum.OTHER_ERROR.toString() + "---" + TokenValidateEnum.OTHER_ERROR.getDesc());
        }
        return resultFormat;
    }

    /**
     * 通过SIGN_KEY解析token字符串,解析成功说明可信任
     *
     * @param compactJws 已生成的token
     * @return Claims
     */
    public static Claims parseJWT(String compactJws) throws Exception {
        Claims claims = Jwts.parser()
                .setSigningKey(SIGN_KEY)
                .parseClaimsJws(compactJws)
                .getBody();
        return claims;
    }

    /**
     * 生成key，生成token
     *
     * @param signatureAlgorithm 指定加密算法
     * @param secretKey          自定义秘钥
     * @return Key
     */
    public static Key generalSecretKey(SignatureAlgorithm signatureAlgorithm, String secretKey) {
        // 在Java 8在java.util包下BASE64编解码API
        byte[] base64EncodedSecretKey = null;
        try {
            base64EncodedSecretKey = Base64.getEncoder().encode(secretKey.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (base64EncodedSecretKey == null) {
            throw new IllegalArgumentException();
        }
        return new SecretKeySpec(base64EncodedSecretKey, signatureAlgorithm.getJcaName());
    }

}
