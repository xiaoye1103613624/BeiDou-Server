/* ============================================================
 * 脚本类型: 地图脚本
 * 模版说明: 玩家进入地图时触发
 * 文件命名: 地图ID.js（如 100000000.js）
 * 存放位置:
 *   scripts-zh-CN/map/onFirstUserEnter/  ← 第一个玩家进入时
 *   scripts-zh-CN/map/onUserEnter/       ← 每个玩家进入时
 *
 * 两种类型的区别:
 *   onFirstUserEnter:
 *     - 当玩家进入一个空地图时触发（该地图之前没有其他玩家）
 *     - 适合：初始化地图怪物、设置地图状态、触发开场动画
 *     - 函数签名: start(ms)
 *
 *   onUserEnter:
 *     - 每个玩家进入该地图时都会触发
 *     - 适合：检查玩家状态、触发个人事件、传送判断
 *     - 函数签名: start(ms)
 *
 * 全局变量:
 *   ms = MapScriptManager 实例
 * ============================================================ */

// ============================================================
//  方式一：onFirstUserEnter（地图首次有玩家进入）
// ============================================================
/*
function start(ms) {
    // 示例：检查是否是特定地图，初始化事件
    var eventName = "MyEvent";
    var em = ms.getClient().getChannelServer().getEventSM().getEventManager(eventName);

    if (em != null) {
        // 设置事件属性，控制地图行为
        em.setProperty("started", "true");
    }

    // 刷新地图怪物（可选）
    // ms.getPlayer().getMap().respawn();
}
*/

// ============================================================
//  方式二：onUserEnter（每个玩家进入时）
// ============================================================
/*
function start(ms) {
    var player = ms.getPlayer();

    // 示例：检查玩家是否有特定任务，自动触发对话
    // if (player.getQuestStatus(20000) == 1) {
    //     ms.getClient().getQM().startQuest(20000);
    // }

    // 示例：等级不足时踢出地图
    if (player.getLevel() < 30) {
        player.dropMessage(5, "等级不足30级，无法进入该地图。");
        player.warp(100000000, 0);
        return;
    }
}
*/

// ============================================================
//  方式三：综合示例（道场地图）
// ============================================================
/*
function start(ms) {
    var player = ms.getPlayer();

    // 重置玩家的脚本入口标记
    player.resetEnteredScript();

    // 根据地图ID计算当前阶段
    var stage = Math.floor(ms.getMapId() / 100) % 100;

    if (stage % 6 == 0) {
        // BOSS关卡：取消计时，直接进入
        ms.getClient().getChannelServer().dismissDojoSchedule(
            ms.getMapId(), ms.getParty()
        );
    } else {
        // 普通关卡：设置进度，生成怪物
        ms.getClient().getChannelServer().setDojoProgress(ms.getMapId());

        var realstage = stage - ((stage / 6) | 0);
        var mob = ms.getMonsterLifeFactory(9300183 + realstage);

        if (mob != null) {
            mob.setBoss(false);
            player.getMap().spawnDojoMonster(mob);
        }
    }
}
*/

/* ============================================================
 * 【地图脚本 ms 常用方法】
 *
 * ---- 地图 ----
 * ms.getMapId()
 *     获取当前地图ID
 * ms.getPlayer().getMap()
 *     获取当前 Map 对象
 * ms.getPlayer().getMap().respawn()
 *     刷新地图怪物
 *
 * ---- 玩家 ----
 * ms.getPlayer()
 *     获取 Character 对象
 * ms.getClient()
 *     获取 Client 对象
 * ms.getParty()
 *     获取当前队伍
 *
 * ---- 事件 ----
 * ms.getClient().getChannelServer().getEventSM().getEventManager(name)
 *     获取事件管理器
 *
 * ---- 怪物 ----
 * ms.getMonsterLifeFactory(monsterId)
 *     创建指定ID的怪物实例
 *
 * ---- 其他 ----
 * ms.getPlayer().resetEnteredScript()
 *     重置脚本入口标记
 * ms.getClient().getChannelServer().dismissDojoSchedule(mapId, party)
 *     取消道场计时
 * ============================================================ */
