var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 挑战中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/10#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = "   "+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";

var fubenm = "高级BOSS"; //副本名称
var bossm = "貔 貅"; //BOSS名称
var bossid = 8880803; //BOSSID
var fubendt = 105200110; //副本地图
var fubenzb1 = 523; //副本坐标
var fubenzb2 = 455; //副本坐标
var bossjyxs = "4亿"; //BOSS经验显示
var bossjy = 400000000; //BOSS经验
var bossxlxs = "150亿"; //BOSS血量显示
var bossxl = 15000000000; //BOSS血量
var minLevel = 120; //最低等级
var maxLevel = 250; //最高等级
var minPartySize = 1; //最低人数
var maxPartySize = 1; //最高人数
var TZCS = 6; //限制次数
var zlyqxs = "200万"; //战力要求显示
var zlyq = 2000000; //战力要求
var inmesoxs = "10亿"; //入场金币显示
var inmeso = 1000000000; //入场金币
var xhwzid = 4000447; //消耗物品
var xhwzsl = 99; //消耗物品数量


var cywp = 2022520; //持有物品
var dlwup1 = 1003946; //掉落物品
var dlwup2 = 2049124; //掉落物品
var dlwup3 = 2049122; //掉落物品
var dlwup4 = 2340000; //掉落物品
var dlwup5 = 4310174; //掉落物品
var dlwup6 = 4310108; //掉落物品
var dlwup7 = 4000464; //掉落物品
var dlwup8 = 4170016; //掉落物品
var dlwup9 = 4170007; //掉落物品
var dlwup10 = 4310143; //掉落物品
function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
	    text += ""+dd+"\r\n\t\t\t"+挑战中心+"\r\n";
		
        text += "\t\t\t\t#b[" + fubenm + "]----#r[" + bossm + "]#k\r\n\r\n#g挑战要求：\r\n"
		text += "\t#k费用消耗: #r " + inmesoxs + " #k金币  物品消耗: #r " + xhwzsl + " #k个#b#v" + xhwzid + "#\r\n\r\n"
		text += "\t#k人数限制:#r " + minPartySize + " #b- #r" + maxPartySize + " #k人  等级限制:#r " + minLevel + " #b- #r" + maxLevel + " #k级 \r\n\r\n"
		text += "\t#k战力要求: #r" + zlyqxs + " #k  您当前战力: #r"+cm.getPlayer().getCombatPower()/10000+" #k万\r\n\r\n"

        text += "\t#r[" + bossm + "]#k血量: #r" + bossxlxs + "   #k基础经验值: #r" + bossjyxs + " \r\n\r\n"
		text += "\t#b[" + fubenm + "]#k每日限挑战: #r" + TZCS + " #k次  您已挑战: #r" + cm.getBossLog(""+fubenm+"") + " #k次\r\n\r\n"

		//text += "\t#k进入条件: 持有#r #v" + cywp + "#* 1 #k个；您拥有: #r #v" + cywp + "#* #c" + cywp + "# #k个\r\n\r\n"
        
        //text += "\t#k主要掉物: \r\n\t#v" + dlwup1 + "##v" + dlwup2 + "##v" + dlwup3 + "##v" + dlwup4 + "##v" + dlwup5 + "##v" + dlwup6 + "##v" + dlwup7 + "##v" + dlwup8 + "##v" + dlwup9 + "##v" + dlwup10 + "#\r\n\r\n"
		
        if (cm.getPlayer().getCombatPower() >= zlyq && cm.getLevel() >= minLevel) { 
        text += "\t\t#e#L1##b#v4031569#挑战#r[" + bossm + "]#v4031569##l#n\r\n\r\n"
        }else {
        text += "\t#r#e您得战力或者等级太低,还无法进入#n\r\n\r\n"
        }
        cm.sendSimple(text);
    } else if (selection == 1) {
			if (cm.getPlayerCount(fubendt) > 0) {//判定地图人数
			cm.sendOk("有人正在挑战，请稍等一会儿再来");
            cm.dispose();
            return;
            } else if (!cm.haveItem(cywp,1)){
            //cm.sendOk("你没有#v"+cywp+"#，无法进入");
			cm.sendOk("使用外挂或脚本直接封号封IP，请自重");
            cm.dispose();
            return;
            } else if (cm.getBossLog(""+fubenm+"") >= TZCS) {
            cm.sendOk("您今天已经挑战太多次了，请明天再来");
            cm.dispose();
            return;
            } else if (cm.getPlayer().getMeso() < inmeso) {
            cm.sendOk("你都没有足够的金币，还想白嫖我？");
            cm.dispose();
            return;
			} else if (cm.getPlayer().getParty()!= null){
				cm.sendOk("只能一个人进入，请先退出组队");
				cm.dispose();
              return;
            } else if (cm.haveItem(xhwzid,xhwzsl)){   //&&cm.getPlayer().getCSPoints(1)>=6666
			var map = cm.getMap(fubendt)
				map.killAllMonsters(false);
				cm.gainMeso(-inmeso); //扣除多少金币
				cm.gainItem(xhwzid,-xhwzsl);
			    cm.warp(fubendt);
				//cm.刷新地图();
				cm.setBossLog(""+fubenm+"");
				cm.喇叭(2, " 【"+cm.getName()+"】开始挑战【"+fubenm+"—"+bossm+"】！ 唢呐一响黄金万两！"); 
	            cm.召唤怪物(bossid, bossxl, bossjy, 1, fubendt, fubenzb1, fubenzb2); 
			    cm.dispose();
		    }else{
			cm.sendOk("您没有足够的#v"+xhwzid+"#,无法进入");
			cm.dispose();
		   }
        }
    }
}
