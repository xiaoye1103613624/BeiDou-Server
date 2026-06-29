/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * @description 拍卖行中心脚本（分页版）
 * 主菜单拆分为分类页+子页，避免 sendSimple 文本过长导致客户端闪退
 */
var status = -1;
var selectedCategory = -1; // 玩家在状态0选择的分类编号
var changeLine = "\r\n";

// ======================== UI 图标常量 ========================
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 皇冠 = "#fUI/UIWindow/UserInfo/bossPetCrown#";
var 粉心 = "#fEffect/CharacterEff/1112903/0/1#";
var 翅膀左 = "#fUI/ChatBalloon/118/nw#";
var 翅膀中间 = "#fUI/ChatBalloon/118/n#";
var 翅膀右 = "#fUI/ChatBalloon/118/ne#";

// ======================== 分类菜单配置 ========================
// 主分类页（状态0）：{ id, label, icon }
var CATEGORIES = [
    { id: 1,  label: "常用功能", icon: 皇冠 },
    { id: 2,  label: "牛马每日", icon: 粉心 },
    { id: 3,  label: "收集功能", icon: 皇冠 },
    { id: 4,  label: "战力提升", icon: 粉心 },
    { id: 5,  label: "师徒家族", icon: 粉心 },
    { id: 6,  label: "会员中心", icon: 粉心 },
    { id: 7,  label: "其他功能", icon: 粉心 },
    { id: 99, label: "GM 功能", icon: 皇冠, gmOnly: true }
];

// 子分类页（状态1）：按主分类id索引，每项 { id, label, red }
var SUB_MENUS = {};

// ---- 常用功能 ----
SUB_MENUS[1] = [
    { id: 3,  label: "[传送自由]", red: true },
    { id: 71, label: "[专车接送]", red: true },
    { id: 11, label: "[匠人街]",   red: true },
    { id: 0,  label: "新人福利",   red: false },
    { id: 73, label: "新人问问",   red: false },
    { id: 113,label: "新手礼包",   red: false },
    { id: 112,label: "等级奖励",   red: false },
    { id: 69, label: "快速转职",   red: false },
    { id: 62, label: "快捷商店",   red: true },
    { id: 165,label: "卷轴中心",   red: false },
    { id: 4,  label: "爆率一览",   red: false },
    { id: 302,label: "学习技能",   red: false }
];

// ---- 牛马每日 ----
SUB_MENUS[2] = [
    { id: 1,   label: "每日签到", red: true },
    { id: 2,   label: "在线奖励", red: true },
    { id: 200, label: "每日探索", red: false },
    { id: 201, label: "每日副本", red: true },
    { id: 202, label: "每日跑环", red: true },
    { id: 203, label: "跑环仓库", red: false },
    { id: 205, label: "每日BOSS", red: false },
    { id: 206, label: "双倍领取", red: false },
    { id: 220, label: "高级BOSS", red: true },
    { id: 221, label: "远征BOSS", red: true }
];

// ---- 收集功能 ----
SUB_MENUS[3] = [
    { id: 311, label: "卡片收集", red: false },
    { id: 301, label: "玩具收集", red: false }
];

// ---- 战力提升 ----
SUB_MENUS[4] = [
    { id: 507, label: "武器中心", red: true },
    { id: 508, label: "套服进阶", red: true },
    { id: 111, label: "戒指中心", red: true },
    { id: 504, label: "时装洗练", red: true },
    { id: 65,  label: "删除物品", red: true }
];

// ---- 师徒家族 ----
SUB_MENUS[5] = [
    { id: 400, label: "师徒系统", red: true },
    { id: 401, label: "家族系统", red: true }
];

// ---- 会员中心 ----
SUB_MENUS[6] = [
    { id: 600, label: "会员中心", red: true },
    { id: 603, label: "赞助中心", red: true },
    { id: 604, label: "全服双倍", red: false },
    { id: 605, label: "全服双爆", red: false },
    { id: 602, label: "会员商店", red: true },
    { id: 607, label: "一键出售", red: true },
    { id: 606, label: "时装洗练", red: true },
    { id: 608, label: "CDK兑换", red: false }
];

// ---- 其他功能 ----
SUB_MENUS[7] = [
    { id: 166, label: "仓库管理", red: true },
    { id: 167, label: "物品兑换", red: false },
    { id: 168, label: "金币赌博", red: false },
    { id: 169, label: "金币抽奖", red: false },
    { id: 170, label: "道具抽奖", red: false },
    { id: 171, label: "枫叶兑换", red: false },
    { id: 172, label: "答题",     red: false },
    { id: 173, label: "精美点装", red: false },
    { id: 174, label: "皇家发型", red: false },
    { id: 176, label: "益智答题", red: false },
    { id: 177, label: "发色选择", red: false },
    { id: 509, label: "时装升星", red: false },
    { id: 511, label: "口令礼包", red: false },
    { id: 178, label: "现金商店", red: false },
    { id: 510, label: "一键回收", red: false },
    { id: 179, label: "银行系统", red: false },
    { id: 180, label: "小游戏中心", red: false },
    { id: 181, label: "椅子抽奖", red: false }
];

// ---- GM 功能 ----
SUB_MENUS[99] = [
    { id: 990, label: "GM商店",      red: false },
    { id: 66,  label: "一键刷道具",  red: false },
    { id: 904, label: "在线玩家",    red: false },
    { id: 64,  label: "UI查询",      red: false },
    { id: 900, label: "发送公告",    red: false },
    { id: 901, label: "巡查面板",    red: false },
    { id: 902, label: "召唤BOSS",    red: false },
    { id: 903, label: "封禁",        red: false },
    { id: 905, label: "物品查询",    red: false },
    { id: 906, label: "虚空索物",    red: false },
    { id: 907, label: "任意门",      red: false },
    { id: 500, label: "装备制作",    red: false }
];

// ======================== 脚本入口 ========================

function start() {
    status = -1;
    selectedCategory = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else if (mode === -1) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    if (status === 0) {
        // ===== 主分类页 =====
        cm.sendSimple(buildMainPage());
    } else if (status === 1) {
        // ===== 用户选择了主分类 → 显示子分类页 =====
        if (mode === 1) {
            selectedCategory = selection; // 仅在前进时更新分类选择（后退时保留原值）
        }
        if (selectedCategory === -1 || !SUB_MENUS[selectedCategory]) {
            cm.dispose();
            return;
        }
        cm.sendSimple(buildSubPage(selectedCategory));
    } else if (status === 2) {
        // ===== 用户选择了子分类中的项目 → 执行 =====
        if (selection === 9999) {
            // "返回主菜单"选项（用9999避免与真实选项ID冲突）
            status = -1;
            selectedCategory = -1;
            action(1, 0, 0);
            return;
        }
        doSelect(selection);
    } else {
        cm.dispose();
    }
}

// ======================== 页面构建函数 ========================

/**
 * 构建主分类页（状态0）
 * 显示玩家信息 + 8个分类入口
 */
function buildMainPage() {
    var text = "";
    // 标题
    text += 翅膀左 + 翅膀中间 + 翅膀中间 + "\t\t#e欢迎来到  #r萧曳  #k冒险岛#n\t\t" + 翅膀中间 + 翅膀中间 + 翅膀右 + "\t" + changeLine;
    // 玩家信息
    text += buildHeaderInfo();
    text += changeLine;
    // 分类入口
    var i, cat;
    for (i = 0; i < CATEGORIES.length; i++) {
        cat = CATEGORIES[i];
        if (cat.gmOnly && !cm.getPlayer().isGM()) {
            continue;
        }
        text += "#L" + cat.id + "#  " + (cat.icon || "") + " " + cat.label + " #l" + changeLine;
    }
    return text;
}

/**
 * 构建子分类页（状态1）
 * @param catId 主分类ID
 */
function buildSubPage(catId) {
    var items = SUB_MENUS[catId];
    if (!items) {
        return "该分类暂无内容。";
    }
    var text = "#L9999#<< 返回主菜单#l" + changeLine + changeLine;
    var i, it;
    for (i = 0; i < items.length; i++) {
        it = items[i];
        if (it.red) {
            text += "#L" + it.id + "##r" + it.label + "#k#l";
        } else {
            text += "#L" + it.id + "#" + it.label + "#l";
        }
        // 每行放2个
        if (i % 2 === 1 && i < items.length - 1) {
            text += changeLine;
        } else if (i < items.length - 1) {
            text += "\t";
        }
    }
    return text;
}

/**
 * 构建顶部玩家信息栏
 */
function buildHeaderInfo() {
    var cashShop = cm.getPlayer().getCashShop();
    var onlineMs = cm.getOnlineTime();
    var timeStr;
    if (onlineMs < 3600) {
        timeStr = "在线时间：#e#r" + Math.floor(onlineMs / 60) + "#k#n 分钟";
    } else {
        var hour = Math.floor(onlineMs / 3600);
        var min = Math.floor((onlineMs % 3600) / 60);
        timeStr = "在线时间：#e#r" + hour + "#k#n 小时 #e#r" + min + "#k#n 分钟";
    }
    var info = "";
    info += "\t" + 正方箭头 + " 点券：" + fmtNum(cashShop.getCash(1)) + "\t\t\t\t" + timeStr + changeLine;
    info += "\t" + 正方箭头 + " 抵用：" + fmtNum(cashShop.getCash(2)) + "\t\t\t\t" + 正方箭头 + " 信用：" + fmtNum(cashShop.getCash(4)) + changeLine;
    info += "\t" + 正方箭头 + " 金币：" + fmtNum(cm.getPlayer().getMeso()) + changeLine;
    return info;
}

/**
 * 数字格式化（兼容GraalJS，Intl可能不可用）
 */
function fmtNum(n) {
    try {
        return new Intl.NumberFormat().format(n);
    } catch (e) {
        return String(n);
    }
}

// ======================== 选择执行 ========================

function doSelect(selection) {
    switch (selection) {
        case 999: openNpc("测试脚本"); break;
        case 69:  openNpc("快速转职"); break;
        case 70:  openNpc("技能学习"); break;
        case 71:  openNpc("万能传送"); break;
        case 72:  openNpc("转世重生"); break;
        case 0:   openNpc("新人福利"); break;
        case 73:  openNpc("新人问问"); break;
        case 1:   openNpc("每日签到"); break;
        case 2:   openNpc("在线奖励_nextlevel"); break;
        case 3:   cm.getPlayer().saveLocation("FREE_MARKET"); cm.warp(910000000, "out00"); break;
        case 11:  cm.getPlayer().saveLocationOnWarp(); cm.getPlayer().dropMessage(6, "[传送中心]：[" + cm.getPlayer().getName() + "玩家] [线路-" + cm.getPlayer().getClient().getChannel() + "] 传送至 匠人街"); cm.warp(910001000); cm.dispose(); break;
        case 4:   openNpc("当前地图掉落"); break;
        case 62:  cm.dispose(); cm.openShopNPC(9900001); break;
        case 63:  openNpc("Salon"); break;
        case 64:  openNpc("UI查询"); break;
        case 65:  openNpc("一键删除道具"); break;
        case 66:  openNpc("一键刷道具"); break;
        case 67:  openNpc("Example1"); break;
        case 68:  openNpc("Example2"); break;
        case 104: openNpc("装备强化"); break;
        case 111: openNpc("xy/装备系统/v002/戒指中心"); break;
        case 112: openNpc("等级奖励"); break;
        case 113: openNpc("新手礼包"); break;
        case 150: openNpc("xy/副本/副本传送"); break;
        case 160: openNpc("快捷商店"); break;
        case 165: openNpc("xy/other/卷轴中心"); break;
        case 166: openNpc("xy/仓库"); break;
        case 167: openNpc("xy/other/物品兑换"); break;
        case 168: openNpc("xy/other/金币赌博"); break;
        case 169: openNpc("xy/other/金币抽奖"); break;
        case 170: openNpc("xy/other/道具抽奖"); break;
        case 171: openNpc("xy/other/枫叶兑换"); break;
        case 172: openNpc("xy/other/答题"); break;
        case 173: openNpc("xy/other/精美时装"); break;
        case 174: openNpc("xy/other/皇家发型"); break;
        case 175: openNpc("xy/gm/物品兑换"); break;
        case 176: openNpc("xy/other/益智答题"); break;
        case 177: openNpc("xy/other/发色选择"); break;
        case 178: openNpc("xy/other/现金商店"); break;
        case 179: openNpc("xy/other/银行系统"); break;
        case 180: openNpc("xy/games/小游戏中心"); break;
        case 181: openNpc("xy/other/椅子抽奖"); break;
        case 200: openNpc("xy/day/每日探索"); break;
        case 201: openNpc("xy/day/每日副本"); break;
        case 202: openNpc("xy/day/每日跑环"); break;
        case 203: openNpc("xy/day/跑环仓库"); break;
        case 204: openNpc("xy/day/每日副本"); break;
        case 205: openNpc("xy/day/每日Boss"); break;
        case 206: openNpc("xy/day/每日双倍领取"); break;
        case 220: openNpc("xy/boss/高级BOSS"); break;
        case 221: openNpc("xy/挑战/9031000_远征"); break;
        case 300: openNpc("xy/mentor/师徒系统"); break;
        case 301: openNpc("xy/collect/玩具收集"); break;
        case 302: openNpc("xy/技能学习"); break;
        case 303: openNpc("xy/强化戒指"); break;
        case 304: openNpc("xy/钓鱼中心"); break;
        case 305: openNpc("xy/小鱼戒指"); break;
        case 306: openNpc("xy/家族系统"); break;
        case 310: openNpc("xy/collect/卡片收集"); break;
        case 311: openNpc("xy/卡片收集"); break;
        case 400: openNpc("xy/mentor/师徒系统"); break;
        case 401: openNpc("xy/家族系统"); break;
        case 500: openNpc("xy/装备系统/v000/套装制作升级"); break;
        case 502: openNpc("xy/天赋学习"); break;
        case 503: openNpc("xy/经验戒指"); break;
        case 504: openNpc("xy/时装洗练"); break;
        case 505: openNpc("xy/翅膀称号"); break;
        case 506: openNpc("xy/血衣合成"); break;
        case 507: openNpc("xy/装备系统/v002/武器中心"); break;
        case 508: openNpc("xy/装备系统/v002/套服进阶"); break;
        case 509: openNpc("xy/other/时装升星"); break;
        case 510: openNpc("xy/other/一键回收"); break;
        case 511: openNpc("xy/other/口令礼包"); break;
        case 600: openNpc("xy/vip/会员中心"); break;
        case 602: openNpc("xy/vip/会员商店"); break;
        case 603: openNpc("xy/vip/赞助中心"); break;
        case 604: openNpc("xy/all/全服双倍"); break;
        case 605: openNpc("xy/all/全服双爆"); break;
        case 606: openNpc("xy/other/时装洗练"); break;
        case 607: openNpc("xy/一键出售"); break;
        case 608: openNpc("xy/vip/CDK_兑换"); break;
        case 900: openNpc("xy/gm/发送公告"); break;
        case 901: openNpc("xy/gm/巡查面板"); break;
        case 902: openNpc("xy/gm/召唤野外BOSS"); break;
        case 903: openNpc("xy/gm/封禁"); break;
        case 904: openNpc("xy/gm/在线玩家"); break;
        case 905: openNpc("xy/gm/物品查询"); break;
        case 906: openNpc("xy/gm/虚空索物"); break;
        case 907: openNpc("xy/gm/任意门"); break;
        case 990: cm.dispose(); cm.openShopNPC(9900001); break;
        default:
            cm.sendOk("该功能暂不支持，敬请期待！");
            cm.dispose();
    }
}

function openNpc(scriptName) {
    cm.dispose();
    cm.openNpc(9900001, scriptName);
}
