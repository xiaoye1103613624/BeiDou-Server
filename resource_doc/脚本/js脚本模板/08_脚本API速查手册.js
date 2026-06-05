/* ============================================================
 * 脚本类型: API 速查手册（仅作参考，不可直接执行）
 * 文件说明: 汇总冒险岛 JS 脚本中所有常用 API 及用法示例
 * 适用引擎: GraalVM JS (Nashorn 兼容模式)
 *
 * 脚本全局变量对照:
 *   NPC / 自定义脚本:  cm  = NPCConversationManager (AbstractPlayerInteraction)
 *   任务脚本:          qm  = QuestActionManager
 *   道具脚本:          im  = ItemScriptManager (同 AbstractPlayerInteraction)
 *   传送门脚本:        pi  = PortalPlayerInteraction
 *   地图脚本:          ms  = MapScriptManager
 *   反应堆脚本:        rm  = ReactorScriptManager
 *   事件脚本:          eim = EventInstanceManager
 * ============================================================ */

/* ============================================================
 *  第一章：对话框方法（cm/qm/im 通用）
 *
 *  这些方法控制 NPC 对话窗口的展示和用户交互
 *  调用后脚本暂停，等待玩家点击后继续执行 action()
 * ============================================================ */

// ---- 基础弹窗 ----

/**
 * sendNext(text)
 * 显示文本，底部只有【下一步】按钮
 * 玩家点击后 mode=1，继续执行
 */
cm.sendNext("欢迎来到冒险岛，冒险家！\r\n这里有很多有趣的冒险等着你。");

/**
 * sendPrev(text)
 * 显示文本，底部只有【上一步】按钮
 * 玩家点击后 mode=0 或 mode=-1
 */
cm.sendPrev("这是上一页的内容。");

/**
 * sendNextPrev(text)
 * 显示文本，底部同时有【下一步】和【上一步】按钮
 */
cm.sendNextPrev("你可以点击下一步继续，或点上一步返回。");

/**
 * sendOk(text)
 * 显示文本，底部只有【确定】按钮
 * 点击后自动调用 dispose() 结束对话
 */
cm.sendOk("操作完成！");

// ---- 交互弹窗 ----

/**
 * sendYesNo(text)
 * 确认弹窗，底部有【是】和【否】按钮
 * 选"是" → mode=1, type=1, selection=0
 * 选"否" → mode=1, type=1, selection=1
 */
cm.sendYesNo("确定要进行此操作吗？");

/**
 * sendAcceptDecline(text)
 * 接受/拒绝弹窗
 * 选"接受" → mode=1, type=1, selection=0
 * 选"拒绝" → mode=1, type=1, selection=1
 */
cm.sendAcceptDecline("你愿意接受这个任务吗？");

/**
 * sendSimple(text)
 * 多选项菜单，使用 #L编号#选项文本#l 定义可选项
 * 玩家选择后 → mode=1, type=1, selection=选中编号
 */
cm.sendSimple(
    "请选择一个选项：\r\n" +
    "#L0##b选项A：查看信息#k#l\r\n" +
    "#L1##b选项B：购买道具#k#l\r\n" +
    "#L2##b选项C：我要离开#k#l"
);

/**
 * sendGetNumber(text, defaultVal, min, max)
 * 数值输入框，让玩家输入一个数字
 * 返回的 selection 即为玩家输入的值
 */
cm.sendGetNumber("请输入你要购买的数量：", 1, 1, 100);

/**
 * sendGetText(text)
 * 文本输入框，让玩家输入一段文字
 * 返回的 selection 为玩家输入的文本（需通过 cm.getText() 获取）
 */
cm.sendGetText("请输入你的留言：");

/* ============================================================
 *  第二章：sendSelectLevel 方法（cm 专用，自动路由）
 * ============================================================ */

/**
 * cm.sendSelectLevel(text)
 * 显示选项菜单，无自定义路由前缀
 * 选择 #L3# 后引擎自动调用 level3(selection)
 */
cm.sendSelectLevel("请选择：#L0#A#l #L1#B#l");

/**
 * cm.sendSelectLevel("Prefix", text)
 * 显示选项菜单，指定路由前缀
 * 选择 #L2# 后引擎自动调用 levelPrefix(selection)
 */
cm.sendSelectLevel("Shop", "欢迎！#L0#买#l #L1#卖#l");

/**
 * cm.sendNextSelectLevel("Next", text)
 * 链式跳转到下一级菜单
 * 新菜单中选 #L1# 后引擎调用 levelNext(selection)
 */
cm.sendNextSelectLevel("Confirm", "确认购买？#L0#是#l #L1#否#l");

/* ============================================================
 *  第三章：传送相关方法
 * ============================================================ */

/**
 * cm.warp(mapId, portalId)
 * 传送玩家到目标地图的指定传送门
 */
cm.warp(100000000, 0);

/**
 * cm.warp(mapId)
 * 传送玩家到目标地图的 0 号传送门
 */
cm.warp(100000000);

/**
 * cm.warpParty(mapId)
 * 将整队玩家传送到目标地图
 */
cm.warpParty(280030000);

/**
 * pi.warp(mapId, portalId)
 * 传送门脚本中的传送
 */
pi.warp(pi.getPortal().getTargetMapId(), pi.getPortal().getTarget());

/**
 * 保存当前位置（用于后续双向传送）
 */
cm.getPlayer().saveLocationOnWarp();
/* 保存后可通过 warp 回到原位置 */

/* ============================================================
 *  第四章：道具/货币操作
 * ============================================================ */

/**
 * cm.gainItem(itemId, quantity)
 * 正数 = 给予道具，负数 = 扣除道具
 */
cm.gainItem(4000000, 10);
/* 给予 10 个蓝蜗牛壳 */
cm.gainItem(4000000, -5);
/* 扣除 5 个蓝蜗牛壳 */

/**
 * cm.gainMeso(amount)
 * 正数 = 给予金币，负数 = 扣除金币
 */
cm.gainMeso(10000);
/* 给予 10000 金币 */
cm.gainMeso(-5000);
/* 扣除 5000 金币 */

/**
 * cm.gainExp(amount)
 * 给予经验值
 */
cm.gainExp(5000);

/**
 * cm.haveItem(itemId, quantity)
 * 检查背包中是否拥有足够数量的道具
 * 返回 true/false
 */
if (cm.haveItem(4000000, 5)) {
    cm.sendOk("你有足够的蓝蜗牛壳！");
}

/**
 * cm.getMeso()
 * 获取当前金币数量
 */
var myMeso = cm.getMeso();

/**
 * cm.openShopNPC(shopId)
 * 打开 NPC 商店界面
 */
cm.openShopNPC(11000);

/**
 * cm.openNpc(npcId, scriptName)
 * 委托到另一个 NPC 脚本
 */
cm.openNpc(9900001, "Example1");

/* ============================================================
 *  第五章：角色属性操作
 * ============================================================ */

var player = cm.getPlayer();
/* 获取 Character 对象 */

var level = player.getLevel();
/* 获取等级 */

var job = player.getJob();
/* 获取职业枚举值 */

var name = player.getName();
/* 获取角色名 */

var mapId = player.getMapId();
/* 获取当前地图ID */

var hp = player.getHp();
/* 获取当前HP */

var maxHp = player.getMaxHp();
/* 获取最大HP */

var mp = player.getMp();
/* 获取当前MP */

player.gainMeso(1000);
/* 角色加金币 */

player.gainAp(5, false);
/* 增加5点属性点，false=显示动画 */

player.gainSp(3);
/* 增加3点技能点 */

player.addHP(-100);
/* 扣除HP（参数为变化值，负数=扣血） */

player.dropMessage(5, "这是一条黄色系统消息");
/* 发送消息: type=0(透明) 5(黄色) 6(红色) */

player.setLevel(30);
/* 直接设置等级（一般不用） */

player.changeJob(Job.WARRIOR);
/* 转职，如 Job.WARRIOR/MAGICIAN/BOWMAN/THIEF/PIRATE */

/* ============================================================
 *  第六章：数据持久化（自定义扩展数据）
 * ============================================================ */

/**
 * cm.getCharacterExtendValue(key)
 * 读取自定义持久化数据
 * 返回 JSON 字符串，需手动 parse
 */
var jsonStr = cm.getCharacterExtendValue("my_custom_data");
if (jsonStr != null) {
    var data = JSON.parse(jsonStr);
    /* 使用 data */
}

/**
 * cm.saveOrUpdateCharacterExtendValue(key, value)
 * 保存自定义持久化数据
 * value 为 JSON 字符串
 */
var saveData = JSON.stringify({
    count: 10,
    lastTime: "2024-01-01"
});
cm.saveOrUpdateCharacterExtendValue("my_custom_data", saveData);

/* ============================================================
 *  第七章：获取服务器数据
 * ============================================================ */

/**
 * cm.getPlayerCount(mapId)
 * 获取指定地图当前在线玩家数量
 */
var count = cm.getPlayerCount(100000000);

/**
 * cm.countAllMonstersOnMap(mapId)
 * 获取指定地图当前怪物数量
 */
var monsterCount = cm.countAllMonstersOnMap(280030000);

/**
 * pi.getPortal().getTargetMapId()
 * 获取传送门的目标地图ID
 */
var targetMap = pi.getPortal().getTargetMapId();

/**
 * im.getNpc()
 * 获取道具WZ中 spec/npc 字段值
 * 常用于卡片道具关联怪物ID
 */
var npcId = im.getNpc();

/**
 * im.getDirectionInfo()
 * 获取道具使用方向
 * 1=Undefined 2=Equipment 3=Consume 4=Equip
 */
var direction = im.getDirectionInfo();

/* ============================================================
 *  第八章：音效与BGM
 * ============================================================ */

/**
 * pi.playPortalSound()
 * 播放传送音效
 */
pi.playPortalSound();

/**
 * pi.changeMusic("BgmPath")
 * 更换当前BGM
 */
pi.changeMusic("Bgm04/ArabPirate");

/* ============================================================
 *  第九章：任务脚本专用方法（qm）
 * ============================================================ */

/**
 * qm.forceStartQuest()
 * 强制接取当前任务
 */
qm.forceStartQuest();

/**
 * qm.forceCompleteQuest()
 * 强制完成当前任务
 */
qm.forceCompleteQuest();

/**
 * qm.canHold()
 * 检查背包是否有足够空间容纳任务奖励
 * 返回 true/false
 */
if (qm.canHold()) {
    qm.forceCompleteQuest();
}

/**
 * qm.getQuestRecordEx(key)
 * 获取任务的扩展进度数据
 */
var progress = qm.getQuestRecordEx("kill_count");

/* ============================================================
 *  第十章：道具脚本专用方法（im）
 * ============================================================ */

/**
 * im.summonSamsaraStone(npcId, durationMinutes, accelRate)
 * 召唤轮回石碑（加速刷怪）
 * npcId: 石碑NPC模板ID
 * durationMinutes: 持续时间（分钟）
 * accelRate: 加速倍率，0.3 表示刷新时间缩短到30%
 */
im.summonSamsaraStone(9900002, 30, 0.3);

/**
 * im.hasSamsaraStone()
 * 检查当前地图是否已有轮回石碑
 */
if (im.hasSamsaraStone()) {
    im.getPlayer().dropMessage(5, "已有轮回石碑生效中");
}

/**
 * im.teachSkill(skillId, level, masterLevel)
 * 教给玩家指定技能
 */
im.teachSkill(1000, 1, 20);

/* ============================================================
 *  第十一章：事件脚本专用方法（eim）
 * ============================================================ */

/**
 * eim.getPlayerCount()
 * 获取事件中当前玩家数量
 */
var pCount = eim.getPlayerCount();

/**
 * eim.getPlayers()
 * 获取玩家列表迭代器
 */
var pIter = eim.getPlayers().iterator();
while (pIter.hasNext()) {
    var p = pIter.next();
    /* 处理每个玩家 */
}

/**
 * eim.schedule("funcName", delayMs)
 * 延迟调度函数调用
 */
eim.schedule("respawnStages", 10000);
/* 10秒后调用 respawnStages(eim) */

/**
 * eim.registerMonster(monsterId)
 * 注册需要追踪的怪物
 */
eim.registerMonster(9300183);

/**
 * eim.setProperty(key, value)
 * 设置事件属性
 */
eim.setProperty("stage", "1");

/**
 * eim.getProperty(key)
 * 获取事件属性
 */
var stage = parseInt(eim.getProperty("stage"));

/* ============================================================
 *  第十二章：反应堆脚本专用方法（rm）
 * ============================================================ */

/**
 * rm.dropItems()
 * 按 WZ 配置掉落道具
 */
rm.dropItems();

/**
 * rm.spawnDropItem(itemId, quantity, xOffset, yOffset)
 * 在反应堆位置生成掉落道具
 */
rm.spawnDropItem(4000000, 5, 0, 0);

/* ============================================================
 *  第十三章：常用JS工具方法
 * ============================================================ */

// ---- 随机数 ----
var rng = Math.floor(Math.random() * 10);
/* 生成 0~9 随机整数 */

var rngRange = Math.floor(Math.random() * (max - min + 1)) + min;
/* 生成 min~max 随机整数 */

// ---- 数组操作 ----
var arr = [100000000, 101000000, 102000000];
arr.push(103000000);
/* 尾部添加 */
var first = arr[0];
/* 取第一个元素 */
var len = arr.length;
/* 数组长度 */

// ---- JSON 处理 ----
var obj = { name: "test", value: 100 };
var json = JSON.stringify(obj);
/* 对象→字符串 */
var parsed = JSON.parse(json);
/* 字符串→对象 */

// ---- 类型转换 ----
var num = parseInt("123");
/* 字符串→整数 */
var str = String(123);
/* 整数→字符串 */

/* ============================================================
 *  第十四章：文本格式增强标记
 *
 *  可在 sendNext/sendSimple 等对话框文本中使用
 * ============================================================ */

/*
#b文本#b               蓝色加粗
#r文本#r               红色
#k文本#k               黑色（默认色）
#g文本#g               绿色
#d文本#d               紫色

#e文本#n               粗体→正常（取消加粗）
#c文本#c               ??（特殊标记）

#i物品ID#              显示道具图标（如 #i4000000# 显示蓝蜗牛壳图标）
#m地图ID#              显示地图名称（如 #m100000000# 显示"射手村"）
#pNPC_ID#              显示NPC名称（如 #p1002000#）
#t物品ID#              显示道具名称（如 #t4000000#）
#s物品ID#              显示道具说明
#fNPC_ID#              显示NPC形象（图片）

#h #                   显示玩家名称
#o数字#                 显示数字
#v物品ID#               显示道具图标（与 #i 类似）

#L编号#选项文本#l       定义可选项，配合 sendSimple 使用
                       选择后 selection=编号

\r\n                   换行
\t                     制表符（用于多列对齐）

// 示例：复杂菜单
cm.sendSimple(
    "#b欢迎光临北斗商城！#k\r\n\r\n" +
    "#L0##i4000000# #t4000000#  x5  #r100金币#k#l\r\n" +
    "#L1##i4000001# #t4000001#  x3  #r200金币#k#l\r\n" +
    "#L2#【离开商店】#l"
);
*/

/* ============================================================
 *  第十五章：常见模式与技巧
 * ============================================================ */

// ---- 金币检查 + 扣除模式 ----
/*
function checkAndPay(cost) {
    if (cm.getMeso() < cost) {
        cm.sendOk("你的金币不够！需要 " + cost + " 金币。");
        return false;
    }
    cm.gainMeso(-cost);
    return true;
}
*/

// ---- 等级检查模式 ----
/*
function checkLevel(minLevel) {
    if (cm.getPlayer().getLevel() < minLevel) {
        cm.sendOk("你的等级不足 " + minLevel + " 级。");
        return false;
    }
    return true;
}
*/

// ---- 地图限制模式 ----
/*
function isForbiddenMap(forbiddenMaps) {
    var currentMap = cm.getPlayer().getMapId();
    for (var i = 0; i < forbiddenMaps.length; i++) {
        if (currentMap == forbiddenMaps[i]) {
            return true;
        }
    }
    return false;
}
*/

// ---- 道具检查模式 ----
/*
function tryUseItem(itemId, cost) {
    if (!cm.haveItem(itemId, cost)) {
        cm.sendOk("你没有足够的道具！");
        cm.dispose();
        return false;
    }
    cm.gainItem(itemId, -cost);
    return true;
}
*/

// ---- 遍历数组生成菜单 ----
/*
function buildMenuFromArray(items) {
    var text = "请选择：\r\n";
    for (var i = 0; i < items.length; i++) {
        text += "#L" + i + "#" + items[i] + "#l\r\n";
    }
    return text;
}
*/

// ---- 日期时间获取（通过持久化或者地图属性） ----
// 注意：GraalVM JS 中 Date.now() 和 Math.random() 可能被限制
// 建议通过 Java互操作 或 服务端提供的方法获取
// var currentTime = java.lang.System.currentTimeMillis();

/* ============================================================
 *  第十六章：脚本文件命名约定
 *
 *  NPC 脚本:
 *    位置: scripts-zh-CN/npc/NPC_ID.js
 *    例:  scripts-zh-CN/npc/1002000.js
 *
 *  自定义脚本 (BeiDouSpecial):
 *    位置: scripts-zh-CN/BeiDouSpecial/脚本名.js
 *    调用: cm.openNpc(9900001, "脚本名")
 *    例:  cm.openNpc(9900001, "万能传送")
 *
 *  任务脚本:
 *    位置: scripts-zh-CN/quest/任务ID.js
 *    例:  scripts-zh-CN/quest/20000.js
 *
 *  道具脚本:
 *    位置: scripts-zh-CN/item/道具ID.js
 *    例:  scripts-zh-CN/item/2432000.js
 *    注意: 仅 243xxxx 范围内的道具ID会触发脚本
 *
 *  传送门脚本:
 *    位置: scripts-zh-CN/portal/脚本名.js
 *    例:  scripts-zh-CN/portal/EBoat1.js
 *
 *  地图脚本:
 *    位置: scripts-zh-CN/map/onFirstUserEnter/地图ID.js
 *    位置: scripts-zh-CN/map/onUserEnter/地图ID.js
 *    例:  scripts-zh-CN/map/onUserEnter/101000301.js
 *
 *  反应堆脚本:
 *    位置: scripts-zh-CN/reactor/反应堆ID.js
 *    例:  scripts-zh-CN/reactor/1002008.js
 *
 *  事件脚本:
 *    位置: scripts-zh-CN/event/事件名称.js
 *    例:  scripts-zh-CN/event/CarnivalPQ.js
 * ============================================================ */
