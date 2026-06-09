package org.gms.util;

/**
 * 数字工具类
 * 提供数字格式化、解析和转换功能
 */
public class NumberTool {

    /**
     * 将8字节数组转换为long值（大端序）
     *
     * @param aToConvert 8字节数组
     * @return long值
     * @throws IllegalArgumentException 如果数组长度不为8
     */
    public static long BytesToLong(byte[] aToConvert) {
        if (aToConvert.length != Long.BYTES) {
            throw new IllegalArgumentException(String.format("Size of input should be %d", (Long.SIZE / 8)));
        }

        long nResult = 0;

        // 大端序：高位在前，逐字节左移后按位或
        for (int i = 0; i < Long.BYTES; i++) {
            nResult <<= Byte.SIZE;
            nResult |= (aToConvert[i] & 0xFF);
        }

        return nResult;
    }

    /**
     * 将long值转换为8字节数组（大端序）
     *
     * @param nToConvert long值
     * @return 8字节数组
     */
    public static byte[] LongToBytes(long nToConvert) {
        byte[] aBytes = new byte[Long.BYTES];

        // 大端序：从低位开始提取，倒序填充
        for (int i = aBytes.length - 1; i >= 0; i--) {
            aBytes[i] = (byte) (nToConvert & 0xFF);
            nToConvert >>= Byte.SIZE;
        }

        return aBytes;
    }

    /**
     * float转int（安全截断）
     *
     * @param f float值
     * @return 转换后的int值
     */
    public static int floatToInt(float f) {
        return doubleToInt(f);
    }

    /**
     * double转int（安全截断，超过Integer.MAX_VALUE则返回MAX_VALUE）
     *
     * @param d double值
     * @return 转换后的int值
     */
    public static int doubleToInt(double d) {
        // 防溢出保护
        if (d > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) d;
    }
}