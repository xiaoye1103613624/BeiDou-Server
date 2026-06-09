package org.gms.scripting;

import net.jcip.annotations.ThreadSafe;

import javax.script.Invocable;
import javax.script.ScriptException;

/**
 * Thread safe wrapper around Invocable.
 * Thread safety is achieved by synchronizing all methods.
 * Needed to get around the restriction that GraalVM imposes on evaluated scripts: no concurrent access allowed.
 */
@ThreadSafe
public class SynchronizedInvocable implements Invocable {
    /** 被包装的原始Invocable实例 */
    private final Invocable invocable;

    /**
     * 私有构造函数
     *
     * @param invocable 被包装的Invocable实例
     */
    private SynchronizedInvocable(Invocable invocable) {
        this.invocable = invocable;
    }

    /**
     * 创建线程安全的Invocable包装实例
     *
     * @param invocable 原始Invocable实例
     * @return 线程安全包装后的Invocable
     */
    public static Invocable of(Invocable invocable) {
        return new SynchronizedInvocable(invocable);
    }

    /**
     * 同步调用对象方法
     *
     * @param thiz 目标对象
     * @param name 方法名
     * @param args 方法参数
     * @return 方法返回值
     * @throws ScriptException        脚本执行异常
     * @throws NoSuchMethodException  方法不存在异常
     */
    @Override
    public synchronized Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        return invocable.invokeMethod(thiz, name, args);
    }

    /**
     * 同步调用全局函数
     *
     * @param name 函数名
     * @param args 函数参数
     * @return 函数返回值
     * @throws ScriptException        脚本执行异常
     * @throws NoSuchMethodException  函数不存在异常
     */
    @Override
    public synchronized Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        return invocable.invokeFunction(name, args);
    }

    /**
     * 获取脚本引擎的接口实现
     *
     * @param clasz 接口类
     * @param <T>   接口类型
     * @return 接口实现实例
     */
    @Override
    public synchronized <T> T getInterface(Class<T> clasz) {
        return invocable.getInterface(clasz);
    }

    /**
     * 获取脚本引擎的接口实现（绑定对象）
     *
     * @param thiz  目标对象
     * @param clasz 接口类
     * @param <T>   接口类型
     * @return 接口实现实例
     */
    @Override
    public synchronized <T> T getInterface(Object thiz, Class<T> clasz) {
        return invocable.getInterface(thiz, clasz);
    }
}
