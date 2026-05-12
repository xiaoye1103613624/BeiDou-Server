package org.gms.net.packet;

import org.gms.constants.string.CharsetConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.gms.util.ThreadLocalUtil;

import java.awt.*;

/**
 * 网络协议层类型「ByteBufInPacket」。
 * 属于 org.gms.net.packet 下的通用封包、读写或工具定义。
 */
public class ByteBufInPacket implements InPacket {
    private final ByteBuf byteBuf;

    public ByteBufInPacket(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    @Override
    public byte[] getBytes() {
        return ByteBufUtil.getBytes(byteBuf);
    }

    @Override
    public byte readByte() {
        return byteBuf.readByte();
    }
    @Override
    public short readUnsignedByte() { return byteBuf.readUnsignedByte(); }

    @Override
    public short readShort() {
        return byteBuf.readShortLE();
    }

    @Override
    public int readInt() {
        return byteBuf.readIntLE();
    }

    @Override
    public long readLong() {
        return byteBuf.readLongLE();
    }

    @Override
    public Point readPos() {
        final short x = byteBuf.readShortLE();
        final short y = byteBuf.readShortLE();
        return new Point(x, y);
    }

    @Override
    public String readString() {
        short length = readShort();
        byte[] stringBytes = new byte[length];
        byteBuf.readBytes(stringBytes);
        return new String(stringBytes, CharsetConstants.getCharset(ThreadLocalUtil.getClientLang()));
    }

    @Override
    public byte[] readBytes(int numberOfBytes) {
        byte[] bytes = new byte[numberOfBytes];
        byteBuf.readBytes(bytes);
        return bytes;
    }

    @Override
    public void skip(int numberOfBytes) {
        byteBuf.skipBytes(numberOfBytes);
    }

    @Override
    public int available() {
        return byteBuf.readableBytes();
    }

    @Override
    public void seek(int byteOffset) {
        byteBuf.readerIndex(byteOffset);
    }

    @Override
    public int getPosition() {
        return byteBuf.readerIndex();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ByteBufInPacket other && byteBuf.equals(other.byteBuf);
    }

    @Override
    public String toString() {
        final int readerIndex = byteBuf.readerIndex();
        byteBuf.markReaderIndex();
        byteBuf.readerIndex(0);

        String hexDumpWithPosition = insertReaderPosition(ByteBufUtil.hexDump(byteBuf).toUpperCase(), readerIndex);
        String toString = String.format("ByteBufInPacket[%s]", hexDumpWithPosition);

        byteBuf.resetReaderIndex();
        return toString;
    }

    private static String insertReaderPosition(String hexDump, int index) {
        StringBuilder sb = new StringBuilder(hexDump);
        sb.insert(2 * index, '_');
        return sb.toString();
    }
}
