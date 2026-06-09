package org.gms.service;

import lombok.AllArgsConstructor;
import org.gms.util.I18nUtil;
import org.gms.util.JwtUtils;
import org.gms.dao.entity.AccountsDO;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务类
 * 提供用户登录认证和Token刷新功能
 */
@Service
@AllArgsConstructor
public class AuthService {
    /**
     * 账号服务，用于查询账号信息和验证密码
     */
    private final AccountService accountService;

    /**
     * JWT工具类，用于生成和解析JWT Token
     */
    private final JwtUtils jwtUtils;

    /**
     * 获取访问Token
     * 根据用户名和密码进行认证，认证成功后生成JWT Token
     *
     * @param name     用户名
     * @param password 密码
     * @return 包含Token的Map，Key为"token"
     */
    public Map<String, String> getToken(String name, String password) {
        AccountsDO account = accountService.findByName(name);
        // 账号不存在或密码不匹配则抛出异常
        RequireUtil.requireFalse(account == null || !accountService.checkPassword(password, account),
                I18nUtil.getExceptionMessage("AuthService.account.or.password.error"));

        HashMap<String, String> result = new HashMap<>();
        result.put("token", jwtUtils.generateJwtToken(account.getName()));
        return result;
    }

    /**
     * 刷新访问Token
     * 解析旧Token获取用户名，验证用户存在后生成新Token
     *
     * @param token 旧的Bearer Token
     * @return 包含新Token的Map，如果Token无效或用户不存在则返回null
     */
    public Map<String, String> refreshToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            // 去掉 "Bearer " 前缀，解析出用户名
            token = token.substring(7);
            String username = jwtUtils.getUserNameFromJwtToken(token);
            AccountsDO account = accountService.findByName(username);
            if (account == null) return null;
            HashMap<String, String> result = new HashMap<>();
            result.put("token", jwtUtils.generateJwtToken(account.getName()));
            return result;
        }
        return null;
    }
}