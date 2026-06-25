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

/**
 * @author: ThreeStep
 * @event: 野外BOSS - 阿司塔洛
 * @description: 记忆者任务专属野外BOSS刷新脚本，负责定时生成BOSS并广播通知
 */

// Java类导入
const Point = Java.type('java.awt.Point');
const LifeFactory = Java.type('org.gms.server.life.LifeFactory');
const PacketCreator = Java.type('org.gms.util.PacketCreator');
const LoggerFactory = Java.type('org.slf4j.LoggerFactory');

// 运行时变量
var log = null;
var channel = null;
var isinit = false;

// BOSS配置参数
/** BOSS所在地图ID - 记忆者任务区域 */
var MapID = 677000012;
/** BOSS怪物ID */
var BossID = 9400633;
/** BOSS名称 */
var BossName = "阿司塔洛";
/** BOSS刷新间隔（分钟） */
var BossTime = 180;
/** BOSS刷新位置坐标 */
var point = new Point(842, 0);
/** BOSS登场公告消息 */
var BossNotice = "玛巴斯现世之时，黄金狮鬃在硫磺风暴中燃烧成几何光轮，其足印所踏之处自动浮现精密齿轮构成的所罗门封印";

/** 刷新函数名称常量 */
const methodName = "start";

/**
 * 初始化函数
 * 获取频道ID和日志实例，启动BOSS刷新任务
 */
function init() {
    channel = em.getChannelServer().getId();
    log = LoggerFactory.getLogger(em.getName());
    scheduleNew();
}

/**
 * 安排新的BOSS刷新任务
 * 服务器启动时立即执行一次，之后定时检测并生成BOSS
 */
function scheduleNew() {
    setupTask = em.schedule(methodName, 0);
}

/**
 * 取消已安排的任务
 */
function cancelSchedule() {
    if (setupTask != null) {
        setupTask.cancel(true);
    }
}

/**
 * BOSS刷新主函数
 * 检查BOSS是否已存在，不存在则生成并广播公告
 */
function start() {
    var graysPrairie = em.getChannelServer().getMapFactory().getMap(MapID);
    var Timer = em.getBossTime(BossTime * 60 * 1000);

    if (graysPrairie.getMonsterById(BossID) != null) {
        em.schedule(methodName, Timer);
        return;
    }
    const BossObj = LifeFactory.getMonster(BossID);
    BossName = BossObj.getName() || BossName;
    try {
        graysPrairie.spawnMonsterOnGroundBelow(BossObj, point);
        if (isinit) {
            log.info(`[事件脚本-野外BOSS] ${em.getName()} 已在频道 ${channel} 的 ${graysPrairie.getMapName()}(${MapID}) ${point.x}, ${point.y}) 生成 ${BossName}(${BossID})，检测间隔：${Timer / 60 / 1000} 分钟`);
        } else {
            isinit = true;
        }
    } catch (e) {
        console.error(`[事件脚本-野外BOSS] ${em.getName()} 在频道 ${channel} 的 ${graysPrairie.getMapName()}(${MapID}) ${point.x}, ${point.y}) 生成 ${BossName}(${BossID}) 时出错`, e);
    }
    graysPrairie.broadcastMessage(PacketCreator.serverNotice(6, `[野外BOSS] ${BossName} ${BossNotice}`));
    em.schedule(methodName, Timer);
}

// ---------- 预留函数(空实现) ----------

function dispose() {}

function setup(eim, leaderid) {}

function monsterValue(eim, mobid) {return 0;}

function disbandParty(eim, player) {}

function playerDisconnected(eim, player) {}

function playerEntry(eim, player) {}

function monsterKilled(mob, eim) {}

function scheduledTimeout(eim) {}

function afterSetup(eim) {}

function changedLeader(eim, leader) {}

function playerExit(eim, player) {}

function leftParty(eim, player) {}

function clearPQ(eim) {}

function allMonstersDead(eim) {}

function playerUnregistered(eim, player) {}