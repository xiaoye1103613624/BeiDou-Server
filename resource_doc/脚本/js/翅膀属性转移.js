var 可锻造的翅膀 = [1102453, 1102487, 1102546, 1102587, 1102588, 1102589, 1102697, 1102698, 1102709, 1102723, 1102724, 1102779, 1102809, 1102811, 1102812, 1102818, 1102624];
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.sendOk("欢迎您的光临！");
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.sendOk("感谢您的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            if (cm.getInventory(1).getItem(1) == null || (cm.getInventory(1).getItem(2) == null) {
                cm.sendOk('抱歉,背包的第一个格子 或者 第二个格子,没有放入翅膀,请放入');
                cm.dispose();
                return;
            }
                var item1 = cm.getInventory(1).getItem(1).getItemId();
                var item2 = cm.getInventory(1).getItem(2).getItemId();
                var itemId1 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
                var itemId2 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(2).copy();
                var text = "";
                text += "如果翅膀的外观不好看,你可以转移到另外一个翅膀中\r\n\r\n";
                text += "本次转移,会把 背包第一个格子的翅膀属性,转移到 第二个格子中\r\n\r\n";
                text += "你是否想把#v" + item1 + "# 的属性,转移到#v" + item2 + "#\r\n\r\n";
                cm.sendYesNo(text);

        } else if (status == 1) {
            var item1 = cm.getInventory(1).getItem(1).getItemId();
            var item2 = cm.getInventory(1).getItem(2).getItemId();
            var itemId1 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
            var itemId2 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(2).copy();

            var canDuanZao = false;
            for (var i = 0, len = 可锻造的翅膀.length; i < len; i++) {
                if (可锻造的翅膀[i] == 装备代码) {
                    canDuanZao = true;
                }
            }
            if (canDuanZao == false) {
                var hua = "" + 装备信息 + " 不是可锻造的物品,只有以下的物品才能锻造\r\n\r\n";
                for (var b = 0, len = 可锻造的翅膀.length; b < len; b++) {
                    hua += "#v" + 可锻造的翅膀[b] + "#";
                }
                cm.说明文字(hua);
                cm.dispose();
                return;
            }
			
        }
    }
}

var ca = java.util.Calendar.getInstance();
var 年 = ca.get(java.util.Calendar.YEAR); //获得年份
var 月 = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var 日 = ca.get(java.util.Calendar.DATE); //获取日
var 时 = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var 分 = ca.get(java.util.Calendar.MINUTE); //获得分钟
var 秒 = ca.get(java.util.Calendar.SECOND); //获得秒
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 蓝色角点 = "#fUI/UIWindow.img/PvP/S croll/enabled/next2#";
var 大心 = "#fEffect/CharacterEff/1051295/0/0#";
var 琴符 = "#fEffect/CharacterEff/1003252/0/0#";
var 小雪花 = "#fEffect/CharacterEff/1003393/0/0#";
var 音符 = "#fEffect/CharacterEff/1032063/0/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 黑水晶 = "#v4021008#";
var 红色双箭头 = "#fUI/UIWindow.img/Quest/icon9/0#";
var 蓝色双箭头 = "#fUI/UIWindow.img/Quest/icon8/0#";
var 选择道具图标 = "#fUI/UIWindow.img/QuestIcon/3/0#";
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 铅笔图标 = "#fUI/UIWindow.img/PvP/btWrite/mouseOver/0#";
var 警报灯 = "#fUI/StatusBar/BtClaim/normal/0#";
var H字母 = "#fUI/CashShop/CSEffect/effect/1#";
var 金币图标 = "#fUI/UIWindow.img/Item/BtCoin/normal/0#";
var 文字 = "#fUI/UIWindow.img/Channel/world/100#";
var 横条 = "#fUI/UIWindow/Quest/TimeQuest/TimeBar/backgrnd#";

var 任务简述 = "#fUI/UIWindow/Quest/summary#";
var 获奖 = "#fUI/UIWindow/Quest/reward#";

var 正在进行中 = "#fUI/UIWindow/Quest/Tab/enabled/1#";
var 完成 = "#fUI/UIWindow/Quest/Tab/enabled/2#";
var 正在进行中蓝 = "#fUI/UIWindow/MonsterCarnival/icon1#";
var 可以开始 = "#fUI/UIWindow/Quest/Tab/enabled/0#";
var 没有可以开始的任务 = "#fUI/UIWindow/Quest/notice0#";
var add = "#fEffect/CharacterEff/1112903/0/0#";
//text += "" + add + add + add + add + add + add + add + add + add + add + add + add + "装备鉴定" + add + add + add + add + add + add + add + add + add + add + add + "\r\n"
var 兔子1 = "#fUI/GuildMark/Mark/Animal/00002013/4#";
var 兔子2 = "#fUI/GuildMark/Mark/Animal/00002013/10#";
var 兔子3 = "#fUI/GuildMark/Mark/Animal/00002013/14#";
//美化
var 蓝色圈圈 = "#fEffect/CharacterEff/1082588/3/0#";
var 金枫叶 = "#fMap/MapHelper/weather/maple/2#";
var 红枫叶 = "#fMap/MapHelper/weather/maple/1#";
var 小天使 = "#fItem/Cash/0501/05010006/effect/default/2#";
var 红心 = "#fEffect/CharacterEff/1082588/0/0#";
var z7 = "#fUI/GuildMark/Mark/Etc/00009023/10#"; //皇冠
var 花草 = "#fEffect/SetEff/208/effect/walk2/4#";
var 花草1 = "#fEffect/SetEff/208/effect/walk2/3#";

var random1 = java.lang.Math.floor(Math.random() * 10000 + 1);
var random2 = java.lang.Math.floor(Math.random() * 10000 + 1);
var 拍卖卷 = 2028050;
var 摆摊卡 = 5030000;
var 金币袋子 = 4031138;
var 潜能 = [1, 2, 3, 4, 5, 6, 7, 8, 9];
var 随机 = Math.floor(Math.random() * 潜能.length);

var MaplePacketCreator = Packages.tools.MaplePacketCreator;
var World = Packages.handling.world.World;
