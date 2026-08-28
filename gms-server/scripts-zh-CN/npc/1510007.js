/**
 * 列娜海峡 Boss 副本入口 NPC (1510007)
 * 基于 Level 阶段式对话框架（北斗 GMS083 org.gms 服务端）
 * 单人副本，无需组队
 * 实例等级参数：255
 *
 * API 映射说明：
 *   getNumberProperty() → parseInt(getProperty()) ✅已确认
 *   cm.sendOkS()        → cm.sendOkLevel('', msg) ✅已确认
 */

var isRepeat = true;
var EventName = 'LNHXBOSS';
var EventLevel = 255;
var PQLog = '副本_列娜海峡_Boss';

var entryMap = 141050200;    // 事件启动时玩家进入的初始地图
var exitMap = 141050200;     // 玩家未能完成事件时被传送至此地图
var recruitMap = entryMap;   // 玩家必须在此地图上才能开始此事件
var clearMap = entryMap;     // 玩家成功完成事件后被传送至此地图

function start() {
    // 不在入口地图时，先传送过去并结束对话
    if (cm.getMapId() != entryMap) {
        cm.warp(entryMap, 0);
        cm.dispose();
        return;
    }

    var em = cm.getEventManager(EventName);
    if (em == null) {
        cm.sendOkLevel('', '配置文件不存在，请联系管理员。');
        cm.dispose();
        return;
    }

    cm.sendSelectLevel('', '#r盖奥勒克#k 就在里面，你准备好了吗？\r\n\r\n#L0##b讨伐#l\r\n#L1#离开#l#k');
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
    cm.sendNextLevel('0_1', '那就交给你了，祝你武运昌隆！');
}

function level0_1() {
    var em = cm.getEventManager(EventName);
    if (em == null) {
        cm.sendOkLevel('', '配置文件不存在，请联系管理员。');
        cm.dispose();
        return;
    }

    // 替代: em.getNumberProperty("state") → parseInt(em.getProperty("state"))
    var state = parseInt(em.getProperty("state") || "0");

    if (state == 0) {
        // 单人副本：直接传入玩家对象，无需组队
        em.startInstance(cm.getPlayer());
        try {
            em.setProperty("PQLog", PQLog);
        } catch (e) {
            // PQLog 跟踪机制可能不同，忽略
        }
    } else {
        cm.sendOkLevel('', '好像已经有人在进行尝试了，换其他频道尝试吧。');
    }
    cm.dispose();
}

function level1() {
    cm.dispose();
}
