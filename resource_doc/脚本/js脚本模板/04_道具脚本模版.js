/* ============================================================
 * 脚本类型: 道具脚本（消耗品双击触发）
 * 模版说明: 玩家双击背包中的道具时触发此脚本
 * 文件命名: 道具ID.js（如 2432000.js）或 功能名.js
 * 存放位置: scripts-zh-CN/item/
 *
 * 重要约束:
 *   - 只有道具ID在 243xxxx 范围内的道具才会触发脚本
 *   - 其他ID范围的道具不会执行任何 item 脚本
 *   - 参考: ItemInformationProvider.java:1596
 *
 * 全局变量:
 *   im = ItemScriptManager 实例（API 与 cm 基本一致）
 *
 * im.getDirectionInfo() 返回值:
 *   1 = Undefined（未定义）
 *   2 = Equipment（装备到装备栏）
 *   3 = Consume（消耗品使用）
 *   4 = Equip（装备到身上）
 *
 * im.getNpc() 返回值:
 *   道具WZ中 spec/npc 字段的值
 *   常用于：卡片道具→对应怪物ID、特殊道具→关联NPC
 * ============================================================ */

/* ===== 可配置区域 ===== */
var ITEM_ID = 2432000;
/* 当前道具ID，与文件名对应 */

// ============================================================
//  方式一：简单道具（一次性使用，无需条件判断）
// ============================================================
/*
function start() {
    im.gainItem(4000000, 10);
    im.gainExp(500);
    im.getPlayer().dropMessage(5, "你使用了神秘宝箱，获得了10个蓝蜗牛壳！");
    im.dispose();
}
*/

// ============================================================
//  方式二：条件道具（需要检查各种条件）
// ============================================================
function start() {
    var player = im.getPlayer();
    var mapId = im.getMapId();

    /* ---- 1. 检查等级限制 ---- */
    if (player.getLevel() < 10) {
        player.dropMessage(5, "你的等级不足10级，无法使用该道具。");
        im.dispose();
        return;
    }

    /* ---- 2. 检查地图限制 ---- */
    var forbiddenMaps = [100000000, 101000000, 102000000, 103000000, 104000000];
    for (var i = 0; i < forbiddenMaps.length; i++) {
        if (mapId == forbiddenMaps[i]) {
            player.dropMessage(5, "当前地图无法使用此道具，请前往野外地图。");
            im.dispose();
            return;
        }
    }

    /* ---- 3. 检查是否已拥有道具 ---- */
    if (!im.haveItem(ITEM_ID, 1)) {
        player.dropMessage(5, "你没有该道具！");
        im.dispose();
        return;
    }

    /* ---- 4. 扣除道具 ---- */
    im.gainItem(ITEM_ID, -1);

    /* ---- 5. 执行业务逻辑 ---- */
    var rng = Math.floor(Math.random() * 3);

    switch (rng) {
        case 0:
            im.gainItem(4000000, 10);
            player.dropMessage(5, "你获得了 10 个蓝蜗牛壳！");
            break;
        case 1:
            im.gainExp(1000);
            player.dropMessage(5, "你获得了 1000 经验！");
            break;
        case 2:
            im.gainMeso(5000);
            player.dropMessage(5, "你获得了 5000 金币！");
            break;
    }

    im.dispose();
}

// ============================================================
//  方式三：带确认弹窗的道具
// ============================================================
/*
var status;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        im.dispose();
        return;
    }
    if (mode == 0 && type > 0) {
        im.dispose();
        return;
    }
    if (mode == 1) status++;
    else status--;

    if (status == 0) {
        im.sendYesNo("确定要使用该道具吗？使用后道具将消失。");
    } else if (status == 1) {
        if (selection == 0) {
            im.gainItem(ITEM_ID, -1);
            im.gainExp(500);
            im.getPlayer().dropMessage(5, "使用成功！");
        }
        im.dispose();
    }
}
*/

/* ============================================================
 * 【道具脚本 im 常用方法】
 *
 * ---- 核心 ----
 * im.getMapId()
 *     获取当前所在地图ID
 * im.getNpc()
 *     获取道具 WZ spec/npc 值
 * im.getDirectionInfo()
 *     获取道具使用方向（1~4）
 * im.haveItem(itemId, qty)
 *     检查是否拥有道具
 * im.gainItem(itemId, qty)
 *     给予/扣除道具
 * im.gainExp(amount)
 *     给予经验
 * im.gainMeso(amount)
 *     给予/扣除金币
 * im.dispose()
 *     结束脚本
 *
 * ---- 玩家 ----
 * im.getPlayer()
 *     获取 Character 对象
 * im.getPlayer().getLevel()
 *     获取等级
 * im.getPlayer().dropMessage(type, text)
 *     发送消息（type=5 黄色系统消息）
 * im.getPlayer().getMapId()
 *     获取当前地图ID
 *
 * ---- 道具脚本专有 ----
 * im.summonSamsaraStone(npcId, duration, rate)
 *     召唤轮回石碑（加速刷怪，rate=0.3 表示刷新时间缩短到30%）
 * im.hasSamsaraStone()
 *     检查当前地图是否已有轮回石碑
 * im.teachSkill(skillId, level, masterLevel)
 *     教玩家技能
 *
 * ---- 对话框（如使用状态机模式）----
 * im.sendNext(text)
 *     普通文本 + 下一步
 * im.sendOk(text)
 *     普通文本 + 确定
 * im.sendYesNo(text)
 *     确认弹窗
 * im.sendSimple(text)
 *     选项菜单
 * ============================================================ */
