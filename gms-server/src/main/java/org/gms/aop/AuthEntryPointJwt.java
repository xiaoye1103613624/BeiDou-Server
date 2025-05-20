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
 * 自定义未授权和非法请求的返回结果
 *
 * @author XiaoYe
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * 重写此方法以处理未授权请求。
     *
     * @param request HttpServletRequest对象，表示客户端的请求。
     * @param response HttpServletResponse对象，用于向客户端发送响应。
     * @param authException AuthenticationException对象，表示认证异常。
     * @throws IOException      如果在发送响应时发生I/O错误。
     * @throws ServletException 如果在发送响应时发生Servlet异常。
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("地址未授权,请登录 {}: {}", request.getRequestURI(), authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: 未授权");
    }

}
