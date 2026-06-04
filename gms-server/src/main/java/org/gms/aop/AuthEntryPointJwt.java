package org.gms.aop;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 【类型】AuthEntryPointJwt（class），包 `org.gms.aop`。
 * <p>JWT认证入口点，处理未授权请求的响应</p>
 * <p>当用户访问受保护资源但未提供有效认证时，返回401错误</p>
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    /** 日志记录器 */
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * 处理未授权访问
     * <p>记录错误日志并返回401 Unauthorized响应</p>
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @param authException 认证异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error with {}: {}", request.getRequestURI(), authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }

}