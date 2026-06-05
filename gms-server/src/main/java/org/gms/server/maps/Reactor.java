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
package org.gms.server.maps;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.config.GameConfig;
import org.gms.net.packet.Packet;
import org.gms.net.server.services.task.channel.OverallService;
import org.gms.net.server.services.type.ChannelServices;
import org.gms.scripting.reactor.ReactorScriptManager;
import org.gms.server.TimerManager;
import org.gms.server.partyquest.GuardianSpawnPoint;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 【类】Reactor（class），包 {@code org.gms.server.maps}。
 * 
 * <p>反应器类，表示地图上可交互的动态对象（如可破坏的罐子、开关、机关等），
 * 管理反应器的生命周期、状态转换、物品掉落和重生逻辑。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理反应器的多种状态（未激活、激活、破坏等）</li>
 *   <li>处理玩家与反应器的交互（点击、攻击等）</li>
 *   <li>控制物品掉落机制</li>
 *   <li>处理反应器的自动重生</li>
 *   <li>支持脚本化的反应器行为</li>
 * </ul>
 * 
 * <p>设计特点：</p>
 * <ul>
 *   <li>线程安全：使用ReentrantLock保护状态变更</li>
 *   <li>状态驱动：通过状态机管理反应器行为</li>
 *   <li>可扩展：支持自定义脚本和事件处理</li>
 * </ul>
 * 
 * @author OdinMS (original)
 * @author Xergon (adaptation)
 * @since 2024-07-18
 */
public class Reactor extends AbstractMapObject {
    /** 反应器模板ID */
    private final int rid;
    /** 反应器状态统计信息 */
    private final ReactorStats stats;
    /** 当前状态（用于状态转换） */
    private byte state;
    /** 事件状态（用于事件系统） */
    private byte evstate;
    /** 状态转换延迟时间 */
    private int delay;
    /** 所属地图引用 */
    private MapleMap map;
    /** 反应器名称 */
    private String name;
    /** 是否存活状态 */
    private boolean alive;
    /** 是否应该收集物品 */
    private boolean shouldCollect;
    /** 是否被攻击击中 */
    private boolean attackHit;
    /** 状态超时任务 */
    private ScheduledFuture<?> timeoutTask = null;
    /** 延迟重生任务 */
    private Runnable delayedRespawnRun = null;
    /** 守卫生成点（用于特定事件） */
    private GuardianSpawnPoint guardian = null;
    /** 面向方向 */
    private byte facingDirection = 0;
    /** 反应器操作锁（保护状态变更） */
    private final Lock reactorLock = new ReentrantLock(true);
    /** 击中操作锁（保护击中逻辑） */
    private final Lock hitLock = new ReentrantLock(true);

    /**
     * 构造函数：创建反应器实例
     * 
     * <p>初始化反应器的基本属性，设置初始状态为存活。</p>
     * 
     * <p>此构造函数会：</p>
     * <ul>
     *   <li>初始化事件状态为0</li>
     *   <li>设置反应器状态统计信息</li>
     *   <li>设置反应器模板ID</li>
     *   <li>设置初始存活状态</li>
     * </ul>
     * 
     * @param stats 反应器状态统计信息
     * @param rid 反应器模板ID
     */
    public Reactor(ReactorStats stats, int rid) {
        this.evstate = (byte) 0;  // 初始化事件状态为0
        this.stats = stats;  // 设置状态统计
        this.rid = rid;  // 设置反应器ID
        this.alive = true;  // 初始状态为存活
    }

    /**
     * 设置是否应该收集物品
     * 
     * <p>控制反应器被破坏后是否自动收集掉落的物品。</p>
     * 
     * @param collect 是否自动收集物品
     */
    public void setShouldCollect(boolean collect) {
        this.shouldCollect = collect;  // 设置收集标志
    }

    /**
     * 获取是否应该收集物品
     * 
     * <p>检查反应器是否设置为自动收集掉落物品。</p>
     * 
     * @return 如果应该收集物品则返回true，否则返回false
     */
    public boolean getShouldCollect() {
        return shouldCollect;  // 返回收集标志
    }

    /**
     * 锁定反应器
     */
    public void lockReactor() {
        reactorLock.lock();  // 获取反应器锁
    }

    /**
     * 解锁反应器
     */
    public void unlockReactor() {
        reactorLock.unlock();  // 释放反应器锁
    }

    /**
     * 锁定击中相关操作
     */
    public void hitLockReactor() {
        hitLock.lock();  // 获取击中锁
        reactorLock.lock();  // 获取反应器锁
    }

    /**
     * 解锁击中相关操作
     */
    public void hitUnlockReactor() {
        reactorLock.unlock();  // 释放反应器锁
        hitLock.unlock();  // 释放击中锁
    }

    /**
     * 设置当前状态
     * @param state 状态值
     */
    public void setState(byte state) {
        this.state = state;  // 设置状态
    }

    /**
     * 获取当前状态
     * @return 状态值
     */
    public byte getState() {
        return state;  // 返回当前状态
    }

    /**
     * 设置事件状态
     * @param substate 事件状态值
     */
    public void setEventState(byte substate) {
        this.evstate = substate;  // 设置事件状态
    }

    /**
     * 获取事件状态
     * @return 事件状态值
     */
    public byte getEventState() {
        return evstate;  // 返回事件状态
    }

    /**
     * 获取反应器状态统计
     * @return 状态统计对象
     */
    public ReactorStats getStats() {
        return stats;  // 返回状态统计
    }

    /**
     * 获取反应器ID
     * @return 反应器ID
     */
    public int getId() {
        return rid;  // 返回反应器ID
    }

    /**
     * 设置延迟时间
     * @param delay 延迟时间(毫秒)
     */
    public void setDelay(int delay) {
        this.delay = delay;  // 设置延迟时间
    }

    /**
     * 获取延迟时间
     * @return 延迟时间(毫秒)
     */
    public int getDelay() {
        return delay;  // 返回延迟时间
    }

    @Override
    public MapObjectType getType() {
        return MapObjectType.REACTOR;  // 返回对象类型为反应器
    }

    /**
     * 获取反应器类型
     * @return 反应器类型
     */
    public int getReactorType() {
        return stats.getType(state);  // 返回当前状态的类型
    }

    /**
     * 检查是否最近被攻击击中
     * @return 是否被击中
     */
    public boolean isRecentHitFromAttack() {
        return attackHit;  // 返回攻击击中标志
    }

    /**
     * 设置所属地图
     * @param map 地图对象
     */
    public void setMap(MapleMap map) {
        this.map = map;  // 设置地图
    }

    /**
     * 获取所属地图
     * @return 地图对象
     */
    public MapleMap getMap() {
        return map;  // 返回地图
    }

    /**
     * 获取反应物品
     * @param index 物品索引
     * @return 物品ID和数量对
     */
    public Pair<Integer, Integer> getReactItem(byte index) {
        return stats.getReactItem(state, index);  // 返回指定状态的物品
    }

    /**
     * 检查是否存活
     * @return 是否存活
     */
    public boolean isAlive() {
        return alive;  // 返回存活状态
    }

    /**
     * 检查是否活跃
     * @return 是否活跃
     */
    public boolean isActive() {
        return alive && stats.getType(state) != -1;  // 存活且状态类型有效
    }

    /**
     * 设置存活状态
     * @param alive 是否存活
     */
    public void setAlive(boolean alive) {
        this.alive = alive;  // 设置存活状态
    }

    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(makeDestroyData());  // 发送销毁数据包
    }

    /**
     * 创建销毁数据包
     * @return 数据包
     */
    public final Packet makeDestroyData() {
        return PacketCreator.destroyReactor(this);  // 生成反应器销毁包
    }

    @Override
    public void sendSpawnData(Client client) {
        if (this.isAlive()) {
            client.sendPacket(makeSpawnData());  // 如果存活则发送生成数据
        }
    }

    /**
     * 创建生成数据包
     * @return 数据包
     */
    public final Packet makeSpawnData() {
        return PacketCreator.spawnReactor(this);  // 生成反应器生成包
    }

    /**
     * 重置反应器动作
     * @param newState 新状态
     */
    /**
     * 重置反应器动作到指定状态
     * 
     * <p>将反应器重置到指定的新状态，包括更新状态值、取消现有超时任务、
     * 设置可收集标志，并重新安排新的超时任务。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>设置反应器到新状态</li>
     *   <li>取消现有的超时任务</li>
     *   <li>启用物品收集功能</li>
     *   <li>重新安排超时任务</li>
     *   <li>在地图上搜索相关的物品反应器</li>
     * </ul>
     * 
     * @param newState 要设置的新状态值
     */
    public void resetReactorActions(int newState) {
        setState((byte) newState);  // 设置新状态
        cancelReactorTimeout();  // 取消超时任务
        setShouldCollect(true);  // 设置可收集
        refreshReactorTimeout();  // 刷新超时

        if (map != null) {
            map.searchItemReactors(this);  // 搜索物品反应器
        }
    }

    /**
     * 强制击中反应器
     * 
     * <p>强制将反应器设置到指定状态，通常用于系统控制或特殊事件触发。
     * 此方法会绕过正常的击中逻辑，直接改变反应器状态并广播触发消息。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>锁定反应器以确保线程安全</li>
     *   <li>重置反应器动作为指定状态</li>
     *   <li>向地图上的玩家广播反应器触发消息</li>
     *   <li>解锁反应器</li>
     * </ul>
     * 
     * @param newState 要强制设置的新状态
     */
    public void forceHitReactor(final byte newState) {
        this.lockReactor();  // 锁定反应器
        try {
            this.resetReactorActions(newState);  // 重置动作
            map.broadcastMessage(PacketCreator.triggerReactor(this, (short) 0));  // 广播触发消息
        } finally {
            this.unlockReactor();  // 解锁反应器
        }
    }

    /**
     * 尝试强制击中反应器(弱信号)
     * @param newState 新状态
     */
    private void tryForceHitReactor(final byte newState) {
        if (!reactorLock.tryLock()) {
            return;  // 如果无法获取锁则直接返回
        }

        try {
            this.resetReactorActions(newState);  // 重置动作
            map.broadcastMessage(PacketCreator.triggerReactor(this, (short) 0));  // 广播触发消息
        } finally {
            reactorLock.unlock();  // 释放锁
        }
    }

    /**
     * 取消反应器超时
     */
    /**
     * 取消反应器超时任务
     * 
     * <p>取消当前注册的反应器超时任务，释放相关的定时器资源。
     * 此方法用于在反应器状态变更或清理时停止之前的超时任务。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>检查是否存在超时任务</li>
     *   <li>取消定时任务的执行</li>
     *   <li>清空任务引用以避免内存泄漏</li>
     * </ul>
     */
    public void cancelReactorTimeout() {
        if (timeoutTask != null) {
            timeoutTask.cancel(false);  // 取消任务
            timeoutTask = null;  // 清空引用
        }
    }

    /**
     * 刷新反应器超时任务
     * 
     * <p>根据当前状态重新安排反应器的超时任务。如果当前状态配置了超时时间，
     * 则安排一个定时任务在指定时间后将反应器转换到超时后的状态。</p>
     * 
     * <p>此方法会：</p>
     * <ul>
     *   <li>获取当前状态的超时时间</li>
     *   <li>检查是否需要安排超时任务</li>
     *   <li>安排新的超时任务</li>
     *   <li>设置任务完成后的行为</li>
     * </ul>
     */
    private void refreshReactorTimeout() {
        int timeOut = stats.getTimeout(state);  // 获取超时时间
        if (timeOut > -1) {
            final byte nextState = stats.getTimeoutState(state);  // 获取超时后状态

            timeoutTask = TimerManager.getInstance().schedule(() -> {
                timeoutTask = null;  // 清空任务引用
                tryForceHitReactor(nextState);  // 尝试强制击中
            }, timeOut);  // 调度超时任务
        }
    }

    /**
     * 延迟击中反应器
     * @param c 客户端
     * @param delay 延迟时间
     */
    public void delayedHitReactor(final Client c, long delay) {
        TimerManager.getInstance().schedule(() -> hitReactor(c), delay);  // 延迟执行击中
    }

    /**
     * 击中反应器
     * @param c 客户端
     */
    public void hitReactor(Client c) {
        hitReactor(false, 0, (short) 0, 0, c);  // 默认参数调用
    }

    /**
     * 击中反应器（完整参数）
     * 
     * <p>处理玩家对反应器的击中操作，根据击中类型和参数执行相应的状态转换和物品掉落逻辑。
     * 此方法是反应器交互的核心，处理玩家攻击、点击等操作。</p>
     * 
     * <p>处理流程：</p>
     * <ol>
     *   <li>检查反应器是否处于可击中状态</li>
     *   <li>尝试获取击中锁以确保线程安全</li>
     *   <li>根据击中类型和技能ID确定下一个状态</li>
     *   <li>执行状态转换和物品掉落</li>
     *   <li>触发反应器脚本</li>
     *   <li>处理重生逻辑（如果需要）</li>
     * </ol>
     * 
     * @param wHit 是否为武器击中（而非鼠标点击）
     * @param charPos 角色位置（用于确定击中方向）
     * @param stance 姿态（角色当前姿态）
     * @param skillid 使用的技能ID（可能影响击中效果）
     * @param c 执行击中操作的客户端
     */
    public void hitReactor(boolean wHit, int charPos, short stance, int skillid, Client c) {
        try {
            if (!this.isActive()) {
                return;  // 如果不活跃则直接返回
            }

            if (hitLock.tryLock()) {
                this.lockReactor();  // 锁定反应器
                try {
                    cancelReactorTimeout();  // 取消超时
                    attackHit = wHit;  // 设置击中标志

                    Character player = c.getPlayer();
                    if (GameConfig.getServerBoolean("use_debug") && player.isGM()) {
                        player.dropMessage(5, "击中反应器 " + this.getId() + " 位置 " + charPos + " , 姿态 " + stance + " , 技能ID " + skillid + " , 状态 " + state + " 状态大小 " + stats.getStateSize(state));  // GM调试信息
                    }
                    ReactorScriptManager.getInstance().onHit(c, this);  // 调用击中脚本

                    int reactorType = stats.getType(state);
                    if (reactorType < 999 && reactorType != -1) {  // 类型2=只能从右侧击中(沼泽植物), 00是左侧空中 02是左侧地面
                        if (!(reactorType == 2 && (stance == 0 || stance == 2))) {  // 获取下一状态
                            for (byte b = 0; b < stats.getStateSize(state); b++) {  // 遍历状态
                                List<Integer> activeSkills = stats.getActiveSkills(state, b);
                                if (activeSkills != null) {
                                    if (!activeSkills.contains(skillid)) {
                                        continue;  // 技能不匹配则跳过
                                    }
                                }

                                this.state = stats.getNextState(state, b);  // 设置下一状态
                                byte nextState = stats.getNextState(state, b);
                                boolean isInEndState = nextState < this.state;
                                if (isInEndState) {  // 反应器结束状态
                                    if (reactorType < 100) {  // 反应器破坏
                                        if (delay > 0) {
                                            map.destroyReactor(getObjectId());  // 延迟销毁
                                        } else {  // 正常触发
                                            map.broadcastMessage(PacketCreator.triggerReactor(this, stance));  // 广播触发
                                        }
                                    } else {  // 最终步骤物品触发
                                        map.broadcastMessage(PacketCreator.triggerReactor(this, stance));  // 广播触发
                                    }

                                    ReactorScriptManager.getInstance().act(c, this);  // 执行脚本动作
                                } else {  // 反应器未完全破坏
                                    map.broadcastMessage(PacketCreator.triggerReactor(this, stance));  // 广播触发
                                    if (state == stats.getNextState(state, b)) {  // 当前状态=下一状态,循环反应器
                                        ReactorScriptManager.getInstance().act(c, this);  // 执行脚本动作
                                    }

                                    setShouldCollect(true);  // 刷新物品掉落反应器的可收集性
                                    refreshReactorTimeout();  // 刷新超时
                                    if (stats.getType(state) == 100) {
                                        map.searchItemReactors(this);  // 搜索物品反应器
                                    }
                                }
                                break;  // 跳出循环
                            }
                        }
                    } else {
                        state++;  // 状态自增
                        map.broadcastMessage(PacketCreator.triggerReactor(this, stance));  // 广播触发
                        if (this.getId() != 9980000 && this.getId() != 9980001) {
                            ReactorScriptManager.getInstance().act(c, this);  // 执行脚本动作(特殊ID除外)
                        }

                        setShouldCollect(true);  // 设置可收集
                        refreshReactorTimeout();  // 刷新超时
                        if (stats.getType(state) == 100) {
                            map.searchItemReactors(this);  // 搜索物品反应器
                        }
                    }
                } finally {
                    this.unlockReactor();  // 解锁反应器
                    hitLock.unlock();  // 解锁击中(感谢MiLin发现非封装解锁)
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  // 打印异常
        }
    }

    /**
     * 销毁反应器
     * @return 是否成功销毁
     */
    public boolean destroy() {
        if (reactorLock.tryLock()) {
            try {
                boolean alive = this.isAlive();
                // 反应器既不存活也不在延迟重生中，允许移除地图对象
                if (alive) {
                    this.setAlive(false);  // 设置死亡
                    this.cancelReactorTimeout();  // 取消超时

                    if (this.getDelay() > 0) {
                        this.delayedRespawn();  // 延迟重生
                    }
                } else {
                    return !this.inDelayedRespawn();  // 返回是否不在延迟重生中
                }
            } finally {
                reactorLock.unlock();  // 解锁
            }
        }

        map.broadcastMessage(PacketCreator.destroyReactor(this));  // 广播销毁消息
        return false;  // 返回失败
    }

    /**
     * 重生反应器
     */
    private void respawn() {
        this.lockReactor();  // 锁定
        try {
            this.resetReactorActions(0);  // 重置动作
            this.setAlive(true);  // 设置存活
        } finally {
            this.unlockReactor();  // 解锁
        }

        map.broadcastMessage(this.makeSpawnData());  // 广播生成消息
    }

    /**
     * 延迟重生
     */
    public void delayedRespawn() {
        Runnable r = () -> {
            delayedRespawnRun = null;  // 清空任务引用
            respawn();  // 执行重生
        };

        delayedRespawnRun = r;  // 设置任务

        OverallService service = (OverallService) map.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
        service.registerOverallAction(map.getId(), r, this.getDelay());  // 注册全局动作
    }

    /**
     * 强制延迟重生
     * @return 是否成功
     */
    public boolean forceDelayedRespawn() {
        Runnable r = delayedRespawnRun;

        if (r != null) {
            OverallService service = (OverallService) map.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
            service.forceRunOverallAction(map.getId(), r);  // 强制运行全局动作
            return true;  // 返回成功
        } else {
            return false;  // 返回失败
        }
    }

    /**
     * 检查是否在延迟重生中
     * @return 是否在延迟重生中
     */
    public boolean inDelayedRespawn() {
        return delayedRespawnRun != null;  // 返回任务是否存在
    }

    /**
     * 获取区域矩形
     * @return 矩形区域
     */
    public Rectangle getArea() {
        return new Rectangle(getPosition().x + stats.getTL().x, getPosition().y + stats.getTL().y, stats.getBR().x - stats.getTL().x, stats.getBR().y - stats.getTL().y);  // 计算并返回区域
    }

    /**
     * 获取名称
     * @return 名称
     */
    public String getName() {
        return name;  // 返回名称
    }

    /**
     * 设置名称
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;  // 设置名称
    }

    /**
     * 获取守卫生成点
     * @return 守卫生成点
     */
    public GuardianSpawnPoint getGuardian() {
        return guardian;  // 返回守卫
    }

    /**
     * 设置守卫生成点
     * @param guardian 守卫生成点
     */
    public void setGuardian(GuardianSpawnPoint guardian) {
        this.guardian = guardian;  // 设置守卫
    }

    /**
     * 设置面向方向
     * @param facingDirection 方向
     */
    public final void setFacingDirection(final byte facingDirection) {
        this.facingDirection = facingDirection;  // 设置方向
    }

    /**
     * 获取面向方向
     * @return 方向
     */
    public final byte getFacingDirection() {
        return facingDirection;  // 返回方向
    }
}