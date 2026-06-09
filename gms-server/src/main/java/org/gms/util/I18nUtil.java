package org.gms.util;

import org.gms.client.Client;
import org.gms.constants.string.CharsetConstants;
import org.gms.manager.ServerManager;
import org.gms.property.ServiceProperty;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * 国际化工具类
 * 提供多语言消息获取功能，支持普通消息、日志消息和异常消息的国际化处理
 *
 * messageSource.getMessage底层是通过循环遍历文件名去读取的，
 * 所以将不同文件名定义不同的bean，这样扫描的时候可以少扫描其他文件，
 * 直接找到想要对应的文件，节约时间
 */
public class I18nUtil {
    /**
     * 服务端默认语言环境，从配置中读取
     */
    public static final Locale LANGUAGE = Locale.forLanguageTag(ServerManager.getApplicationContext().getBean(ServiceProperty.class).getLanguage());

    /**
     * 普通消息资源源，用于获取用户界面显示的消息
     */
    public static final MessageSource messageSource = ServerManager.getApplicationContext().getBean("messageSource", MessageSource.class);

    /**
     * 日志消息资源源，用于获取日志输出的消息
     */
    public static final MessageSource logSource = ServerManager.getApplicationContext().getBean("logSource", MessageSource.class);

    /**
     * 异常消息资源源，用于获取异常提示消息
     */
    public static final MessageSource exceptionSource = ServerManager.getApplicationContext().getBean("exceptionSource", MessageSource.class);

    /**
     * 获取普通消息
     * 如果当前存在客户端请求，则以客户端的语言为准；
     * 如果当前非客户端请求（服务端主动发给客户端），则以服务端语言为准
     *
     * @param code 消息代码
     * @param args 消息参数
     * @return 组合后的本地化消息
     */
    public static String getMessage(String code, Object... args) {
        Locale clientLang = CharsetConstants.getLanguageLocale(ThreadLocalUtil.getClientLang());
        String[] stringArgs = Arrays.stream(args)
                .map(String::valueOf)
                .toArray(String[]::new);
        return messageSource.getMessage(code, stringArgs, clientLang);
    }

    /**
     * 根据指定语言环境获取普通消息
     *
     * @param locale 语言环境
     * @param code   消息代码
     * @param args   消息参数
     * @return 组合后的本地化消息
     */
    public static String getMessage(Locale locale, String code, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }

    /**
     * 获取日志消息
     * 使用StringFormat格式，传参通过{0} {1}等占位符
     *
     * @param code 消息代码
     * @param args 消息参数
     * @return 组合后的日志消息
     */
    public static String getLogMessage(String code, Object... args) {
        return logSource.getMessage(code, args, LANGUAGE);
    }

    /**
     * 根据指定语言环境获取日志消息
     *
     * @param locale 语言环境
     * @param code   消息代码
     * @param args   消息参数
     * @return 组合后的日志消息
     */
    public static String getLogMessage(Locale locale, String code, Object... args) {
        return logSource.getMessage(code, args, locale);
    }

    /**
     * 获取异常消息
     *
     * @param code 消息代码
     * @param args 消息参数
     * @return 组合后的异常消息
     */
    public static String getExceptionMessage(String code, Object... args) {
        return exceptionSource.getMessage(code, args, LANGUAGE);
    }

    /**
     * 根据指定语言环境获取异常消息
     *
     * @param locale 语言环境
     * @param code   消息代码
     * @param args   消息参数
     * @return 组合后的异常消息
     */
    public static String getExceptionMessage(Locale locale, String code, Object... args) {
        return exceptionSource.getMessage(code, args, locale);
    }
}