package org.gms.util;

import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 自定义Spring Bean配置
 * 为SpringDoc提供默认配置Bean，避免当Swagger被禁用时因缺少Bean而启动失败
 */
@Configuration
public class CustomSpringBeanConfig {
    /**
     * 当springdoc.api-docs.enabled为false时，提供默认的SpringDocConfigProperties
     *
     * @return SpringDocConfigProperties实例
     */
    @Bean
    @ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "false")
    public SpringDocConfigProperties springDocConfigProperties() {
        return new SpringDocConfigProperties();
    }

    /**
     * 当springdoc.swagger-ui.enabled为false时，提供默认的SwaggerUiConfigProperties
     *
     * @return SwaggerUiConfigProperties实例
     */
    @Bean
    @ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "false")
    public SwaggerUiConfigProperties swaggerUiConfigProperties() {
        return new SwaggerUiConfigProperties();
    }
}