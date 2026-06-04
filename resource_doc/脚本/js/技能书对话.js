/*
脚本：超强兑换脚本
作者：小米冒险岛longms
时间：2019.8.1

*/



var itemList = new Array(
    //需要物品ID 数量 兑换物品ID 数量  注意最后一组的标点符号
            Array(2290000,4000000,1),
			Array(2290001,4000000,1),
			Array(2290002,4000000,1),
			Array(2290003,4000000,1),
			Array(2290004,4000000,1),
			Array(2290005,4000000,1),
			Array(2290006,4000000,1),
			Array(2290007,4000000,1),
			Array(2290008,4000000,1),
			//Array(2290009,4000000,1),进阶开心
			Array(2290010,4000000,1),
			Array(2290011,4000000,1),//轻舞飞扬
			Array(2290012,4000000,1),
			Array(2290013,4000000,1),
			Array(2290014,4000000,1),
			Array(2290015,4000000,1),
			Array(2290016,4000000,1),
			//Array(2290017,4000000,1),葵花宝典 -  以50%
			Array(2290018,4000000,1),
			Array(2290019,4000000,1),
			Array(2290020,4000000,1),
			Array(2290021,4000000,1),
			Array(2290022,4000000,1),
			Array(2290023,4000000,1),//恶龙附身 -  以50%
			Array(2290024,4000000,1),
			Array(2290025,4000000,1),
			Array(2290026,4000000,1),
			Array(2290027,4000000,1),
			Array(2290028,4000000,1),
			Array(2290029,4000000,1),
			Array(2290030,4000000,1),
			//Array(2290031,4000000,1),连环爆破 -  以50%
			Array(2290032,4000000,1),
			Array(2290033,4000000,1),//链环闪电 -  以50%
			Array(2290034,4000000,1),
			Array(2290035,4000000,1),
			Array(2290036,4000000,1),
			Array(2290037,4000000,1),
			Array(2290038,4000000,1),
			Array(2290039,4000000,1),
			Array(2290040,4000000,1),
			//Array(2290041,4000000,1),天降落星 -  以50%
			Array(2290042,4000000,1),
			Array(2290043,4000000,1),
			Array(2290044,4000000,1),
			Array(2290045,4000000,1),
			Array(2290046,4000000,1),
			//Array(2290047,4000000,1),落霜冰破 -  以50%
			Array(2290048,4000000,1),
			//Array(2290049,4000000,1),圣光普照 -  以50%
			Array(2290050,4000000,1),
			Array(2290051,4000000,1),
			Array(2290052,4000000,1),
			Array(2290053,4000000,1),//火眼晶晶 -  以50%
			Array(2290054,4000000,1),
			Array(2290055,4000000,1),
			Array(2290056,4000000,1),
			//Array(2290057,4000000,1),神箭手 -  以50%
			Array(2290058,4000000,1),
			Array(2290059,4000000,1),
			Array(2290060,4000000,1),
			Array(2290061,4000000,1),//暴风箭雨 -  以50%
			Array(2290062,4000000,1),
			Array(2290063,4000000,1),
			Array(2290064,4000000,1),
			Array(2290065,4000000,1),
			Array(2290066,4000000,1),
			//Array(2290067,4000000,1),神弩手 -  以50%
			Array(2290068,4000000,1),
			Array(2290069,4000000,1),
			Array(2290070,4000000,1),
			Array(2290071,4000000,1),
			Array(2290072,4000000,1),
			Array(2290073,4000000,1),
			Array(2290074,4000000,1),
			Array(2290075,4000000,1),
			Array(2290076,4000000,1),
			Array(2290077,4000000,1),
			Array(2290078,4000000,1),
			Array(2290079,4000000,1),
			Array(2290080,4000000,1),
			Array(2290081,4000000,1),
			Array(2290082,4000000,1),
			Array(2290083,4000000,1),
			Array(2290084,4000000,1),
			Array(2290085,4000000,1),//三连环光击破 -  以50%
			Array(2290086,4000000,1),
			Array(2290087,4000000,1),
			Array(2290088,4000000,1),
			Array(2290089,4000000,1),
			Array(2290090,4000000,1),
			Array(2290091,4000000,1),//一出双击 -  以50%
			Array(2290092,4000000,1),
			Array(2290093,4000000,1),//暗杀 -  以50%
			Array(2290094,4000000,1),
			Array(2290095,4000000,1),
			//Array(2290096,4000000,1),冒险岛勇士 -  以70%
			Array(2290097,4000000,1),
			Array(2290098,4000000,1),
			Array(2290099,4000000,1),
			Array(2290100,4000000,1),
			Array(2290101,4000000,1),
			Array(2290102,4000000,1),
			Array(2290103,4000000,1),
			Array(2290104,4000000,1),
			Array(2290105,4000000,1),
			Array(2290106,4000000,1),
			Array(2290107,4000000,1),
			Array(2290108,4000000,1),
			Array(2290109,4000000,1),
			Array(2290110,4000000,1),
			Array(2290111,4000000,1),
			Array(2290112,4000000,1),
			Array(2290113,4000000,1),
			Array(2290114,4000000,1),
			Array(2290115,4000000,1),
			Array(2290116,4000000,1),
			Array(2290117,4000000,1),
			Array(2290118,4000000,1),
			Array(2290119,4000000,1),
			Array(2290120,4000000,1),
			Array(2290121,4000000,1),
			Array(2290122,4000000,1),
			Array(2290123,4000000,1),
			Array(2290124,4000000,1),
			//Array(2290125,4000000,1),冒险岛勇士30 - 以50%
			Array(2290126,4000000,1),
			Array(2290127,4000000,1),
			Array(2290128,4000000,1),
			Array(2290129,4000000,1),
			Array(2290130,4000000,1),
			Array(2290131,4000000,1),
			Array(2290132,4000000,1),
			Array(2290133,4000000,1),
			Array(2290134,4000000,1),
			Array(2290135,4000000,1)

	
	
	
);
var sels;
var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        var msg = "";
        msg += "             #e#d#v2044825#技能书兑换中心#v2044825##n\r\n\r\n";
        //msg += "#b兑换的品客斌属性点实际为能力值3 攻击力7点 实际是这个属性哦\r\n";//itemList[i][1]  1是  2是数量  3是
        for (var i = 0; i < itemList.length; i++) {
            msg += "#r#L" + i + "#";
            msg += "使用#i" + itemList[i][1] + ":#×" + itemList[i][2] + "兑换#i" + itemList[i][0] + ":##z" + itemList[i][0] + "# × " + itemList[i][2] + "#l\r\n";
        }
        cm.sendSimple("" + msg + "");
    } else if (status == 1) {
        sels = selection;
    /*if (cm.getInventory(1).isFull()) {
        cm.sendOk("请保证 #b装备栏#k 至少有2个位置。");
        cm.dispose();
        return;
    } else*/ if (cm.getInventory(2).isFull()) {
        cm.sendOk("请保证 #b消耗栏#k 至少有2个位置。");
         cm.dispose();
        return;
    /*} else if (cm.getInventory(3).isFull()) {
        cm.sendOk("请保证 #b设置栏#k 至少有2个位置。");
         cm.dispose();
        return;
    } else if (cm.getInventory(4).isFull()) {
        cm.sendOk("请保证 #b其他栏#k 至少有2个位置。");
         cm.dispose();
        return;
    } else if (cm.getInventory(5).isFull()) {
        cm.sendOk("请保证 #b特殊栏#k 至少有2个位置。");
         cm.dispose();
        return;
    } else if (cm.getPlayer().getName()==0) {//黑名单名字
        cm.sendOk("由于你被判断为交易行黑名单无法使用交易行");
         cm.dispose();
        return;
    }*/
}
        if (cm.haveItem(itemList[sels][1], itemList[sels][2])==false) {
			cm.sendNext("#b身上没有#r#i" + itemList[sels][1] + "##t" + itemList[sels][1] + "#x" + itemList[sels][2] + "");
            cm.dispose();
            return;
        }
        cm.sendYesNo("#b是否要兑换#r #i" + itemList[sels][0] + "# × " + itemList[sels][2] + "? \r\n");
    } else if (status == 2) {
        cm.gainItem(itemList[sels][1], -itemList[sels][2]);
        cm.gainItem(itemList[sels][0], itemList[sels][2]);
        cm.sendNext("#b已经兑换了 #i" + itemList[sels][0] + "# × "+itemList[sels][2]+"");
        cm.dispose();
    } else {
        cm.sendNext("#r发生错误: mode : " + mode + " status : " + status);
        cm.dispose();
    }
}