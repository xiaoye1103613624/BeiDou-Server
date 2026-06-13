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
 2倍掉落活动脚本
 支持NPC驱动（属性保存原始倍率）和直接调度两种模式
 **/

var timer1;
var timer2;
var timer3;
var timer4;

function init() {}

function cancelSchedule() {
    if (timer1 != null) {
        timer1.cancel(true); // 取消定时器1
    }
    if (timer2 != null) {
        timer2.cancel(true); // 取消定时器2
    }
    if (timer3 != null) {
        timer3.cancel(true); // 取消定时器3
    }
    if (timer4 != null) {
        timer4.cancel(true); // 取消定时器4
    }
}

/**
 * 开始双倍掉落活动
 * 支持属性驱动（从NPC脚本调用时保存原始倍率）和直接调度两种模式
 */
function start() {
    const Server = Java.type('org.gms.net.server.Server');
    const PacketCreator = Java.type('org.gms.util.PacketCreator');
    var world = Server.getInstance().getWorld(em.getChannelServer().getWorld());
    var currentRate = world.getDropRate();   // 获取当前掉落倍率

    // 如果NPC脚本已预先保存了原始倍率，则使用保存值；否则自行记录（兼容直接调度模式）
    var savedRate = em.getProperty("originalDropRate");
    if (savedRate == null || savedRate.equals("")) {
        // 直接调度模式：保存当前倍率作为原始倍率（即加倍前的值）
        em.setProperty("originalDropRate", String(currentRate));
    }

    world.setDropRate(currentRate * 2); // 将掉落倍率调整为双倍
    world.broadcastPacket(PacketCreator.serverNotice(6, "【双倍掉落】惊喜时刻来临！GM团队已激活紧急掉落池，在接下来的两小时内获得的掉落率将翻倍！"));
}

/**
 * 结束双倍掉落活动
 * 恢复至原始掉落倍率并重置活动状态
 */
function stop() {
    const Server = Java.type('org.gms.net.server.Server');
    const PacketCreator = Java.type('org.gms.util.PacketCreator');
    var world = Server.getInstance().getWorld(em.getChannelServer().getWorld());

    // 恢复原始掉落倍率
    var savedRate = em.getProperty("originalDropRate");
    if (savedRate != null && !savedRate.equals("")) {
        world.setDropRate(Number(savedRate)); // 恢复为开启双倍前的原始倍率
        em.setProperty("originalDropRate", ""); // 清除保存的原始倍率
    } else {
        // 兼容模式：没有保存原始倍率时，恢复默认1倍掉落
        world.setDropRate(1);
    }

    // 重置活动状态，允许再次开启
    em.setProperty("state", "0");

    world.broadcastPacket(PacketCreator.serverNotice(6, "【双倍掉落】很遗憾，紧急掉落池能量已耗尽，掉落倍率已恢复正常。"));
}

// ---------- 预留函数(空实现) ----------

function dispose() {}
function setup(eim, leaderid) {}
function monsterValue(eim, mobid) { return 0; }
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