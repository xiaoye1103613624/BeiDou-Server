package org.gms.net.opcodes;

/**
 * 网络层类型「Opcode」。
 * 位于 `org.gms.net.opcodes`，参与客户端会话、封包路由或服务器间协作。
 */
public interface Opcode {
    int getValue();
    String getName();
}
