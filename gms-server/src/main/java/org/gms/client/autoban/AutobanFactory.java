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

import lombok.Getter;
import org.gms.client.Character;
import org.gms.config.GameConfig;
import org.gms.dao.entity.AutobanConfigDO;
import org.gms.net.server.Server;
import org.gms.util.I18nUtil;
import org.gms.util.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 自动封禁工厂（枚举）
 * 定义各种违规检测类型及其积分阈值和过期时间
 * 支持从数据库动态加载配置，玩家积分达到阈值后自动封禁
 *
 * @author kevintjuh93
 */
public enum AutobanFactory {
    /** 怪物数量异常 */
    MOB_COUNT(I18nUtil.getMessage("autoban.name.MOB_COUNT")),
    /** 通用检测 */
    GENERAL(I18nUtil.getMessage("autoban.name.GENERAL")),
    /** 固定伤害 */
    FIX_DAMAGE(I18nUtil.getMessage("autoban.name.FIX_DAMAGE")),
    /** 伤害外挂（15分，1分钟过期） */
    DAMAGE_HACK(I18nUtil.getMessage("autoban.name.DAMAGE_HACK"), 15, MINUTES.toMillis(1)),
    /** 距离异常（10分，2分钟过期） */
    DISTANCE_HACK(I18nUtil.getMessage("autoban.name.DISTANCE_HACK"), 10, MINUTES.toMillis(2)),
    /** 传送门距离异常（5分，30秒过期） */
    PORTAL_DISTANCE(I18nUtil.getMessage("autoban.name.PORTAL_DISTANCE"), 5, SECONDS.toMillis(30)),
    /** 封包篡改 */
    PACKET_EDIT(I18nUtil.getMessage("autoban.name.PACKET_EDIT")),
    /** 命中率异常 */
    ACC_HACK(I18nUtil.getMessage("autoban.name.ACC_HACK")),
    /** 生成器异常 */
    CREATION_GENERATOR(I18nUtil.getMessage("autoban.name.CREATION_GENERATOR")),
    /** 高HP治疗 */
    HIGH_HP_HEALING(I18nUtil.getMessage("autoban.name.HIGH_HP_HEALING")),
    /** 快速HP治疗（15分） */
    FAST_HP_HEALING(I18nUtil.getMessage("autoban.name.FAST_HP_HEALING"), 15),
    /** 快速MP治疗（20分，30秒过期） */
    FAST_MP_HEALING(I18nUtil.getMessage("autoban.name.FAST_MP_HEALING"), 20, SECONDS.toMillis(30)),
    /** 扭蛋经验异常 */
    GACHA_EXP(I18nUtil.getMessage("autoban.name.GACHA_EXP")),
    /** TUBI检测（20分，15秒过期） */
    TUBI(I18nUtil.getMessage("autoban.name.TUBI"), 20, SECONDS.toMillis(15)),
    /** 短距离吸物 */
    SHORT_ITEM_VAC(I18nUtil.getMessage("autoban.name.SHORT_ITEM_VAC")),
    /** 吸物外挂 */
    ITEM_VAC(I18nUtil.getMessage("autoban.name.ITEM_VAC")),
    /** 快速拾取（5分，30秒过期） */
    FAST_ITEM_PICKUP(I18nUtil.getMessage("autoban.name.FAST_ITEM_PICKUP"), 5, SECONDS.toMillis(30)),
    /** 快速攻击（10分，30秒过期） */
    FAST_ATTACK(I18nUtil.getMessage("autoban.name.FAST_ATTACK"), 10, SECONDS.toMillis(30)),
    /** MP消耗异常（25分，30秒过期） */
    MPCON(I18nUtil.getMessage("autoban.name.MPCON"), 25, SECONDS.toMillis(30)),
    /** 攻击间隔异常（60分，60秒过期） */
    ATTACK_INTERVAL(I18nUtil.getMessage("autoban.name.ATTACK_INTERVAL"), 60, SECONDS.toMillis(60));

    private static final Logger log = LoggerFactory.getLogger(AutobanFactory.class);
    /** 忽略检测的玩家ID集合 */
    private static final Set<Integer> ignoredChrIds = new HashSet<>();

    /**
     * 配置缓存：type -> AutobanConfigDO
     */
    private static final Map<String, AutobanConfigDO> CONFIG_CACHE = new HashMap<>();

    @Getter
    /** 检测类型名称 */
    private final String name;
    /** 默认积分阈值 */
    private final int points;
    /** 默认过期时间（毫秒） */
    private final long expiretime;

    AutobanFactory(String name) {
        this(name, 1, -1);
    }

    AutobanFactory(String name, int points) {
        this.name = name;
        this.points = points;
        this.expiretime = -1;
    }

    AutobanFactory(String name, int points, long expire) {
        this.name = name;
        this.points = points;
        this.expiretime = expire;
    }

    public int getMaximum() {
        return points;
    }

    public long getExpire() {
        return expiretime;
    }

    /**
     * 初始化配置缓存
     */
    public static void initConfig(Map<String, AutobanConfigDO> configs) {
        CONFIG_CACHE.clear();
        CONFIG_CACHE.putAll(configs);
        log.info("Loaded {} autoban configs", configs.size());
    }

    /**
     * 更新单个配置
     */
    public static void updateConfig(String type, AutobanConfigDO config) {
        if (config == null) {
            CONFIG_CACHE.remove(type);
        } else {
            CONFIG_CACHE.put(type, config);
        }
    }

    /**
     * 获取配置
     */
    public static AutobanConfigDO getConfig(String type) {
        return CONFIG_CACHE.get(type);
    }

    /**
     * 获取生效的 points（优先使用数据库配置）
     */
    public int getEffectivePoints() {
        AutobanConfigDO config = CONFIG_CACHE.get(this.name());
        if (config != null && config.getPoints() != null) {
            return config.getPoints();
        }
        return points;
    }

    /**
     * 获取生效的 expiretime（优先使用数据库配置）
     */
    public long getEffectiveExpiretime() {
        AutobanConfigDO config = CONFIG_CACHE.get(this.name());
        if (config != null && config.getExpireTime() != null) {
            return config.getExpireTime();
        }
        return expiretime;
    }

    /**
     * 检查该类型是否被禁用
     */
    public boolean isDisabled() {
        AutobanConfigDO config = CONFIG_CACHE.get(this.name());
        return config != null && Boolean.TRUE.equals(config.getDisabled());
    }

    public void addPoint(AutobanManager ban, String reason) {
        ban.addPoint(this, reason);
    }

    public void alert(Character chr, String reason) {
        if (GameConfig.getServerBoolean("use_auto_ban")) {
            if (chr != null && isIgnored(chr.getId())) {
                return;
            }
            Server.getInstance().broadcastGMMessage((chr != null ? chr.getWorld() : 0), PacketCreator.sendYellowTip((chr != null ? Character.makeMapleReadable(chr.getName()) : "") + " caused " + this.name() + " " + reason));
        }
        if (GameConfig.getServerBoolean("use_auto_ban_log")) {
            final String chrName = chr != null ? Character.makeMapleReadable(chr.getName()) : "";
            log.info("Autoban alert - chr {} caused {}-{}", chrName, this.name(), reason);
        }
    }

    public void autoban(Character chr, String value) {
        if (GameConfig.getServerBoolean("use_auto_ban")) {
            chr.autoBan("Autobanned for (" + this.name() + ": " + value + ")");
            //chr.sendPolice("You will be disconnected for (" + this.name() + ": " + value + ")");
        }
    }

    /**
     * Toggle ignored status for a character id.
     * An ignored character will not trigger GM alerts.
     *
     * @return new status. true if the chrId is now ignored, otherwise false.
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

    private static boolean isIgnored(int chrId) {
        return ignoredChrIds.contains(chrId);
    }

    public static Collection<Integer> getIgnoredChrIds() {
        return ignoredChrIds;
    }
}