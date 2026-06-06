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

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * @description 北斗脚本中心 - 主入口
 */
var OldTitle = "\t\t\t\t\t\t\t\t#e欢迎来到#r 萧 曳 #k脚本中心#n\t\t\t\t\r\n";
var status = -1;

function start() {
    action(1, 0, 0)
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
        var text = OldTitle;
        text += "当前点券    ：" + cm.getPlayer().getCashShop().getCash(1) + "\r\n";
        text += "当前抵用券：" + cm.getPlayer().getCashShop().getCash(2) + "\r\n";
        text += "当前信用券：" + cm.getPlayer().getCashShop().getCash(4) + "\r\n";
        text += "\r\n\r\n";

        // ======================== 快捷入口 ========================
        text += "#d========== 快捷入口 ==========#k\r\n";
        text += "#L5#回到自由#l \t #L61#快捷传送#l \t #L77#Boss入口#l\r\n";
        text += "\r\n";

        // ======================== 日常福利 ========================
        text += "#d========== 日常福利 ==========#k\r\n";
        text += "#L0#新人福利#l \t #L1#每日签到#l \t #L2#在线奖励#l\r\n";
        text += "\r\n";

        // ======================== 信息查询 ========================
        text += "#d========== 信息查询 ==========#k\r\n";
        text += "#L4#爆率一览#l \t #L75#道具搜索#l \t #L76#角色信息#l\r\n";
        text += "#L69#卡片收集#l \t #L70#XY收集#l \t #L71#玩具收集#l\r\n";
        text += "#L86#商店查找#l \t #L87#抽奖查看#l\r\n";
        text += "\r\n";

        // ======================== 经济交易 ========================
        text += "#d========== 经济交易 ==========#k\r\n";
        text += "#L80#拍卖行#l \t #L81#金币商城#l \t #L82#装备回收#l\r\n";
        text += "#L83#制作合成#l \t #L110#装备进阶#l\r\n";
        text += "#L97#售卖装备#l \t #L98#售卖其他#l\r\n";
        text += "\r\n";

        // ======================== 社交活动 ========================
        text += "#d========== 社交活动 ==========#k\r\n";
        text += "#L84#活动管理#l \t #L85#钓鱼#l \t #L72#城镇任务#l\r\n";
        text += "#L78#成就中心#l \t #L79#任务板#l\r\n";
        text += "\r\n";

        // ======================== 角色相关 ========================
        text += "#d========== 角色相关 ==========#k\r\n";
        text += "#L88#结婚信息#l \t #L94#家族信息#l \t #L101#全能转职#l\r\n";

        if (cm.getPlayer().isGM()) {
            text += "\r\n";
            text += "\t\t\t\t#r===== 以下内容仅GM可见 =====#k\r\n";
            text += "\r\n";

            // ======================== GM | 传送/商店 ========================
            text += "#d========== 传送/商店 ==========#k\r\n";
            text += "#L62#超级商店#l \t #L63#整容集合#l\r\n";
            text += "\r\n";

            // ======================== GM | 数据管理 ========================
            text += "#d========== 数据管理 ==========#k\r\n";
            text += "#L64#UI查询#l \t #L65#一键删除道具#l \t #L66#一键刷道具#l\r\n";
            text += "#L89#地图监控#l \t #L90#服务器设置#l \t #L91#合服工具#l\r\n";
            text += "#L92#GM日志查看#l \t #L93#UI速查脚本#l \t #L95#玩家管理#l\r\n";
            text += "#L96#批量发放#l \t #L102#怪物召唤#l \t #L108#技能给予#l\r\n#L109#怪物攻城#l\r\n";
            text += "\r\n";

            // ======================== GM | 快速操作 ========================
            text += "#d========== 快速操作 ==========#k\r\n";
            text += "#L73#快速转职#l \t #L74#一键满技能#l\r\n";
            text += "#L99#快速售卖装备#l \t #L100#快速售卖其他#l\r\n";
            text += "\r\n";

            // ======================== GM | 脚本示例 ========================
            text += "#d========== 脚本示例 ==========#k\r\n";
            text += "#L67#状态脚本示例#l \t #L68#NextLevel示例#l\r\n";

            // ======================== 待测试功能 ========================
            text += "\r\n";
            text += "\t\t\t\t#r===== 待测试功能（need_test）=====#k\r\n";
            text += "#L103#抽奖#l \t #L104#装备强化#l \t #L105#装备合成#l\r\n";
            text += "#L106#时装强化#l \t #L107#时装洗练#l \t #L108#勋章强化#l\r\n";
            text += "#L111#装备进阶#l\r\n";
        }
        cm.sendSimple(text);
    } else if (status === 1) {
        doSelect(selection);
    } else {
        cm.dispose();
    }
}

function doSelect(selection) {
    switch (selection) {
        // ========== 快捷入口 ==========
        case 5:
            cm.getPlayer().saveLocation("FREE_MARKET");
            cm.warp(910000000, "out00");
            break;
        case 61:
            openNpc("万能传送");
            break;
        case 77:
            openNpc("Boss入口");
            break;

        // ========== 日常福利 ==========
        case 0:
            openNpc("新人福利");
            break;
        case 1:
            openNpc("每日签到");
            break;
        case 2:
            openNpc("在线奖励_nextlevel");
            break;

        // ========== 信息查询 ==========
        case 4:
            openNpc("当前地图掉落");
            break;
        case 75:
            openNpc("道具搜索");
            break;
        case 76:
            openNpc("角色信息卡");
            break;
        case 69:
            openNpc("卡片收集");
            break;
        case 70:
            openNpc("XY收集");
            break;
        case 71:
            openNpc("玩具收集");
            break;
        case 86:
            openNpc("商店查找器");
            break;
        case 87:
            openNpc("抽奖查看器");
            break;

        // ========== 经济交易 ==========
        case 80:
            openNpc("拍卖行");
            break;
        case 81:
            openNpc("金币商城");
            break;
        case 82:
            openNpc("装备回收");
            break;
        case 83:
            openNpc("制作合成");
            break;
        case 110:
            openNpc("装备进阶");
            break;
        case 97:
            openNpc("一键售卖装备");
            break;
        case 98:
            openNpc("一键售卖其他");
            break;

        // ========== 社交活动 ==========
        case 84:
            openNpc("活动管理器");
            break;
        case 85:
            openNpc("钓鱼");
            break;
        case 72:
            openNpc("城镇任务");
            break;
        case 78:
            openNpc("成就中心");
            break;
        case 79:
            openNpc("任务板");
            break;

        // ========== 角色相关 ==========
        case 88:
            openNpc("结婚扩展");
            break;
        case 94:
            openNpc("家族信息");
            break;
        case 101:
            openNpc("全能转职");
            break;

        // ========== GM | 传送/商店 ==========
        case 62:
            cm.dispose();
            cm.openShopNPC(9900001);
            break;
        case 63:
            openNpc("Salon");
            break;

        // ========== GM | 数据管理 ==========
        case 64:
            openNpc("gm/UI查询");
            break;
        case 65:
            openNpc("一键删除道具");
            break;
        case 66:
            openNpc("gm/一键刷道具");
            break;
        case 89:
            openNpc("gm/地图监控");
            break;
        case 90:
            openNpc("gm/服务器设置");
            break;
        case 91:
            openNpc("gm/合服工具");
            break;
        case 92:
            openNpc("gm/GM日志查看");
            break;
        case 93:
            openNpc("gm/UI速查脚本");
            break;
        case 95:
            openNpc("gm/玩家管理");
            break;
        case 96:
            openNpc("gm/批量发放");
            break;
        case 102:
            openNpc("gm/怪物召唤");
            break;
        case 108:
            openNpc("gm/玩家技能给予");
            break;
        case 109:
            openNpc("gm/怪物攻城");
            break;

        // ========== GM | 快速操作 ==========
        case 73:
            openNpc("快速转职");
            break;
        case 74:
            openNpc("满技能2");
            break;
        case 99:
            openNpc("快速售卖装备");
            break;
        case 100:
            openNpc("快速售卖其他");
            break;

        // ========== GM | 脚本示例 ==========
        case 67:
            openNpc("Example1");
            break;
        case 68:
            openNpc("Example2");
            break;

        // ========== 待测试功能 ==========
        case 103:
            openNpc("need_test/xy_抽奖");
            break;
        case 104:
            openNpc("装备强化");
            break;
        case 105:
            openNpc("need_test/xy_装备合成");
            break;
        case 106:
            openNpc("need_test/xy_时装强化");
            break;
        case 107:
            openNpc("need_test/xy_时装洗练");
            break;
        case 108:
            openNpc("gm/xy_勋章强化");
            break;
        case 111:
            openNpc("装备进阶");
            break;

        default:
            cm.sendOk("该功能暂不支持，敬请期待！");
            cm.dispose();
    }
}

function openNpc(scriptName) {
    cm.dispose();
    cm.openNpc(9900001, scriptName);
}
