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
package org.gms.scripting.reactor;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.constants.inventory.ItemConstants;
import org.gms.scripting.AbstractPlayerInteraction;
import org.gms.server.ItemInformationProvider;
import org.gms.server.TimerManager;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.Monster;
import org.gms.server.maps.MapMonitor;
import org.gms.server.maps.Reactor;
import org.gms.server.maps.ReactorDropEntry;
import org.gms.server.partyquest.CarnivalFactory;
import org.gms.server.partyquest.CarnivalFactory.MCSkill;
import org.gms.util.NumberTool;

import javax.script.Invocable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * 反应堆动作管理器
 * 处理反应堆交互脚本中的玩家操作，包括怪物生成、掉落和传送等
 */
public class ReactorActionManager extends AbstractPlayerInteraction {
    /** 关联的反应堆对象 */
    private final Reactor reactor;
    /** JS脚本可调用对象 */
    private final Invocable iv;
    /** 延迟掉落定时任务 */
    private ScheduledFuture<?> sprayTask = null;

    public ReactorActionManager(Client c, Reactor reactor, Invocable iv) {
        super(c);
        this.reactor = reactor;
        this.iv = iv;
    }

    /**
     * 触发反应堆击打事件
     */
    public void hitReactor() {
        reactor.hitReactor(c);
    }

    /**
     * 销毁指定NPC
     *
     * @param npcId NPC ID
     */
    public void destroyNpc(int npcId) {
        reactor.getMap().destroyNPC(npcId);
    }

    /**
     * 分类反应堆掉落条目
     * 将掉落物按普通、可见任务、其他任务分类
     *
     * @param from         原始掉落列表
     * @param item         普通掉落列表（输出参数）
     * @param visibleQuest 可见任务掉落列表（输出参数）
     * @param otherQuest   其他任务掉落列表（输出参数）
     * @param chr          角色
     */
    private static void sortDropEntries(List<ReactorDropEntry> from, List<ReactorDropEntry> item, List<ReactorDropEntry> visibleQuest, List<ReactorDropEntry> otherQuest, Character chr) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        for (ReactorDropEntry mde : from) {
            if (!ii.isQuestItem(mde.itemId)) {
                item.add(mde);
            } else {
                if (chr.needQuestItem(mde.questid, mde.itemId)) {
                    visibleQuest.add(mde);
                } else {
                    otherQuest.add(mde);
                }
            }
        }
    }

    /**
     * 组装并打乱反应堆掉落列表
     * 按类别分组后洗牌，调整掉落位置顺序
     *
     * @param chr  角色
     * @param items 原始掉落列表
     * @return 组装后的掉落列表
     */
    private static List<ReactorDropEntry> assembleReactorDropEntries(Character chr, List<ReactorDropEntry> items) {
        final List<ReactorDropEntry> dropEntry = new ArrayList<>();
        final List<ReactorDropEntry> visibleQuestEntry = new ArrayList<>();
        final List<ReactorDropEntry> otherQuestEntry = new ArrayList<>();
        sortDropEntries(items, dropEntry, visibleQuestEntry, otherQuestEntry, chr);

        Collections.shuffle(dropEntry);
        Collections.shuffle(visibleQuestEntry);
        Collections.shuffle(otherQuestEntry);

        items.clear();
        items.addAll(dropEntry);
        items.addAll(visibleQuestEntry);
        items.addAll(otherQuestEntry);

        List<ReactorDropEntry> items1 = new ArrayList<>(items.size());
        List<ReactorDropEntry> items2 = new ArrayList<>(items.size() / 2);

        for (int i = 0; i < items.size(); i++) {
            if (i % 2 == 0) {
                items1.add(items.get(i));
            } else {
                items2.add(items.get(i));
            }
        }

        Collections.reverse(items1);
        items1.addAll(items2);

        return items1;
    }

    /**
     * 喷射物品（延迟掉落模式）
     */
    public void sprayItems() {
        sprayItems(false, 0, 0, 0, 0);
    }

    /**
     * 喷射物品（延迟掉落，带金币参数）
     *
     * @param meso       是否包含金币
     * @param mesoChance 金币掉落概率（1/N）
     * @param minMeso    最小金币数
     * @param maxMeso    最大金币数
     */
    public void sprayItems(boolean meso, int mesoChance, int minMeso, int maxMeso) {
        sprayItems(meso, mesoChance, minMeso, maxMeso, 0);
    }

    /**
     * 喷射物品（延迟掉落，带最少掉落参数）
     *
     * @param meso       是否包含金币
     * @param mesoChance 金币掉落概率（1/N）
     * @param minMeso    最小金币数
     * @param maxMeso    最大金币数
     * @param minItems   最少掉落物品数
     */
    public void sprayItems(boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
        sprayItems((int) reactor.getPosition().getX(), (int) reactor.getPosition().getY(), meso, mesoChance, minMeso, maxMeso, minItems);
    }

    /**
     * 喷射物品（延迟掉落，指定位置）
     *
     * @param posX       位置X
     * @param posY       位置Y
     * @param meso       是否包含金币
     * @param mesoChance 金币掉落概率
     * @param minMeso    最小金币数
     * @param maxMeso    最大金币数
     * @param minItems   最少掉落物品数
     */
    public void sprayItems(int posX, int posY, boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
        dropItems(true, posX, posY, meso, mesoChance, minMeso, maxMeso, minItems);
    }

    /**
     * 掉落物品（立即模式）
     */
    public void dropItems() {
        dropItems(false, 0, 0, 0, 0);
    }

    /**
     * 掉落物品（立即模式，指定金币参数）
     *
     * @param meso       是否包含金币
     * @param mesoChance 金币掉落概率
     * @param minMeso    最小金币数
     * @param maxMeso    最大金币数
     */
    public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso) {
        dropItems(meso, mesoChance, minMeso, maxMeso, 0);
    }

    public void dropItems(boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
        dropItems((int) reactor.getPosition().getX(), (int) reactor.getPosition().getY(), meso, mesoChance, minMeso, maxMeso, minItems);
    }

    public void dropItems(int posX, int posY, boolean meso, int mesoChance, int minMeso, int maxMeso, int minItems) {
        dropItems(true, posX, posY, meso, mesoChance, minMeso, maxMeso, minItems);
    }

    /**
     * 核心物品掉落逻辑
     * 根据反应堆掉落列表生成物品/金币，支持立即掉落和延迟喷射两种模式
     *
     * @param delayed    是否延迟掉落（逐帧喷射）
     * @param posX       掉落位置X
     * @param posY       掉落位置Y
     * @param meso       是否包含金币
     * @param mesoChance 金币掉落概率
     * @param minMeso    最小金币数
     * @param maxMeso    最大金币数
     * @param minItems   最少掉落物品数
     */
    public void dropItems(boolean delayed, int posX, int posY, boolean meso, int mesoChance, final int minMeso, final int maxMeso, int minItems) {
        Character chr = c.getPlayer();
        if (chr == null) {
            return;
        }

        List<ReactorDropEntry> items = assembleReactorDropEntries(chr, generateDropList(getDropChances(), chr.getDropRate(), meso, mesoChance, minItems));
        if (items.size() % 2 == 0) {
            posX -= 12;
        }
        final Point dropPos = new Point(posX, posY);

        if (!delayed) {
            ItemInformationProvider ii = ItemInformationProvider.getInstance();

            byte p = 1;
            for (ReactorDropEntry d : items) {
                dropPos.x = posX + ((p % 2 == 0) ? (25 * ((p + 1) / 2)) : -(25 * (p / 2)));
                p++;

                if (d.itemId == 0) {
                    int range = maxMeso - minMeso;
                    double displayDrop = Math.random() * range + minMeso;
                    int mesoDrop = NumberTool.doubleToInt(displayDrop * c.getWorldServer().getMesoRate());
                    reactor.getMap().spawnMesoDrop(mesoDrop, reactor.getMap().calcDropPos(dropPos, reactor.getPosition()), reactor, c.getPlayer(), false, (byte) 2);
                } else {
                    Item drop;

                    if (ItemConstants.getInventoryType(d.itemId) != InventoryType.EQUIP) {
                        drop = new Item(d.itemId, (short) 0, (short) 1);
                    } else {
                        drop = ii.randomizeStats((Equip) ii.getEquipById(d.itemId));
                    }

                    reactor.getMap().dropFromReactor(getPlayer(), reactor, drop, dropPos, (short) d.questid);
                }
            }
        } else {
            final Reactor r = reactor;
            final List<ReactorDropEntry> dropItems = items;
            final float worldMesoRate = c.getWorldServer().getMesoRate();

            dropPos.x -= (12 * items.size());

            sprayTask = TimerManager.getInstance().register(() -> {
                if (dropItems.isEmpty()) {
                    sprayTask.cancel(false);
                    return;
                }

                ReactorDropEntry d = dropItems.remove(0);
                if (d.itemId == 0) {
                    int range = maxMeso - minMeso;
                    double displayDrop = Math.random() * range + minMeso;
                    int mesoDrop = NumberTool.doubleToInt(displayDrop * worldMesoRate);
                    r.getMap().spawnMesoDrop(mesoDrop, r.getMap().calcDropPos(dropPos, r.getPosition()), r, chr, false, (byte) 2);
                } else {
                    Item drop;

                    if (ItemConstants.getInventoryType(d.itemId) != InventoryType.EQUIP) {
                        drop = new Item(d.itemId, (short) 0, (short) 1);
                    } else {
                        ItemInformationProvider ii = ItemInformationProvider.getInstance();
                        drop = ii.randomizeStats((Equip) ii.getEquipById(d.itemId));
                    }

                    r.getMap().dropFromReactor(getPlayer(), r, drop, dropPos, (short) d.questid);
                }

                dropPos.x += 25;
            }, 200);
        }
    }

    private List<ReactorDropEntry> getDropChances() {
        return ReactorScriptManager.getInstance().getDrops(reactor.getId());
    }

    private List<ReactorDropEntry> generateDropList(List<ReactorDropEntry> drops, float dropRate, boolean meso, int mesoChance, int minItems) {
        List<ReactorDropEntry> items = new ArrayList<>();
        if (meso && Math.random() < (1 / (double) mesoChance)) {
            items.add(new ReactorDropEntry(0, mesoChance, -1));
        }

        for (ReactorDropEntry mde : drops) {
            if (Math.random() < (dropRate / (double) mde.chance)) {
                items.add(mde);
            }
        }

        while (items.size() < minItems) {
            items.add(new ReactorDropEntry(0, mesoChance, -1));
        }

        return items;
    }

    /**
     * 生成怪物
     *
     * @param id 怪物ID
     */
    public void spawnMonster(int id) {
        spawnMonster(id, 1, getPosition());
    }

    /**
     * 创建地图监视器
     *
     * @param mapId  地图ID
     * @param portal 传送门名称
     */
    public void createMapMonitor(int mapId, String portal) {
        new MapMonitor(c.getChannelServer().getMapFactory().getMap(mapId), portal);
    }

    public void spawnMonster(int id, int qty) {
        spawnMonster(id, qty, getPosition());
    }

    public void spawnMonster(int id, int qty, int x, int y) {
        spawnMonster(id, qty, new Point(x, y));
    }

    /**
     * 在指定位置生成多个怪物
     *
     * @param id  怪物ID
     * @param qty 数量
     * @param pos 生成位置
     */
    public void spawnMonster(int id, int qty, Point pos) {
        for (int i = 0; i < qty; i++) {
            reactor.getMap().spawnMonsterOnGroundBelow(LifeFactory.getMonster(id), pos);
        }
    }

    /**
     * 杀死地图中的指定怪物
     *
     * @param id 怪物ID
     */
    public void killMonster(int id) {
        killMonster(id, false);
    }

    /**
     * 杀死地图中的指定怪物
     *
     * @param id        怪物ID
     * @param withDrops 是否掉落物品
     */
    public void killMonster(int id, boolean withDrops) {
        if (withDrops) {
            getMap().killMonsterWithDrops(id);
        } else {
            getMap().killMonster(id);
        }
    }

    /**
     * 获取反应堆位置（Y轴偏移-10）
     *
     * @return 掉落位置
     */
    public Point getPosition() {
        Point pos = reactor.getPosition();
        pos.y -= 10;
        return pos;
    }

    /**
     * 在反应堆位置生成NPC
     *
     * @param npcId NPC ID
     */
    public void spawnNpc(int npcId) {
        spawnNpc(npcId, getPosition());
    }

    /**
     * 在指定位置生成NPC
     *
     * @param npcId NPC ID
     * @param pos   生成位置
     */
    public void spawnNpc(int npcId, Point pos) {
        spawnNpc(npcId, pos, reactor.getMap());
    }

    /**
     * 获取关联的反应堆
     *
     * @return 反应堆对象
     */
    public Reactor getReactor() {
        return reactor;
    }

    /**
     * 生成伪装怪物
     *
     * @param id 怪物ID
     */
    public void spawnFakeMonster(int id) {
        reactor.getMap().spawnFakeMonsterOnGroundBelow(LifeFactory.getMonster(id), getPosition());
    }

    /**
     * 延迟召唤Boss（Targa/Scarlion专用）
     *
     * @param mobId         怪物ID
     * @param delayMs        延迟毫秒数
     * @param x              召唤位置X
     * @param y              召唤位置Y
     * @param bgm            背景音乐
     * @param summonMessage  召唤提示消息
     */
    public void summonBossDelayed(final int mobId, final int delayMs, final int x, final int y, final String bgm,
                                  final String summonMessage) {
        TimerManager.getInstance().schedule(() -> {
            summonBoss(mobId, x, y, bgm, summonMessage);
        }, delayMs);
    }

    /**
     * 召唤Boss（生成怪物 + 切换BGM + 地图公告）
     *
     * @param mobId         怪物ID
     * @param x             召唤位置X
     * @param y             召唤位置Y
     * @param bgmName       背景音乐名称
     * @param summonMessage 召唤提示消息
     */
    private void summonBoss(int mobId, int x, int y, String bgmName, String summonMessage) {
        spawnMonster(mobId, x, y);
        changeMusic(bgmName);
        mapMessage(6, summonMessage);
    }

    /**
     * 驱散队伍的所有怪物（CPQ嘉年华专用）
     *
     * @param num  守护者编号
     * @param team 队伍编号（0=红队, 1=蓝队）
     */
    public void dispelAllMonsters(int num, int team) {
        final MCSkill skil = CarnivalFactory.getInstance().getGuardian(num);
        if (skil != null) {
            for (Monster mons : getMap().getAllMonsters()) {
                if (mons.getTeam() == team) {
                    mons.dispelSkill(skil.getSkill());
                }
            }
        }
        if (team == 0) {
            getPlayer().getMap().getRedTeamBuffs().remove(skil);
        } else {
            getPlayer().getMap().getBlueTeamBuffs().remove(skil);
        }
    }
}