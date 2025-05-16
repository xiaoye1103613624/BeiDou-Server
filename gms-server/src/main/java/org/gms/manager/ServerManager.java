package org.gms.manager;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.gms.ServerApplication;
import org.gms.constants.net.ServerConstants;
import org.gms.net.server.Server;
import org.gms.util.I18nUtil;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.InetAddress;

/**
 * 服务管理器
 */
@Component
@Slf4j
public class ServerManager implements ApplicationContextAware, ApplicationRunner, DisposableBean {
    /**
     * spring 上下文对象
     */
    @Getter
    private static ApplicationContext applicationContext;

    /**
     * 设置应用程序上下文。 该方法将在ApplicationContext被初始化后调用(Aware通知)。
     *
     * @param applicationContext 应用程序上下文对象
     * @throws BeansException 如果设置上下文时发生异常，抛出此异常
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ServerManager.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 服务器初始化，启动服务器实例。
        Server.getInstance().init();
        // 加载文档配置信息
        SpringDocConfigProperties springDocConfigProperties =
                applicationContext.getBean(SpringDocConfigProperties.class);

        // 加载swagger配置信息
        SwaggerUiConfigProperties swaggerUiConfigProperties =
                applicationContext.getBean(SwaggerUiConfigProperties.class);
        Environment environment = applicationContext.getBean(Environment.class);
        log.info(I18nUtil.getLogMessage("ServerManager.run.info3"), ServerConstants.BEI_DOU_VERSION,
                ServerConstants.BEI_DOU_BUILD_TIME);
        if (springDocConfigProperties.getApiDocs().isEnabled() && swaggerUiConfigProperties.isEnabled()) {
            log.info(I18nUtil.getLogMessage("ServerManager.run.info1"), InetAddress.getLocalHost().getHostAddress(),
                    environment.getProperty("server.port"));
        }
        // 判断是否集成前端，集成则提示前端地址
        try (InputStream resource = ServerApplication.class.getClassLoader().getResourceAsStream("static/index.html")) {
            if (resource != null) {
                log.info(I18nUtil.getLogMessage("ServerManager.run.info2"), InetAddress.getLocalHost().getHostAddress(),
                        environment.getProperty("server.port"));
            }
        }
    }

    /**
     * 销毁方法，用于关闭服务器实例。
     *
     * @throws Exception 如果销毁过程中发生异常，则抛出此异常
     */
    @Override
    public void destroy() throws Exception {
        Server.getInstance().shutdownInternal(false);
    }
}
