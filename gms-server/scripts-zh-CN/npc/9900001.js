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
 * @description 拍卖行中心脚本
 */
var OldTitle = "\t\t\t\t\t\t\t\t#e欢迎来到#r 萧 曳 #k脚本中心#n\t\t\t\t\r\n";
var status = -1;
var i = 0;
var changeLine = "\r\n";
var changeTwoLine = "\r\n\r\n";
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
		let text = OldTitle;
        text += "当前点券    ：" + cm.getPlayer().getCashShop().getCash(1) + changeLine;
        text += "当前抵用券：" + cm.getPlayer().getCashShop().getCash(2) + changeLine;
        text += "当前信用券：" + cm.getPlayer().getCashShop().getCash(4) + changeLine;
        text += changeTwoLine;
        text += "#L5#[回到自由]#l \t #L61#[快捷传送]#l \t #L70#[副本大厅]#l";
        text += changeTwoLine;
        text += "#L0#新人福利#l \t #L1#每日签到#l \t #L2#在线奖励#l" + changeLine;
        text += "#L3#传送自由#l \t #L4#爆率一览#l \t #L69#卡片收集#l" + changeLine;
        text += "#L71#玩具收集#l \t #L72#城镇任务#l" + changeLine;
        text += "#L75#道具搜索#l \t #L76#角色信息#l \t #L77#Boss入口#l" + changeLine;
        text += "#L78#成就中心#l \t #L79#任务板#l \t #L80#拍卖行#l" + changeLine;
        text += "#L81#金币商城#l \t #L82#装备回收#l \t #L83#制作合成#l" + changeLine;
        text += "#L84#活动管理#l \t #L85#钓鱼#l \t #L86#商店查找#l" + changeLine;
        text += "#L87#抽奖查看#l \t #L88#结婚信息#l" + changeLine;

        if (cm.getPlayer().isGM()) {
            text += changeTwoLine;
            text += "\t\t\t\t#r=====以下内容仅GM可见=====" + changeLine;
            text += "#L61#超级传送#l \t #L62#超级商店#l \t #L63#整容集合#l" + changeTwoLine;
            text += "#L64#UI查询#l \t #L65#一键删除道具#l \t #L66#一键刷道具#l" + changeTwoLine;
			text += "#L67#有状态脚本示例#l \t #L68#NextLevel脚本示例#l" + changeTwoLine;
			text += "#L73#快速转职#l \t #L74#一键满技能#l" + changeTwoLine;
			text += "#L89#地图监控#l \t #L90#服务器设置#l \t #L91#合服工具#l" + changeTwoLine;
			text += "#L92#GM日志查看#l";
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
        // 非GM功能
        case 0:
            openNpc("新人福利");
            break;
        case 1:
            openNpc("每日签到");
            break;
        case 2:
            openNpc("在线奖励_nextlevel");
            break;
        case 3:
            cm.getPlayer().saveLocation("FREE_MARKET");
            cm.warp(910000000, "out00");
            break;
        case 4:
            openNpc("当前地图掉落");
            break;
        case 69:
            openNpc("卡片收集");
            break;
        case 71:
            openNpc("玩具收集");
            break;
        case 72:
            openNpc("城镇任务");
            break;
        // GM功能
        case 61:
            openNpc("万能传送");
            break;
        case 62:
            cm.dispose();
            cm.openShopNPC(9900001);
            cm.dispose();
            break;
        case 63:
            openNpc("Salon");
            break;
        case 64:
            openNpc("UI查询");
            break;	
        case 65:
            openNpc("一键删除道具");
            break;
        case 66:
            openNpc("一键刷道具");
            break;
        case 67:
            openNpc("Example1")
            break;
        case 68:
            openNpc("Example2")
            break;
        case 73:
            openNpc("快速转职");
            break;
        case 74:
            openNpc("一键满技能");
            break;
        // 非GM功能 - 新增
        case 75:
            openNpc("道具搜索");
            break;
        case 76:
            openNpc("角色信息卡");
            break;
        case 77:
            openNpc("Boss入口");
            break;
        case 78:
            openNpc("成就中心");
            break;
        case 79:
            openNpc("任务板");
            break;
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
        case 84:
            openNpc("活动管理器");
            break;
        case 85:
            openNpc("钓鱼");
            break;
        case 86:
            openNpc("商店查找器");
            break;
        case 87:
            openNpc("抽奖查看器");
            break;
        case 88:
            openNpc("结婚扩展");
            break;
        // GM功能 - 新增
        case 89:
            openNpc("地图监控");
            break;
        case 90:
            openNpc("服务器设置");
            break;
        case 91:
            openNpc("合服工具");
            break;
        case 92:
            openNpc("GM日志查看");
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