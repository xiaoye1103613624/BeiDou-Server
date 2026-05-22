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
 * 认证服务
 * <p>处理用户登录认证、JWT Token生成和刷新</p>
 */
@Service
@AllArgsConstructor
public class AuthService {
    private final AccountService accountService;
    private final JwtUtils jwtUtils;

    /**
     * 用户登录获取Token
     * <p>验证账号密码，成功后返回JWT Token</p>
     *
     * @param name 账号名称
     * @param password 账号密码
     * @return 包含token的Map
     */
    public Map<String, String> getToken(String name, String password) {
        // 查询账号并验证密码
        AccountsDO account = accountService.findByName(name);
        RequireUtil.requireFalse(account == null || !accountService.checkPassword(password, account),
                I18nUtil.getExceptionMessage("AuthService.account.or.password.error"));

        // 生成并返回JWT Token
        HashMap<String, String> result = new HashMap<>();
        result.put("token", jwtUtils.generateJwtToken(account.getName()));
        return result;
    }

    /**
     * 刷新JWT Token
     * <p>根据现有Token解析用户名，重新生成新Token</p>
     *
     * @param token 现有Token（需带Bearer前缀）
     * @return 包含新token的Map，失败返回null
     */
    public Map<String, String> refreshToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // 从Token解析用户名
            String username = jwtUtils.getUserNameFromJwtToken(token);
            AccountsDO account = accountService.findByName(username);
            if (account == null) return null;

            // 生成新Token
            HashMap<String, String> result = new HashMap<>();
            result.put("token", jwtUtils.generateJwtToken(account.getName()));
            return result;
        }
        return null;
    }
}
