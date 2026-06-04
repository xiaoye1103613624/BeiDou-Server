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
 * 【类型】SpawnPoint（class），包 {@code org.gms.server.life}。
 * 怪物刷新点，记录地图上怪物的刷新位置、刷新间隔与生成条件。
 * 通过 {@link #shouldSpawn()} 判断是否达到刷新时机，{@link #getMonster()} 创建怪物实例并注册死亡监听器。
 */
public class SpawnPoint {
    /** 怪物ID */
    private final int monster;
    /** 怪物刷新冷却时间（秒，-1表示不刷新） */
    private final int mobTime;
    /** 怪物所属队伍 */
    private final int team;
    /** 怪物站立平台高度 */
    private final int fh;
    /** 怪物朝向（左/右） */
    private final int f;
    /** 刷新位置坐标 */
    private final Point pos;
    /** 下次可刷新时间戳 */
    private long nextPossibleSpawn;
    /** 怪物刷新间隔（毫秒） */
    private int mobInterval = 5000;
    /** 当前已刷新的怪物数量 */
    private final AtomicInteger spawnedMonsters = new AtomicInteger(0);
    /** 是否为不可移动怪物 */
    private final boolean immobile;
    /** 是否禁止刷新 */
    private boolean denySpawn = false;
    /** 重生速率倍率（0.1~1.0，越小越快，1.0=正常速度） */
    private float respawnRateMultiplier = 1.0f;

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

    public int getSpawned() {
        return spawnedMonsters.intValue();
    }

    public void setDenySpawn(boolean val) {
        denySpawn = val;
    }

    public boolean getDenySpawn() {
        return denySpawn;
    }

    public boolean shouldSpawn() {
        if (denySpawn || mobTime < 0 || spawnedMonsters.get() > 0) {
            return false;
        }
        return nextPossibleSpawn <= Server.getInstance().getCurrentTime();
    }

    public boolean shouldForceSpawn() {
        return mobTime >= 0 && spawnedMonsters.get() <= 0;
    }

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
                    // 应用重生速率倍率，加速怪物刷新
                    nextPossibleSpawn += (long) (SECONDS.toMillis(mobTime) * respawnRateMultiplier);
                } else {
                    nextPossibleSpawn += (long) (aniTime * respawnRateMultiplier);
                }
                spawnedMonsters.decrementAndGet();
            }

            @Override
            public void monsterDamaged(Character from, int trueDmg) {}

            @Override
            public void monsterHealed(int trueHeal) {}
        });
        if (mobTime == 0) {
            // 应用重生速率倍率
            nextPossibleSpawn = Server.getInstance().getCurrentTime() + (long) (mobInterval * respawnRateMultiplier);
        }
        return mob;
    }

    public int getMonsterId() {
        return monster;
    }

    public Point getPosition() {
        return pos;
    }

    public final int getF() {
        return f;
    }

    public final int getFh() {
        return fh;
    }

    public int getMobTime() {
        return mobTime;
    }

    public int getTeam() {
        return team;
    }

    /**
     * 设置重生速率倍率（0.1~1.0，越小怪物重生越快，1.0=正常速度）
     */
    public void setRespawnRateMultiplier(float multiplier) {
        this.respawnRateMultiplier = Math.max(0.1f, Math.min(multiplier, 1.0f));
    }

    public float getRespawnRateMultiplier() {
        return respawnRateMultiplier;
    }

    /**
     * 立即对正在等待重生的刷怪点应用新的倍率。
     * 将剩余等待时间按新旧倍率比例缩放。
     */
    public void applyRespawnReduction(float oldMultiplier) {
        if (oldMultiplier <= 0.0f || respawnRateMultiplier <= 0.0f) {
            return;
        }
        long now = Server.getInstance().getCurrentTime();
        if (spawnedMonsters.get() <= 0 && nextPossibleSpawn > now) {
            long remaining = nextPossibleSpawn - now;
            nextPossibleSpawn = now + (long) (remaining * respawnRateMultiplier / oldMultiplier);
        }
    }
}
