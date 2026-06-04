var 黄条上 = "#fUI/ChatBalloon.img/pet/25/head#";
var 黄条下 = "#fUI/ChatBalloon.img/pet/25/s#";
var 黄条下左 = "#fUI/ChatBalloon.img/pet/25/sw#";
var 黄条下右 = "#fUI/ChatBalloon.img/pet/25/se#";
var 黄条左 = "#fUI/ChatBalloon.img/pet/25/nw#";
var 黄条右 = "#fUI/ChatBalloon.img/pet/25/ne#";
var 五子棋 = "#fUI/ChatBalloon.img/miniroom/Omok#";
var 斜金币 = "#fUI/ChatBalloon.img/miniroom/PersonalShop#";
var 熊猫 = "#fUI/ChatBalloon.img/pet/1/nw#";
var 毛球 = "#fUI/ChatBalloon.img/pet/12/nw#";
var 金冠 = "#fUI/UIWindow.img/UserInfo/bossPetCrown#";
var 红蓝点 = "#fEffect/CharacterEff.img/1032054/0/0#";
var 蓝星 = "#fEffect/CharacterEff.img/1052203/1/0#";
var 红星 = "#fEffect/CharacterEff.img/1052203/2/0#";
var 大蓝星 = "#fEffect/CharacterEff.img/1022223/2/0#";
var 大红星 = "#fEffect/CharacterEff.img/1022223/1/0#";
var 蓝点 = "#fEffect/CharacterEff.img/1022223/6/0#";
var 红点 = "#fEffect/CharacterEff.img/1022223/7/0#";
var 窗口名称="绯红BOSS副本";

var status = 0;

var ttt = "#fUI/UIWindow.img/Quest/icon9/0#";
var xxx = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;

        if (status == 0) {
            var //textz = "\r\n勇士:#r#h ##k，此副本每日可召唤999次，召唤费用3000W。\r\n\r\n";
			textz = "\t#r#e   	     "+ 红星 + ""+ 大红星 + ""+ 红点 + "" + cm.开服名称() + ""+ 红蓝点 + ""+ 蓝点 + ""+ 大蓝星 + ""+ 蓝星 + "#k \r\n";
			textz += " "+ 黄条左 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 金冠 + "#b#e#r"+窗口名称+"#b#n"+ 金冠 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条右 + "#k  \r\n";
            textz += "  #b#L0#"+ 大红星 + "【#v4000487#召唤5只战士 - 血焰将军】#v3605016#"+ 大蓝星 + "#l\r\n\r\n";
            textz += "  #b#L1#"+ 大红星 + "【#v4000487#召唤5只法师 - 海之魔女】#v3605017#"+ 大蓝星 + "#l\r\n\r\n";
            textz += "  #b#L2#"+ 大红星 + "【#v4000487#召唤5只弓手 - 猎 魔 人】#v3605015#"+ 大蓝星 + "#l\r\n\r\n";
            textz += "  #r#L3#"+ 大红星 + "【#v4000487#召唤5只飞侠 - 暗影杀手】#v3605019#"+ 大蓝星 + "#l\r\n\r\n";
            textz += "  #r#L4#"+ 大红星 + "【#v4000487#召唤5只海盗 - 地狱船长】#v3605018#"+ 大蓝星 + "#l\r\n\r\n";
        //    textz += "\t\t \t\t    #r#L5#返回自由市场#l\r\n";
			textz += "\r\n "+ 黄条下左 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下右 + "#k  ";
            cm.sendSimple(textz);  
        } else {
			var monsters = cm.getMap().getAllMonstersThreadsafe(); // 获取怪物列表
			var monsterCount = monsters.size(); // 统计怪物数量
            var party = cm.getPlayer().getParty();
            if (party == null || party.getLeader().getId() != cm.getPlayer().getId()) {
                cm.sendOk("你不是队长。请你们队长来说话吧！");
                cm.dispose();
            } else if (cm.getLevel() < 150) {
                cm.sendOk("需要150级才能召唤.");
                cm.dispose();
            } else if (cm.getBossLog('绯红召唤次数') >= 2000) {
                cm.sendOk("你今天挑战次数超过2000次!");
                cm.dispose();    
           /* } else if (cm.getMeso() < 30000000) {
                cm.sendOk("你身上不足3000万金币!");
                cm.dispose();*/
			} else if (!cm.haveItem(4000487, 5)) {
				cm.sendOk("你没有5个#v" + 4000487 + "#，无法召唤");
				cm.dispose();
			} else if (monsterCount >= 5) {
				cm.sendOk("请把当前地图的怪物清理干净才能召唤,以免被抢！\r\n当前地图怪物数量：#r"+ monsterCount+"只#k！");
				cm.dispose();
            } else {
               // cm.gainMeso(-30000000);
			    cm.gainItem(4000487,-5);//
                cm.setBossLog('绯红召唤次数');
				cm.setBossLog('绯红召唤次数');
				cm.setBossLog('绯红召唤次数');
				cm.setBossLog('绯红召唤次数');
				cm.setBossLog('绯红召唤次数');
                var mobId = 0;
				var bossLogCount = cm.getPlayer().getBossLog('绯红召唤次数');  // 获取玩家的副本挑战次数
                switch (selection) {
                    case 0:
                        mobId = 9400421;
                        cm.喇叭(2,"[绯红副本]：玩家[" + cm.getPlayer().getName() + "] 开始挑战 血焰将军，今日已召唤：" + bossLogCount +"次！");
                        break;
                    case 1:
                        mobId = 9400420;
                        cm.喇叭(2,"[绯红副本]：玩家[" + cm.getPlayer().getName() + "] 开始挑战 海之魔女，今日已召唤：" + bossLogCount +"次！");
                        break;
                    case 2:
                        mobId = 9400422;
                        cm.喇叭(2,"[绯红副本]：玩家[" + cm.getPlayer().getName() + "] 开始挑战 猎 魔 人，今日已召唤：" + bossLogCount +"次！");
                        break;
                    case 3:
                        mobId = 9400423;
                        cm.喇叭(2,"[绯红副本]：玩家[" + cm.getPlayer().getName() + "] 开始挑战 暗影刺客，今日已召唤：" + bossLogCount +"次！");
                        break;
                    case 4:
                        mobId = 9400419;
                        cm.喇叭(2,"[绯红副本]：玩家[" + cm.getPlayer().getName() + "] 开始挑战 地狱船长，今日已召唤：" + bossLogCount +"次！");
                        break; 
					case 5: // 返回自由市场的选项
                        cm.warp(910000000); // 假设自由市场的地图ID为910000000
                        cm.dispose();  
                        return; // 结束函数执行
                }
           //     cm.spawnMobOnMap(mobId, 5, 1385, 276, 803001200, 500000000);
				cm.召唤怪物(mobId, 500000000, 15000000, 5, 803001200, 1385, 276);
				cm.getPlayer().dropMessage(5, "今日已召唤：" + bossLogCount +"次");
                cm.dispose();
            }
        }
    }
}