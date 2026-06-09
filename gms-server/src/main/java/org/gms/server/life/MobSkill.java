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
import org.gms.client.Disease;
import org.gms.client.status.MonsterStatus;
import org.gms.constants.id.MapId;
import org.gms.constants.id.MobId;
import org.gms.constants.skills.Bishop;
import org.gms.net.server.services.task.channel.OverallService;
import org.gms.net.server.services.type.ChannelServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.maps.MapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.Mist;
import org.gms.util.Randomizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 怪物技能
 * 代表怪物可以使用的技能，包含技能参数配置和技能效果执行逻辑
 * 支持多种技能类型：增益、伤害、召唤、异常状态、区域毒雾等
 * 使用Builder模式构造，确保参数完整性
 *
 * @author Danny (Leifde)
 */
public class MobSkill {
    private static final Logger log = LoggerFactory.getLogger(MobSkill.class);

    /** 技能ID */
    private final MobSkillId id;
    /** MP消耗 */
    private final int mpCon;
    /** 召唤特效 */
    private final int spawnEffect;
    /** 技能HP参数 */
    private final int hp;
    /** 技能X参数 */
    private final int x;
    /** 技能Y参数 */
    private final int y;
    /** 技能数量参数 */
    private final int count;
    /** 持续时间（毫秒） */
    private final long duration;
    /** 冷却时间（毫秒） */
    private final long cooltime;
    /** 触发概率（0.0~1.0） */
    private final float prop;
    /** 技能范围左上角 */
    private final Point lt;
    /** 技能范围右下角 */
    private final Point rb;
    /** 召唤数量限制 */
    private final int limit;
    /** 召唤怪物ID列表 */
    private final List<Integer> toSummon;

    private MobSkill(MobSkillType type, int level, int mpCon, int spawnEffect, int hp, int x, int y, int count,
                     long duration, long cooltime, float prop, Point lt, Point rb, int limit, List<Integer> toSummon) {
        this.id = new MobSkillId(type, level);
        this.mpCon = mpCon;
        this.spawnEffect = spawnEffect;
        this.hp = hp;
        this.x = x;
        this.y = y;
        this.count = count;
        this.duration = duration;
        this.cooltime = cooltime;
        this.prop = prop;
        this.lt = lt;
        this.rb = rb;
        this.limit = limit;
        this.toSummon = toSummon;
    }

    /**
     * 怪物技能建造器
     * 使用Builder模式构建MobSkill，支持链式调用设置所有参数
     */
    static class Builder {
        private final MobSkillType type;
        private final int level;
        private int mpCon;
        private int spawnEffect;
        private int hp;
        private int x;
        private int y;
        private int count;
        private long duration;
        private long cooltime;
        private float prop;
        private Point lt;
        private Point rb;
        private int limit;
        private List<Integer> toSummon;

        /**
         * 构造建造器
         *
         * @param type 技能类型
         * @param level 技能等级
         */
        public Builder(MobSkillType type, int level) {
            this.type = type;
            this.level = level;
        }

        public Builder mpCon(int mpCon) {
            this.mpCon = mpCon;
            return this;
        }

        public Builder spawnEffect(int spawnEffect) {
            this.spawnEffect = spawnEffect;
            return this;
        }

        public Builder hp(int hp) {
            this.hp = hp;
            return this;
        }

        public Builder x(int x) {
            this.x = x;
            return this;
        }

        public Builder y(int y) {
            this.y = y;
            return this;
        }

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder cooltime(long cooltime) {
            this.cooltime = cooltime;
            return this;
        }

        public Builder prop(float prop) {
            this.prop = prop;
            return this;
        }

        public Builder lt(Point lt) {
            this.lt = lt;
            return this;
        }

        public Builder rb(Point rb) {
            this.rb = rb;
            return this;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder toSummon(List<Integer> toSummon) {
            this.toSummon = Collections.unmodifiableList(toSummon);
            return this;
        }

        /**
     * 构建MobSkill对象
     *
     * @return 构建完成的MobSkill
     */
    public MobSkill build() {
            return new MobSkill(type, level, mpCon, spawnEffect, hp, x, y, count, duration, cooltime, prop, lt, rb,
                    limit, toSummon);
        }
    }

    /**
     * 延迟执行技能效果
     * 经过animationTime毫秒后执行技能效果，前提是怪物仍然存活
     *
     * @param player 目标玩家
     * @param monster 释放技能的怪物
     * @param skill 是否为技能（true表示范围技能）
     * @param animationTime 动画时间（毫秒）
     */
    public void applyDelayedEffect(final Character player, final Monster monster, final boolean skill, int animationTime) {
        Runnable toRun = () -> {
            if (monster.isAlive()) {
                applyEffect(player, monster, skill, null);
            }
        };

        OverallService service = (OverallService) monster.getMap().getChannelServer().getServiceAccess(ChannelServices.OVERALL);
        service.registerOverallAction(monster.getMap().getId(), toRun, animationTime);
    }

    /**
     * 对怪物自身应用技能效果
     *
     * @param monster 目标怪物
     */
    public void applyEffect(Monster monster) {
        applyEffect(null, monster, false, Collections.emptyList());
    }

    /**
     * 应用技能效果
     * 根据技能类型执行不同的效果：属性增益、治疗、异常状态、驱散、放逐、区域毒雾、召唤等
     * 首先根据概率判定是否成功触发
     *
     * @param player 目标玩家（单体技能时为具体玩家，范围技能时为null）
     * @param monster 释放技能的怪物
     * @param skill 是否为范围技能
     * @param banishPlayersOutput 被放逐的玩家列表（输出参数）
     */
    public void applyEffect(Character player, Monster monster, boolean skill, List<Character> banishPlayersOutput) {
        // See if the MobSkill is successful before doing anything
        if (!makeChanceResult()) {
            return;
        }

        Disease disease = null;
        Map<MonsterStatus, Integer> stats = new EnumMap<>(MonsterStatus.class);
        List<Integer> reflection = new ArrayList<>();
        switch (id.type()) {
            case ATTACK_UP, ATTACK_UP_M, PAD -> stats.put(MonsterStatus.WEAPON_ATTACK_UP, x);
            case MAGIC_ATTACK_UP, MAGIC_ATTACK_UP_M, MAD -> stats.put(MonsterStatus.MAGIC_ATTACK_UP, x);
            case DEFENSE_UP, DEFENSE_UP_M, PDR -> stats.put(MonsterStatus.WEAPON_DEFENSE_UP, x);
            case MAGIC_DEFENSE_UP, MAGIC_DEFENSE_UP_M, MDR -> stats.put(MonsterStatus.MAGIC_DEFENSE_UP, x);
            case HEAL_M -> applyHealEffect(skill, monster);
            case SEAL -> disease = Disease.SEAL;
            case DARKNESS -> disease = Disease.DARKNESS;
            case WEAKNESS -> disease = Disease.WEAKEN;
            case STUN -> disease = Disease.STUN;
            case CURSE -> disease = Disease.CURSE;
            case POISON -> disease = Disease.POISON;
            case SLOW -> disease = Disease.SLOW;
            case DISPEL -> applyDispelEffect(skill, monster, player);
            case SEDUCE -> disease = Disease.SEDUCE;
            case BANISH -> applyBanishEffect(skill, monster, player, banishPlayersOutput);
            case AREA_POISON -> spawnMonsterMist(monster);
            case REVERSE_INPUT -> disease = Disease.CONFUSE;
            case UNDEAD -> disease = Disease.ZOMBIFY;
            case PHYSICAL_IMMUNE -> {
                if (!monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY)) {
                    stats.put(MonsterStatus.WEAPON_IMMUNITY, x);
                }
            }
            case MAGIC_IMMUNE -> {
                if (!monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY)) {
                    stats.put(MonsterStatus.MAGIC_IMMUNITY, x);
                }
            }
            case PHYSICAL_COUNTER -> {
                stats.put(MonsterStatus.WEAPON_REFLECT, 10);
                stats.put(MonsterStatus.WEAPON_IMMUNITY, 10);
                reflection.add(x);
            }
            case MAGIC_COUNTER -> {
                stats.put(MonsterStatus.MAGIC_REFLECT, 10);
                stats.put(MonsterStatus.MAGIC_IMMUNITY, 10);
                reflection.add(x);
            }
            case PHYSICAL_AND_MAGIC_COUNTER -> {
                stats.put(MonsterStatus.WEAPON_REFLECT, 10);
                stats.put(MonsterStatus.WEAPON_IMMUNITY, 10);
                stats.put(MonsterStatus.MAGIC_REFLECT, 10);
                stats.put(MonsterStatus.MAGIC_IMMUNITY, 10);
                reflection.add(x);
            }
            case ACC -> stats.put(MonsterStatus.ACC, x);
            case EVA -> stats.put(MonsterStatus.AVOID, x);
            case SPEED -> stats.put(MonsterStatus.SPEED, x);
            case SEAL_SKILL -> stats.put(MonsterStatus.SEAL_SKILL, x);
            case SUMMON -> summonMonsters(monster);
        }
        if (stats.size() > 0) {
            applyMonsterBuffs(stats, skill, monster, reflection);
        }
        if (disease != null) {
            applyDisease(disease, skill, monster, player);
        }
    }

    /**
     * 对范围或单体目标执行治疗
     * 范围技能时治疗范围内所有怪物，单体技能时仅治疗自身
     *
     * @param skill 是否为范围技能
     * @param monster 释放技能的怪物
     */
    private void applyHealEffect(boolean skill, Monster monster) {
        if (lt != null && rb != null && skill) {
            List<MapObject> objects = getObjectsInRange(monster, MapObjectType.MONSTER);
            final int hps = (getX() / 1000) * (int) (950 + 1050 * Math.random());
            for (MapObject mons : objects) {
                ((Monster) mons).heal(hps, getY());
            }
        } else {
            monster.heal(getX(), getY());
        }
    }

    /**
     * 对范围或单体目标执行驱散
     * 范围技能时驱散范围内所有玩家的增益效果，单体技能时仅驱散目标玩家
     *
     * @param skill 是否为范围技能
     * @param monster 释放技能的怪物
     * @param player 目标玩家
     */
    private void applyDispelEffect(boolean skill, Monster monster, Character player) {
        if (lt != null && rb != null && skill) {
            getPlayersInRange(monster).forEach(Character::dispel);
        } else {
            player.dispel();
        }
    }

    /**
     * 对范围或单体目标执行放逐
     *
     * @param skill 是否为范围技能
     * @param monster 释放技能的怪物
     * @param player 目标玩家
     * @param banishPlayersOutput 被放逐的玩家列表（输出参数）
     */
    private void applyBanishEffect(boolean skill, Monster monster, Character player,
                                   List<Character> banishPlayersOutput) {
        if (lt != null && rb != null && skill) {
            banishPlayersOutput.addAll(getPlayersInRange(monster));
        } else {
            banishPlayersOutput.add(player);
        }
    }

    /**
     * 在怪物位置生成区域毒雾
     * 毒雾区域由技能配置的lt/rb和怪物当前位置计算得出
     * 毒雾持续时间由x参数乘以100毫秒
     *
     * @param monster 释放技能的怪物
     */
    private void spawnMonsterMist(Monster monster) {
        Rectangle mistArea = calculateBoundingBox(monster.getPosition());
        var mist = new Mist(mistArea, monster, this);
        int mistDuration = x * 100;
        monster.getMap().spawnMist(mist, mistDuration, false, false, false);
    }

    /**
     * 召唤怪物
     * 在当前地图生成技能配置的召唤怪物，考虑地图怪物数量限制
     * 特殊处理武陵道场、BOSS RUSH、帕普拉图斯、皮亚努斯等特殊地图
     *
     * @param monster 释放技能的怪物
     */
    private void summonMonsters(Monster monster) {
        int skillLimit = this.limit;
        MapleMap map = monster.getMap();

        if (MapId.isDojo(map.getId())) {  // spawns in dojo should be unlimited
            skillLimit = Integer.MAX_VALUE;
        }

        if (map.getSpawnedMonstersOnMap() < 80) {
            List<Integer> summons = new ArrayList<>(toSummon);
            int summonLimit = monster.countAvailableMobSummons(summons.size(), skillLimit);
            if (summonLimit >= 1) {
                boolean bossRushMap = MapId.isBossRush(map.getId());

                Collections.shuffle(summons);
                for (Integer mobId : summons.subList(0, summonLimit)) {
                    Monster toSpawn = LifeFactory.getMonster(mobId);
                    if (toSpawn != null) {
                        if (bossRushMap) {
                            toSpawn.disableDrops();  // no littering on BRPQ pls
                        }
                        toSpawn.setPosition(monster.getPosition());
                        int ypos, xpos;
                        xpos = (int) monster.getPosition().getX();
                        ypos = (int) monster.getPosition().getY();
                        switch (mobId) {
                            case MobId.HIGH_DARKSTAR: // Pap bomb high
                                toSpawn.setFh((int) Math.ceil(Math.random() * 19.0));
                                ypos = -590;
                                break;
                            case MobId.LOW_DARKSTAR: // Pap bomb
                                xpos = (int) (monster.getPosition().getX() + Randomizer.nextInt(1000) - 500);
                                if (ypos != -590) {
                                    ypos = (int) monster.getPosition().getY();
                                }
                                break;
                            case MobId.BLOODY_BOOM: //Pianus bomb
                                if (Math.ceil(Math.random() * 5) == 1) {
                                    ypos = 78;
                                    xpos = Randomizer.nextInt(5) + (Randomizer.nextInt(2) == 1 ? 180 : 0);
                                } else {
                                    xpos = (int) (monster.getPosition().getX() + Randomizer.nextInt(1000) - 500);
                                }
                                break;
                        }
                        switch (map.getId()) {
                            case MapId.ORIGIN_OF_CLOCKTOWER: //Pap map
                                if (xpos < -890) {
                                    xpos = (int) (Math.ceil(Math.random() * 150) - 890);
                                } else if (xpos > 230) {
                                    xpos = (int) (230 - Math.ceil(Math.random() * 150));
                                }
                                break;
                            case MapId.CAVE_OF_PIANUS: // Pianus map
                                if (xpos < -239) {
                                    xpos = (int) (Math.ceil(Math.random() * 150) - 239);
                                } else if (xpos > 371) {
                                    xpos = (int) (371 - Math.ceil(Math.random() * 150));
                                }
                                break;
                        }
                        toSpawn.setPosition(new Point(xpos, ypos));
                        if (toSpawn.getId() == MobId.LOW_DARKSTAR) {
                            map.spawnFakeMonster(toSpawn);
                        } else {
                            map.spawnMonsterWithEffect(toSpawn, spawnEffect, toSpawn.getPosition());
                        }
                        monster.addSummonedMob(toSpawn);
                    }
                }
            }
        }
    }

    /**
     * 应用怪物增益效果
     * 范围技能时对范围内所有怪物应用增益，单体技能时仅对自身应用
     *
     * @param stats 增益状态映射
     * @param skill 是否为范围技能
     * @param monster 释放技能的怪物
     * @param reflection 反射伤害列表
     */
    private void applyMonsterBuffs(Map<MonsterStatus, Integer> stats, boolean skill, Monster monster, List<Integer> reflection) {
        if (lt != null && rb != null && skill) {
            for (MapObject mons : getObjectsInRange(monster, MapObjectType.MONSTER)) {
                ((Monster) mons).applyMonsterBuff(stats, getX(), getDuration(), this, reflection);
            }
        } else {
            monster.applyMonsterBuff(stats, getX(), getDuration(), this, reflection);
        }
    }

    /**
     * 应用异常状态给玩家
     * 范围技能时对范围内所有玩家应用（除被圣盾保护的玩家），
     * 魅惑状态有数量限制
     *
     * @param disease 异常状态类型
     * @param skill 是否为范围技能
     * @param monster 释放技能的怪物
     * @param player 目标玩家
     */
    private void applyDisease(Disease disease, boolean skill, Monster monster, Character player) {
        if (lt != null && rb != null && skill) {
            int i = 0;
            for (Character character : getPlayersInRange(monster)) {
                if (!character.hasActiveBuff(Bishop.HOLY_SHIELD)) {
                    if (disease.equals(Disease.SEDUCE)) {
                        if (i < count) {
                            character.giveDebuff(Disease.SEDUCE, this);
                            i++;
                        }
                    } else {
                        character.giveDebuff(disease, this);
                    }
                }
            }
        } else {
            player.giveDebuff(disease, this);
        }
    }

    /**
     * 获取技能范围内的玩家列表
     *
     * @param monster 释放技能的怪物
     * @return 范围内的玩家列表
     */
    private List<Character> getPlayersInRange(Monster monster) {
        return monster.getMap().getPlayersInRange(calculateBoundingBox(monster.getPosition()));
    }

    public MobSkillId getId() {
        return id;
    }

    public MobSkillType getType() {
        return id.type();
    }

    public int getMpCon() {
        return mpCon;
    }

    public int getHP() {
        return hp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public long getDuration() {
        return duration;
    }

    public long getCoolTime() {
        return cooltime;
    }

    /**
     * 判断技能是否触发
     * 概率为1.0时必定触发，否则根据概率随机判定
     *
     * @return true表示触发成功
     */
    public boolean makeChanceResult() {
        return prop == 1.0 || Math.random() < prop;
    }

    /**
     * 计算技能范围矩形
     * 以怪物位置为基准，偏移lt/rb得到实际范围
     *
     * @param posFrom 基准位置
     * @return 技能范围矩形
     */
    private Rectangle calculateBoundingBox(Point posFrom) {
        Point mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
        Point myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        Rectangle bounds = new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
        return bounds;
    }

    /**
     * 获取技能范围内的地图对象
     *
     * @param monster 释放技能的怪物
     * @param objectType 对象类型
     * @return 范围内的对象列表
     */
    private List<MapObject> getObjectsInRange(Monster monster, MapObjectType objectType) {
        return monster.getMap().getMapObjectsInBox(calculateBoundingBox(monster.getPosition()), Collections.singletonList(objectType));
    }
}