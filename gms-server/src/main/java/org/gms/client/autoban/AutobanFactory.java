/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation version 3 as published by
the Free Software Foundation. You may not use, modify or distribute
this program under any other version of the GNU Affero General Public
License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gms.client.autoban;

import org.gms.client.Character;
import org.gms.config.GameConfig;
import org.gms.net.server.Server;
import org.gms.util.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 【类型】AutobanFactory（enum），包 {@code org.gms.client.autoban}。
 *
 * 自动封禁违规类型枚举，定义每种违规（怪物数量、伤害异常、距离异常、快速攻击等）的扣分阈值与过期时间，
 * 提供 GM 广播告警、自动封禁执行及角色忽略名单管理。
 *
 * @author kevintjuh93
 */
public enum AutobanFactory {
    MOB_COUNT,              // 怪物数量异常
    GENERAL,                // 通用违规
    FIX_DAMAGE,             // 伤害固定（外挂特征）
    DAMAGE_HACK(15, MINUTES.toMillis(1)),      // 伤害作弊
    DISTANCE_HACK(10, MINUTES.toMillis(2)),    // 距离作弊
    PORTAL_DISTANCE(5, SECONDS.toMillis(30)),  // 传送门距离异常
    PACKET_EDIT,            // 封包修改
    ACC_HACK,               // 连击作弊
    CREATION_GENERATOR,     // 物品生成器
    HIGH_HP_HEALING,        // 高额HP恢复
    FAST_HP_HEALING(15),    // 快速HP恢复
    FAST_MP_HEALING(20, SECONDS.toMillis(30)), // 快速MP恢复
    GACHA_EXP,              // 扭蛋经验异常
    TUBI(20, SECONDS.toMillis(15)),            // 瞬移作弊
    SHORT_ITEM_VAC,         // 短距吸物
    ITEM_VAC,               // 吸物作弊
    FAST_ITEM_PICKUP(5, SECONDS.toMillis(30)), // 快速拾取
    FAST_ATTACK(10, SECONDS.toMillis(30)),     // 快速攻击
    MPCON(25, SECONDS.toMillis(30)),           // MP消耗异常
    ATTACK_INTERVAL(60, SECONDS.toMillis(60)); // 攻击频率

    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(AutobanFactory.class);
    /** 忽略名单（不会触发GM告警的角色ID） */
    private static final Set<Integer> ignoredChrIds = new HashSet<>();

    /** 违规阈值（达到此分数触发封禁） */
    private final int points;
    /** 分数过期时间（毫秒，-1表示不过期） */
    private final long expiretime;

    AutobanFactory() {
        this(1, -1);
    }

    AutobanFactory(int points) {
        this.points = points;
        this.expiretime = -1;
    }

    AutobanFactory(int points, long expire) {
        this.points = points;
        this.expiretime = expire;
    }

    /**
     * 获取违规阈值
     * @return 触发封禁所需的分数
     */
    public int getMaximum() {
        return points;
    }

    /**
     * 获取分数过期时间
     * @return 过期时间（毫秒），-1表示不过期
     */
    public long getExpire() {
        return expiretime;
    }

    /**
     * 增加违规分数
     * @param ban AutobanManager实例
     * @param reason 违规原因
     */
    public void addPoint(AutobanManager ban, String reason) {
        ban.addPoint(this, reason);
    }

    /**
     * 向GM广播告警
     * @param chr 触发违规的角色
     * @param reason 违规原因
     */
    public void alert(Character chr, String reason) {
        if (GameConfig.getServerBoolean("use_auto_ban")) {
            if (chr != null && isIgnored(chr.getId())) {
                return;
            }
            Server.getInstance().broadcastGMMessage((chr != null ? chr.getWorld() : 0), 
                PacketCreator.sendYellowTip((chr != null ? Character.makeMapleReadable(chr.getName()) : "") + " caused " + this.name() + " " + reason));
        }
        if (GameConfig.getServerBoolean("use_auto_ban_log")) {
            final String chrName = chr != null ? Character.makeMapleReadable(chr.getName()) : "";
            log.info("Autoban alert - chr {} caused {}-{}", chrName, this.name(), reason);
        }
    }

    /**
     * 执行自动封禁
     * @param chr 要封禁的角色
     * @param value 封禁详情
     */
    public void autoban(Character chr, String value) {
        if (GameConfig.getServerBoolean("use_auto_ban")) {
            chr.autoBan("Autobanned for (" + this.name() + ": " + value + ")");
        }
    }

    /**
     * 切换角色的忽略状态
     * 被忽略的角色不会触发GM告警
     *
     * @param chrId 角色ID
     * @return 新状态：true=已忽略, false=未忽略
     */
    public static boolean toggleIgnored(int chrId) {
        if (ignoredChrIds.contains(chrId)) {
            ignoredChrIds.remove(chrId);
            return false;
        } else {
            ignoredChrIds.add(chrId);
            return true;
        }
    }

    /**
     * 判断角色是否在忽略名单中
     * @param chrId 角色ID
     * @return true=在忽略名单中
     */
    private static boolean isIgnored(int chrId) {
        return ignoredChrIds.contains(chrId);
    }

    /**
     * 获取忽略名单
     * @return 忽略的角色ID集合
     */
    public static Collection<Integer> getIgnoredChrIds() {
        return ignoredChrIds;
    }
}