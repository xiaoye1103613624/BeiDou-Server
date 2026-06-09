package org.gms.util;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT工具类
 * 提供JWT令牌的生成、验证和解析功能
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /** JWT签名密钥 */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /** JWT有效期（毫秒） */
    @Value("${jwt.duration}")
    private int jwtDuration;

    /**
     * 生成JWT令牌
     *
     * @param username 用户名
     * @return JWT令牌字符串
     */
    public String generateJwtToken(String username) {
        // 构建JWT：设置主题、签发时间、过期时间，使用HS512算法签名
        return Jwts.builder()
                .setSubject((username))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtDuration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * 从JWT令牌中解析用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 验证JWT令牌是否有效
     *
     * @param authToken JWT令牌
     * @return true表示有效，false表示无效
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("访问者的Token签名无效: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("访问者的Token无效: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("访问者的Token已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("访问者的Token不被支持: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("访问者的Token参数为空: {}", e.getMessage());
        }

        // 任何异常都表示令牌无效
        return false;
    }
}