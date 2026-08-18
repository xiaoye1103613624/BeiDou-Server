/**
 * 妖精学院·解救2 副本入口 NPC (1500025)
 * 基于 Level 阶段式对话框架（北斗 GMS083 org.gms 服务端）
 * 前置任务：32125 已完成，32126 已开始且未完成
 * 实例等级参数：255
 */

const isRepeat = false;
const EventName = 'YJXYJjiejiu2';
const EventLevel = 1;
const LevelMin = 30, LevelMax = 255;
var entryMap = 101073110;
var exitMap = 101073100;
var recruitMap = exitMap;
var clearMap = entryMap;
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

    // ── 任务门控：32125 已完成 + 32126 已开始但未完成 ──
    if (!cm.isQuestCompleted(32125) || !cm.isQuestStarted(32126) || cm.isQuestCompleted(32126)) {
        cm.sendOkLevel('', '你现在无法进入副本。');
        cm.dispose();
        return;
    }

    cm.sendSelectLevel('', '敬礼！\r\n你好，#e#b#h ##k#n，我是#b#e#p' + cm.getNpc() + '##k#n，想做点什么呢？\r\n\r\n#L0##b解救妖精耶波尼和帕伊尼#l\r\n#L1#离开#l#k');
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
    cm.sendNextLevel('0_1', '请你救救妖精耶波尼和帕伊尼……\r\n你准备好了吗？');
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