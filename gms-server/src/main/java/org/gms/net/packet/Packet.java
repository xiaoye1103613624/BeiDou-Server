package org.gms.net.packet;

/**
 * 数据包接口
 * 定义网络数据包的基本操作，提供字节数组形式的数据访问
 */
public interface Packet {
    byte[] getBytes();
}