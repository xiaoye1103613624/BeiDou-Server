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
package org.gms.server.life;

import org.gms.client.Character;
import org.gms.net.server.Server;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 怪物刷新点
 * 管理单个怪物刷新位置，控制刷新时间和已刷新数量，支持组队通道设置
 * 使用原子变量保证多线程安全的已刷怪物计数
 *
 * @author OdinMS Team
 */
public class SpawnPoint {
    /** 怪物ID */
    private final int monster;
    /** 刷新冷却时间（秒），0表示不冷却，负数表示不自动刷新 */
    private final int mobTime;
    /** 队伍频道编号，用于区分不同队伍刷怪 */
    private final int team;
    /** 初始站立平台ID */
    private final int fh;
    /** 初始朝向 */
    private final int f;
    /** 刷新位置坐标 */
    private final Point pos;
    /** 下次可刷新时间戳（毫秒） */
    private long nextPossibleSpawn;
    /** 固定刷新间隔（毫秒），mobTime为0时使用 */
    private int mobInterval = 5000;
    /** 当前已刷新怪物数量，原子变量保证线程安全 */
    private final AtomicInteger spawnedMonsters = new AtomicInteger(0);
    /** 是否固定位置 immobile=true表示怪物不能移动位置 */
    private final boolean immobile;
    /** 是否拒绝刷新，用于地图事件等场景临时关闭刷新 */
    private boolean denySpawn = false;

    /**
     * 构造怪物刷新点
     * 根据怪物配置初始化，设置下次刷新时间为当前时间立即可刷新
     *
     * @param monster 怪物对象
     * @param pos 刷新位置
     * @param immobile 是否固定位置
     * @param mobTime 刷新冷却时间（秒）
     * @param mobInterval 固定刷新间隔（毫秒）
     * @param team 队伍频道编号
     */
    public SpawnPoint(final Monster monster, Point pos, boolean immobile, int mobTime, int mobInterval, int team) {
        this.monster = monster.getId();
        this.pos = new Point(pos);
        this.mobTime = mobTime;
        this.team = team;
        this.fh = monster.getFh();
        this.f = monster.getF();
        this.immobile = immobile;
        this.mobInterval = mobInterval;
        this.nextPossibleSpawn = Server.getInstance().getCurrentTime();
    }

    /**
     * 获取当前已刷新怪物数量
     *
     * @return 已刷新怪物数量
     */
    public int getSpawned() {
        return spawnedMonsters.intValue();
    }

    /**
     * 设置是否拒绝刷新
     * 用于地图事件、BOSS战等场景临时关闭怪物刷新
     *
     * @param val true表示拒绝刷新
     */
    public void setDenySpawn(boolean val) {
        denySpawn = val;
    }

    /**
     * 获取是否拒绝刷新
     *
     * @return true表示拒绝刷新
     */
    public boolean getDenySpawn() {
        return denySpawn;
    }

    /**
     * 判断是否应该刷新怪物
     * 满足条件：未拒绝刷新、mobTime>=0、当前无已刷新怪物、且到达刷新时间
     *
     * @return true表示可以刷新
     */
    public boolean shouldSpawn() {
        if (denySpawn || mobTime < 0 || spawnedMonsters.get() > 0) {
            return false;
        }
        return nextPossibleSpawn <= Server.getInstance().getCurrentTime();
    }

    /**
     * 判断是否应强制刷新
     * 当mobTime>=0且当前无已刷新怪物时返回true
     *
     * @return true表示需要强制刷新
     */
    public boolean shouldForceSpawn() {
        return mobTime >= 0 && spawnedMonsters.get() <= 0;
    }

    /**
     * 在此刷新点生成怪物
     * 创建怪物对象并设置位置、队伍、平台等属性，同时注册怪物死亡/伤害/治疗事件监听器
     * 当怪物死亡时，根据mobTime计算下次刷新时间：
     * - mobTime>0：使用配置的冷却时间
     * - mobTime=0：使用动画时间
     * - mobTime<0：不自动刷新
     *
     * @return 生成的怪物对象
     */
    public Monster getMonster() {
        Monster mob = new Monster(LifeFactory.getMonster(monster));
        mob.setPosition(new Point(pos));
        mob.setTeam(team);
        mob.setFh(fh);
        mob.setF(f);
        spawnedMonsters.incrementAndGet();
        mob.addListener(new MonsterListener() {
            @Override
            public void monsterKilled(int aniTime) {
                nextPossibleSpawn = Server.getInstance().getCurrentTime();
                if (mobTime > 0) {
                    nextPossibleSpawn += SECONDS.toMillis(mobTime);
                } else {
                    nextPossibleSpawn += aniTime;
                }
                spawnedMonsters.decrementAndGet();
            }

            @Override
            public void monsterDamaged(Character from, int trueDmg) {}

            @Override
            public void monsterHealed(int trueHeal) {}
        });
        if (mobTime == 0) {
            nextPossibleSpawn = Server.getInstance().getCurrentTime() + mobInterval;
        }
        return mob;
    }

    /**
     * 获取怪物ID
     *
     * @return 怪物ID
     */
    public int getMonsterId() {
        return monster;
    }

    /**
     * 获取刷新位置
     *
     * @return 刷新位置坐标
     */
    public Point getPosition() {
        return pos;
    }

    /**
     * 获取初始朝向
     *
     * @return 朝向（0=右, 1=左）
     */
    public final int getF() {
        return f;
    }

    /**
     * 获取初始站立平台ID
     *
     * @return 平台ID
     */
    public final int getFh() {
        return fh;
    }

    /**
     * 获取刷新冷却时间
     *
     * @return 冷却时间（秒）
     */
    public int getMobTime() {
        return mobTime;
    }

    /**
     * 获取队伍频道编号
     *
     * @return 队伍频道编号
     */
    public int getTeam() {
        return team;
    }
}