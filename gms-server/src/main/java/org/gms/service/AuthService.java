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
 * 【业务服务】AuthService：认证服务类，处理用户登录认证、JWT Token生成和刷新。
 * 
 * <p>提供账号密码验证、JWT Token颁发和Token刷新功能，是系统安全认证的核心组件。</p>
 */
@Service
@AllArgsConstructor
public class AuthService {
    /** 账号服务，提供账号查询和密码验证功能 */
    private final AccountService accountService;
    /** JWT工具类，提供Token生成和解析功能 */
    private final JwtUtils jwtUtils;

    /**
     * 用户登录获取JWT Token。
     * 
     * <p>验证流程：
     * <ol>
     *   <li>根据账号名查询账号信息</li>
     *   <li>验证密码是否匹配</li>
     *   <li>生成JWT Token并返回</li>
     * </ol></p>
     * 
     * @param name 账号名称
     * @param password 账号密码
     * @return 包含token的Map，key为"token"
     * @throws BizException 当账号不存在或密码错误时抛出异常
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
     * 刷新JWT Token。
     * 
     * <p>刷新流程：
     * <ol>
     *   <li>验证Token格式（必须以"Bearer "开头）</li>
     *   <li>提取Token并解析用户名</li>
     *   <li>验证账号是否存在</li>
     *   <li>生成新的JWT Token并返回</li>
     * </ol></p>
     * 
     * @param token 现有Token（需带Bearer前缀）
     * @return 包含新token的Map（key为"token"），失败返回null
     */
    public Map<String, String> refreshToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            // 移除Bearer前缀
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