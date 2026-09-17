package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.I18nUtil;
import org.gms.util.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Kaentake 头顶动态聊天表情 — 收包 0x11C，广播 0x17F。
 * <p>
 * 链路：客户端点击表情按钮选中表情 → 上行 0x11C（int 表情ID）→ 本处理器校验 ID 与冷却 →
 * 向同地图（含发起者）广播 0x17F（int 角色ID + int 表情ID）→ 客户端按
 * {@code Effect/ChatEmoticon.img/Dynamic{分类}/{表情ID}/effect} 在角色头顶挂气泡 + 动画层，约 3 秒后自行消失。
 * <p>
 * 注意：本功能与 516xxxx 原版脸部表情（FACE_EXPRESSION）是两套资源与协议，互不依赖。
 * 表情贴图由客户端 Data 提供，服务端只做 ID 白名单校验与广播，不需要读取 ChatEmoticon.img。
 */
public final class ChatEmoticonHandler extends AbstractPacketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatEmoticonHandler.class);

    /** 同一角色两次表情的最小间隔，避免刷包与频繁创建动画层（与客户端 3 秒动画时长配套）。 */
    private static final long COOLDOWN_MILLIS = 1500L;

    /** 收包载荷固定为 1 个 int（表情ID）。 */
    private static final int PAYLOAD_BYTES = Integer.BYTES;

    /** 角色ID → 上次成功触发表情的时刻；条目数上限约等于在线角色数。 */
    private static final ConcurrentMap<Integer, Long> LAST_USE = new ConcurrentHashMap<>();

    @Override
    public void handlePacket(InPacket packet, Client client) {
        final Character player = client.getPlayer();
        if (player == null || player.getMap() == null) {
            return;
        }

        if (packet.available() != PAYLOAD_BYTES) {
            log.warn(I18nUtil.getLogMessage("ChatEmoticon.warn.badLength"), player.getId(), packet.available());
            return;
        }

        final int emoticonId = packet.readInt();
        if (!isSupported(emoticonId)) {
            // 非白名单 ID 一律丢弃：防止任意整数被广播给同地图玩家。
            log.warn(I18nUtil.getLogMessage("ChatEmoticon.warn.unsupportedId"), player.getId(), emoticonId);
            return;
        }

        final long now = System.currentTimeMillis();
        final Long previous = LAST_USE.get(player.getId());
        if (previous != null && now - previous < COOLDOWN_MILLIS) {
            // 冷却期内不刷新时间戳，否则连续点击会被永久拒绝（滑动窗口失效）。
            log.debug(I18nUtil.getLogMessage("ChatEmoticon.debug.cooldown"),
                    player.getId(), emoticonId, now - previous);
            return;
        }
        LAST_USE.put(player.getId(), now);

        // repeatToSource=true：发起者本人也要看到自己头顶的动画。
        player.getMap().broadcastMessage(player, PacketCreator.chatEmoticon(player.getId(), emoticonId), true);
    }

    /**
     * 表情 ID 白名单，与客户端 chatemoticon.cpp 中 kDynamic0~kDynamic3 四组数组等价
     * （各段内 ID 连续，无空洞；Dynamic0 尾部 108025/108026/108027 乱序但均在 108000~108027 内）。
     * 修改表情数量时必须同步客户端数组，否则会出现"能发送但无贴图"。
     */
    private static boolean isSupported(int id) {
        return inRange(id, 100001, 100010)
                || inRange(id, 101001, 101010)
                || inRange(id, 102001, 102024)
                || inRange(id, 103001, 103007)
                || inRange(id, 104001, 104005)
                || inRange(id, 105001, 105004)
                || inRange(id, 106001, 106006)
                || inRange(id, 107001, 107006)
                || inRange(id, 108000, 108027)
                || inRange(id, 200001, 200005)
                || inRange(id, 201001, 201004)
                || inRange(id, 202001, 202004)
                || inRange(id, 203001, 203006)
                || inRange(id, 204001, 204006)
                || inRange(id, 205001, 205006)
                || inRange(id, 206001, 206006)
                || inRange(id, 207001, 207006)
                || inRange(id, 208000, 208003)
                || inRange(id, 300001, 300120)
                || inRange(id, 301000, 301002);
    }

    private static boolean inRange(int value, int first, int last) {
        return value >= first && value <= last;
    }
}
