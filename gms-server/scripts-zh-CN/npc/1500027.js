/**
 * 妖精学院·Boss 副本入口 NPC (1500027)
 * 基于 Level 阶段式对话框架（北斗 GMS083 org.gms 服务端）
 * 单人副本，无组队检查
 * 门控：32102已完成且32127未完成时拒绝入场
 * 实例等级参数：255
 */

const isRepeat = false;
const EventName = 'YJXYBoss';
const EventLevel = 1;
const LevelMin = 30, LevelMax = 255;
var entryMap = 101073210;
var exitMap = 101073200;
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

    // ── 任务门控：32102已完成 且 32127未完成 → 拒绝入场 ──
    if (cm.isQuestCompleted(32102) && !cm.isQuestCompleted(32127)) {
        cm.sendOkLevel('', '现在不是乱逛的时候。');
        cm.dispose();
        return;
    }

    cm.sendSelectLevel('', '敬礼！\r\n你好，#e#b#h ##k#n，我是#b#e#p' + cm.getNpc() + '##k#n，想做点什么呢？\r\n\r\n#L0##b挑战野外演出舞台#l\r\n#L1#离开#l#k');
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
    cm.sendNextLevel('0_1', '你准备好挑战野外演出舞台了吗？\r\n#b（只能单人入场 / 等级：30以上）');
}

function level0_1() {
    if (em.getIntProperty("state") != 0) {
        cm.sendOkLevel('', '好像已经有人在进行了，换其他频道尝试吧。');
    } else {
        if (em.startInstance(cm.getPlayer())) {
            em.setProperty("PQLog", EventName);
        } else {
            cm.sendOkLevel('', '副本启动失败，请稍后再试。');
        }
    }
    cm.dispose();
}

function level1() {
    cm.dispose();
}