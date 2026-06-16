package org.gms.constants.string;

import lombok.Getter;
import org.gms.util.PacketCreator;

/**
 * 全服广播消息前缀枚举
 * <p>
 * 统一管理各业务系统的广播前缀和通知类型（type），用于
 * {@link PacketCreator#serverNotice(int, String)} 等全服通告封包。
 * 修改枚举值即可批量调整对应系统的前缀文字或显示样式。
 * </p>
 *
 * <pre>{@code
 * // Java 端使用示例
 * BroadcastPrefix prefix = BroadcastPrefix.MENTOR;
 * Server.getInstance().broadcastMessage(worldId,
 *         PacketCreator.serverNotice(prefix.getType(), prefix.getPrefix() + "xxx 拜 xxx 为师"));
 *
 * // JS 脚本端使用示例
 * var BroadcastPrefix = Java.type('org.gms.constants.string.BroadcastPrefix');
 * PacketCreator.serverNotice(BroadcastPrefix.MENTOR.getType(),
 *         BroadcastPrefix.MENTOR.getPrefix() + "xxx 拜 xxx 为师");
 * }</pre>
 *
 * @author BeiDou
 * @since 0.0.1
 */
public enum BroadcastPrefix {

    // ==================== 社交系统 ====================

    /** 师徒系统：拜师、出师、逐出师门、退出师门等通告 — 粉色文字 */
    MENTOR("[师徒系统] ", 5),

    /** 家族系统：家族创建、加入、退出、职位变更等通告 — 浅蓝文字 */
    FAMILY("[家族系统] ", 6),

    /** 结婚系统：婚礼进行、结婚成功等通告 — 粉色文字 */
    WEDDING("[结婚系统] ", 5),

    // ==================== 装备与道具 ====================

    /** 装备系统：装备强化成功、极品装备掉落等通告 — 超级扩音器 */
    EQUIP("[装备系统] ", 3),

    /** 时装系统：时装合成、稀有时装获得等通告 — 粉色文字 */
    FASHION("[时装系统] ", 5),

    /** 兑换系统：CDK兑换、道具兑换等通告 — 浅蓝文字 */
    EXCHANGE("[兑换系统] ", 6),

    // ==================== 会员与活动 ====================

    /** 会员中心：VIP开通、会员升级、会员特权等通告 — 超级扩音器 */
    MEMBER("[会员中心] ", 3),

    /** 双倍活动：双倍经验、双倍掉落开启/结束通告 — 粉色文字 */
    DOUBLE_EVENT("[双倍活动] ", 5),

    /** 野外BOSS：野外BOSS刷新、击杀通告 — 浅蓝文字 */
    WORLD_BOSS("[野外BOSS] ", 6),

    /** 抽奖系统：扭蛋、红包、转盘等中奖通告 — 浅蓝文字 */
    LOTTERY("[抽奖系统] ", 6),

    /** 赞助系统：玩家赞助成功通告 — 超级扩音器 */
    SPONSOR("[赞助系统] ", 3),

    /** 全服公告：GM发布的服务器级别通知 — 浅蓝文字 */
    ANNOUNCEMENT("[全服公告] ", 6);

    /** 广播前缀文字（不含消息正文） */
    @Getter
    private final String prefix;

    /**
     * 通知类型（对应客户端 SERVERMESSAGE 封包的 type 字段）
     * <ul>
     *   <li>3 = 超级扩音器（全服横幅）</li>
     *   <li>5 = 聊天框粉色文字</li>
     *   <li>6 = 聊天框浅蓝色文字</li>
     * </ul>
     */
    @Getter
    private final int type;

    BroadcastPrefix(String prefix, int type) {
        this.prefix = prefix;
        this.type = type;
    }

    /**
     * 构建带前缀的完整广播消息
     *
     * @param message 消息正文（不含前缀）
     * @return 前缀 + 消息正文 的完整字符串
     */
    public String msg(String message) {
        return this.prefix + message;
    }
}
