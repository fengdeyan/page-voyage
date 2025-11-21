package me.yan.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT 令牌操作工具类
 * 依赖：JJWT 0.12.x（api + impl + jackson）
 */
public class JwtUtils {

    // 1. 密钥（与测试类一致，256位安全密钥）
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 2. 令牌过期时间：12小时（12 * 60 * 60 * 1000 毫秒）
    private static final long EXPIRATION_TIME = 12 * 60 * 60 * 1000L;

    /**
     * 生成 JWT 令牌
     * @param claims 自定义负载（键值对数据，如用户ID、角色等）
     * @return 生成的 JWT 令牌字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        // 当前时间（签发时间）
        Date now = new Date();
        // 过期时间（当前时间 + 12小时）
        Date expiration = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims) // 传入自定义负载（替代 addClaims，功能一致）
                .setIssuedAt(now) // 可选：添加签发时间（增强令牌可读性）
                .setExpiration(expiration) // 设置过期时间（12小时）
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 用256位密钥签名
                .compact();
    }

    /**
     * 解析 JWT 令牌
     * @param token 待解析的 JWT 令牌字符串
     * @return 令牌中的负载数据（Claims 对象，可获取自定义键值对和标准字段）
     * @throws Exception 令牌无效、过期、签名错误等异常（可根据业务细化异常类型）
     */
    public static Claims parseToken(String token) throws Exception {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY) // 用相同密钥验证签名
                    .build()
                    .parseClaimsJws(token) // 验证令牌有效性（签名、过期时间等）
                    .getBody(); // 获取负载数据
        } catch (Exception e) {
            // 可根据实际业务需求捕获具体异常（如过期、签名错误等）
            // 此处统一抛出，方便调用方处理
            throw new Exception("JWT令牌解析失败：" + e.getMessage(), e);
        }
    }
}