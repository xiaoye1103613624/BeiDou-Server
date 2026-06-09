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

import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 怪物技能工厂
 * 从WZ文件中加载和缓存怪物技能配置，使用读写锁保证线程安全
 * 支持按类型和等级获取技能，缓存未命中时动态加载
 *
 * @author Danny (Leifde)
 */
public class MobSkillFactory {
    /** 怪物技能缓存 */
    private static final Map<String, MobSkill> mobSkills = new HashMap<>();
    /** WZ技能数据源 */
    private static final DataProvider dataSource = DataProviderFactory.getDataProvider(WZFiles.SKILL);
    /** 技能根数据 */
    private static final Data skillRoot = dataSource.getData("MobSkill.img");
    /** 读写锁 */
    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    /** 读锁 */
    private static final Lock readLock = readWriteLock.readLock();
    /** 写锁 */
    private static final Lock writeLock = readWriteLock.writeLock();

    /**
     * 获取怪物技能，不存在时抛出异常
     *
     * @param type 技能类型
     * @param level 技能等级
     * @return 怪物技能
     * @throws IllegalArgumentException 技能不存在时抛出
     */
    public static MobSkill getMobSkillOrThrow(MobSkillType type, int level) {
        return getMobSkill(type, level).orElseThrow(
                () -> new IllegalArgumentException("No MobSkill exists for type %s, level %d".formatted(type, level))
        );
    }

    /**
     * 获取怪物技能
     * 先使用读锁从缓存查找，未命中则使用写锁从WZ文件加载
     *
     * @param type 技能类型
     * @param level 技能等级
     * @return 包含怪物技能的Optional
     */
    public static Optional<MobSkill> getMobSkill(final MobSkillType type, final int level) {
        readLock.lock();
        try {
            MobSkill ms = mobSkills.get(createKey(type, level));
            if (ms != null) {
                return Optional.of(ms);
            }
        } finally {
            readLock.unlock();
        }

        return loadMobSkill(type, level);
    }

    /**
     * 从WZ文件加载怪物技能
     * 使用写锁和双重检查锁定，避免重复加载
     *
     * @param type 技能类型
     * @param level 技能等级
     * @return 包含已加载技能的Optional
     */
    private static Optional<MobSkill> loadMobSkill(final MobSkillType type, final int level) {
        writeLock.lock();
        try {
            MobSkill existingMs = mobSkills.get(createKey(type, level));
            if (existingMs != null) {
                return Optional.of(existingMs);
            }

            Data skillData = skillRoot.getChildByPath("%d/level/%d".formatted(type.getId(), level));
            if (skillData == null) {
                return Optional.empty();
            }

            int mpCon = DataTool.getInt("mpCon", skillData, 0);
            List<Integer> toSummon = new ArrayList<>();
            for (int i = 0; i > -1; i++) {
                if (skillData.getChildByPath(String.valueOf(i)) == null) {
                    break;
                }
                toSummon.add(DataTool.getInt(skillData.getChildByPath(String.valueOf(i)), 0));
            }
            int effect = DataTool.getInt("summonEffect", skillData, 0);
            int hp = DataTool.getInt("hp", skillData, 100);
            int x = DataTool.getInt("x", skillData, 100);
            int y = DataTool.getInt("y", skillData, 100);
            int count = DataTool.getInt("count", skillData, 100);
            long duration = SECONDS.toMillis(DataTool.getInt("time", skillData, 0));
            long cooltime = SECONDS.toMillis(DataTool.getInt("interval", skillData, 0));
            int iprop = DataTool.getInt("prop", skillData, 100);
            float prop = iprop / 100.0f;
            int limit = DataTool.getInt("limit", skillData, 0);

            Data ltData = skillData.getChildByPath("lt");
            Data rbData = skillData.getChildByPath("rb");
            Point lt = null;
            Point rb = null;
            if (ltData != null && rbData != null) {
                lt = (Point) ltData.getData();
                rb = (Point) rbData.getData();
            }

            MobSkill loadedMobSkill = new MobSkill.Builder(type, level)
                    .mpCon(mpCon)
                    .toSummon(toSummon)
                    .cooltime(cooltime)
                    .duration(duration)
                    .hp(hp)
                    .x(x)
                    .y(y)
                    .count(count)
                    .prop(prop)
                    .limit(limit)
                    .lt(lt)
                    .rb(rb)
                    .build();

            mobSkills.put(createKey(type, level), loadedMobSkill);
            return Optional.of(loadedMobSkill);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 生成缓存键
     *
     * @param type 技能类型
     * @param skillLevel 技能等级
     * @return 缓存键字符串
     */
    private static String createKey(MobSkillType type, int skillLevel) {
        return type.getId() + "" + skillLevel;
    }
}