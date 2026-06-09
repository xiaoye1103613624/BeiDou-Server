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
 * JWT认证入口点
 * 处理未授权请求的异常响应
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    /** 日志记录器 */
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * 处理未授权访问
     * 记录错误日志并返回401未授权状态码
     *
     * @param request        HTTP请求
     * @param response       HTTP响应
     * @param authException 认证异常
     * @throws IOException      IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error with {}: {}", request.getRequestURI(), authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }

}