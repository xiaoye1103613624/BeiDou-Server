/* ============================================================
 * 脚本类型: 反应堆脚本
 * 模版说明: 玩家攻击/触碰反应堆（如采集物、宝箱、机关）时触发
 * 文件命名: 反应堆ID.js（如 1002008.js）
 * 存放位置: scripts-zh-CN/reactor/
 *
 * 调用链路:
 *   玩家攻击反应堆 → ReactorScriptManager → act()
 *
 * 全局变量:
 *   rm = ReactorScriptManager 实例
 *
 * 反应堆生命周期:
 *   WZ 中配置的触发条件（如被攻击N次）满足后，act() 被调用
 * ============================================================ */

function act() {

    /* ---- 方式一：掉落道具（最简单） ---- */
    rm.dropItems();

    /* ---- 方式二：随机掉落多个道具 ---- */
    /*
    // 掉落道具: dropItems(是否按WZ配置掉落)
    rm.dropItems(true);

    // 额外自定义掉落: spawnDropItem(itemId, quantity, x偏移, y偏移)
    // 随机 1~5 个蓝蜗牛壳
    var count = Math.floor(Math.random() * 5) + 1;
    rm.spawnDropItem(4000000, count, 0, 0);

    // 小概率掉落稀有道具
    if (Math.random() < 0.1) {
        rm.spawnDropItem(4000001, 1, 20, 20);
    }
    */

    /* ---- 方式三：带条件判断的掉落 ---- */
    /*
    var player = rm.getPlayer();

    if (player.getLevel() >= 30) {
        // 高级玩家获得更好的掉落
        rm.spawnDropItem(4000039, 3, 0, 0);
    }

    rm.dropItems();
    */

    /* ---- 方式四：触发事件（如宝箱打开后召唤怪物） ---- */
    /*
    var map = rm.getPlayer().getMap();

    // 在当前地图召唤怪物
    map.spawnMonsterOnGroundBelow(9300183, rm.getPositionX(), rm.getPositionY());

    // 给掉落
    rm.dropItems();
    */

    /* ---- 方式五：传送玩家 ---- */
    /*
    var player = rm.getPlayer();
    player.dropMessage(5, "你触发了一个神秘机关！");
    rm.warp(100000000, 0);
    */
}

/* ============================================================
 * 【反应堆脚本 rm 常用方法】
 *
 * ---- 掉落 ----
 * rm.dropItems()
 *     按 WZ 配置掉落道具
 * rm.spawnDropItem(itemId, quantity, xOffset, yOffset)
 *     在反应堆位置生成掉落道具
 *
 * ---- 玩家 ----
 * rm.getPlayer()
 *     获取触发反应堆的玩家
 * rm.getPlayer().getMap()
 *     获取当前地图对象
 *
 * ---- 位置 ----
 * rm.getPositionX()
 *     获取反应堆 X 坐标
 * rm.getPositionY()
 *     获取反应堆 Y 坐标
 *
 * ---- 地图操作 ----
 * rm.getPlayer().getMap().spawnMonsterOnGroundBelow(monsterId, x, y)
 *     在地面生成怪物
 * rm.warp(mapId, portalId)
 *     传送玩家
 * ============================================================ */
