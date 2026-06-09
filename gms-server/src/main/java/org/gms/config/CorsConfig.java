package org.gms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS跨域配置类
 * 配置前端页面的跨域访问权限
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /** 前端Vue应用地址，从配置文件读取 */
    @Value("${app.vue}")
    private String vue;

    /**
     * 配置跨域映射规则
     *
     * @param registry CORS注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")// 项目中的所有接口都支持跨域  
                .allowedOriginPatterns(vue)// 所有地址都可以访问，也可以配置具体地址  
                .allowCredentials(true)
                .allowedMethods("*")//"GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"  
                .maxAge(3600);// 跨域允许时间  
    }
}