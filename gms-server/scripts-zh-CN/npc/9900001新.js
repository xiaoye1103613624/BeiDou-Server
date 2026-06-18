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
var acc = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 皇冠白 ="#fUI/GuildMark/Mark/Etc/00009004/16#";
var 红爱心 ="#fEffect/CharacterEff/1112905/0/1#";
var 金币图标 = "#fUI/UIWindow.img/QuestIcon/7/0#";
var 小金币 = "#fUI/UIWindow.img/Item/BtCoin/normal/0#";
var 点券图标 = "#fUI/CashShop/CashItem/0#";

var 小烟花 = "#fMap/MapHelper/weather/squib/squib4/1#";
var 红枫叶 = "#fMap/MapHelper/weather/maple/1#";
var OldTitle ="\t\t\t#e"+小烟花 +""+小烟花 +""+红枫叶+"欢迎来到#rBeiDou#k脚本中心"+红枫叶+""+小烟花 +""+小烟花 +"#n\t\t\t\t\r\n";
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
		//text = ""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+""+红枫叶+"\r\n"
		let text = OldTitle;
        text += "\t\t\t\t\t"+小烟花 +""+点券图标+"#e当前点券："  + cm.getPlayer().getCashShop().getCash(1) + " "+点券图标+""+小烟花 +"\r\n";
        //text += "当前抵用券：" + cm.getPlayer().getCashShop().getCash(2) + "\r\n";
        //text += "当前信用券：" + cm.getPlayer().getCashShop().getCash(4) + "\r\n";
        //text += " \r\n";
        //text += "#b#n  注：点击NPC无反应可输入 @dispose 来解卡#k\r\n";
        text += "\t\t#b注：点击NPC无反应可输入 @dispose 来解卡#k\r\n \t#b    全屏拾取@itemvac 可以加入到技能宏个性名#k\r\n\r\n";
        text += "\t#d   【随着等级提升，将自动开放更多便捷功能】#k\r\n";

        text += "\t\t\t\t#r#n#L1#"+红爱心 +"每日签到"+红爱心 +"#l\t\t #L2#"+红爱心 +"在线奖励"+红爱心 +"#l\r\n";	

        text += "\t\t\t\t#r#n#L0#"+红爱心 +"新人福利"+红爱心 +"#l\t\t #L502#"+红爱心 +"道具手册"+红爱心 +"#l\r\n"; 

        text += "\t\t\t\t#r#n#L74#"+红爱心 +"快捷箱子"+红爱心 +"#l\t\t #L503#"+红爱心 +"装备制作"+红爱心 +"#l\r\n\r\n";	


        if (cm.getPlayer().getLevel()>=10) {
         text += "#b#L3#"+小烟花 +"万能传送#l\t #L9#"+小烟花 +"快速转职#l\t #L802#"+小烟花 +"一键出售#l\r\n";
        }

        if (cm.getPlayer().getLevel()>=30) {
         text += "#b#L6#"+小烟花 +"便利商店#l\t #L12#"+小烟花 +"血衣合成#l\t #L11#"+小烟花 +"爆率一览#l\r\n";
       }

        if (cm.getPlayer().getLevel()>=60) {
         text += "#b#L15#"+小烟花 +"随身仓库#l\t #L25#"+小烟花 +"怪物卡戒#l\t #L26#"+小烟花 +"矿卷背包#l\r\n";
       }

        if (cm.getPlayer().getLevel()>=90) {
         text += "#b#L14#"+小烟花 +"金币兑换#l\t #L13#"+小烟花 +"物品兑换#l\t #L17#"+小烟花 +"益智答题#l\r\n";
       }

        if (cm.getPlayer().getLevel()>=120) {
         text += "#b#L5#" + 小烟花 + "时尚点装#l\t #L10#" + 小烟花 + "三宠技能#l\t #L16#" + 小烟花 +"删除道具#l\r\n\r\n";
       }

        if (cm.getPlayer().getLevel()>=160) {
        text += "#b#e#L8#"+红爱心 +"大药商店"+红爱心 +"#l\t#L7#"+红爱心 +"卷轴商店"+红爱心 +"#l\t#L801#"+红爱心 +"技能管理"+红爱心 +"#l\r\n";	

        text += "#b#e#L23#"+红爱心 +"更换职业"+红爱心 +"#l\t#L22#"+红爱心 +"技能全满"+红爱心 +"#l\t#L24#"+红爱心 +"转生系统"+红爱心 +"#l\r\n"; 


       }



  
        //text += " \r\n 以下是暂不支持的脚本：\r\n";
        //text += "#L18#矿石仓库#l\t #L19#道具抽奖#l \t #L20#音乐点播#l\t #l\r\n";
        //text += "#L21#战力系统#l\t #L24#一键转生#l\r\n";
        // 从083V2无法移植的脚本： 矿石仓库，道具抽奖，音乐点播，战力系统，

        if (cm.getPlayer().isGM()) {
            text += "\r\n";
            text += "\t\t\t\t#r=====以下内容仅GM可见=====\r\n";
            text += "#L62#GM商店#l \t #L63#整容集合#l \t #L73#技能绑定#l\r\n";
	text += "#L65#删除道具#l \t #L66#刷道具(代码)#l \t #L69#刷道具(名称)#l\r\n";
	text += "#L70#远程任务#l \t #L71#任意门#l \t #L72#召唤怪物#l\r\n";
	text += "#L75#GM怪物手册#l \t #L67#有状态脚本#l \t #L68#NextLevel脚本#l\r\n";
	text += "#L800#在线跟踪#l \t #L802#一键出售#l\r\n";
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
 //       case 0:
 //           cm.getPlayer().saveLocation("FREE_MARKET");
 //           cm.warp(910000000, "out00");
 //           break;
 // 脚本移植注意编码改为UTF-8
        case 0:
            openNpc("新人福利");
            break;
        case 1:
            openNpc("每日签到");
            break;
        case 2:
            openNpc("在线奖励");
            break;
        case 3:
            openNpc("万能传送");
            break;
        case 4:
            openNpc("皇家发型");
            break;
        case 5:
            openNpc("时尚点装");
            break;
        case 6:
            cm.openShopNPC(9201099); //便利商店
            cm.dispose();
            break;
        case 7:
            cm.dispose();
            cm.openShopNPC(2082014); //卷轴商店
            cm.dispose();
            break;
        case 8:
            cm.openShopNPC(9201101);  //大药商店
            cm.dispose();
            break;
        case 9:
            openNpc("快速转职");
            break;
        case 10:
            openNpc("三宠技能"); 
            break;
        case 11:
            openNpc("爆率一览");
            break;
        case 12:
            openNpc("血衣合成");
            break;
        case 13:
            openNpc("物品兑换");
            break;
        case 14:
            openNpc("金币兑换");
            break;
        case 15:
            openNpc("随身仓库");
            break;
        case 16:
            openNpc("删除道具");
            break;
        case 17:
            openNpc("益智答题");
            break;
 //       case 18:
 //           openNpc("矿石仓库");
 //           break;
 //       case 19:
 //           openNpc("道具抽奖");
 //           break;
  //       case 20:
  //           openNpc("音乐点播"); 
  //           break;
   //      case 21:
    //         openNpc("战力系统"); 
    //         break;
        case 22:
            openNpc("技能全满"); 
            break;
        case 23:
            openNpc("更换职业"); 
            break;
        case 24:
            openNpc("转生系统"); 
            break;

        case 25:
            openNpc("2006");  //明珠港怪物卡戒指NPC
            break;

        case 26:
            openNpc("矿物背包");  //矿卷背包
            break;
        case 501:
            openNpc("技能管理");  //矿卷背包
            break;
        case 502:
            openNpc("怪物手册");  //矿卷背包
            break;
        case 503:
            openNpc("套装制作升级");  //矿卷背包
            break;

        // GM功能--------------------------------------------------
        case 62:
            openNpc("GM商店"); 
            break;
        case 63:
            openNpc("Salon");
            break;
//        case 64:
//            openNpc("UI查询");
//           break;	
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
        case 69:
            openNpc("虚空索物");
            break;
        case 70:
            openNpc("远程任务");
            break;
        case 71:
            openNpc("任意门");
            break;
        case 72:
            openNpc("召唤怪物");
            break;
        case 73:
            openNpc("技能绑定");
            break;
        case 74:
            openNpc("快捷功能");
            break;
        case 75:
            openNpc("怪物手册1");
            break;
        case 799:
            openNpc("重置技能");
            break;
        case 800:
            openNpc("在线跟踪");
            break;
        case 801:
            openNpc("技能管理");
            break;
        case 802:
            openNpc("一键出售");
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