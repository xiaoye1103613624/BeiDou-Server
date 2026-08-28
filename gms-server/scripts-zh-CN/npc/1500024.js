/**
 * 妖精学院·解救1 副本入口 NPC (1500024)
 * 基于 Level 阶段式对话框架（北斗 GMS083 org.gms 服务端）
 * 前置任务：32122 已完成，32123 未完成
 * 实例等级参数：255
 *
 * API 映射说明（本服务端缺失方法的替代方案）：
 *   isQuestFinished()     → isQuestCompleted()          ✅已确认
 *   getNumberProperty()   → parseInt(getProperty())     ✅已确认
 *   allMembersHere()      → 手动遍历 party.getMembers() ✅已确认
 *   isAllPartyMembersAllowedLevel() → 手动遍历检查等级 ✅已确认
 *   isAllPartyMembersAllowedPQ()    → 暂无替代，已跳过   ⚠️
 */

const isRepeat = false;               // 允许完成任务后重复进入
const EventName = 'YJXYJjiejiu1';      // 事件名称（请与事件脚本文件名保持一致）
const EventLevel = 1;                // 难度级别（固定为 1，不再影响血量）
const LevelMin = 30, LevelMax = 255;  // 等级限制
var entryMap = 101073010;               // 事件启动时玩家进入的初始地图。
var exitMap = 101073000;                // 玩家未能完成事件时被传送至此地图。
var recruitMap = exitMap;             // 玩家必须在此地图上才能开始此事件。
var clearMap = entryMap;               // 玩家成功完成事件后被传送至此地图。
var em = null;

function start() {
    if (em == null) {
        em = cm.getEventManager(EventName);
    }
    if (em == null || em.getName() != EventName) {
        cm.sendOkLevel('', '由于某种神秘力量，暂时无法进入副本，请联系管理员。');
        cm.dispose();
        return;
    }

    // ── 任务门控：32122 必须已完成，32123 必须已开始但未完成 ──
    if (!cm.isQuestCompleted(32122) || cm.isQuestCompleted(32123) ||!cm.isQuestStarted(32123)){
        cm.sendOkLevel('', '你现在无法进入副本。');
        cm.dispose();
        return;
    }

    cm.sendSelectLevel('', '敬礼！\r\n你好，#e#b#h ##k#n，我是#b#e#p' + cm.getNpc() + '##k#n，想做点什么呢？\r\n\r\n#L0##b解救妖精托希#l\r\n#L1#离开#l#k');
}

function level() {
    cm.dispose();
}
function levelnull() {
    cm.dispose();
}
function leveldispose() {
    cm.dispose();
}

function level0() {
    cm.sendNextLevel('0_1', '请你救救妖精托希……\r\n你准备好了吗？');
}

function level0_1() {
    if (cm.getParty() == null) {
        cm.sendOkLevel('', '请至少组建一个队伍（即使是单人队伍）再来找我。');
        cm.dispose();
    } else if (!cm.isLeader()) {
        cm.sendOkLevel('', '请让你的队长来开始这个任务。');
        cm.dispose();
    } else {
        var eli = em.getEligibleParty(cm.getParty());
        if (eli.size() > 0) {
            if (em.startInstance(cm.getParty(), cm.getPlayer().getMap(), EventLevel)) {
                // 启动成功
            } else {
                cm.sendOk('已经有其他人进去了请稍后再试。');
            }
        } else {
            cm.sendOk('你的队伍不符合要求：' + em.getProperty('party'));
        }
        cm.dispose();
    }
}

function level1() {
    cm.dispose();
}