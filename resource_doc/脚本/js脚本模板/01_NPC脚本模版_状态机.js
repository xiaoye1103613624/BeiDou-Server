/* ============================================================
 * 脚本类型: NPC 对话脚本（状态机模式）
 * 模版说明: 适用于线性/简单分支的 NPC 对话，通过 status 变量控制流程
 * 适用场景: 简单对话、线性任务对话、确认弹窗
 *
 * 调用链路:
 *   玩家点击NPC → NPCScriptManager.start() → start()
 *   → 玩家点击对话框 → action(mode, type, selection)
 *
 * mode 参数说明:
 *   -1 = 玩家点了"结束聊天"
 *    0 = 玩家点了"上一步"（或关闭了对话框）
 *    1 = 玩家点了"下一步"（或选择了选项）
 *
 * type 参数说明:
 *    0 = 普通"下一步"点击
 *    1 = 玩家在 sendSimple/sendYesNo 等选项中做了选择
 *
 * selection 参数说明:
 *    当 type=1 时，selection 为玩家选中的选项索引（0-based）
 *    对应 #L0#、#L1# ... #Ln 中的编号
 * ============================================================ */

/* ===== 状态计数器，start() 中重置为 -1 ===== */
var status;

/* ===== 入口函数 ===== */
function start() {
    status = -1;
    /* 模拟一次"下一步"点击，进入 status=0 */
    action(1, 0, 0);
}

/* ===== 核心处理函数 ===== */
function action(mode, type, selection) {
    /* ---------- 处理退出 ---------- */
    if (mode == -1) {
        /* 玩家点"结束聊天"，释放资源 */
        cm.dispose();
        return;
    }
    if (mode == 0 && type > 0) {
        /* 玩家在选项弹窗中点了"上一步" */
        cm.dispose();
        return;
    }

    /* ---------- 状态步进 ---------- */
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    /* ---------- 对话分支 ---------- */
    if (status == 0) {
        /* 示例：普通文本 + 下一步按钮 */
        cm.sendNext("你好，冒险家！欢迎来到#b射手村#k。");

    } else if (status == 1) {
        /* 示例：带选项的菜单 */
        cm.sendSimple("我能为你做些什么呢？\r\n" +
            "#L0##b我想了解这个城镇#k#l\r\n" +
            "#L1##b我想买东西#k#l\r\n" +
            "#L2##b我想去别的地方#k#l");

    } else if (status == 2) {
        /* 处理 status==1 时的选择 */
        if (selection == 0) {
            cm.sendOk("射手村是射手们的聚集地，这里的人们都擅长弓箭。");
            cm.dispose();
        } else if (selection == 1) {
            /* 打开商店（shopId 需在 WZ/DB 中存在） */
            cm.openShopNPC(11000);
            cm.dispose();
        } else if (selection == 2) {
            cm.sendYesNo("你想传送到#b魔法密林#k吗？");
        }

    } else if (status == 3) {
        /* 处理确认弹窗结果 */
        if (selection == 0) {
            /* 传送到魔法密林，0号传送门 */
            cm.warp(101000000, 0);
        }
        cm.dispose();
    }
}

/* ===== 辅助函数 ===== */

/**
 * 从数组生成选项菜单字符串
 * @param {Array} array - 选项文本数组
 * @returns {string} 格式化的菜单文本
 */
function generateSelectionMenu(array) {
    var menu = "";
    for (var i = 0; i < array.length; i++) {
        menu += "#L" + i + "#" + array[i] + "#l\r\n";
    }
    return menu;
}

/* ============================================================
 * 【常用 cm 方法速查】
 *
 * ---- 对话框 ----
 * cm.sendNext("文本")
 *     普通文本 + 【下一步】
 * cm.sendPrev("文本")
 *     普通文本 + 【上一步】
 * cm.sendNextPrev("文本")
 *     普通文本 + 【下一步】+【上一步】
 * cm.sendOk("文本")
 *     普通文本 + 【确定】(点击后 dispose)
 * cm.sendYesNo("文本")
 *     确认弹窗 + 【是】/【否】
 * cm.sendSimple("文本#L0#选项#l")
 *     多选项菜单，用 #L编号#...#l 定义选项
 * cm.sendAcceptDecline("文本")
 *     接受/拒绝 弹窗
 * cm.sendGetNumber("文本", 默认值, 最小值, 最大值)
 *     数值输入框
 * cm.sendGetText("文本")
 *     文本输入框
 * cm.dispose()
 *     结束对话，释放资源
 *
 * ---- 传送 ----
 * cm.warp(mapId, portalId)
 *     传送到指定地图的指定传送门
 * cm.warp(mapId)
 *     传送到指定地图的 0 号传送门
 * cm.warpParty(mapId)
 *     全队传送到指定地图
 *
 * ---- 物品/货币 ----
 * cm.gainItem(itemId, qty)
 *     给予/扣除道具（正数给予，负数扣除）
 * cm.gainMeso(amount)
 *     给予/扣除金币（正数给予，负数扣除）
 * cm.haveItem(itemId, qty)
 *     检查是否拥有足够道具
 * cm.getMeso()
 *     获取当前金币数量
 * cm.openShopNPC(shopId)
 *     打开 NPC 商店
 *
 * ---- 角色属性 ----
 * cm.getPlayer()
 *     获取当前 Character 对象
 * cm.getPlayer().getLevel()
 *     获取等级
 * cm.getPlayer().getJob()
 *     获取职业
 * cm.getPlayer().gainAp(n, silent)
 *     获得属性点
 * cm.getPlayer().gainMeso(n)
 *     获得金币
 * cm.getPlayer().getMapId()
 *     获取当前地图ID
 * cm.getPlayer().getName()
 *     获取角色名
 *
 * ---- 数据持久化 ----
 * cm.getCharacterExtendValue(key)
 *     读取持久化 JSON 数据
 * cm.saveOrUpdateCharacterExtendValue(key, value)
 *     保存持久化 JSON 数据
 *
 * ---- 对话文本增强 ----
 * #b文本#b          蓝色文字
 * #r文本#r          红色文字
 * #k文本#k          黑色文字
 * #g文本#g          绿色文字
 * #i物品ID#         显示道具图标（如 #i4000000#）
 * #m地图ID#         显示地图名称（如 #m100000000#）
 * #pNPC_ID#         显示NPC名称（如 #p1002000#）
 * #t物品ID#         显示道具名称（如 #t4000000#）
 * #L编号#选项文本#l 定义可选项（配合 sendSimple 使用）
 * \r\n              换行
 * \t                制表符（对齐用）
 * ============================================================ */
