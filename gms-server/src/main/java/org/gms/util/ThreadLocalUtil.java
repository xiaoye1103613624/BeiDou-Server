package org.gms.util;

import org.gms.client.Client;

import java.util.Optional;

/**
 * 线程本地变量工具类
 * 用于在当前线程中存储和获取客户端对象，支持多语言环境
 */
public class ThreadLocalUtil {
    /** 线程本地客户端存储 */
    private static final ThreadLocal<Client> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的客户端
     *
     * @param c 客户端对象
     */
    public static void setCurrentClient(Client c) {
        threadLocal.set(c);
    }

    /**
     * 获取当前线程的客户端
     *
     * @return 客户端对象，可能为null
     */
    public static Client getCurrentClient() {
        return threadLocal.get();
    }

    /**
     * 移除当前线程的客户端
     */
    public static void removeCurrentClient() {
        threadLocal.remove();
    }

    /**
     * 获取当前线程客户端语言
     *
     * @return 语言代码，默认为0
     */
    public static int getClientLang() {
        return Optional.ofNullable(threadLocal.get()).map(Client::getLanguage).orElse(0);
    }
}