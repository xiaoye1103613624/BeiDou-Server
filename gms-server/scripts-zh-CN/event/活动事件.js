// 核心配置：与主脚本完全对齐
var EVENT_NAME = "活动事件";       // 对应主脚本eventName
var FIELD_MAP = 109020001;        // 对应主脚本fubendt
var RETURN_MAP = 910000000;       // 对应主脚本超时返回地图
var TIME_LIMIT = 1800000;         // 30分钟（1800*1000），对应主脚本限时
var BOSS_MOB_ID = 8880803;        // 对应主脚本刷怪ID

// 初始化事件状态
function init() {
    em.setProperty("state", "0");   // 0=未开启 1=进行中
    em.setProperty("leader", "true");
}

// 事件启动逻辑（适配主脚本startInstance调用）
function setup(eim, leaderid) {
    em.setProperty("state", "1");
    em.setProperty("leader", "true");
    // 创建事件实例，名称与主脚本一致
    var eim = em.newInstance(EVENT_NAME);
    // 初始化副本地图并重置
    eim.setInstanceMap(FIELD_MAP).resetFully();
    // 设置30分钟限时，与主脚本一致
    eim.startEventTimer(TIME_LIMIT);
    return eim;
}

// 玩家进入副本逻辑
function playerEntry(eim, player) {
    // 直接传送至活动副本地图
    var map = eim.getMapFactory().getMap(FIELD_MAP);
    player.changeMap(map, map.getPortal(0));
    // 标记玩家为副本参与者
    eim.setProperty("isSquadPlayerID_" + player.getId(), "1");
}

// 禁止副本内原地复活
function playerRevive(eim, player) {
    return false;
}

// 玩家切换地图监听（防止离开副本）
function changedMap(eim, player, mapid) {
    // 仅允许停留在活动副本地图
    if (mapid !== FIELD_MAP) {
        eim.unregisterPlayer(player);
        // 若副本无玩家，重置事件状态
        if (eim.disposeIfPlayerBelow(0, 0)) {
            em.setProperty("leader", "true");
            em.setProperty("state", "0");
        }
    }
}

// 玩家断线处理
function playerDisconnected(eim, player) {
    return 0;
}

// 事件超时处理（30分钟到）
function scheduledTimeout(eim) {
    end(eim);
}

// 怪物击杀回调（触发公共奖项）
function monsterValue(eim, mobId) {
    // 击杀目标BOSS时触发公共奖项
    if (mobId === BOSS_MOB_ID) {
        publicAwards(eim); // 调用公共奖项逻辑
    }
    return 1;
}

// ========== 新增：公共奖项核心逻辑 ==========
function publicAwards(eim) {
    // 读取配置表中的公共奖项配置
    awards = eval(em.getProperty("公共奖项"));
    // 获取活动副本地图
    var map = em.getMapFactory().getMap(FIELD_MAP);
    // 获取地图内所有玩家（线程安全）
    var list = map.getCharactersThreadsafe();
    // 有玩家时才触发奖励
    if (map.getCharactersSize() >= 1 && list != null) {
        for (var i = 0; i < list.length; i++) {
            var chr = list[i];
            // 打开远征队奖励NPC
            openNpc(chr, 9900004, "远征队奖励");
        }
    }
}

// 打开NPC脚本辅助函数
function openNpc(chr, id, script) {
    Packages.scripting.NPCScriptManager.getInstance().dispose(chr.getClient());
    Packages.scripting.NPCScriptManager.getInstance().start(chr.getClient(), id, script);
}
// ==========================================

// 玩家主动退出副本
function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    // 无剩余玩家时重置事件状态
    if (eim.disposeIfPlayerBelow(0, 0)) {
        em.setProperty("leader", "true");
        em.setProperty("state", "0");
    }
}

// 事件结束逻辑（超时/全部退出）
function end(eim) {
    // 传送剩余玩家返回主城
    eim.disposeIfPlayerBelow(100, RETURN_MAP);
    // 重置事件状态
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
}

// 副本通关（所有怪物击杀）
function clearPQ(eim) {
    end(eim);
}

// 所有怪物死亡回调
function allMonstersDead(eim) {
    var currentState = em.getProperty("state");
    if (currentState === "1") {
        em.setProperty("state", "2");
    } else if (currentState === "2") {
        em.setProperty("state", "3");
    }
}

// 玩家死亡处理
function playerDead(eim, player) {
    eim.setProperty("isSquadPlayerID_" + player.getId(), "1");
}

// 保留事件框架必需的空函数（防止报错）
function leftParty(eim, player) {}
function disbandParty(eim) {}
function cancelSchedule() {}
function monsterDamaged(eim, chr, mobId, damage) {}