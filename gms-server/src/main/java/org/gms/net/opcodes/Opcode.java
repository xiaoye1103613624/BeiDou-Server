package org.gms.net.opcodes;

/**
 * 操作码接口
 * 定义网络协议中操作码的基本属性：数值和名称
 */
public interface Opcode {
    int getValue();
    String getName();
}