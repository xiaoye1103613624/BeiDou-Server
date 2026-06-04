/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gms.client.autoban;

import org.gms.client.Character;
import org.gms.config.GameConfig;
import org.gms.net.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 【类型】AutobanManager（class），包 {@code org.gms.client.autoban}。
 *
 * 角色级自动封禁管理器，记录各类违规行为的扣分累计、过期衰减、MISS 计数与操作频率检测（spam/timestamp），
 * 当违规分数达到 {@link AutobanFactory} 定义的阈值时触发自动封禁。
 *
 * @author kevintjuh93
 */
public class AutobanManager {
    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(AutobanManager.class);

    /** 所属角色 */
    private final Character chr;
    /** 违规分数映射（违规类型→分数） */
    private final Map<AutobanFactory, Integer> points = new HashMap<>();
    /** 最后违规时间映射（违规类型→时间戳） */
    private final Map<AutobanFactory, Long> lastTime = new HashMap<>();
    /** 当前MISS计数 */
    private int misses = 0;
    /** 上次MISS计数 */
    private int lastmisses = 0;
    /** 连续相同MISS次数 */
    private int samemisscount = 0;
    /** 垃圾信息时间戳数组 */
    private final long[] spam = new long[20];
    /** 操作时间戳数组 */
    private final int[] timestamp = new int[20];
    /** 操作计数器数组 */
    private final byte[] timestampcounter = new byte[20];

    /**
     * 构造自动封禁管理器
     * @param chr 所属角色
     */
    public AutobanManager(Character chr) {
        this.chr = chr;
    }

    /**
     * 增加违规分数
     * <p>分数达到阈值时自动封禁角色</p>
     *
     * @param fac 违规类型
     * @param reason 违规原因
     */
    public void addPoint(AutobanFactory fac, String reason) {
        if (GameConfig.getServerBoolean("use_auto_ban")) {
            if (chr.isGM() || chr.isBanned()) {
                return;
            }

            if (lastTime.containsKey(fac)) {
                if (lastTime.get(fac) < (Server.getInstance().getCurrentTime() - fac.getExpire())) {
                    points.put(fac, points.get(fac) / 2);
                }
            }
            if (fac.getExpire() != -1) {
                lastTime.put(fac, Server.getInstance().getCurrentTime());
            }

            if (points.containsKey(fac)) {
                points.put(fac, points.get(fac) + 1);
            } else {
                points.put(fac, 1);
            }

            if (points.get(fac) >= fac.getMaximum()) {
                chr.autoBan(reason);
            }
        }
        if (GameConfig.getServerBoolean("use_auto_ban_log")) {
            log.info("Autoban - chr {} caused {} {}", Character.makeMapleReadable(chr.getName()), fac.name(), reason);
        }
    }

    /**
     * 增加MISS计数
     */
    public void addMiss() {
        this.misses++;
    }

    /**
     * 重置MISS计数
     * <p>检测连续多次相同MISS数，可能是无敌外挂</p>
     */
    public void resetMisses() {
        if (lastmisses == misses && misses > 6) {
            samemisscount++;
        }
        if (samemisscount > 4) {
            chr.sendPolice("You will be disconnected for miss godmode.");
        } else if (samemisscount > 0) {
            this.lastmisses = misses;
        }
        this.misses = 0;
    }

    /**
     * 记录垃圾信息时间戳
     * @param type 垃圾信息类型
     */
    public void spam(int type) {
        this.spam[type] = Server.getInstance().getCurrentTime();
    }

    /**
     * 记录垃圾信息时间戳（自定义时间）
     * @param type 垃圾信息类型
     * @param timestamp 时间戳
     */
    public void spam(int type, int timestamp) {
        this.spam[type] = timestamp;
    }

    /**
     * 获取上次垃圾信息时间
     * @param type 垃圾信息类型
     * @return 时间戳
     */
    public long getLastSpam(int type) {
        return spam[type];
    }

    /**
     * 操作频率检测
     * <p>检测同一帧内重复操作次数，超过阈值则断开连接</p>
     *
     * <code>type</code>:<br>
     * 1: Pet Food<br>
     * 2: InventoryMerge<br>
     * 3: InventorySort<br>
     * 4: SpecialMove<br>
     * 5: UseCatchItem<br>
     * 6: Item Drop<br>
     * 7: Chat<br>
     * 8: HealOverTimeHP<br>
     * 9: HealOverTimeMP<br>
     *
     * @param type 操作类型
     * @param time 当前帧时间
     * @param times 允许的最大重复次数
     */
    public void setTimestamp(int type, int time, int times) {
        if (this.timestamp[type] == time) {
            this.timestampcounter[type]++;
            if (this.timestampcounter[type] >= times) {
                if (GameConfig.getServerBoolean("use_auto_ban")) {
                    chr.getClient().disconnect(false, false);
                }
                log.info("Autoban - Chr {} was caught spamming TYPE {} and has been disconnected", chr, type);
            }
        } else {
            this.timestamp[type] = time;
            this.timestampcounter[type] = 0;
        }
    }
}