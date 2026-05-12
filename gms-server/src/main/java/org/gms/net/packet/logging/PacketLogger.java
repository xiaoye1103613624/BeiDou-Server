package org.gms.net.packet.logging;

import org.gms.net.packet.Packet;

/**
 * 封包日志与可观测性「PacketLogger」。
 * 用于记录、过滤或诊断收发包内容，便于 GM 与开发排查协议问题。
 */
public interface PacketLogger {
    void log(Packet packet);
}
