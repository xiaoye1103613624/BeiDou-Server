package org.gms.aop;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.gms.service.UserDetailsServiceImpl;
import org.gms.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.*;

/**
 * 【类型】AuthTokenFilter（class），包 `org.gms.aop`。
 * <p>JWT认证过滤器，负责解析和验证请求中的JWT Token，将用户信息存入Spring Security上下文</p>
 * <p>继承OncePerRequestFilter确保每个请求只执行一次过滤</p>
 */
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
    /** JWT工具类 */
    @Autowired
    private JwtUtils jwtUtils;
    /** 用户详情服务 */
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    /** SpringDoc配置（可选，用于Swagger测试） */
    @Autowired(required = false)
    private SpringDocConfigProperties springDocConfigProperties;
    /** SwaggerUI配置（可选，用于Swagger测试） */
    @Autowired(required = false)
    private SwaggerUiConfigProperties swaggerUiConfigProperties;

    /** 日志记录器 */
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * 执行JWT认证过滤
     * <p>1. 授权接口(/auth/)直接放行</p>
     * <p>2. 解析请求中的JWT Token</p>
     * <p>3. 验证Token并设置安全上下文</p>
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 授权接口不做身份校验
            if (request.getRequestURI().startsWith("/auth/")) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwt = parseJwt(request);
            // 测试token（仅用于Swagger测试环境，生产环境应关闭Swagger）
            if (springDocConfigProperties != null && swaggerUiConfigProperties != null 
                    && "swagger".equals(jwt) 
                    && springDocConfigProperties.getApiDocs().isEnabled() 
                    && swaggerUiConfigProperties.isEnabled()) {
                UserDetails userDetails = userDetailsService.loadUserByUsername("admin");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // 从Token解析用户名并加载用户详情
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 设置安全上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Filter error", e);
            // 释放流，否则可能内存泄漏
            request.getInputStream().close();
            response.getOutputStream().close();
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中解析JWT Token
     *
     * @param request HTTP请求
     * @return JWT Token字符串（不含Bearer前缀），无Token时返回null
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}