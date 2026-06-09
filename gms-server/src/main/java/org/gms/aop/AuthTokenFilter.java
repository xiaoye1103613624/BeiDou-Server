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
 * JWT认证过滤器
 * 拦截所有请求，验证JWT令牌并设置安全上下文
 */
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
    /** JWT工具类 */
    @Autowired
    private JwtUtils jwtUtils;

    /** 用户详情服务 */
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /** SpringDoc配置属性 */
    @Autowired(required = false)
    private SpringDocConfigProperties springDocConfigProperties;

    /** Swagger UI配置属性 */
    @Autowired(required = false)
    private SwaggerUiConfigProperties swaggerUiConfigProperties;

    /** 日志记录器 */
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * 执行过滤逻辑
     * 授权接口跳过校验，其他接口验证JWT令牌
     *
     * @param request     HTTP请求
     * @param response    HTTP响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException      IO异常
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
            // 测试token，所以生产环境一定要把swagger关掉，否则裸奔
            if (springDocConfigProperties != null && swaggerUiConfigProperties != null && "swagger".equals(jwt) && springDocConfigProperties.getApiDocs().isEnabled() && swaggerUiConfigProperties.isEnabled()) {
                UserDetails userDetails = userDetailsService.loadUserByUsername("admin");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

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
     * 从请求头中解析JWT令牌
     * 从Authorization头中提取Bearer token
     *
     * @param request HTTP请求
     * @return JWT令牌字符串，如果不存在返回null
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}