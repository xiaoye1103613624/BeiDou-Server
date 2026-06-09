package org.gms.net.packet.logging;

import org.gms.net.packet.Packet;

/**
 * 数据包日志接口
 * 定义数据包日志记录行为
 */
public interface PacketLogger {
    void log(Packet packet);
}