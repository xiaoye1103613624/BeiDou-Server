package org.gms.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * messageSource.getMessage底层是通过循环遍历文件名去读取的
 * 所以将不同文件名定义不同的bean，这样扫描的时候可以少扫描其他文件，直接找到想要对应的文件，节约时间
 */
@Configuration
public class I18nConfig {

    /**
     * 创建通用消息资源Bean
     *
     * @return 消息资源
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * 创建日志消息资源Bean
     *
     * @return 日志消息资源
     */
    @Bean
    public MessageSource logSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/log");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * 创建异常消息资源Bean
     *
     * @return 异常消息资源
     */
    @Bean
    public MessageSource exceptionSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/exception");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}