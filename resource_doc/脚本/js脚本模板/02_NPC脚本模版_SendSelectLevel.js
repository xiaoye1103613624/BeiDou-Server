/* ============================================================
 * 脚本类型: NPC 对话脚本（sendSelectLevel 自动路由模式）
 * 模版说明: 适用于多层菜单的复杂 NPC，利用引擎自动路由分发
 * 适用场景: 多级菜单、功能面板、传送菜单、商城NPC
 *
 * 路由机制:
 *   1. cm.sendSelectLevel("LevelName", text)
 *      显示菜单，设定路由前缀
 *   2. cm.sendNextSelectLevel("NextLevel", text)
 *      链式跳转到下一级菜单
 *   3. 引擎自动调用 level<前缀>(selection) 处理玩家选择
 *
 * 示例流程:
 *   levelStart() 显示主菜单
 *   玩家选 #L3# → 引擎调用 levelStart(3)
 *   levelStart(3) 中调用 cm.sendNextSelectLevel("Shop")
 *   新菜单中玩家选 #L2# → 引擎调用 levelShop(2) → 处理业务
 *
 * 优势:
 *   - 不需要手动管理 status 变量
 *   - 每个功能独立成函数，逻辑清晰
 *   - 适合多层级、多功能的大型 NPC 脚本
 *   - 扩展新功能只需加新函数，不影响现有逻辑
 * ============================================================ */

/* ===== 入口函数 ===== */
function start() {
    /* 直接调用主菜单 */
    levelStart();
}

/**
 * 兜底函数，正常情况下不会被触发
 * （因为 sendSelectLevel 会自动路由到对应函数）
 */
function action(mode, type, selection) {
    cm.dispose();
}

// ============================================================
//  主菜单
// ============================================================

/** 显示主菜单 */
function levelStart() {
    let text = "欢迎来到北斗冒险岛！请选择你需要的服务：\r\n";
    text += "#L0#每日签到#l \t #L1#万能传送#l\r\n";
    text += "#L2#自由市场#l \t #L3#BOSS挑战#l";
    /* 无第二参数时路由前缀同函数名（即 "Start"） */
    /* 引擎会调用 levelStart(selection) */
    cm.sendSelectLevel(text);
}

/**
 * 处理主菜单的选择
 * @param {number} selection - 玩家选中的选项索引
 */
function levelStart(selection) {
    switch (selection) {
        case 0:
            /* 签到逻辑 */
            cm.sendOk("签到成功！获得 100 金币。");
            cm.dispose();
            break;
        case 1:
            /* 跳转到传送子菜单 */
            levelWarpMenu();
            break;
        case 2:
            /* 直接传送到自由市场 */
            cm.warp(910000000, 0);
            cm.dispose();
            break;
        case 3:
            /* 跳转到 BOSS 子菜单 */
            levelBossMenu();
            break;
        default:
            cm.dispose();
            break;
    }
}

// ============================================================
//  传送子菜单
// ============================================================

/** 显示传送菜单 */
function levelWarpMenu() {
    let text = "请选择你要前往的区域：\r\n";
    text += "#L0##b射手村#k      #r（免费）#k#l\r\n";
    text += "#L1##b魔法密林#k    #r（免费）#k#l\r\n";
    text += "#L2##b废弃都市#k    #r（免费）#k#l\r\n";
    text += "#L3#返回主菜单#l";
    /* 路由前缀="Warp"，引擎调用 levelWarp(selection) */
    cm.sendNextSelectLevel("Warp", text);
}

/**
 * 处理传送选择
 * @param {number} selection
 */
function levelWarp(selection) {
    /* 目标地图数组（与上面 #L0#、#L1#... 一一对应） */
    let warps = [100000000, 101000000, 103000000];

    if (selection < warps.length) {
        /* 保存当前位置，便于后续返回 */
        cm.getPlayer().saveLocationOnWarp();
        cm.warp(warps[selection], 0);
    }
    /* 无效选项 → 回到主菜单 */
    levelStart();
}

// ============================================================
//  BOSS 子菜单
// ============================================================

/** 显示BOSS菜单 */
function levelBossMenu() {
    let text = "请选择你要挑战的 BOSS：\r\n";
    text += "#L0#扎昆         #r（消耗10万金币）#k#l\r\n";
    text += "#L1#黑龙王        #r（消耗50万金币）#k#l\r\n";
    text += "#L2#品克缤        #r（消耗100万金币）#k#l\r\n";
    text += "#L3#返回主菜单#l";
    /* 路由前缀="Boss"，引擎调用 levelBoss(selection) */
    cm.sendNextSelectLevel("Boss", text);
}

/**
 * 处理BOSS选择
 * @param {number} selection
 */
function levelBoss(selection) {
    /* BOSS 数据数组 [地图ID, 费用] */
    let bosses = [
        [280030000, 100000],
        [240060200, 500000],
        [270050100, 1000000]
    ];

    if (selection < bosses.length) {
        if (cm.getMeso() < bosses[selection][1]) {
            cm.sendOk("你的金币不够！需要 " + bosses[selection][1] + " 金币。");
            /* 金币不够，回到BOSS菜单 */
            levelBossMenu();
            return;
        }
        cm.gainMeso(-bosses[selection][1]);
        cm.getPlayer().saveLocationOnWarp();
        cm.warp(bosses[selection][0], 0);
    }
    /* 无效选项 → 回到主菜单 */
    levelStart();
}

/* ============================================================
 * 【sendSelectLevel 常用方法】
 *
 * ---- 显示菜单 ----
 * cm.sendSelectLevel(text)
 *     路由前缀=""（空字符串），选择后调用 level<index>(selection)
 *     例: 玩家选 #L3# → 调用 level3(3)
 *
 * cm.sendSelectLevel("Prefix", text)
 *     路由前缀="Prefix"，选择后调用 levelPrefix(selection)
 *     例: 玩家选 #L2# → 调用 levelPrefix(2)
 *
 * ---- 链式跳转 ----
 * cm.sendNextSelectLevel("NextPrefix", text)
 *     显示新菜单，选择后调用 levelNextPrefix(selection)
 *     等价于先 dispose 再 sendSelectLevel，但保持在同一对话流程中
 *
 * ---- 注意 ----
 * 1. sendSelectLevel 内的 text 中应使用 #L编号#...#l 定义选项
 * 2. 每个 levelXxx(selection) 函数最后必须 dispose() 或跳转到新菜单
 * 3. 如果同一函数既做菜单显示又做选择处理，引擎通过是否传入 selection 参数区分
 * 4. 路由函数名必须一致：level + 前缀（首字母大写）
 * ============================================================ */
