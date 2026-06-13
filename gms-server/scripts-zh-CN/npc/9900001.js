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
var chr = null;
var say = "";
var sel = null;
var 皇冠白 = "#fUI/GuildMark/Mark/Etc/00009004/15#";
var 完成 = "#fUI/UIWindow/Quest/Tab/enabled/2#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 粉心 = "#fEffect/CharacterEff/1112903/0/1#";
var 红心 = "#fEffect/CharacterEff/1082229/0/0#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 皇冠 = "#fUI/UIWindow/UserInfo/bossPetCrown#";
var 火箭 = "#fUI/GuildMark/Mark/Etc/00009022/12#";
var 奖励 = "#fUI/CashShop/CSDiscount/bonus#";
var line = "#fUI/CashShop/CSDiscount/Line#";
var 翅膀左 = "#fUI/ChatBalloon/118/nw#";
var 翅膀中间 = "#fUI/ChatBalloon/118/n#";
var 翅膀右 = "#fUI/ChatBalloon/118/ne#";

// ======================== 数字图标
var zero = "#fUI/Basic/LevelNo/0#";
var one = "#fUI/Basic/LevelNo/1#";
var nine = "#fUI/Basic/LevelNo/9#";

let changeLine = "\r\n"

var OldTitle = `${翅膀左}${翅膀中间.repeat(2)}\t\t#e欢迎来到  #r萧曳  #k冒险岛#n\t\t${翅膀中间.repeat(2)}${翅膀右}\t${changeLine.repeat(1)}`;
var status = -1;
var i = 0;
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
        // ========================================展示信息============================================
        if (cm.getOnlineTime() < 3600) {
            time = `在线时间：#e#r${Math.floor(cm.getOnlineTime() / 60)}#k#n 分钟${changeLine}`;
        } else {
            let hour = Math.floor(cm.getOnlineTime() / 3600);
            let min = Math.floor((cm.getOnlineTime() - hour * 3600) / 60);
            time = `在线时间：#e#r${hour}#k#n 小时 #e#r${min}#k#n 分钟${changeLine}`;
        }
        let cashShop = cm.getPlayer().getCashShop();
        text += `\t${正方箭头} 点券：${new Intl.NumberFormat().format(cashShop.getCash(1))}\t\t\t\t${time}`;
        text += `\t${正方箭头} 抵用：${new Intl.NumberFormat().format(cashShop.getCash(2))}\t\t\t\t${正方箭头} 信用：${new Intl.NumberFormat().format(cashShop.getCash(4))}${changeLine}`;
        text += `\t${正方箭头} 金币：${new Intl.NumberFormat().format(cm.getPlayer().getMeso())}${changeLine}`;
        //text += changeLine;
        // ========================================常用功能============================================1
        // 将字符串复制8次
        //text += `\t${皇冠.repeat(6)}\t 常用功能 \t${皇冠.repeat(6)}${changeLine}`;
        text += `${redSelect(3, "传送自由")}\t${generalSelect(71, "专车接送")}\t${generalSelect(69, "快速转职")}${changeLine.repeat(2)}`;
        text += `${redSelect(0, "新人福利")}\t${generalSelect(113, "新手礼包")}\t${generalSelect(112, "等级奖励")}\t${redSelect(65, "删除物品")}${changeLine.repeat(2)}`;
        text += `${redSelect(160, "快捷商店")}\t${generalSelect(165, "卷轴中心")}\t${generalSelect(4, "爆率一览")}\t${generalSelect(170, "金币兑换")}${changeLine.repeat(2)}`;
        text += `${redSelect(166, "仓库管理")}${changeLine.repeat(2)}`;

        //text += changeLine.repeat(2);
        // =======================================每日牛马============================================2
        //text += `\t${粉心.repeat(7)}\t 牛马每日 \t${粉心.repeat(7)}${changeLine}`;
        text += `${redSelect(1, "每日签到")}\t${redSelect(2, "在线奖励")}\t${generalSelect(200, "每日探索")}\t${redSelect(201, "每日副本")}${changeLine.repeat(2)}`;
        text += `${redSelect(202, "每日跑环")}\t${generalSelect(203, "跑环仓库")}\t${generalSelect(204, "每日副本")}\t${generalSelect(205, "每日BOSS")}${changeLine.repeat(2)}`;
        text += `${redSelect(220, "挑战大厅")}${changeLine.repeat(2)}`;

        //text += changeLine.repeat(2);
        // =======================================收集功能============================================3
        //text += `\t${皇冠.repeat(6)}\t 收集功能 \t${皇冠.repeat(6)}${changeLine}`;
        text += `${generalSelect(310, "卡片收集")}\t${generalSelect(301, "玩具收集")}\t${generalSelect(302, "学习技能")}${changeLine.repeat(2)}`;
        text += `${generalSelect(303, "强化戒指")}\t${generalSelect(304, "钓鱼中心")}\t${generalSelect(305, "小鱼戒指")}${changeLine.repeat(2)}`;

        //text += changeLine.repeat(2);
        // =======================================战力提升============================================4
        //text += `\t${粉心.repeat(7)}\t 战力提升 \t${粉心.repeat(7)}${changeLine}`;
        text += `${redSelect(104, "装备强化")}\t${redSelect(111, "装备进阶")}\t${generalSelect(300, "天赋学习")}\t${generalSelect(72, "转世重生")}${changeLine.repeat(2)}`;
        text += `${redSelect(300, "经验戒指")}\t${redSelect(300, "时装洗练")}\t${generalSelect(4, "翅膀称号")}\t${generalSelect(300, "血衣制作")}${changeLine.repeat(2)}`;
        text += `${redSelect(300, "套装进阶")}${changeLine.repeat(2)}`;

        //text += changeLine.repeat(2);
        // =======================================师徒家族============================================5
        //text += `\t${粉心.repeat(7)}\t 师徒家族 \t${粉心.repeat(7)}${changeLine}`;
        text += `${redSelect(300, "师徒系统")}\t${redSelect(300, "家族系统")}${changeLine.repeat(2)}`;


        //text += changeLine.repeat(2);
        // =======================================会员中心============================================6
        //text += `\t${粉心.repeat(7)}\t 会员中心 \t${粉心.repeat(7)}${changeLine}`;
        text += `${redSelect(600, "会员中心")}\t${redSelect(603, "赞助中心")}\t${generalSelect(604, "全服双倍")}\t${generalSelect(605, "全服双爆")}${changeLine.repeat(2)}`;
        text += `${redSelect(602, "会员商店")}\t${redSelect(606, "时装洗练")}\t${generalSelect(607, "------")}\t${generalSelect(608, "CDK兑换")}${changeLine.repeat(2)}`;


        // text += "#L999#测试脚本>>>未上线#l\t\r\n";
        if (cm.getPlayer().isGM()) {
            text += changeLine.repeat(2);
            // =======================================GM功能============================================9
            //text += `\t${皇冠.repeat(5)}\t GM功能 \t${皇冠.repeat(5)}${changeLine}`;
            text += `${generalSelect(62, "超级商店")}\t${generalSelect(66, "一键刷道具")}\t${generalSelect(904, "在线玩家")}${generalSelect(64, "UI查询")}${changeLine}`;
            text += `${generalSelect(900, "发送公告")}\t${generalSelect(901, "巡查面板")}\t${generalSelect(902, "召唤BOSS")}\t${generalSelect(903, "封禁")}${changeLine}`;
            text += `${generalSelect(905, "物品查询")}${changeLine}`;
            text += `${generalSelect(67, "有状态脚本示例")}\t${generalSelect(68, "NextLevel脚本示例")}${changeLine}`;
        }
        cm.sendSimple(text);
    } else if (status === 1) {
        doSelect(selection);
    } else {
        cm.dispose();
    }
}

/**
 * 红色选项
 * @param idNum
 * @param text
 * @returns {string}
 */
function redSelect(idNum, text) {
    return `#L${idNum}##r${text}#k#n#l`;
}

/**
 * 一般选项
 * @param idNum
 * @param text
 * @returns {string}
 */
function generalSelect(idNum, text) {
    return `#L${idNum}#${text}#l`;
}
function doSelect(selection) {
    switch (selection) {
        // 非GM功能
		case 999:
            openNpc("测试脚本");
            break;
        case 69:
            openNpc("快速转职");
            break;
        case 70:
            openNpc("技能学习");
            break;
        case 71:
            openNpc("万能传送");
            break;
        case 72:
            openNpc("转世重生");
            break;
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
        // GM功能
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
        case 104:
            openNpc("装备强化");
            break;
        case 111:
            openNpc("装备进阶");
            break;
        case 112:
            openNpc("等级奖励");
            break;
        case 113:
            openNpc("新手礼包");
            break;
        case 150:
            openNpc("xy/副本/副本传送");
            break;
        case 160:
            openNpc("快捷商店");
            break;
        case 165:
            openNpc("xy/other/卷轴中心");
            break;
        case 166:
            openNpc("xy/仓库");
            break;
        case 170:
            openNpc("xy/other/金币兑换");
            break;
        case 200:
            openNpc("xy/day/每日探索");
            break;
        case 201:
            openNpc("xy/day/每日副本");
            break;
        case 202:
            openNpc("xy/day/每日跑环");
            break;
        case 203:
            openNpc("xy/day/跑环仓库");
            break;
        case 204:
            openNpc("xy/day/每日副本");
            break;
        case 205:
            openNpc("xy/day/每日BOSS");
            break;
        case 310:
            openNpc("xy/卡片收集");
            break;
        case 301:
            openNpc("xy/玩具收集");
            break;
        case 302:
            openNpc("xy/技能学习");
            break;
        case 303:
            openNpc("xy/强化戒指");
            break;
        case 304:
            openNpc("xy/钓鱼中心");
            break;
        case 305:
            openNpc("xy/小鱼戒指");
            break;
        case 220:
            openNpc("xy/day/挑战大厅");
            break;
        case 600:
            openNpc("xy/vip/会员中心");
            break;
        case 602:
            openNpc("xy/vip/会员商店");
            break;
        case 603:
            openNpc("xy/vip/赞助中心");
            break;
        case 604:
            openNpc("xy/all/全服双倍");
            break;
        case 605:
            openNpc("xy/all/全服双爆");
            break;
        case 606:
            openNpc("xy/other/时装洗练");
            break;
        case 607:
            openNpc("xy/other/CDK_兑换");
            break;
        case 608:
            openNpc("xy/vip/CDK_兑换");
            break;
        case 900:
            openNpc("xy/gm/发送公告");
            break;
        case 901:
            openNpc("xy/gm/巡查面板");
            break;
        case 902:
            openNpc("xy/gm/召唤野外BOSS");
            break;
        case 903:
            openNpc("xy/gm/封禁");
            break;
        case 904:
            openNpc("xy/gm/在线玩家");
            break;
        case 905:
            openNpc("xy/gm/物品查询");
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