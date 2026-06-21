var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
var 爱心 = "#fEffect/CharacterEff/1032063/0/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#";
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#";
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#";
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#";
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 音符3 = "#fEffect/CharacterEff.img/1112949/2/0#";
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#"; 
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#"; 
var 任务简介 = "#fUI/UIWindow.img/Quest/summary#"; 
var 任务提示 = "#fUI/UIWindow.img/Quest/BtAlert/mouseOver/0#"; 
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#"; 
var 蓝色小喇叭 = "#fUI/CN_Chat.img/ChattingRoom/BtVolUp/0/mouseOver/0#";  
var 热点推荐 = "#fUI/CashShop.img/CSChar/BtCoordination/normal/0#";
var 铅笔 = "#fUI/GuildBBS.img/GuildBBS/BtReply/mouseOver/0#";
///////////////////////////////////////////
var ticket = [4031047];
//var cost = new Array(5000, 6000, 30000, 5000, 6000);
var cost = [5000];
//var mapNames = new Array("前往魔法森林", "前往玩具城", "前往神木村", "前往纳西沙漠");
var mapNames = ["前往魔法森林"];
//var mapName2 = new Array("前往魔法森林", "前往玩具城", "前往神木村", "前往纳西沙漠");
var mapName2 = ["前往魔法森林"];
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
            var where = "你好,我是负责售船票的,请问你想去哪里?";
             for (var i = 0; i < ticket.length; i++)
				where += "\r\n#L" + i + "##b" + mapNames[i] + "#k#l";
				cm.sendSimple(where);
        } else if (status == 1){
				select = selection;
				cm.sendYesNo("您是否确认#b"+mapNames[select]+"#k,这将花费#r"+cost[select]+"#k冒险币")   
        } else if (status == 2){      
			if (cm.getMeso() < cost[select] || !cm.canHold(ticket[select])){
				cm.sendOk("你确定你有 #b"+cost[select]+" 金币#k? 如果有的话,我劝您检查下身上其他栏位看是否有没有满了."); 
			} else {
				cm.gainMeso(-cost[select]);
                cm.gainItem(ticket[select],1);
			}     
				cm.dispose();			
        }
    }
}


