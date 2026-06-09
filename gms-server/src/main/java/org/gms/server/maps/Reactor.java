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
 * 反应器
 * 地图上的可交互对象，如宝箱、矿脉、药草等，支持多状态切换和掉落物品
 * 使用ReentrantLock保证状态切换的线程安全，通过定时器管理超时和重置
 *
 * @author Lerk
 * @author Ronan
 */
public class Reactor extends AbstractMapObject {
    /** 反应器ID */
    private final int rid;
    /** 反应器属性 */
    private final ReactorStats stats;
    /** 当前状态 */
    private byte state;
    /** 事件状态 */
    private byte evstate;
    /** 延迟时间 */
    private int delay;
    /** 所属地图 */
    private MapleMap map;
    /** 名称 */
    private String name;
    /** 是否存活 */
    private boolean alive;
    /** 是否应该收集物品 */
    private boolean shouldCollect;
    /** 是否被攻击击中 */
    private boolean attackHit;
    /** 超时任务 */
    private ScheduledFuture<?> timeoutTask = null;
    /** 延迟重生任务 */
    private Runnable delayedRespawnRun = null;
    /** 守卫生成点 */
    private GuardianSpawnPoint guardian = null;
    /** 面向方向 */
    private byte facingDirection = 0;
    /** 反应器锁 */
    private final Lock reactorLock = new ReentrantLock(true);
    /** 击中锁 */
    private final Lock hitLock = new ReentrantLock(true);

    /**
     * 构造函数
     * @param stats 反应器状态统计
     * @param rid 反应器ID
     */
    public Reactor(ReactorStats stats, int rid) {
        // 初始化事件状态为0
        this.evstate = (byte) 0;
        // 设置状态统计
        this.stats = stats;
        // 设置反应器ID
        this.rid = rid;
        // 初始状态为存活
        this.alive = true;
    }

    /**
     * 设置是否应该收集物品
     * @param collect 是否收集
     */
    public void setShouldCollect(boolean collect) {
        // 设置收集标志
        this.shouldCollect = collect;
    }

    /**
     * 获取是否应该收集物品
     * @return 是否收集
     */
    public boolean getShouldCollect() {
        // 返回收集标志
        return shouldCollect;
    }

    /**
     * 锁定反应器
     */
    public void lockReactor() {
        // 获取反应器锁
        reactorLock.lock();
    }

    /**
     * 解锁反应器
     */
    public void unlockReactor() {
        // 释放反应器锁
        reactorLock.unlock();
    }

    /**
     * 锁定击中相关操作
     */
    public void hitLockReactor() {
        // 获取击中锁
        hitLock.lock();
        // 获取反应器锁
        reactorLock.lock();
    }

    /**
     * 解锁击中相关操作
     */
    public void hitUnlockReactor() {
        // 释放反应器锁
        reactorLock.unlock();
        // 释放击中锁
        hitLock.unlock();
    }

    /**
     * 设置当前状态
     * @param state 状态值
     */
    public void setState(byte state) {
        // 设置状态
        this.state = state;
    }

    /**
     * 获取当前状态
     * @return 状态值
     */
    public byte getState() {
        // 返回当前状态
        return state;
    }

    /**
     * 设置事件状态
     * @param substate 事件状态值
     */
    public void setEventState(byte substate) {
        // 设置事件状态
        this.evstate = substate;
    }

    /**
     * 获取事件状态
     * @return 事件状态值
     */
    public byte getEventState() {
        // 返回事件状态
        return evstate;
    }

    /**
     * 获取反应器状态统计
     * @return 状态统计对象
     */
    public ReactorStats getStats() {
        // 返回状态统计
        return stats;
    }

    /**
     * 获取反应器ID
     * @return 反应器ID
     */
    public int getId() {
        // 返回反应器ID
        return rid;
    }

    /**
     * 设置延迟时间
     * @param delay 延迟时间(毫秒)
     */
    public void setDelay(int delay) {
        // 设置延迟时间
        this.delay = delay;
    }

    /**
     * 获取延迟时间
     * @return 延迟时间(毫秒)
     */
    public int getDelay() {
        // 返回延迟时间
        return delay;
    }

    @Override
    public MapObjectType getType() {
        // 返回对象类型为反应器
        return MapObjectType.REACTOR;
    }

    /**
     * 获取反应器类型
     * @return 反应器类型
     */
    public int getReactorType() {
        // 返回当前状态的类型
        return stats.getType(state);
    }

    /**
     * 检查是否最近被攻击击中
     * @return 是否被击中
     */
    public boolean isRecentHitFromAttack() {
        // 返回攻击击中标志
        return attackHit;
    }

    /**
     * 设置所属地图
     * @param map 地图对象
     */
    public void setMap(MapleMap map) {
        // 设置地图
        this.map = map;
    }

    /**
     * 获取所属地图
     * @return 地图对象
     */
    public MapleMap getMap() {
        // 返回地图
        return map;
    }

    /**
     * 获取反应物品
     * @param index 物品索引
     * @return 物品ID和数量对
     */
    public Pair<Integer, Integer> getReactItem(byte index) {
        // 返回指定状态的物品
        return stats.getReactItem(state, index);
    }

    /**
     * 检查是否存活
     * @return 是否存活
     */
    public boolean isAlive() {
        // 返回存活状态
        return alive;
    }

    /**
     * 检查是否活跃
     * @return 是否活跃
     */
    public boolean isActive() {
        // 存活且状态类型有效
        return alive && stats.getType(state) != -1;
    }

    /**
     * 设置存活状态
     * @param alive 是否存活
     */
    public void setAlive(boolean alive) {
        // 设置存活状态
        this.alive = alive;
    }

    @Override
    public void sendDestroyData(Client client) {
        // 发送销毁数据包
        client.sendPacket(makeDestroyData());
    }

    /**
     * 创建销毁数据包
     * @return 数据包
     */
    public final Packet makeDestroyData() {
        // 生成反应器销毁包
        return PacketCreator.destroyReactor(this);
    }

    @Override
    public void sendSpawnData(Client client) {
        if (this.isAlive()) {
            // 如果存活则发送生成数据
            client.sendPacket(makeSpawnData());
        }
    }

    /**
     * 创建生成数据包
     * @return 数据包
     */
    public final Packet makeSpawnData() {
        // 生成反应器生成包
        return PacketCreator.spawnReactor(this);
    }

    /**
     * 重置反应器动作
     * @param newState 新状态
     */
    public void resetReactorActions(int newState) {
        // 设置新状态
        setState((byte) newState);
        // 取消超时任务
        cancelReactorTimeout();
        // 设置可收集
        setShouldCollect(true);
        // 刷新超时
        refreshReactorTimeout();

        if (map != null) {
            // 搜索物品反应器
            map.searchItemReactors(this);
        }
    }

    /**
     * 强制击中反应器
     * @param newState 新状态
     */
    public void forceHitReactor(final byte newState) {
        // 锁定反应器
        this.lockReactor();
        try {
            // 重置动作
            this.resetReactorActions(newState);
            // 广播触发消息
            map.broadcastMessage(PacketCreator.triggerReactor(this, (short) 0));
        } finally {
            // 解锁反应器
            this.unlockReactor();
        }
    }

    /**
     * 尝试强制击中反应器(弱信号)
     * @param newState 新状态
     */
    private void tryForceHitReactor(final byte newState) {
        if (!reactorLock.tryLock()) {
            // 如果无法获取锁则直接返回
            return;
        }

        try {
            // 重置动作
            this.resetReactorActions(newState);
            // 广播触发消息
            map.broadcastMessage(PacketCreator.triggerReactor(this, (short) 0));
        } finally {
            // 释放锁
            reactorLock.unlock();
        }
    }

    /**
     * 取消反应器超时
     */
    public void cancelReactorTimeout() {
        if (timeoutTask != null) {
            // 取消任务
            timeoutTask.cancel(false);
            // 清空引用
            timeoutTask = null;
        }
    }

    /**
     * 刷新反应器超时
     */
    private void refreshReactorTimeout() {
        // 获取超时时间
        int timeOut = stats.getTimeout(state);
        if (timeOut > -1) {
            // 获取超时后状态
            final byte nextState = stats.getTimeoutState(state);

            timeoutTask = TimerManager.getInstance().schedule(() -> {
                // 清空任务引用
                timeoutTask = null;
                // 尝试强制击中
                tryForceHitReactor(nextState);
            }, timeOut);
        }
    }

    /**
     * 延迟击中反应器
     * @param c 客户端
     * @param delay 延迟时间
     */
    public void delayedHitReactor(final Client c, long delay) {
        // 延迟执行击中
        TimerManager.getInstance().schedule(() -> hitReactor(c), delay);
    }

    /**
     * 击中反应器
     * @param c 客户端
     */
    public void hitReactor(Client c) {
        // 默认参数调用
        hitReactor(false, 0, (short) 0, 0, c);
    }

    /**
     * 击中反应器(完整参数)
     * @param wHit 是否为武器击中
     * @param charPos 角色位置
     * @param stance 姿态
     * @param skillid 技能ID
     * @param c 客户端
     */
    public void hitReactor(boolean wHit, int charPos, short stance, int skillid, Client c) {
        try {
            if (!this.isActive()) {
                // 如果不活跃则直接返回
                return;
            }

            if (hitLock.tryLock()) {
                // 锁定反应器
                this.lockReactor();
                try {
                    // 取消超时
                    cancelReactorTimeout();
                    // 设置击中标志
                    attackHit = wHit;

                    Character player = c.getPlayer();
                    if (GameConfig.getServerBoolean("use_debug") && player.isGM()) {
                        // GM调试信息
                        player.dropMessage(5, "击中反应器 " + this.getId() + " 位置 " + charPos + " , 姿态 " + stance + " , 技能ID " + skillid + " , 状态 " + state + " 状态大小 " + stats.getStateSize(state));
                    }
                    // 调用击中脚本
                    ReactorScriptManager.getInstance().onHit(c, this);

                    int reactorType = stats.getType(state);
                    // 类型2=只能从右侧击中(沼泽植物), 00是左侧空中 02是左侧地面
                    if (reactorType < 999 && reactorType != -1) {
                        if (!(reactorType == 2 && (stance == 0 || stance == 2))) {
                            // 遍历状态
                            for (byte b = 0; b < stats.getStateSize(state); b++) {
                                List<Integer> activeSkills = stats.getActiveSkills(state, b);
                                if (activeSkills != null) {
                                    if (!activeSkills.contains(skillid)) {
                                        // 技能不匹配则跳过
                                        continue;
                                    }
                                }

                                // 设置下一状态
                                this.state = stats.getNextState(state, b);
                                byte nextState = stats.getNextState(state, b);
                                boolean isInEndState = nextState < this.state;
                                // 反应器结束状态
                                if (isInEndState) {
                                    // 反应器破坏
                                    if (reactorType < 100) {
                                        if (delay > 0) {
                                            // 延迟销毁
                                            map.destroyReactor(getObjectId());
                                        } else {
                                            // 正常触发
                                            map.broadcastMessage(PacketCreator.triggerReactor(this, stance));
                                        }
                                    } else {
                                        // 最终步骤物品触发
                                        map.broadcastMessage(PacketCreator.triggerReactor(this, stance));
                                    }

                                    // 执行脚本动作
                                    ReactorScriptManager.getInstance().act(c, this);
                                } else {
                                    // 反应器未完全破坏
                                    map.broadcastMessage(PacketCreator.triggerReactor(this, stance));
                                    // 当前状态=下一状态,循环反应器
                                    if (state == stats.getNextState(state, b)) {
                                        // 执行脚本动作
                                        ReactorScriptManager.getInstance().act(c, this);
                                    }

                                    // 刷新物品掉落反应器的可收集性
                                    setShouldCollect(true);
                                    // 刷新超时
                                    refreshReactorTimeout();
                                    if (stats.getType(state) == 100) {
                                        // 搜索物品反应器
                                        map.searchItemReactors(this);
                                    }
                                }
                                // 跳出循环
                                break;
                            }
                        }
                    } else {
                        // 状态自增
                        state++;
                        // 广播触发
                        map.broadcastMessage(PacketCreator.triggerReactor(this, stance));
                        if (this.getId() != 9980000 && this.getId() != 9980001) {
                            // 执行脚本动作(特殊ID除外)
                            ReactorScriptManager.getInstance().act(c, this);
                        }

                        // 设置可收集
                        setShouldCollect(true);
                        // 刷新超时
                        refreshReactorTimeout();
                        if (stats.getType(state) == 100) {
                            // 搜索物品反应器
                            map.searchItemReactors(this);
                        }
                    }
                } finally {
                    // 解锁反应器
                    this.unlockReactor();
                    // 解锁击中(感谢MiLin发现非封装解锁)
                    hitLock.unlock();
                }
            }
        } catch (Exception e) {
            // 打印异常
            e.printStackTrace();
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
                    // 设置死亡
                    this.setAlive(false);
                    // 取消超时
                    this.cancelReactorTimeout();

                    if (this.getDelay() > 0) {
                        // 延迟重生
                        this.delayedRespawn();
                    }
                } else {
                    // 返回是否不在延迟重生中
                    return !this.inDelayedRespawn();
                }
            } finally {
                // 解锁
                reactorLock.unlock();
            }
        }

        // 广播销毁消息
        map.broadcastMessage(PacketCreator.destroyReactor(this));
        // 返回失败
        return false;
    }

    /**
     * 重生反应器
     */
    private void respawn() {
        // 锁定
        this.lockReactor();
        try {
            // 重置动作
            this.resetReactorActions(0);
            // 设置存活
            this.setAlive(true);
        } finally {
            // 解锁
            this.unlockReactor();
        }

        // 广播生成消息
        map.broadcastMessage(this.makeSpawnData());
    }

    /**
     * 延迟重生
     */
    public void delayedRespawn() {
        Runnable r = () -> {
            // 清空任务引用
            delayedRespawnRun = null;
            // 执行重生
            respawn();
        };

        // 设置任务
        delayedRespawnRun = r;

        OverallService service = (OverallService) map.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
        // 注册全局动作
        service.registerOverallAction(map.getId(), r, this.getDelay());
    }

    /**
     * 强制延迟重生
     * @return 是否成功
     */
    public boolean forceDelayedRespawn() {
        Runnable r = delayedRespawnRun;

        if (r != null) {
            OverallService service = (OverallService) map.getChannelServer().getServiceAccess(ChannelServices.OVERALL);
            // 强制运行全局动作
            service.forceRunOverallAction(map.getId(), r);
            // 返回成功
            return true;
        } else {
            // 返回失败
            return false;
        }
    }

    /**
     * 检查是否在延迟重生中
     * @return 是否在延迟重生中
     */
    public boolean inDelayedRespawn() {
        // 返回任务是否存在
        return delayedRespawnRun != null;
    }

    /**
     * 获取区域矩形
     * @return 矩形区域
     */
    public Rectangle getArea() {
        // 计算并返回区域
        return new Rectangle(getPosition().x + stats.getTL().x, getPosition().y + stats.getTL().y, stats.getBR().x - stats.getTL().x, stats.getBR().y - stats.getTL().y);
    }

    /**
     * 获取名称
     * @return 名称
     */
    public String getName() {
        // 返回名称
        return name;
    }

    /**
     * 设置名称
     * @param name 名称
     */
    public void setName(String name) {
        // 设置名称
        this.name = name;
    }

    /**
     * 获取守卫生成点
     * @return 守卫生成点
     */
    public GuardianSpawnPoint getGuardian() {
        // 返回守卫
        return guardian;
    }

    /**
     * 设置守卫生成点
     * @param guardian 守卫生成点
     */
    public void setGuardian(GuardianSpawnPoint guardian) {
        // 设置守卫
        this.guardian = guardian;
    }

    /**
     * 设置面向方向
     * @param facingDirection 方向
     */
    public final void setFacingDirection(final byte facingDirection) {
        // 设置方向
        this.facingDirection = facingDirection;
    }

    /**
     * 获取面向方向
     * @return 方向
     */
    public final byte getFacingDirection() {
        // 返回方向
        return facingDirection;
    }
}