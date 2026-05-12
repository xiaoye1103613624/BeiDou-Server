package org.gms.net.packet;

/**
 * 网络协议层类型「Packet」。
 * 属于 org.gms.net.packet 下的通用封包、读写或工具定义。
 */
public interface Packet {
    byte[] getBytes();
}
