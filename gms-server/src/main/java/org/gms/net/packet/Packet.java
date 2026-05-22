package org.gms.net.packet;

/**
 * 【接口】Packet，包 `org.gms.net.packet`。
 *
 * 封包顶级接口，定义获取封包字节数组的基本方法，是入站包和出站包的公共父接口。
 *
 * @author Ronan
 */
public interface Packet {
    byte[] getBytes();
}
