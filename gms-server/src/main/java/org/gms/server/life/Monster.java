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

import org.gms.client.BuffStat;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.FamilyEntry;
import org.gms.client.Job;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.client.status.MonsterStatus;
import org.gms.client.status.MonsterStatusEffect;
import org.gms.config.CatchUpExpConfigManager;
import org.gms.config.GameConfig;
import org.gms.constants.id.MobId;
import org.gms.constants.skills.Crusader;
import org.gms.constants.skills.FPMage;
import org.gms.constants.skills.Hermit;
import org.gms.constants.skills.ILMage;
import org.gms.constants.skills.NightLord;
import org.gms.constants.skills.NightWalker;
import org.gms.constants.skills.Priest;
import org.gms.constants.skills.Shadower;
import org.gms.constants.skills.WhiteKnight;
import org.gms.net.packet.Packet;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.coordinator.world.MonsterAggroCoordinator;
import org.gms.net.server.services.task.channel.MobAnimationService;
import org.gms.net.server.services.task.channel.MobClearSkillService;
import org.gms.net.server.services.task.channel.MobStatusService;
import org.gms.net.server.services.task.channel.OverallService;
import org.gms.net.server.services.type.ChannelServices;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.event.EventInstanceManager;
import org.gms.server.StatEffect;
import org.gms.server.TimerManager;
import org.gms.server.life.LifeFactory.BanishInfo;
import org.gms.server.loot.LootManager;
import org.gms.server.maps.AbstractAnimatedMapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.Summon;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 【类型】Monster（class），包 `org.gms.server.life`。
 *
 * 游戏怪物的核心类，继承 {@link AbstractLoadedLife}，代表地图上一个活着的怪物实例。
 * 负责管理怪物的 HP/MP、状态效果（BUFF/DEBUFF）、仇恨系统、控制器（谁在打）、掉落物生成、
 * 技能使用、召唤小怪、自毁倒计时等完整的怪物生命周期。
 *
 * 关键机制：
 * <ul>
 *   <li><b>控制器（controller）</b>：第一个攻击怪物的玩家成为控制器，负责接收怪物的移动/攻击同步数据</li>
 *   <li><b>仇恨追踪</b>：通过 {@link MonsterAggroCoordinator} 追踪每个攻击者的伤害贡献，决定掉落归属</li>
 *   <li><b>状态系统</b>：通过 {@link MonsterStatus} 和 {@link MonsterStatusEffect} 管理封印、眩晕、中毒等异常状态</li>
 *   <li><b>掉落系统</b>：死亡时通过 {@link org.gms.server.loot.LootManager} 计算并生成掉落物</li>
 *   <li><b>Banish 机制</b>：部分 BOSS 可将玩家驱逐出地图，由 {@link LifeFactory.BanishInfo} 配置</li>
 * </ul>
 *
 * @see MonsterStats
 * @see MonsterInformationProvider
 * @see org.gms.server.loot.LootManager
 */
public class Monster extends AbstractLoadedLife {
    private static final Logger log = LoggerFactory.getLogger(Monster.class);

    /** 可变属性（v83 WZ 不支持，未使用） */
    private ChangeableStats ostats = null;
    /** 怪物基础属性（HP/MP/EXP/攻击力等），从 WZ 加载 */
    private MonsterStats stats;
    /** 当前 HP（原子操作，保证多线程安全） */
    private final AtomicInteger hp = new AtomicInteger(1);
    /** 最大 HP + 治疗量上限（怪物治疗不能超过此值） */
    private final AtomicLong maxHpPlusHeal = new AtomicLong(1);
    /** 当前 MP */
    private int mp;
    /** 当前控制器（第一个攻击此怪的玩家），WeakReference 防止内存泄漏 */
    private WeakReference<Character> controller = new WeakReference<>(null);
    /** 控制器是否已建立仇恨 */
    private boolean controllerHasAggro;
    /** 控制器是否知道自己有仇恨 */
    private boolean controllerKnowsAboutAggro;
    /** 控制器是否放置了替身（飞镖职业的 puppet） */
    private boolean controllerHasPuppet;
    /** 怪物死亡监听器列表 */
    private final Collection<MonsterListener> listeners = new LinkedList<>();
    /** 当前状态效果集合（EnumMap 高效枚举索引） */
    private final EnumMap<MonsterStatus, MonsterStatusEffect> stati = new EnumMap<>(MonsterStatus.class);
    /** 已经施加过的 BUFF 列表（防止重复施放） */
    private final ArrayList<MonsterStatus> alreadyBuffed = new ArrayList<>();
    /** 所在的地图实例 */
    private MapleMap map;
    /** 毒液（Venom）层数 */
    private int VenomMultiplier = 0;
    /** 是否为假怪物（仅动画，不参与战斗） */
    private boolean fake = false;
    /** 是否禁用掉落 */
    private boolean dropsDisabled = false;
    /** 已使用的技能 ID 集合（防止同技能重复使用） */
    private final Set<MobSkillId> usedSkills = new HashSet<>();
    /** 已使用的攻击 ID 集合 */
    private final Set<Integer> usedAttacks = new HashSet<>();
    /** 召唤出的小怪 OID 集合 */
    private Set<Integer> calledMobOids = null;
    /** 召唤本怪物的父怪物（WeakReference） */
    private WeakReference<Monster> callerMob = new WeakReference<>(null);
    /** 被偷取的物品 ID 列表（飞侠技能） */
    private final List<Integer> stolenItems = new ArrayList<>(5);
    /** 队伍标识（用于组队副本） */
    private int team;
    /** 父怪物 OID */
    private int parentMobOid = 0;
    /** 生成特效 ID */
    private int spawnEffect = 0;
    /** 各玩家对怪物的累计伤害：<角色ID, 伤害值> */
    private final HashMap<Integer, AtomicLong> takenDamage = new HashMap<>();
    /** 怪物掉落物生成定时任务 */
    private ScheduledFuture<?> monsterItemDrop = null;
    /** 怪物移除后执行的回调 */
    private Runnable removeAfterAction = null;
    /** 是否允许更新傀儡状态标记 */
    private boolean availablePuppetUpdate = true;

    /** 外部调用锁 */
    private final Lock externalLock = new ReentrantLock();
    /** 怪物内部状态锁 */
    private final Lock monsterLock = new ReentrantLock(true);
    /** 状态效果锁 */
    private final Lock statiLock = new ReentrantLock();
    /** 动画/技能锁 */
    private final Lock animationLock = new ReentrantLock();
    /** 仇恨更新锁 */
    private final Lock aggroUpdateLock = new ReentrantLock();

    /**
     * 构造怪物实例
     *
     * @param id    怪物ID
     * @param stats 怪物属性配置
     */
    public Monster(int id, MonsterStats stats) {
        super(id);
        initWithStats(stats);
    }

    /**
     * 复制构造怪物实例
     *
     * @param monster 源怪物实例
     */
    public Monster(Monster monster) {
        super(monster);
        initWithStats(monster.stats);
    }

    /**
     * 锁定怪物外部操作锁，用于保护怪物的并发访问
     */
    public void lockMonster() {
        externalLock.lock();
    }

    /**
     * 解锁怪物外部操作锁
     */
    public void unlockMonster() {
        externalLock.unlock();
    }

    /**
     * 使用基础属性初始化怪物状态
     *
     * @param baseStats 基础属性配置
     */
    private void initWithStats(MonsterStats baseStats) {
        setStance(5);
        this.stats = baseStats.copy();
        hp.set(stats.getHp());
        mp = stats.getMp();

        maxHpPlusHeal.set(hp.get());
    }

    /**
     * 设置怪物生成特效ID
     *
     * @param effect 特效ID
     */
    public void setSpawnEffect(int effect) {
        spawnEffect = effect;
    }

    /**
     * 获取怪物生成特效ID
     *
     * @return 特效ID
     */
    public int getSpawnEffect() {
        return spawnEffect;
    }

    /**
     * 禁用怪物掉落
     */
    public void disableDrops() {
        this.dropsDisabled = true;
    }

    /**
     * 启用怪物掉落
     */
    public void enableDrops() {
        this.dropsDisabled = false;
    }

    /**
     * 检查掉落是否被禁用
     *
     * @return true表示掉落已禁用
     */
    public boolean dropsDisabled() {
        return dropsDisabled;
    }

    /**
     * 设置怪物所在地图
     *
     * @param map 地图实例
     */
    public void setMap(MapleMap map) {
        this.map = map;
    }

    /**
     * 获取父怪物的OID（用于召唤关系）
     *
     * @return 父怪物OID
     */
    public int getParentMobOid() {
        return parentMobOid;
    }

    /**
     * 设置父怪物的OID
     *
     * @param parentMobId 父怪物OID
     */
    public void setParentMobOid(int parentMobId) {
        this.parentMobOid = parentMobId;
    }

    /**
     * 计算当前可召唤的怪物数量（受技能限制和已召唤数量影响）
     *
     * @param summonsSize 技能允许召唤的数量
     * @param skillLimit  技能等级限制
     * @return 实际可召唤数量
     */
    public int countAvailableMobSummons(int summonsSize, int skillLimit) {    // limit prop for summons has another conotation, found thanks to MedicOP
        int summonsCount;

        Set<Integer> calledOids = this.calledMobOids;
        if (calledOids != null) {
            summonsCount = calledOids.size();
        } else {
            summonsCount = 0;
        }

        return Math.min(summonsSize, skillLimit - summonsCount);
    }

    /**
     * 添加被召唤的怪物到当前怪物的召唤列表
     *
     * @param mob 被召唤的怪物
     */
    public void addSummonedMob(Monster mob) {
        Set<Integer> calledOids = this.calledMobOids;
        if (calledOids == null) {
            calledOids = Collections.synchronizedSet(new HashSet<>());
            this.calledMobOids = calledOids;
        }

        calledOids.add(mob.getObjectId());
        mob.setSummonerMob(this);
    }

    /**
     * 从召唤列表中移除指定怪物
     *
     * @param mobOid 怪物OID
     */
    private void removeSummonedMob(int mobOid) {
        Set<Integer> calledOids = this.calledMobOids;
        if (calledOids != null) {
            calledOids.remove(mobOid);
        }
    }

    /**
     * 设置召唤当前怪物的父怪物
     *
     * @param mob 父怪物
     */
    private void setSummonerMob(Monster mob) {
        this.callerMob = new WeakReference<>(mob);
    }

    /**
     * 清理当前怪物的召唤关系（通知父怪物并清空召唤列表）
     */
    private void dispatchClearSummons() {
        Monster caller = this.callerMob.get();
        if (caller != null) {
            caller.removeSummonedMob(this.getObjectId());
        }

        this.calledMobOids = null;
    }

    /**
     * 设置怪物被移除后的回调动作
     *
     * @param run 回调函数
     */
    public void pushRemoveAfterAction(Runnable run) {
        this.removeAfterAction = run;
    }

    /**
     * 获取并清除怪物被移除后的回调动作
     *
     * @return 回调函数
     */
    public Runnable popRemoveAfterAction() {
        Runnable r = this.removeAfterAction;
        this.removeAfterAction = null;

        return r;
    }

    /**
     * 获取当前 HP
     *
     * @return 当前 HP 值
     */
    public int getHp() {
        return hp.get();
    }

    /**
     * 增加 HP（治疗）
     *
     * @param hp 增加的 HP 值
     */
    public synchronized void addHp(int hp) {
        if (this.hp.get() <= 0) {
            return;
        }
        this.hp.addAndGet(hp);
    }

    /**
     * 设置初始 HP（用于重置怪物状态）
     *
     * @param hp HP 值
     */
    public synchronized void setStartingHp(int hp) {
        stats.setHp(hp);    // refactored mob stats after non-static HP pool suggestion thanks to twigs
        this.hp.set(hp);
    }

    /**
     * 获取最大 HP
     *
     * @return 最大 HP 值
     */
    public int getMaxHp() {
        return stats.getHp();
    }

    /**
     * 获取当前 MP
     *
     * @return 当前 MP 值
     */
    public int getMp() {
        return mp;
    }

    /**
     * 设置当前 MP
     *
     * @param mp MP 值（最小值为0）
     */
    public void setMp(int mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    /**
     * 获取最大 MP
     *
     * @return 最大 MP 值
     */
    public int getMaxMp() {
        return stats.getMp();
    }

    /**
     * 获取怪物经验值
     *
     * @return 经验值
     */
    public int getExp() {
        return stats.getExp();
    }

    /**
     * 获取怪物等级
     *
     * @return 等级
     */
    public int getLevel() {
        return stats.getLevel();
    }

    /**
     * 获取 CP（组队副本点数）
     *
     * @return CP 值
     */
    public int getCP() {
        return stats.getCP();
    }

    /**
     * 获取队伍标识（用于组队副本）
     *
     * @return 队伍标识
     */
    public int getTeam() {
        return team;
    }

    /**
     * 设置队伍标识
     *
     * @param team 队伍标识
     */
    public void setTeam(int team) {
        this.team = team;
    }

    /**
     * 获取毒液层数（飞侠毒技能）
     *
     * @return 毒液层数
     */
    public int getVenomMulti() {
        return this.VenomMultiplier;
    }

    /**
     * 设置毒液层数
     *
     * @param multiplier 毒液层数
     */
    public void setVenomMulti(int multiplier) {
        this.VenomMultiplier = multiplier;
    }

    /**
     * 获取怪物属性配置
     *
     * @return 怪物属性配置
     */
    public MonsterStats getStats() {
        return stats;
    }

    /**
     * 设置怪物属性配置
     *
     * @param stats 怪物属性配置
     */
    public void setStats(MonsterStats stats) {
        this.stats = stats;
    }

    /**
     * 判断是否为BOSS怪物
     *
     * @return true表示是BOSS
     */
    public boolean isBoss() {
        return stats.isBoss();
    }

    /**
     * 获取指定动画的时间长度
     *
     * @param name 动画名称（如"die1"）
     * @return 动画时间（毫秒）
     */
    public int getAnimationTime(String name) {
        return stats.getAnimationTime(name);
    }

    /**
     * 获取死亡后复活的怪物ID列表（BOSS技能）
     *
     * @return 复活怪物ID列表
     */
    private List<Integer> getRevives() {
        return stats.getRevives();
    }

    /**
     * 获取BOSS血条标签颜色
     *
     * @return 颜色值
     */
    private byte getTagColor() {
        return stats.getTagColor();
    }

    /**
     * 获取BOSS血条背景颜色
     *
     * @return 背景颜色值
     */
    private byte getTagBgColor() {
        return stats.getTagBgColor();
    }

    /**
     * 强制将HP设为0（杀死怪物）
     */
    public void setHpZero() {
        applyAndGetHpDamage(Integer.MAX_VALUE, false);
    }

    /**
     * 如果怪物处于漫游状态（非施法/攻击中），尝试应用动画锁定
     *
     * @param attackPos 攻击位置
     * @param skill     技能（可为null表示普通攻击）
     * @return true表示动画锁定成功
     */
    private boolean applyAnimationIfRoaming(int attackPos, MobSkill skill) {
        if (!animationLock.tryLock()) {
            return false;
        }

        try {
            long animationTime;

            if (skill == null) {
                animationTime = MonsterInformationProvider.getInstance().getMobAttackAnimationTime(this.getId(), attackPos);
            } else {
                animationTime = MonsterInformationProvider.getInstance().getMobSkillAnimationTime(skill);
            }

            if (animationTime > 0) {
                MobAnimationService service = (MobAnimationService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_ANIMATION);
                return service.registerMobOnAnimationEffect(map.getId(), this.hashCode(), animationTime);
            } else {
                return true;
            }
        } finally {
            animationLock.unlock();
        }
    }

    /**
     * 应用HP伤害并返回实际伤害/治疗量
     *
     * @param delta      伤害值（正数为伤害，负数为治疗）
     * @param stayAlive  是否保持不死（用于最后一击判定）
     * @return 实际伤害/治疗量，怪物已死亡返回null
     */
    public synchronized Integer applyAndGetHpDamage(int delta, boolean stayAlive) {
        int curHp = hp.get();
        if (curHp <= 0) {
            return null;
        }

        if (delta >= 0) {
            if (stayAlive) {
                curHp--;
            }
            int trueDamage = Math.min(curHp, delta);

            hp.addAndGet(-trueDamage);
            return trueDamage;
        } else {
            int trueHeal = -delta;
            int hp2Heal = curHp + trueHeal;
            int maxHp = getMaxHp();

            if (hp2Heal > maxHp) {
                trueHeal -= (hp2Heal - maxHp);
            }

            hp.addAndGet(trueHeal);
            return trueHeal;
        }
    }

    /**
     * 释放地图对象关联（将HP设为-1标记死亡）
     */
    public synchronized void disposeMapObject() {
        hp.set(-1);
    }

    /**
     * 广播怪物血条信息给相关玩家
     *
     * @param from 发起攻击的玩家
     */
    public void broadcastMobHpBar(Character from) {
        if (hasBossHPBar()) {
            from.setPlayerAggro(this.hashCode());
            from.getMap().broadcastBossHpMessage(this, this.hashCode(), makeBossHPBarPacket(), getPosition());
        } else if (!isBoss()) {
            int remainingHP = (int) Math.max(1, hp.get() * 100f / getMaxHp());
            Packet packet = PacketCreator.showMonsterHP(getObjectId(), remainingHP);
            if (from.getParty() != null) {
                for (PartyCharacter mpc : from.getParty().getMembers()) {
                    Character member = from.getMap().getCharacterById(mpc.getId());
                    if (member != null) {
                        member.sendPacket(packet);
                    }
                }
            } else {
                from.sendPacket(packet);
            }
        }
    }

    /**
     * 处理怪物受到玩家攻击的伤害
     *
     * @param attacker   攻击者
     * @param damage     伤害值
     * @param stayAlive  是否保持不死
     * @return true表示这是最后一击（怪物死亡）
     */
    public boolean damage(Character attacker, int damage, boolean stayAlive) {
        boolean lastHit = false;

        this.lockMonster();
        try {
            if (!this.isAlive()) {
                return false;
            }

            if (damage > 0) {
                this.applyDamage(attacker, damage, stayAlive, false);
                if (!this.isAlive()) {
                    lastHit = true;
                }
            }
        } finally {
            this.unlockMonster();
        }

        return lastHit;
    }

    /**
     * 应用伤害到怪物
     *
     * @param from      攻击者
     * @param damage    伤害值
     * @param stayAlive 是否保持不死
     * @param fake      是否为假伤害（不记录仇恨）
     */
    private void applyDamage(Character from, int damage, boolean stayAlive, boolean fake) {
        Integer trueDamage = applyAndGetHpDamage(damage, stayAlive);
        if (trueDamage == null) {
            return;
        }

        if (GameConfig.getServerBoolean("use_debug") && from.isGM()) {
            from.dropMessage(5, I18nUtil.getMessage("Monster.applyDamage.message1") + this.getId() + ", OID " + this.getObjectId());
        }

        if (!fake) {
            dispatchMonsterDamaged(from, trueDamage);
        }

        if (!takenDamage.containsKey(from.getId())) {
            takenDamage.put(from.getId(), new AtomicLong(trueDamage));
        } else {
            takenDamage.get(from.getId()).addAndGet(trueDamage);
        }

        broadcastMobHpBar(from);
    }

    /**
     * 应用假伤害（不记录仇恨和伤害统计）
     *
     * @param from      攻击者
     * @param damage    伤害值
     * @param stayAlive 是否保持不死
     */
    public void applyFakeDamage(Character from, int damage, boolean stayAlive) {
        applyDamage(from, damage, stayAlive, true);
    }

    /**
     * 治疗怪物 HP 和 MP
     *
     * @param hp 治疗的 HP 量
     * @param mp 治疗的 MP 量
     */
    public void heal(int hp, int mp) {
        Integer hpHealed = applyAndGetHpDamage(-hp, false);
        if (hpHealed == null) {
            return;
        }

        int mp2Heal = getMp() + mp;
        int maxMp = getMaxMp();
        if (mp2Heal >= maxMp) {
            mp2Heal = maxMp;
        }
        setMp(mp2Heal);

        if (hp > 0) {
            getMap().broadcastMessage(PacketCreator.healMonster(getObjectId(), hp, getHp(), getMaxHp()));
        }

        maxHpPlusHeal.addAndGet(hpHealed);
        dispatchMonsterHealed(hpHealed);
    }

    /**
     * 检查指定玩家是否攻击过当前怪物
     *
     * @param chr 玩家
     * @return true表示玩家攻击过此怪物
     */
    public boolean isAttackedBy(Character chr) {
        return takenDamage.containsKey(chr.getId());
    }

    /**
     * 判断玩家是否获得白色经验（高于标准差阈值的经验分配）
     *
     * @param chr         玩家
     * @param personalRatio 个人伤害占比映射
     * @param sdevRatio   标准差阈值
     * @return true表示获得白色经验
     */
    private static boolean isWhiteExpGain(Character chr, Map<Integer, Float> personalRatio, double sdevRatio) {
        Float pr = personalRatio.get(chr.getId());
        if (pr == null) {
            return false;
        }

        return pr >= sdevRatio;
    }

    /**
     * 计算经验分配的标准差阈值（用于判断白色/黄色经验）
     *
     * @param entryExpRatio 各参与者的经验占比列表
     * @param totalEntries  参与总数
     * @return 标准差阈值
     */
    private static double calcExperienceStandDevThreshold(List<Float> entryExpRatio, int totalEntries) {
        float avgExpReward = 0.0f;
        for (Float exp : entryExpRatio) {
            avgExpReward += exp;
        }

        avgExpReward /= totalEntries;

        float varExpReward = 0.0f;
        for (Float exp : entryExpRatio) {
            varExpReward += Math.pow(exp - avgExpReward, 2);
        }
        varExpReward /= entryExpRatio.size();

        return avgExpReward + Math.sqrt(varExpReward);
    }

    /**
     * 分配经验给单个玩家
     *
     * @param chr               玩家
     * @param exp               基础经验
     * @param partyBonusMod     组队加成系数
     * @param totalPartyLevel   队伍总等级
     * @param highestPartyDamager 是否为最高伤害者
     * @param whiteExpGain      是否获得白色经验
     * @param hasPartySharers   是否有队友共享
     */
    private void distributePlayerExperience(Character chr, float exp, float partyBonusMod, int totalPartyLevel, boolean highestPartyDamager, boolean whiteExpGain, boolean hasPartySharers) {
        float playerExp = (GameConfig.getServerFloat("exp_split_common_mod") * chr.getLevel()) / totalPartyLevel;
        if (highestPartyDamager) {
            playerExp += GameConfig.getServerFloat("exp_split_mvp_mod");
        }

        playerExp *= exp;
        float bonusExp = partyBonusMod * playerExp;

        this.giveExpToCharacter(chr, playerExp, bonusExp, whiteExpGain, hasPartySharers);
        giveFamilyRep(chr.getFamilyEntry());
    }

    /**
     * 分配经验给组队玩家
     *
     * @param partyParticipation 队伍成员伤害贡献映射
     * @param expPerDmg          每点伤害对应的经验值
     * @param underleveled       等级不符的玩家集合（用于通知）
     * @param personalRatio      个人伤害占比映射
     * @param sdevRatio          标准差阈值
     */
    private void distributePartyExperience(Map<Character, Long> partyParticipation, float expPerDmg, Set<Character> underleveled, Map<Integer, Float> personalRatio, double sdevRatio) {
        IntervalBuilder leechInterval = new IntervalBuilder();
        leechInterval.addInterval(this.getLevel() - GameConfig.getServerInt("exp_split_level_interval"), this.getLevel() + GameConfig.getServerInt("exp_split_level_interval"));

        long maxDamage = 0, partyDamage = 0;
        Character participationMvp = null;
        for (Entry<Character, Long> e : partyParticipation.entrySet()) {
            long entryDamage = e.getValue();
            partyDamage += entryDamage;

            if (maxDamage < entryDamage) {
                maxDamage = entryDamage;
                participationMvp = e.getKey();
            }

            int chrLevel = e.getKey().getLevel();
            leechInterval.addInterval(chrLevel - GameConfig.getServerInt("exp_split_leech_interval"), chrLevel + GameConfig.getServerInt("exp_split_leech_interval"));
        }

        List<Character> expMembers = new LinkedList<>();
        int totalPartyLevel = 0;

        if (GameConfig.getServerBoolean("use_enforce_mob_level_range")) {
            for (Character member : partyParticipation.keySet().iterator().next().getPartyMembersOnSameMap()) {
                if (!leechInterval.inInterval(member.getLevel())) {
                    underleveled.add(member);
                    continue;
                }

                totalPartyLevel += member.getLevel();
                expMembers.add(member);
            }
        } else {
            for (Character member : partyParticipation.keySet().iterator().next().getPartyMembersOnSameMap()) {
                totalPartyLevel += member.getLevel();
                expMembers.add(member);
            }
        }

        int membersSize = expMembers.size();
        float participationExp = partyDamage * expPerDmg;

        boolean hasPartySharers = membersSize > 1;
        float partyBonusMod = hasPartySharers ? 0.05f * membersSize : 0.0f;

        for (Character mc : expMembers) {
            distributePlayerExperience(mc, participationExp, partyBonusMod, totalPartyLevel, mc == participationMvp, isWhiteExpGain(mc, personalRatio, sdevRatio), hasPartySharers);
            giveFamilyRep(mc.getFamilyEntry());
        }
    }

    /**
     * 分配怪物死亡后的经验给所有攻击者
     *
     * @param killerId 击杀者ID
     */
    private void distributeExperience(int killerId) {
        if (isAlive()) {
            return;
        }

        Map<Party, Map<Character, Long>> partyExpDist = new HashMap<>();
        Map<Character, Long> soloExpDist = new HashMap<>();

        Map<Integer, Character> mapPlayers = map.getMapAllPlayers();

        int totalEntries = 0;   // counts "participant parties", players who no longer are available in the map is an "independent party"
        for (Entry<Integer, AtomicLong> e : takenDamage.entrySet()) {
            Character chr = mapPlayers.get(e.getKey());
            if (chr != null) {
                long damage = e.getValue().longValue();

                Party p = chr.getParty();
                if (p != null) {
                    Map<Character, Long> partyParticipation = partyExpDist.get(p);
                    if (partyParticipation == null) {
                        partyParticipation = new HashMap<>(6);
                        partyExpDist.put(p, partyParticipation);

                        totalEntries += 1;
                    }

                    partyParticipation.put(chr, damage);
                } else {
                    soloExpDist.put(chr, damage);
                    totalEntries += 1;
                }
            } else {
                totalEntries += 1;
            }
        }

        long totalDamage = maxHpPlusHeal.get();
        int mobExp = getExp();
        float expPerDmg = ((float) mobExp) / totalDamage;

        Map<Integer, Float> personalRatio = new HashMap<>();
        List<Float> entryExpRatio = new LinkedList<>();
        for (Entry<Character, Long> e : soloExpDist.entrySet()) {
            float ratio = ((float) e.getValue()) / totalDamage;

            personalRatio.put(e.getKey().getId(), ratio);
            entryExpRatio.add(ratio);
        }

        for (Map<Character, Long> m : partyExpDist.values()) {
            float ratio = 0.0f;
            for (Entry<Character, Long> e : m.entrySet()) {
                float chrRatio = ((float) e.getValue()) / totalDamage;

                personalRatio.put(e.getKey().getId(), chrRatio);
                ratio += chrRatio;
            }

            entryExpRatio.add(ratio);
        }

        double sdevRatio = calcExperienceStandDevThreshold(entryExpRatio, totalEntries);

        // GMS-like player and party split calculations found thanks to Russt, KaidaTan, Dusk, AyumiLove - src: https://ayumilovemaple.wordpress.com/maplestory_calculator_formula/
        Set<Character> underleveled = new HashSet<>();
        for (Entry<Character, Long> chrParticipation : soloExpDist.entrySet()) {
            float exp = chrParticipation.getValue() * expPerDmg;
            Character chr = chrParticipation.getKey();

            distributePlayerExperience(chr, exp, 0.0f, chr.getLevel(), true, isWhiteExpGain(chr, personalRatio, sdevRatio), false);
        }

        for (Map<Character, Long> partyParticipation : partyExpDist.values()) {
            distributePartyExperience(partyParticipation, expPerDmg, underleveled, personalRatio, sdevRatio);
        }

        EventInstanceManager eim = getMap().getEventInstance();
        if (eim != null) {
            Character chr = mapPlayers.get(killerId);
            if (chr != null) {
                eim.monsterKilled(chr, this);
            }
        }

        for (Character mc : underleveled) {
            mc.showUnderLeveledInfo(this);
        }

    }

    /**
     * 获取经验加成倍率（神圣符号、怪物弱点等）
     *
     * @param attacker        攻击者
     * @param hasPartySharers 是否有队友共享
     * @return 经验倍率
     */
    private float getStatusExpMultiplier(Character attacker, boolean hasPartySharers) {
        float multiplier = 1.0f;

        Integer holySymbol = attacker.getBuffedValue(BuffStat.HOLY_SYMBOL);
        if (holySymbol != null) {
            if (GameConfig.getServerBoolean("use_full_holy_symbol")) {
                multiplier *= (1.0 + (holySymbol.doubleValue() / 100.0));
            } else {
                multiplier *= (1.0 + (holySymbol.doubleValue() / (hasPartySharers ? 100.0 : 500.0)));
            }
        }

        statiLock.lock();
        try {
            MonsterStatusEffect mse = stati.get(MonsterStatus.SHOWDOWN);
            if (mse != null) {
                multiplier *= (1.0 + (mse.getStati().get(MonsterStatus.SHOWDOWN).doubleValue() / 100.0));
            }
        } finally {
            statiLock.unlock();
        }

        return multiplier;
    }

    /**
     * 将经验值转换为整数（处理溢出和精度问题）
     *
     * @param exp 经验值
     * @return 整数经验值
     */
    private static int expValueToInteger(double exp) {
        if (exp > Integer.MAX_VALUE) {
            exp = Integer.MAX_VALUE;
        } else if (exp < Integer.MIN_VALUE) {
            exp = Integer.MIN_VALUE;
        }

        return (int) Math.round(exp);
    }

    /**
     * 给予玩家经验值
     *
     * @param attacker        攻击者
     * @param personalExp     个人经验
     * @param partyExp        组队经验
     * @param white           是否白色经验
     * @param hasPartySharers 是否有队友共享
     */
    private void giveExpToCharacter(Character attacker, Float personalExp, Float partyExp, boolean white, boolean hasPartySharers) {
        if (attacker.isAlive()) {
            if (personalExp != null) {
                personalExp *= getStatusExpMultiplier(attacker, hasPartySharers);
                personalExp *= (attacker.getExpRate() * attacker.getMobExpRate());
                personalExp *= CatchUpExpConfigManager.getMultiplier(attacker.getLevel());
            } else {
                personalExp = 0.0f;
            }

            Integer expBonus = attacker.getBuffedValue(BuffStat.EXP_INCREASE);
            if (expBonus != null) {     // exp increase player buff found thanks to HighKey21
                personalExp += expBonus;
            }

            Integer expBuff = attacker.getBuffedValue(BuffStat.EXP_BUFF);
            if (expBuff != null) {
                personalExp *= 2;
            }

            if(attacker.isFamilyBuff()){
                personalExp *= attacker.getFamilyExp();
            }

            int _personalExp = expValueToInteger(personalExp); // assuming no negative xp here

            if (partyExp != null) {
                partyExp *= getStatusExpMultiplier(attacker, hasPartySharers);
                partyExp *= (attacker.getExpRate() * attacker.getMobExpRate());
                partyExp *= CatchUpExpConfigManager.getMultiplier(attacker.getLevel());
                partyExp *= GameConfig.getServerFloat("party_bonus_exp_rate");
            } else {
                partyExp = 0.0f;
            }

            int _partyExp = expValueToInteger(partyExp);

            attacker.gainExp(_personalExp, _partyExp, true, false, white);
            attacker.increaseEquipExp(_personalExp);
            attacker.raiseQuestMobCount(getId());
        }
    }

    /**
     * 获取相关掉落物列表
     *
     * @return 掉落物条目列表
     */
    public List<MonsterDropEntry> retrieveRelevantDrops() {
        if (this.getStats().isFriendly()) {
            return MonsterInformationProvider.getInstance().retrieveEffectiveDrop(this.getId());
        }

        Map<Integer, Character> pchars = map.getMapAllPlayers();

        List<Character> lootChars = new LinkedList<>();
        for (Integer cid : takenDamage.keySet()) {
            Character chr = pchars.get(cid);
            if (chr != null && chr.isLoggedInWorld()) {
                lootChars.add(chr);
            }
        }

        return LootManager.retrieveRelevantDrops(this.getId(), lootChars);
    }

    /**
     * 处理怪物被击杀的逻辑
     *
     * @param killer 击杀者
     * @return 获得掉落物的玩家（最高伤害者或击杀者）
     */
    public Character killBy(final Character killer) {
        distributeExperience(killer != null ? killer.getId() : 0);

        final Pair<Character, Boolean> lastController = aggroRemoveController();
        final List<Integer> toSpawn = this.getRevives();
        if (toSpawn != null) {
            final MapleMap reviveMap = map;
            if (toSpawn.contains(MobId.TRANSPARENT_ITEM) && reviveMap.getId() > 925000000 && reviveMap.getId() < 926000000) {
                reviveMap.broadcastMessage(PacketCreator.playSound("Dojang/clear"));
                reviveMap.broadcastMessage(PacketCreator.showEffect("dojang/end/clear"));
            }
            Pair<Integer, String> timeMob = reviveMap.getTimeMob();
            if (timeMob != null) {
                if (toSpawn.contains(timeMob.getLeft())) {
                    reviveMap.broadcastMessage(PacketCreator.serverNotice(6, timeMob.getRight()));
                }
            }

            if (toSpawn.size() > 0) {
                final EventInstanceManager eim = this.getMap().getEventInstance();

                TimerManager.getInstance().schedule(() -> {
                    Character controller = lastController.getLeft();
                    boolean aggro = lastController.getRight();

                    for (Integer mid : toSpawn) {
                        final Monster mob = LifeFactory.getMonster(mid);
                        mob.setPosition(getPosition());
                        mob.setFh(getFh());
                        mob.setParentMobOid(getObjectId());

                        if (dropsDisabled()) {
                            mob.disableDrops();
                        }
                        reviveMap.spawnMonster(mob);

                        if (MobId.isDeadHorntailPart(mob.getId()) && reviveMap.isHorntailDefeated()) {
                            boolean htKilled = false;
                            Monster ht = reviveMap.getMonsterById(MobId.HORNTAIL);

                            if (ht != null) {
                                ht.lockMonster();
                                try {
                                    htKilled = ht.isAlive();
                                    ht.setHpZero();
                                } finally {
                                    ht.unlockMonster();
                                }

                                if (htKilled) {
                                    reviveMap.killMonster(ht, killer, true);
                                }
                            }

                            for (int i = MobId.DEAD_HORNTAIL_MAX; i >= MobId.DEAD_HORNTAIL_MIN; i--) {
                                reviveMap.killMonster(reviveMap.getMonsterById(i), killer, true);
                            }
                        } else if (controller != null) {
                            mob.aggroSwitchController(controller, aggro);
                        }

                        if (eim != null) {
                            eim.reviveMonster(mob);
                        }
                    }
                }, getAnimationTime("die1"));
            }
        } else {
            log.warn("[CRITICAL LOSS] toSpawn is null for {}", getName());
        }

        Character looter = map.getCharacterById(getHighestDamagerId());
        return looter != null ? looter : killer;
    }

    /**
     * 设置友好怪物的定时掉落任务
     *
     * @param delay 掉落间隔（毫秒）
     */
    public void dropFromFriendlyMonster(long delay) {
        final Monster m = this;
        monsterItemDrop = TimerManager.getInstance().register(() -> {
            if (!m.isAlive()) {
                if (monsterItemDrop != null) {
                    monsterItemDrop.cancel(false);
                }

                return;
            }

            MapleMap map = m.getMap();
            List<Character> chrList = map.getAllPlayers();
            if (!chrList.isEmpty()) {
                Character chr = chrList.get(0);

                EventInstanceManager eim = map.getEventInstance();
                if (eim != null) {
                    eim.friendlyItemDrop(m);
                }

                map.dropFromFriendlyMonster(chr, m);
            }
        }, delay, delay);
    }

    /**
     * 分发任务怪物计数增加事件给所有攻击者
     */
    private void dispatchRaiseQuestMobCount() {
        Set<Integer> attackerChrids = takenDamage.keySet();
        if (!attackerChrids.isEmpty()) {
            Map<Integer, Character> mapChars = map.getMapPlayers();
            if (!mapChars.isEmpty()) {
                int mobid = getId();

                for (Integer chrid : attackerChrids) {
                    Character chr = mapChars.get(chrid);

                    if (chr != null && chr.isLoggedInWorld()) {
                        chr.raiseQuestMobCount(mobid);
                    }
                }
            }
        }
    }

    /**
     * 分发怪物死亡事件
     *
     * @param hasKiller 是否有击杀者
     */
    public void dispatchMonsterKilled(boolean hasKiller) {
        processMonsterKilled(hasKiller);

        EventInstanceManager eim = getMap().getEventInstance();
        if (eim != null) {
            if (!this.getStats().isFriendly()) {
                eim.monsterKilled(this, hasKiller);
            } else {
                eim.friendlyKilled(this, hasKiller);
            }
        }
    }

    /**
     * 处理怪物死亡的内部逻辑
     *
     * @param hasKiller 是否有击杀者
     */
    private synchronized void processMonsterKilled(boolean hasKiller) {
        if (!hasKiller) {
            dispatchRaiseQuestMobCount();
        }

        this.aggroClearDamages();
        this.dispatchClearSummons();

        MonsterListener[] listenersList;
        statiLock.lock();
        try {
            listenersList = listeners.toArray(new MonsterListener[listeners.size()]);
        } finally {
            statiLock.unlock();
        }

        for (MonsterListener listener : listenersList) {
            listener.monsterKilled(getAnimationTime("die1"));
        }

        statiLock.lock();
        try {
            stati.clear();
            alreadyBuffed.clear();
            listeners.clear();
        } finally {
            statiLock.unlock();
        }
    }

    /**
     * 分发怪物受到伤害事件给监听器
     *
     * @param from    攻击者
     * @param trueDmg 实际伤害值
     */
    private void dispatchMonsterDamaged(Character from, int trueDmg) {
        MonsterListener[] listenersList;
        statiLock.lock();
        try {
            listenersList = listeners.toArray(new MonsterListener[listeners.size()]);
        } finally {
            statiLock.unlock();
        }

        for (MonsterListener listener : listenersList) {
            listener.monsterDamaged(from, trueDmg);
        }
    }

    /**
     * 分发怪物被治疗事件给监听器
     *
     * @param trueHeal 实际治疗值
     */
    private void dispatchMonsterHealed(int trueHeal) {
        MonsterListener[] listenersList;
        statiLock.lock();
        try {
            listenersList = listeners.toArray(new MonsterListener[listeners.size()]);
        } finally {
            statiLock.unlock();
        }

        for (MonsterListener listener : listenersList) {
            listener.monsterHealed(trueHeal);
        }
    }

    /**
     * 给予家族声望
     *
     * @param entry 家族条目
     */
    private void giveFamilyRep(FamilyEntry entry) {
        if (entry != null) {
            int repGain = isBoss() ? GameConfig.getServerInt("family_rep_per_boss_kill") : GameConfig.getServerInt("family_rep_per_kill");
            if (getMaxHp() <= 1) {
                repGain = 0;
            }
            entry.giveReputationToSenior(repGain, true);
        }
    }

    /**
     * 获取造成最高伤害的玩家ID
     *
     * @return 最高伤害者ID
     */
    public int getHighestDamagerId() {
        int curId = 0;
        long curDmg = 0;

        for (Entry<Integer, AtomicLong> damage : takenDamage.entrySet()) {
            curId = damage.getValue().get() >= curDmg ? damage.getKey() : curId;
            curDmg = damage.getKey() == curId ? damage.getValue().get() : curDmg;
        }

        return curId;
    }

    /**
     * 检查怪物是否存活
     *
     * @return true表示怪物存活
     */
    public boolean isAlive() {
        return this.hp.get() > 0;
    }

    /**
     * 添加怪物监听器
     *
     * @param listener 监听器
     */
    public void addListener(MonsterListener listener) {
        statiLock.lock();
        try {
            listeners.add(listener);
        } finally {
            statiLock.unlock();
        }
    }

    /**
     * 获取当前控制器（第一个攻击怪物的玩家）
     *
     * @return 控制器玩家
     */
    public Character getController() {
        return controller.get();
    }

    /**
     * 设置控制器
     *
     * @param controller 控制器玩家
     */
    private void setController(Character controller) {
        this.controller = new WeakReference<>(controller);
    }

    /**
     * 检查控制器是否有仇恨
     *
     * @return true表示控制器有仇恨
     */
    public boolean isControllerHasAggro() {
        return !fake && controllerHasAggro;
    }

    /**
     * 设置控制器仇恨状态
     *
     * @param controllerHasAggro 是否有仇恨
     */
    private void setControllerHasAggro(boolean controllerHasAggro) {
        if (!fake) {
            this.controllerHasAggro = controllerHasAggro;
        }
    }

    /**
     * 检查控制器是否知道自己有仇恨
     *
     * @return true表示控制器知道仇恨状态
     */
    public boolean isControllerKnowsAboutAggro() {
        return !fake && controllerKnowsAboutAggro;
    }

    /**
     * 设置控制器是否知道仇恨状态
     *
     * @param controllerKnowsAboutAggro 是否知道仇恨
     */
    private void setControllerKnowsAboutAggro(boolean controllerKnowsAboutAggro) {
        if (!fake) {
            this.controllerKnowsAboutAggro = controllerKnowsAboutAggro;
        }
    }

    /**
     * 设置控制器是否放置了替身
     *
     * @param controllerHasPuppet 是否有替身
     */
    private void setControllerHasPuppet(boolean controllerHasPuppet) {
        this.controllerHasPuppet = controllerHasPuppet;
    }

    /**
     * 创建BOSS血条数据包
     *
     * @return BOSS血条数据包
     */
    public Packet makeBossHPBarPacket() {
        return PacketCreator.showBossHP(getId(), getHp(), getMaxHp(), getTagColor(), getTagBgColor());
    }

    /**
     * 检查是否有BOSS血条
     *
     * @return true表示有BOSS血条
     */
    public boolean hasBossHPBar() {
        return isBoss() && getTagColor() > 0;
    }

    /**
     * 发送怪物生成数据给客户端
     *
     * @param client 客户端
     */
    @Override
    public void sendSpawnData(Client client) {
        if (hp.get() <= 0) {
            return;
        }
        if (fake) {
            client.sendPacket(PacketCreator.spawnFakeMonster(this, 0));
        } else {
            client.sendPacket(PacketCreator.spawnMonster(this, false));
        }

        if (hasBossHPBar()) {
            client.announceBossHpBar(this, this.hashCode(), makeBossHPBarPacket());
        }
    }

    /**
     * 发送怪物销毁数据给客户端
     *
     * @param client 客户端
     */
    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(PacketCreator.killMonster(getObjectId(), false));
        client.sendPacket(PacketCreator.killMonster(getObjectId(), true));
    }

    /**
     * 获取地图对象类型
     *
     * @return MONSTER 类型
     */
    @Override
    public MapObjectType getType() {
        return MapObjectType.MONSTER;
    }

    /**
     * 检查怪物是否可移动
     *
     * @return true表示可移动
     */
    public boolean isMobile() {
        return stats.isMobile();
    }

    /**
     * 检查怪物是否面向左
     *
     * @return true表示面向左
     */
    @Override
    public boolean isFacingLeft() {
        int fixedStance = stats.getFixedStance();
        if (fixedStance != 0) {
            return Math.abs(fixedStance) % 2 == 1;
        }

        return super.isFacingLeft();
    }

    /**
     * 获取元素属性效果（考虑DOOM状态）
     *
     * @param e 元素类型
     * @return 元素效果
     */
    public ElementalEffectiveness getElementalEffectiveness(Element e) {
        statiLock.lock();
        try {
            if (stati.get(MonsterStatus.DOOM) != null) {
                return ElementalEffectiveness.NORMAL;
            }
        } finally {
            statiLock.unlock();
        }

        return getMonsterEffectiveness(e);
    }

    /**
     * 获取怪物本身的元素属性效果
     *
     * @param e 元素类型
     * @return 元素效果
     */
    private ElementalEffectiveness getMonsterEffectiveness(Element e) {
        monsterLock.lock();
        try {
            return stats.getEffectiveness(e);
        } finally {
            monsterLock.unlock();
        }
    }

    /**
     * 获取活跃的控制器（在线且在同一地图）
     *
     * @return 活跃控制器，不存在返回null
     */
    private Character getActiveController() {
        Character chr = getController();

        if (chr != null && chr.isLoggedInWorld() && chr.getMap() == this.getMap()) {
            return chr;
        } else {
            return null;
        }
    }

    /**
     * 广播怪物状态消息
     *
     * @param packet 数据包
     */
    private void broadcastMonsterStatusMessage(Packet packet) {
        map.broadcastMessage(packet, getPosition());

        Character chrController = getActiveController();
        if (chrController != null && !chrController.isMapObjectVisible(Monster.this)) {
            chrController.sendPacket(packet);
        }
    }

    /**
     * 广播状态效果并返回动画时间
     *
     * @param status 状态效果
     * @return 动画时间
     */
    private int broadcastStatusEffect(final MonsterStatusEffect status) {
        int animationTime = status.getSkill().getAnimationTime();
        Packet packet = PacketCreator.applyMonsterStatus(getObjectId(), status, null);
        broadcastMonsterStatusMessage(packet);

        return animationTime;
    }

    /**
     * 应用状态效果到怪物
     *
     * @param from     施法者
     * @param status   状态效果
     * @param poison   是否为毒药
     * @param duration 持续时间
     * @return true表示应用成功
     */
    public boolean applyStatus(Character from, final MonsterStatusEffect status, boolean poison, long duration) {
        return applyStatus(from, status, poison, duration, false);
    }

    /**
     * 应用状态效果到怪物（支持毒液）
     *
     * @param from     施法者
     * @param status   状态效果
     * @param poison   是否为毒药
     * @param duration 持续时间
     * @param venom    是否为毒液
     * @return true表示应用成功
     */
    public boolean applyStatus(Character from, final MonsterStatusEffect status, boolean poison, long duration, boolean venom) {
        // 根据怪物的元素抗性判断状态能否应用
        switch (getMonsterEffectiveness(status.getSkill().getElement())) {
            // 免疫/抵抗/中性：状态无效
            case IMMUNE:
            case STRONG:
            case NEUTRAL:
                return false;
            // 正常/弱点：可以应用状态
            case NORMAL:
            case WEAK:
                break;
            default: {
                log.warn("Unknown elemental effectiveness: {}", getMonsterEffectiveness(status.getSkill().getElement()));
                return false;
            }
        }

        if (status.getSkill().getId() == FPMage.ELEMENT_COMPOSITION) { // fp compo
            ElementalEffectiveness effectiveness = getMonsterEffectiveness(Element.POISON);
            if (effectiveness == ElementalEffectiveness.IMMUNE || effectiveness == ElementalEffectiveness.STRONG) {
                return false;
            }
        } else if (status.getSkill().getId() == ILMage.ELEMENT_COMPOSITION) { // il compo
            ElementalEffectiveness effectiveness = getMonsterEffectiveness(Element.ICE);
            if (effectiveness == ElementalEffectiveness.IMMUNE || effectiveness == ElementalEffectiveness.STRONG) {
                return false;
            }
        } else if (status.getSkill().getId() == NightLord.VENOMOUS_STAR || status.getSkill().getId() == Shadower.VENOMOUS_STAB || status.getSkill().getId() == NightWalker.VENOM) {// venom
            if (getMonsterEffectiveness(Element.POISON) == ElementalEffectiveness.WEAK) {
                return false;
            }
        }
        if (poison && hp.get() <= 1) {
            return false;
        }

        final Map<MonsterStatus, Integer> statis = status.getStati();
        if (stats.isBoss()) {
            if (!(statis.containsKey(MonsterStatus.SPEED)
                    && statis.containsKey(MonsterStatus.NINJA_AMBUSH)
                    && statis.containsKey(MonsterStatus.WATK))) {
                return false;
            }
        }

        final Channel ch = map.getChannelServer();
        final int mapid = map.getId();
        if (statis.size() > 0) {
            statiLock.lock();
            try {
                for (MonsterStatus stat : statis.keySet()) {
                    final MonsterStatusEffect oldEffect = stati.get(stat);
                    if (oldEffect != null) {
                        oldEffect.removeActiveStatus(stat);
                        if (oldEffect.getStati().isEmpty()) {
                            MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
                            service.interruptMobStatus(mapid, oldEffect);
                        }
                    }
                }
            } finally {
                statiLock.unlock();
            }
        }

        final Runnable cancelTask = () -> {
            if (isAlive()) {
                Packet packet = PacketCreator.cancelMonsterStatus(getObjectId(), status.getStati());
                broadcastMonsterStatusMessage(packet);
            }

            statiLock.lock();
            try {
                for (MonsterStatus stat : status.getStati().keySet()) {
                    stati.remove(stat);
                }
            } finally {
                statiLock.unlock();
            }

            setVenomMulti(0);
        };

        Runnable overtimeAction = null;
        int overtimeDelay = -1;

        int animationTime;
        if (poison) {
            int poisonLevel = from.getSkillLevel(status.getSkill());
            int poisonDamage = Math.min(Short.MAX_VALUE, (int) (getMaxHp() / (70.0 - poisonLevel) + 0.999));
            status.setValue(MonsterStatus.POISON, poisonDamage);
            animationTime = broadcastStatusEffect(status);

            overtimeAction = new DamageTask(poisonDamage, from, status, 0);
            overtimeDelay = 1000;
        } else if (venom) {
            if (from.getJob() == Job.NIGHTLORD || from.getJob() == Job.SHADOWER || from.getJob().isA(Job.NIGHTWALKER3)) {
                int poisonLevel, matk, jobid = from.getJob().getId();
                int skillid = (jobid == 412 ? NightLord.VENOMOUS_STAR : (jobid == 422 ? Shadower.VENOMOUS_STAB : NightWalker.VENOM));
                poisonLevel = from.getSkillLevel(SkillFactory.getSkill(skillid));
                if (poisonLevel <= 0) {
                    return false;
                }
                matk = SkillFactory.getSkill(skillid).getEffect(poisonLevel).getMatk();
                int luk = from.getLuk();
                int maxDmg = (int) Math.ceil(Math.min(Short.MAX_VALUE, 0.2 * luk * matk));
                int minDmg = (int) Math.ceil(Math.min(Short.MAX_VALUE, 0.1 * luk * matk));
                int gap = maxDmg - minDmg;
                if (gap == 0) {
                    gap = 1;
                }
                int poisonDamage = 0;
                for (int i = 0; i < getVenomMulti(); i++) {
                    poisonDamage += (Randomizer.nextInt(gap) + minDmg);
                }
                poisonDamage = Math.min(Short.MAX_VALUE, poisonDamage);
                status.setValue(MonsterStatus.VENOMOUS_WEAPON, poisonDamage);
                status.setValue(MonsterStatus.POISON, poisonDamage);
                animationTime = broadcastStatusEffect(status);

                overtimeAction = new DamageTask(poisonDamage, from, status, 0);
                overtimeDelay = 1000;
            } else {
                return false;
            }
            /*
        } else if (status.getSkill().getId() == Hermit.SHADOW_WEB || status.getSkill().getId() == NightWalker.SHADOW_WEB) { //Shadow Web
            int webDamage = (int) (getMaxHp() / 50.0 + 0.999);
            status.setValue(MonsterStatus.SHADOW_WEB, Integer.valueOf(webDamage));
            animationTime = broadcastStatusEffect(status);
            
            overtimeAction = new DamageTask(webDamage, from, status, 1);
            overtimeDelay = 3500;
            */
        } else if (status.getSkill().getId() == 4121004 || status.getSkill().getId() == 4221004) { // Ninja Ambush
            final Skill skill = SkillFactory.getSkill(status.getSkill().getId());
            final byte level = from.getSkillLevel(skill);
            final int damage = (int) ((from.getStr() + from.getLuk()) * ((3.7 * skill.getEffect(level).getDamage()) / 100));

            status.setValue(MonsterStatus.NINJA_AMBUSH, damage);
            animationTime = broadcastStatusEffect(status);

            overtimeAction = new DamageTask(damage, from, status, 2);
            overtimeDelay = 1000;
        } else {
            animationTime = broadcastStatusEffect(status);
        }

        statiLock.lock();
        try {
            for (MonsterStatus stat : status.getStati().keySet()) {
                stati.put(stat, status);
                alreadyBuffed.add(stat);
            }
        } finally {
            statiLock.unlock();
        }

        MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
        service.registerMobStatus(mapid, status, cancelTask, duration + animationTime - 100, overtimeAction, overtimeDelay);
        return true;
    }

    /**
     * 驱散怪物身上指定类型的技能效果
     *
     * @param skill 要驱散的技能
     */
    public final void dispelSkill(final MobSkill skill) {
        List<MonsterStatus> toCancel = new ArrayList<>();
        for (Entry<MonsterStatus, MonsterStatusEffect> effects : stati.entrySet()) {
            MonsterStatusEffect mse = effects.getValue();
            if (mse.getMobSkill() != null && mse.getMobSkill().getType() == skill.getType()) {
                toCancel.add(effects.getKey());
            }
        }
        for (MonsterStatus stat : toCancel) {
            debuffMobStat(stat);
        }
    }

    /**
     * 应用怪物增益效果
     *
     * @param stats      状态映射
     * @param x          位置参数
     * @param duration   持续时间
     * @param skill      技能
     * @param reflection 反射列表
     */
    public void applyMonsterBuff(final Map<MonsterStatus, Integer> stats, final int x, long duration, MobSkill skill, final List<Integer> reflection) {
        final Runnable cancelTask = () -> {
            if (isAlive()) {
                Packet packet = PacketCreator.cancelMonsterStatus(getObjectId(), stats);
                broadcastMonsterStatusMessage(packet);

                statiLock.lock();
                try {
                    for (final MonsterStatus stat : stats.keySet()) {
                        stati.remove(stat);
                    }
                } finally {
                    statiLock.unlock();
                }
            }
        };
        final MonsterStatusEffect effect = new MonsterStatusEffect(stats, null, skill, true);
        Packet packet = PacketCreator.applyMonsterStatus(getObjectId(), effect, reflection);
        broadcastMonsterStatusMessage(packet);

        statiLock.lock();
        try {
            for (MonsterStatus stat : stats.keySet()) {
                stati.put(stat, effect);
                alreadyBuffed.add(stat);
            }
        } finally {
            statiLock.unlock();
        }

        MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
        service.registerMobStatus(map.getId(), effect, cancelTask, duration);
    }

    /**
     * 刷新怪物位置（使用当前位置重置）
     */
    public void refreshMobPosition() {
        resetMobPosition(getPosition());
    }

    /**
     * 重置怪物位置
     *
     * @param newPoint 新位置
     */
    public void resetMobPosition(Point newPoint) {
        aggroRemoveController();

        setPosition(newPoint);
        map.broadcastMessage(PacketCreator.moveMonster(this.getObjectId(), false, -1, 0, 0, 0, this.getPosition(), this.getIdleMovement(), AbstractAnimatedMapObject.IDLE_MOVEMENT_PACKET_LENGTH));
        map.moveMonster(this, this.getPosition());

        aggroUpdateController();
    }

    /**
     * 移除怪物身上指定状态
     *
     * @param stat 状态类型
     */
    private void debuffMobStat(MonsterStatus stat) {
        MonsterStatusEffect oldEffect;
        statiLock.lock();
        try {
            oldEffect = stati.remove(stat);
        } finally {
            statiLock.unlock();
        }

        if (oldEffect != null) {
            Packet packet = PacketCreator.cancelMonsterStatus(getObjectId(), oldEffect.getStati());
            broadcastMonsterStatusMessage(packet);
        }
    }

    /**
     * 根据技能ID驱散怪物增益效果
     *
     * @param skillid 技能ID
     */
    public void debuffMob(int skillid) {
        MonsterStatus[] statups = {MonsterStatus.WEAPON_ATTACK_UP, MonsterStatus.WEAPON_DEFENSE_UP, MonsterStatus.MAGIC_ATTACK_UP, MonsterStatus.MAGIC_DEFENSE_UP};
        statiLock.lock();
        try {
            if (skillid == Hermit.SHADOW_MESO) {
                debuffMobStat(statups[1]);
                debuffMobStat(statups[3]);
            } else if (skillid == Priest.DISPEL) {
                for (MonsterStatus ms : statups) {
                    debuffMobStat(ms);
                }
            } else {    // is a crash skill
                int i = (skillid == Crusader.ARMOR_CRASH ? 1 : (skillid == WhiteKnight.MAGIC_CRASH ? 2 : 0));
                debuffMobStat(statups[i]);

                if (GameConfig.getServerBoolean("use_anti_immunity_crash")) {
                    if (skillid == Crusader.ARMOR_CRASH) {
                        if (!isBuffed(MonsterStatus.WEAPON_REFLECT)) {
                            debuffMobStat(MonsterStatus.WEAPON_IMMUNITY);
                        }
                        if (!isBuffed(MonsterStatus.MAGIC_REFLECT)) {
                            debuffMobStat(MonsterStatus.MAGIC_IMMUNITY);
                        }
                    } else if (skillid == WhiteKnight.MAGIC_CRASH) {
                        if (!isBuffed(MonsterStatus.MAGIC_REFLECT)) {
                            debuffMobStat(MonsterStatus.MAGIC_IMMUNITY);
                        }
                    } else {
                        if (!isBuffed(MonsterStatus.WEAPON_REFLECT)) {
                            debuffMobStat(MonsterStatus.WEAPON_IMMUNITY);
                        }
                    }
                }
            }
        } finally {
            statiLock.unlock();
        }
    }

    /**
     * 检查怪物是否有指定状态效果
     *
     * @param status 状态类型
     * @return true表示有该状态
     */
    public boolean isBuffed(MonsterStatus status) {
        statiLock.lock();
        try {
            return stati.containsKey(status);
        } finally {
            statiLock.unlock();
        }
    }

    /**
     * 设置怪物是否为假怪物（仅动画，不参与战斗）
     *
     * @param fake 是否为假怪物
     */
    public void setFake(boolean fake) {
        monsterLock.lock();
        try {
            this.fake = fake;
        } finally {
            monsterLock.unlock();
        }
    }

    /**
     * 检查怪物是否为假怪物
     *
     * @return true表示是假怪物
     */
    public boolean isFake() {
        monsterLock.lock();
        try {
            return fake;
        } finally {
            monsterLock.unlock();
        }
    }

    /**
     * 获取怪物所在地图
     *
     * @return 地图实例
     */
    public MapleMap getMap() {
        return map;
    }

    /**
     * 获取地图仇恨协调器
     *
     * @return 仇恨协调器
     */
    public MonsterAggroCoordinator getMapAggroCoordinator() {
        return map.getAggroCoordinator();
    }

    /**
     * 获取怪物技能集合
     *
     * @return 技能ID集合
     */
    public Set<MobSkillId> getSkills() {
        return stats.getSkills();
    }

    /**
     * 检查怪物是否有指定技能
     *
     * @param skillId 技能ID
     * @param level   技能等级
     * @return true表示有该技能
     */
    public boolean hasSkill(int skillId, int level) {
        return stats.hasSkill(skillId, level);
    }

    /**
     * 检查怪物是否可以使用指定技能
     *
     * @param toUse 要使用的技能
     * @param apply 是否实际使用（消耗MP并添加冷却）
     * @return true表示可以使用
     */
    public boolean canUseSkill(MobSkill toUse, boolean apply) {
        if (toUse == null || isBuffed(MonsterStatus.SEAL_SKILL)) {
            return false;
        }

        if (isReflectSkill(toUse)) {
            if (this.isBuffed(MonsterStatus.WEAPON_REFLECT) || this.isBuffed(MonsterStatus.MAGIC_REFLECT)) {
                return false;
            }
        }

        monsterLock.lock();
        try {
            if (usedSkills.contains(toUse.getId())) {
                return false;
            }

            int mpCon = toUse.getMpCon();
            if (mp < mpCon) {
                return false;
            }

            if (apply) {
                this.usedSkill(toUse);
            }
        } finally {
            monsterLock.unlock();
        }

        return true;
    }

    /**
     * 检查技能是否为反射技能
     *
     * @param mobSkill 技能
     * @return true表示是反射技能
     */
    private boolean isReflectSkill(MobSkill mobSkill) {
        // 物理反击/魔法反击/物理魔法反击都属于反射技能
        return switch (mobSkill.getType()) {
            case PHYSICAL_COUNTER, MAGIC_COUNTER, PHYSICAL_AND_MAGIC_COUNTER -> true;
            default -> false;
        };
    }

    /**
     * 使用技能（消耗MP并添加冷却）
     *
     * @param skill 技能
     */
    private void usedSkill(MobSkill skill) {
        final MobSkillId msId = skill.getId();
        monsterLock.lock();
        try {
            mp -= skill.getMpCon();

            this.usedSkills.add(msId);
        } finally {
            monsterLock.unlock();
        }

        final Monster mons = this;
        MapleMap mmap = mons.getMap();
        Runnable r = () -> mons.clearSkill(skill.getId());

        MobClearSkillService service = (MobClearSkillService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_CLEAR_SKILL);
        service.registerMobClearSkillAction(mmap.getId(), r, skill.getCoolTime());
    }

    /**
     * 清除技能冷却
     *
     * @param msId 技能ID
     */
    private void clearSkill(MobSkillId msId) {
        monsterLock.lock();
        try {
            usedSkills.remove(msId);
        } finally {
            monsterLock.unlock();
        }
    }

    /**
     * 检查怪物是否可以使用指定攻击
     *
     * @param attackPos 攻击位置
     * @param isSkill   是否为技能攻击
     * @return 1表示可以使用，-1表示不可用
     */
    public int canUseAttack(int attackPos, boolean isSkill) {
        monsterLock.lock();
        try {
            Pair<Integer, Integer> attackInfo = MonsterInformationProvider.getInstance().getMobAttackInfo(this.getId(), attackPos);
            if (attackInfo == null) {
                return -1;
            }

            int mpCon = attackInfo.getLeft();
            if (mp < mpCon) {
                return -1;
            }

            usedAttack(attackPos, mpCon, attackInfo.getRight());
            return 1;
        } finally {
            monsterLock.unlock();
        }
    }

    /**
     * 使用攻击（消耗MP并添加冷却）
     *
     * @param attackPos 攻击位置
     * @param mpCon     MP消耗
     * @param cooltime  冷却时间
     */
    private void usedAttack(final int attackPos, int mpCon, int cooltime) {
        monsterLock.lock();
        try {
            mp -= mpCon;
            usedAttacks.add(attackPos);

            final Monster mons = this;
            MapleMap mmap = mons.getMap();
            Runnable r = () -> mons.clearAttack(attackPos);

            MobClearSkillService service = (MobClearSkillService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_CLEAR_SKILL);
            service.registerMobClearSkillAction(mmap.getId(), r, cooltime);
        } finally {
            monsterLock.unlock();
        }
    }

    /**
     * 清除攻击冷却
     *
     * @param attackPos 攻击位置
     */
    private void clearAttack(int attackPos) {
        monsterLock.lock();
        try {
            usedAttacks.remove(attackPos);
        } finally {
            monsterLock.unlock();
        }
    }

    /**
     * 检查怪物是否有技能
     *
     * @return true表示有技能
     */
    public boolean hasAnySkill() {
        return this.stats.getNoSkills() > 0;
    }

    /**
     * 获取随机技能
     *
     * @return 随机技能ID
     */
    public MobSkillId getRandomSkill() {
        Set<MobSkillId> skills = stats.getSkills();
        if (skills.size() == 0) {
            return null;
        }
        return skills.stream()
                .skip(Randomizer.nextInt(skills.size()))
                .findAny()
                .orElse(null);
    }

    /**
     * 检查怪物是否优先攻击
     *
     * @return true表示优先攻击
     */
    public boolean isFirstAttack() {
        return this.stats.isFirstAttack();
    }

    public int getBuffToGive() {
        return this.stats.getBuffToGive();
    }

    private final class DamageTask implements Runnable {

        private final int dealDamage;
        private final Character chr;
        private final MonsterStatusEffect status;
        private final int type;
        private final MapleMap map;

        private DamageTask(int dealDamage, Character chr, MonsterStatusEffect status, int type) {
            this.dealDamage = dealDamage;
            this.chr = chr;
            this.status = status;
            this.type = type;
            this.map = chr.getMap();
        }

        @Override
        public void run() {
            int curHp = hp.get();
            if (curHp <= 1) {
                MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
                service.interruptMobStatus(map.getId(), status);
                return;
            }

            int damage = dealDamage;
            if (damage >= curHp) {
                damage = curHp - 1;
                if (type == 1 || type == 2) {
                    MobStatusService service = (MobStatusService) map.getChannelServer().getServiceAccess(ChannelServices.MOB_STATUS);
                    service.interruptMobStatus(map.getId(), status);
                }
            }
            if (damage > 0) {
                lockMonster();
                try {
                    applyDamage(chr, damage, true, false);
                } finally {
                    unlockMonster();
                }

                if (type == 1) {
                    map.broadcastMessage(PacketCreator.damageMonster(getObjectId(), damage), getPosition());
                } else if (type == 2) {
                    if (damage < dealDamage) {    // ninja ambush (type 2) is already displaying DOT to the caster
                        map.broadcastMessage(PacketCreator.damageMonster(getObjectId(), damage), getPosition());
                    }
                }
            }
        }
    }

    public String getName() {
        return stats.getName();
    }

    public void addStolen(int itemId) {
        stolenItems.add(itemId);
    }

    public List<Integer> getStolen() {
        return stolenItems;
    }

    public void setTempEffectiveness(Element e, ElementalEffectiveness ee, long milli) {
        monsterLock.lock();
        try {
            final Element fE = e;
            final ElementalEffectiveness fEE = stats.getEffectiveness(e);
            if (!fEE.equals(ElementalEffectiveness.WEAK)) {
                stats.setEffectiveness(e, ee);

                MapleMap mmap = this.getMap();
                Runnable r = () -> {
                    monsterLock.lock();
                    try {
                        stats.removeEffectiveness(fE);
                        stats.setEffectiveness(fE, fEE);
                    } finally {
                        monsterLock.unlock();
                    }
                };

                MobClearSkillService service = (MobClearSkillService) mmap.getChannelServer().getServiceAccess(ChannelServices.MOB_CLEAR_SKILL);
                service.registerMobClearSkillAction(mmap.getId(), r, milli);
            }
        } finally {
            monsterLock.unlock();
        }
    }

    public Collection<MonsterStatus> alreadyBuffedStats() {
        statiLock.lock();
        try {
            return Collections.unmodifiableCollection(alreadyBuffed);
        } finally {
            statiLock.unlock();
        }
    }

    public BanishInfo getBanish() {
        return stats.getBanishInfo();
    }

    public void setBoss(boolean boss) {
        this.stats.setBoss(boss);
    }

    public int getDropPeriodTime() {
        return stats.getDropPeriod();
    }

    public int getPADamage() {
        return stats.getPADamage();
    }

    public Map<MonsterStatus, MonsterStatusEffect> getStati() {
        statiLock.lock();
        try {
            return new HashMap<>(stati);
        } finally {
            statiLock.unlock();
        }
    }

    public MonsterStatusEffect getStati(MonsterStatus ms) {
        statiLock.lock();
        try {
            return stati.get(ms);
        } finally {
            statiLock.unlock();
        }
    }

    // ---- one can always have fun trying these pieces of codes below in-game rofl ----

    public final ChangeableStats getChangedStats() {
        return ostats;
    }

    public final int getMobMaxHp() {
        if (ostats != null) {
            return ostats.hp;
        }
        return stats.getHp();
    }

    public final void setOverrideStats(final OverrideMonsterStats ostats) {
        this.ostats = new ChangeableStats(stats, ostats);
        this.hp.set(ostats.getHp());
        this.mp = ostats.getMp();
    }

    public final void changeLevel(final int newLevel) {
        changeLevel(newLevel, true);
    }

    public final void changeLevel(final int newLevel, boolean pqMob) {
        if (!stats.isChangeable()) {
            return;
        }
        this.ostats = new ChangeableStats(stats, newLevel, pqMob);
        this.hp.set(ostats.getHp());
        this.mp = ostats.getMp();
    }

    /**
     * 根据难度等级获取怪物属性倍率
     * <p>难度等级越高，怪物属性提升越多</p>
     * @param difficulty 难度等级（2-6）
     * @return 属性倍率值
     */
    private float getDifficultyRate(final int difficulty) {
        switch (difficulty) {
            // 最高难度：7.7倍属性
            case 6:
                return (7.7f);
            // 高难度：5.6倍属性
            case 5:
                return (5.6f);
            // 中高难度：3.2倍属性
            case 4:
                return (3.2f);
            // 中等难度：2.1倍属性
            case 3:
                return (2.1f);
            // 低难度：1.4倍属性
            case 2:
                return (1.4f);
        }

        // 默认难度：1.0倍属性（不变）
        return (1.0f);
    }

    private void changeLevelByDifficulty(final int difficulty, boolean pqMob) {
        changeLevel((int) (this.getLevel() * getDifficultyRate(difficulty)), pqMob);
    }

    public final void changeDifficulty(final int difficulty, boolean pqMob) {
        changeLevelByDifficulty(difficulty, pqMob);
    }

    // ---------------------------------------------------------------------------------

    private boolean isPuppetInVicinity(Summon summon) {
        return summon.getPosition().distanceSq(this.getPosition()) < 177777;
    }

    public boolean isCharacterPuppetInVicinity(Character chr) {
        StatEffect mse = chr.getBuffEffect(BuffStat.PUPPET);
        if (mse != null) {
            Summon summon = chr.getSummonByKey(mse.getSourceId());

            // check whether mob is currently under a puppet's field of action or not
            if (summon != null) {
                return isPuppetInVicinity(summon);
            } else {
                map.getAggroCoordinator().removePuppetAggro(chr.getId());
            }
        }

        return false;
    }

    public boolean isLeadingPuppetInVicinity() {
        Character chrController = this.getActiveController();

        if (chrController != null) {
            return this.isCharacterPuppetInVicinity(chrController);
        }

        return false;
    }

    private Character getNextControllerCandidate() {
        int mincontrolled = Integer.MAX_VALUE;
        Character newController = null;

        int mincontrolleddead = Integer.MAX_VALUE;
        Character newControllerDead = null;

        Character newControllerWithPuppet = null;

        for (Character chr : getMap().getAllPlayers()) {
            if (!chr.isHidden()) {
                int ctrlMonsSize = chr.getNumControlledMonsters();

                if (isCharacterPuppetInVicinity(chr)) {
                    newControllerWithPuppet = chr;
                    break;
                } else if (chr.isAlive()) {
                    if (ctrlMonsSize < mincontrolled) {
                        mincontrolled = ctrlMonsSize;
                        newController = chr;
                    }
                } else {
                    if (ctrlMonsSize < mincontrolleddead) {
                        mincontrolleddead = ctrlMonsSize;
                        newControllerDead = chr;
                    }
                }
            }
        }

        if (newControllerWithPuppet != null) {
            return newControllerWithPuppet;
        } else if (newController != null) {
            return newController;
        } else {
            return newControllerDead;
        }
    }

    /**
     * Removes controllability status from the current controller of this mob.
     */
    public Pair<Character, Boolean> aggroRemoveController() {
        Character chrController;
        boolean hadAggro;

        aggroUpdateLock.lock();
        try {
            chrController = getActiveController();
            hadAggro = isControllerHasAggro();

            this.setController(null);
            this.setControllerHasAggro(false);
            this.setControllerKnowsAboutAggro(false);
        } finally {
            aggroUpdateLock.unlock();
        }

        if (chrController != null) { // this can/should only happen when a hidden gm attacks the monster
            if (!this.isFake()) {
                chrController.sendPacket(PacketCreator.stopControllingMonster(this.getObjectId()));
            }
            chrController.stopControllingMonster(this);
        }

        return new Pair<>(chrController, hadAggro);
    }

    /**
     * Pass over the mob controllability and updates aggro status on the new
     * player controller.
     */
    public void aggroSwitchController(Character newController, boolean immediateAggro) {
        if (aggroUpdateLock.tryLock()) {
            try {
                Character prevController = getController();
                if (prevController == newController) {
                    return;
                }

                aggroRemoveController();
                if (!(newController != null && newController.isLoggedInWorld() && newController.getMap() == this.getMap())) {
                    return;
                }

                this.setController(newController);
                this.setControllerHasAggro(immediateAggro);
                this.setControllerKnowsAboutAggro(false);
                this.setControllerHasPuppet(false);
            } finally {
                aggroUpdateLock.unlock();
            }

            this.aggroUpdatePuppetVisibility();
            aggroMonsterControl(newController.getClient(), this, immediateAggro);
            newController.controlMonster(this);
        }
    }

    public void aggroAddPuppet(Character player) {
        MonsterAggroCoordinator mmac = map.getAggroCoordinator();
        mmac.addPuppetAggro(player);

        aggroUpdatePuppetController(player);

        if (this.isControllerHasAggro()) {
            this.aggroUpdatePuppetVisibility();
        }
    }

    public void aggroRemovePuppet(Character player) {
        MonsterAggroCoordinator mmac = map.getAggroCoordinator();
        mmac.removePuppetAggro(player.getId());

        aggroUpdatePuppetController(null);

        if (this.isControllerHasAggro()) {
            this.aggroUpdatePuppetVisibility();
        }
    }

    /**
     * Automagically finds a new controller for the given monster from the chars
     * on the map it is from...
     */
    public void aggroUpdateController() {
        Character chrController = this.getActiveController();
        if (chrController != null && chrController.isAlive()) {
            return;
        }

        Character newController = getNextControllerCandidate();
        if (newController == null) {    // was a new controller found? (if not no one is on the map)
            return;
        }

        this.aggroSwitchController(newController, false);
    }

    /**
     * Finds a new controller for the given monster from the chars with deployed
     * puppet nearby on the map it is from...
     */
    private void aggroUpdatePuppetController(Character newController) {
        Character chrController = this.getActiveController();
        boolean updateController = false;

        if (chrController != null && chrController.isAlive()) {
            if (isCharacterPuppetInVicinity(chrController)) {
                return;
            }
        } else {
            updateController = true;
        }

        if (newController == null || !isCharacterPuppetInVicinity(newController)) {
            MonsterAggroCoordinator mmac = map.getAggroCoordinator();

            List<Integer> puppetOwners = mmac.getPuppetAggroList();
            List<Integer> toRemovePuppets = new LinkedList<>();

            for (Integer cid : puppetOwners) {
                Character chr = map.getCharacterById(cid);

                if (chr != null) {
                    if (isCharacterPuppetInVicinity(chr)) {
                        newController = chr;
                        break;
                    }
                } else {
                    toRemovePuppets.add(cid);
                }
            }

            for (Integer cid : toRemovePuppets) {
                mmac.removePuppetAggro(cid);
            }

            if (newController == null) {    // was a new controller found? (if not there's no puppet nearby)
                if (updateController) {
                    aggroUpdateController();
                }

                return;
            }
        } else if (chrController == newController) {
            this.aggroUpdatePuppetVisibility();
        }

        this.aggroSwitchController(newController, this.isControllerHasAggro());
    }

    /**
     * Ensures controllability removal of the current player controller, and
     * fetches for any player on the map to start controlling in place.
     */
    public void aggroRedirectController() {
        this.aggroRemoveController();   // don't care if new controller not found, at least remove current controller
        this.aggroUpdateController();
    }

    /**
     * Returns the current aggro status on the specified player, or null if the
     * specified player is currently not this mob's controller.
     */
    public Boolean aggroMoveLifeUpdate(Character player) {
        Character chrController = getController();
        if (chrController != null && player.getId() == chrController.getId()) {
            boolean aggro = this.isControllerHasAggro();
            if (aggro) {
                this.setControllerKnowsAboutAggro(true);
            }

            return aggro;
        } else {
            return null;
        }
    }

    /**
     * Refreshes auto aggro for the player passed as parameter, does nothing if
     * there is already an active controller for this mob.
     */
    public void aggroAutoAggroUpdate(Character player) {
        Character chrController = this.getActiveController();

        if (chrController == null) {
            this.aggroSwitchController(player, true);
        } else if (chrController.getId() == player.getId()) {
            this.setControllerHasAggro(true);
            if (!GameConfig.getServerBoolean("use_auto_aggro_nearby")) {   // thanks Lichtmager for noticing autoaggro not updating the player properly
                aggroMonsterControl(player.getClient(), this, true);
            }
        }
    }

    /**
     * Applied damage input for this mob, enough damage taken implies an aggro
     * target update for the attacker shortly.
     */
    public void aggroMonsterDamage(Character attacker, int damage) {
        MonsterAggroCoordinator mmac = this.getMapAggroCoordinator();
        mmac.addAggroDamage(this, attacker.getId(), damage);

        Character chrController = this.getController();    // aggro based on DPS rather than first-come-first-served, now live after suggestions thanks to MedicOP, Thora, Vcoc
        if (chrController != attacker) {
            if (this.getMapAggroCoordinator().isLeadingCharacterAggro(this, attacker)) {
                this.aggroSwitchController(attacker, true);
            } else {
                this.setControllerHasAggro(true);
                this.aggroUpdatePuppetVisibility();
            }
            
            /*
            For some reason, some mobs loses aggro on controllers if other players also attacks them.
            Maybe Nexon intended to interchange controllers at every attack...
            
            else if (chrController != null) {
                chrController.sendPacket(PacketCreator.stopControllingMonster(this.getObjectId()));
                aggroMonsterControl(chrController.getClient(), this, true);
            }
            */
        } else {
            this.setControllerHasAggro(true);
            this.aggroUpdatePuppetVisibility();
        }
    }

    private static void aggroMonsterControl(Client c, Monster mob, boolean immediateAggro) {
        c.sendPacket(PacketCreator.controlMonster(mob, false, immediateAggro));
    }

    private void aggroRefreshPuppetVisibility(Character chrController, Summon puppet) {
        // lame patch for client to redirect all aggro to the puppet

        List<Monster> puppetControlled = new LinkedList<>();
        for (Monster mob : chrController.getControlledMonsters()) {
            if (mob.isPuppetInVicinity(puppet)) {
                puppetControlled.add(mob);
            }
        }

        for (Monster mob : puppetControlled) {
            chrController.sendPacket(PacketCreator.stopControllingMonster(mob.getObjectId()));
        }
        chrController.sendPacket(PacketCreator.removeSummon(puppet, false));

        Client c = chrController.getClient();
        for (Monster mob : puppetControlled) { // thanks BHB for noticing puppets disrupting mobstatuses for bowmans
            aggroMonsterControl(c, mob, mob.isControllerKnowsAboutAggro());
        }
        chrController.sendPacket(PacketCreator.spawnSummon(puppet, false));
    }

    public void aggroUpdatePuppetVisibility() {
        if (!availablePuppetUpdate) {
            return;
        }

        availablePuppetUpdate = false;
        Runnable r = () -> {
            try {
                Character chrController = Monster.this.getActiveController();
                if (chrController == null) {
                    return;
                }

                StatEffect puppetEffect = chrController.getBuffEffect(BuffStat.PUPPET);
                if (puppetEffect != null) {
                    Summon puppet = chrController.getSummonByKey(puppetEffect.getSourceId());

                    if (puppet != null && isPuppetInVicinity(puppet)) {
                        controllerHasPuppet = true;
                        aggroRefreshPuppetVisibility(chrController, puppet);
                        return;
                    }
                }

                if (controllerHasPuppet) {
                    controllerHasPuppet = false;

                    chrController.sendPacket(PacketCreator.stopControllingMonster(Monster.this.getObjectId()));
                    aggroMonsterControl(chrController.getClient(), Monster.this, Monster.this.isControllerHasAggro());
                }
            } finally {
                availablePuppetUpdate = true;
            }
        };

        // had to schedule this since mob wouldn't stick to puppet aggro who knows why
        OverallService service = (OverallService) this.getMap().getChannelServer().getServiceAccess(ChannelServices.OVERALL);
        service.registerOverallAction(this.getMap().getId(), r, GameConfig.getServerLong("update_interval"));
    }

    /**
     * Clears all applied damage input for this mob, doesn't refresh target
     * aggro.
     */
    public void aggroClearDamages() {
        this.getMapAggroCoordinator().removeAggroEntries(this);
    }

    /**
     * Clears this mob aggro on the current controller.
     */
    public void aggroResetAggro() {
        aggroUpdateLock.lock();
        try {
            this.setControllerHasAggro(false);
            this.setControllerKnowsAboutAggro(false);
        } finally {
            aggroUpdateLock.unlock();
        }
    }

    public final int getRemoveAfter() {
        return stats.removeAfter();
    }

    public void dispose() {
        if (monsterItemDrop != null) {
            monsterItemDrop.cancel(false);
        }

        this.getMap().dismissRemoveAfter(this);
    }
}