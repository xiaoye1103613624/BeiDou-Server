package org.gms.util;

import java.util.Random;

/**
 * 随机数工具类
 * 封装java.util.Random，提供全局共享的随机数生成器
 */
public class Randomizer {

    /** 全局随机数生成器 */
    private final static Random rand = new Random();

    /**
     * 生成一个随机int值（全范围）
     *
     * @return 随机int值
     */
    public static int nextInt() {
        return rand.nextInt();
    }

    /**
     * 生成[0, arg0)范围内的随机int值
     *
     * @param arg0 上限（不包含）
     * @return 随机int值，范围[0, arg0)
     */
    public static int nextInt(final int arg0) {
        return rand.nextInt(arg0);
    }

    /**
     * 填充随机字节到指定数组
     *
     * @param bytes 要填充的目标字节数组
     */
    public static void nextBytes(final byte[] bytes) {
        rand.nextBytes(bytes);
    }

    /**
     * 生成随机布尔值
     *
     * @return 随机布尔值
     */
    public static boolean nextBoolean() {
        return rand.nextBoolean();
    }

    /**
     * 生成[0.0, 1.0)范围内的随机double值
     *
     * @return 随机double值，范围[0.0, 1.0)
     */
    public static double nextDouble() {
        return rand.nextDouble();
    }

    /**
     * 生成[0.0, 1.0)范围内的随机float值
     *
     * @return 随机float值，范围[0.0, 1.0)
     */
    public static float nextFloat() {
        return rand.nextFloat();
    }

    /**
     * 生成随机long值（全范围）
     *
     * @return 随机long值
     */
    public static long nextLong() {
        return rand.nextLong();
    }

    /**
     * 生成[lbound, ubound]范围内的随机int值（闭区间）
     *
     * @param lbound 下限（包含）
     * @param ubound 上限（包含）
     * @return 随机int值，范围[lbound, ubound]
     */
    public static int rand(final int lbound, final int ubound) {
        // 通过double随机值映射到目标区间
        return (int) ((rand.nextDouble() * (ubound - lbound + 1)) + lbound);
    }
}