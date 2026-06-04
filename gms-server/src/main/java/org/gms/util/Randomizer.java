package org.gms.util;

import java.util.Random;

/**
 * 【类型】Randomizer（class），包 `org.gms.util`。
 * <p>随机数工具类，封装 Java Random 的常用方法，提供线程安全的随机数生成</p>
 */
public class Randomizer {

    /** 线程安全的随机数生成器实例 */
    private final static Random rand = new Random();

    /**
     * 获取下一个随机整数
     * @return int 范围内的随机整数
     */
    public static int nextInt() {
        return rand.nextInt();
    }

    /**
     * 获取指定范围内的随机整数
     * @param arg0 上限（不包含）
     * @return [0, arg0) 范围内的随机整数
     */
    public static int nextInt(final int arg0) {
        return rand.nextInt(arg0);
    }

    /**
     * 生成随机字节数组
     * @param bytes 目标字节数组，将被随机填充
     */
    public static void nextBytes(final byte[] bytes) {
        rand.nextBytes(bytes);
    }

    /**
     * 获取随机布尔值
     * @return true 或 false，各约 50% 概率
     */
    public static boolean nextBoolean() {
        return rand.nextBoolean();
    }

    /**
     * 获取随机双精度浮点数
     * @return [0.0, 1.0) 范围内的随机浮点数
     */
    public static double nextDouble() {
        return rand.nextDouble();
    }

    /**
     * 获取随机单精度浮点数
     * @return [0.0f, 1.0f) 范围内的随机浮点数
     */
    public static float nextFloat() {
        return rand.nextFloat();
    }

    /**
     * 获取随机长整数
     * @return long 范围内的随机整数
     */
    public static long nextLong() {
        return rand.nextLong();
    }

    /**
     * 获取指定闭区间内的随机整数
     * @param lbound 下限（包含）
     * @param ubound 上限（包含）
     * @return [lbound, ubound] 范围内的随机整数
     */
    public static int rand(final int lbound, final int ubound) {
        return (int) ((rand.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
}