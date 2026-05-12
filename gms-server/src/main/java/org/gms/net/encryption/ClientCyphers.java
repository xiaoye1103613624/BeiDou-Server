package org.gms.net.encryption;

import org.gms.constants.net.ServerConstants;

/**
 * 协议加解密与编解码「ClientCyphers」。
 * 在客户端与服务器之间对帧或载荷进行变换，与 PacketCodec / Protocol 配置一致。
 */
public class ClientCyphers {
    private final MapleAESOFB send;
    private final MapleAESOFB receive;

    private ClientCyphers(MapleAESOFB send, MapleAESOFB receive) {
        this.send = send;
        this.receive = receive;
    }

    public static ClientCyphers of(InitializationVector sendIv, InitializationVector receiveIv) {
        MapleAESOFB send = new MapleAESOFB(sendIv, (short) (0xFFFF - ServerConstants.VERSION));
        MapleAESOFB receive = new MapleAESOFB(receiveIv, ServerConstants.VERSION);
        return new ClientCyphers(send, receive);
    }

    public MapleAESOFB getSendCypher() {
        return send;
    }

    public MapleAESOFB getReceiveCypher() {
        return receive;
    }
}
