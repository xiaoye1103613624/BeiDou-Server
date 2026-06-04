package org.gms.util;

/**
 * 【类型】NumberTool（class），包 `org.gms.util`。
 * <p>数值工具类，提供字节数组与数值类型之间的转换功能</p>
 *
 * @author Shavit
 */
public class NumberTool {

    /**
     * 将8字节数组转换为long类型
     *
     * @param aToConvert 待转换的字节数组（必须为8字节）
     * @return 转换后的long值
     * @throws IllegalArgumentException 数组长度不为8时抛出
     */
    public static long BytesToLong(byte[] aToConvert) {
        if (aToConvert.length != Long.BYTES) {
            throw new IllegalArgumentException(String.format("Size of input should be %d", (Long.SIZE / 8)));
        }

        long nResult = 0;

        for (int i = 0; i < Long.BYTES; i++) {
            nResult <<= Byte.SIZE;
            nResult |= (aToConvert[i] & 0xFF);
        }

        return nResult;
    }

    /**
     * 将long类型转换为8字节数组
     *
     * @param nToConvert 待转换的long值
     * @return 转换后的8字节数组
     */
    public static byte[] LongToBytes(long nToConvert) {
        byte[] aBytes = new byte[Long.BYTES];

        for (int i = aBytes.length - 1; i >= 0; i--) {
            aBytes[i] = (byte) (nToConvert & 0xFF);
            nToConvert >>= Byte.SIZE;
        }

        return aBytes;
    }

    /**
     * 将float转换为int
     *
     * @param f 待转换的float值
     * @return 转换后的int值
     */
    public static int floatToInt(float f) {
        return doubleToInt(f);
    }

    /**
     * 将double转换为int（超出范围时返回Integer.MAX_VALUE）
     *
     * @param d 待转换的double值
     * @return 转换后的int值
     */
    public static int doubleToInt(double d) {
        if (d > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) d;
    }
}