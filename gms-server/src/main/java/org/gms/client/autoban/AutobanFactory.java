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
 * 自动封禁
 *
 * @author kevintjuh93
 */
public enum AutobanFactory {
    MOB_COUNT,
    GENERAL,
    FIX_DAMAGE,
    DAMAGE_HACK(15, MINUTES.toMillis(1)),
    DISTANCE_HACK(10, MINUTES.toMillis(2)),
    PORTAL_DISTANCE(5, SECONDS.toMillis(30)),
    PACKET_EDIT,
    ACC_HACK,
    CREATION_GENERATOR,
    HIGH_HP_HEALING,
    FAST_HP_HEALING(15),
    FAST_MP_HEALING(20, SECONDS.toMillis(30)),
    GACHA_EXP,
    TUBI(20, SECONDS.toMillis(15)),
    SHORT_ITEM_VAC,
    ITEM_VAC,
    FAST_ITEM_PICKUP(5, SECONDS.toMillis(30)),
    FAST_ATTACK(10, SECONDS.toMillis(30)),
    MPCON(25, SECONDS.toMillis(30));

    private static final Logger log = LoggerFactory.getLogger(AutobanFactory.class);
    /**
     * 忽略的玩家ID集合
     */
    private static final Set<Integer> IGNORED_CHR_IDS = new HashSet<>();

    private final int points;
    /**
     * 过期时间，单位毫秒
     */
    private final long expireTime;

    AutobanFactory() {
        this(1, -1);
    }

    AutobanFactory(int points) {
        this.points = points;
        this.expireTime = -1;
    }

    AutobanFactory(int points, long expire) {
        this.points = points;
        this.expireTime = expire;
    }

    public int getMaximum() {
        return points;
    }

    public long getExpire() {
        return expireTime;
    }

    public void addPoint(AutobanManager ban, String reason) {
        ban.addPoint(this, reason);
    }

    public void alert(Character chr, String reason) {
        // 开启自动封禁
        if (GameConfig.getServerBoolean("use_auto_ban")) {
            if (chr != null && isIgnored(chr.getId())) {
                return;
            }
            Server.getInstance().broadcastGMMessage((chr != null ? chr.getWorld() : 0), PacketCreator.sendYellowTip(
                    (chr != null ? Character.makeMapleReadable(chr.getName()) : "") + " caused " + this.name() + " " +
                            reason));
        }
        // 自动封禁日志
        if (GameConfig.getServerBoolean("use_auto_ban_log")) {
            final String chrName = chr != null ? Character.makeMapleReadable(chr.getName()) : "";
            log.info("自动封禁提示 - 玩家 [{}] 因 [{}-{}]", chrName, this.name(), reason);
        }
    }

    public void autoBan(Character chr, String value) {
        if (GameConfig.getServerBoolean("use_auto_ban")) {
            chr.autoBan("Autobanned for (" + this.name() + ": " + value + ")");
            //chr.sendPolice("You will be disconnected for (" + this.name() + ": " + value + ")");
        }
    }

    /**
     * 切换玩家 ID 的忽略状态.
     * 被忽略的玩家ID 不会触发警报.
     *
     * @return new status. true if the chrId is now ignored, otherwise false.
     */
    public static boolean toggleIgnored(int chrId) {
        if (IGNORED_CHR_IDS.contains(chrId)) {
            IGNORED_CHR_IDS.remove(chrId);
            return false;
        } else {
            IGNORED_CHR_IDS.add(chrId);
            return true;
        }
    }

    private static boolean isIgnored(int chrId) {
        return IGNORED_CHR_IDS.contains(chrId);
    }

    public static Collection<Integer> getIgnoredChrIds() {
        return IGNORED_CHR_IDS;
    }
}
