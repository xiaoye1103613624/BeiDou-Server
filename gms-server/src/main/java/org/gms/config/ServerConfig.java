package org.gms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.gms.aop.ServerFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * 服务器配置类
 * 配置过滤器注册和OpenAPI文档
 */
@Configuration
public class ServerConfig {
    /**
     * 注册业务过滤器
     *
     * @param serverFilter 业务过滤器
     * @return 过滤器注册Bean
     */
    @Bean
    public FilterRegistrationBean<ServerFilter> filterRegistrationBean(ServerFilter serverFilter) {
        FilterRegistrationBean<ServerFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(serverFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    /**
     * 创建OpenAPI配置Bean
     * 配置API文档信息和安全认证
     *
     * @return OpenAPI配置
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("BeiDou api").description("北斗项目地址：https://github.com/BeiDouMS/BeiDou-Server").version("v1"))
                // 给swagger的所有接口装配AUTHORIZATION请求头
                .schemaRequirement(HttpHeaders.AUTHORIZATION, new SecurityScheme().type(SecurityScheme.Type.APIKEY).name(HttpHeaders.AUTHORIZATION).in(SecurityScheme.In.HEADER))
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));
    }
}