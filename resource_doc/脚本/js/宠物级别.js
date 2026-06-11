/*importPackage(java.lang);
importPackage(Packages.tools);
importPackage(Packages.client);
importPackage(Packages.server);
importPackage(Packages.tools.packet);*/
var effect = "#fUI/CashShop/CSEffect/effect/0#"
var 桃花 = "#fMap/MapHelper/weather/rose/4#";
var 银杏叶 = "#fMap/MapHelper/weather/maple/3#";
var 蓝色向右大箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var text = "";
var status = 0;
var 列表 = [
    //Array(属性名称, 概率, 力量 敏捷 智力 运气 
    ["普通", "20力量", "20敏捷", "20运气", "20智力", "20攻击", "20魔力", "0血量", "0蓝量", "0命中", "0回避", "0物防", "0魔防", "0移速", "0跳跃", 1000, 1], // "跳跃", 几率, 等级
    ["精品", "40力量", "40敏捷", "40运气", "40智力", "40攻击", "40魔力", "0血量", "0蓝量", "0命中", "0回避", "0物防", "0魔防", "0移速", "0跳跃", 900, 2],
    ["稀有", "60力量", "60敏捷", "60运气", "60智力", "60攻击", "60魔力", "0血量", "0蓝量", "0命中", "0回避", "0物防", "0魔防", "0移速", "0跳跃", 800, 3],
    ["神级", "80力量", "80敏捷", "80运气", "80智力", "80攻击", "80魔力", "0血量", "0蓝量", "0命中", "0回避", "0物防", "0魔防", "0移速", "0跳跃", 700, 4],
    ["传说", "100力量", "100敏捷", "100运气", "100智力", "100攻击", "100魔力", "0血量", "0蓝量", "0命中", "0回避", "0物防", "0魔防", "0移速", "0跳跃", 600, 5],
    ["史诗", "120力量", "120敏捷", "120运气", "120智力", "120攻击", "120魔力", "0血量", "0蓝量", "0命中", "0回避", "0物防", "0魔防", "0移速", "0跳跃", 500, 6],
    ["圣灵", "140力量", "140敏捷", "140运气", "140智力", "140攻击", "140魔力", "0血量", "0蓝量", "0命中", "0回避", "0物防", "0魔防", "0移速", "0跳跃", 400, 7],
    ["洪荒", "160力量", "160敏捷", "160运气", "160智力", "160攻击", "160魔力", "0血量", "0蓝量", "0命中", "0回避", "0物防", "0魔防", "0移速", "0跳跃", 300, 8],
    ["创世", "180力量", "180敏捷", "180运气", "180智力", "180攻击", "180魔力", "0血量", "0蓝量", "0命中", "0回避", "0物防", "0魔防", "0移速", "0跳跃", 200, 9],
	["混沌", "200力量", "200敏捷", "200运气", "200智力", "200攻击", "200魔力", "0血量", "0蓝量", "0命中", "0回避", "0物防", "0魔防", "0移速", "0跳跃", 50, 10]


];

var 砸蛋喇叭名称 = "【骑兽系统】";
var 砸蛋喇叭内容 =  " 一发就砸中了，真是幸运!";
var 砸蛋喇叭内容1 = " 一发就砸中了，真是幸运!";
//0=初始状态、1=锁定、8=不可交易、item.getFlag()=不变
var 分类
var 属性显示 = "";
var 力量 = 0;
var 敏捷 = 0;
var 运气 = 0;
var 智力 = 0;
var 攻击 = 0;
var 魔力 = 0;
var 血量 = 0;
var 蓝量 = 0;
var 命中 = 0;
var 回避 = 0;
var 物防 = 0;
var 魔防 = 0;
var 移速 = 0;
var 跳跃 = 0;


//自定义区域
var 获得宠物 = 1902007;  //1902001


var 消耗积分 = 4310144;
var 消耗积分数量 = 3000;
//修改参数在这里
var 收费列表 = {
    金币: 5000000, 点券: 10000, 抵用: 10000, 材料: [4001245, 2]

}

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
		if (mode == 1) {
			status++;
		} else {
			status--;
		}
		if (status == 0) {
            text = "\r\n#k┏" + 银杏叶 + "━━━━━━━━ #r[" + cm.开服名称() + "]#n#k ━━━━━━━━" + 银杏叶 + "┓#k\r\n";
            text += "说明:\r\n";
            text += "1.宠物级别(骑宠)分为#r#e普通 精品 稀有 神级 传说 史诗 圣灵 洪荒 创世 混沌#k#n 10个等级.\r\n";
			text += "2.宠物命格(鞍子)分为#r#e平凡 出色 优秀 杰出 完美 卓越 璀璨 超凡 天赐 神赐#k#n 10个等级.\r\n";
            text += "3.需要满足以下条件.\r\n";
			text += "4.砸蛋之前请先放生原有宠物.\r\n";
            text += "5.每次#b消耗点券：" + 收费列表.点券 + "、抵用：" + 收费列表.抵用 + "、金币：" + 收费列表.金币 + "#k.\r\n";
			text += "6.每次#b消耗道具：#z4001245# 2 个.\r\n";
            text += "\r\n";
            text += "\r\n             #d#L2#" + 蓝色向右大箭头 + "开始砸蛋[神宠]级别#l\r\n"
            text += "\r\n             #d#L1#" + 蓝色向右大箭头 + "开始砸蛋[鞍子]命格#l\r\n"
            text += "\r\n#k┗" + 银杏叶 + "━━━━━━━━━━━━━━━━━━━━━━" + 银杏叶 + "┛#k";
            cm.sendSimple(text);
 } else if (status == 1) {
                  if(selection == 1){
			      cm.dispose();
				  cm.openNpc(9900004,"宠物命格");
				  return;	
				  }else if(selection == 2){
				  if(cm.getInventory(1).isFull(0)){
				  cm.sendOk("对不起.你的背包已经满了./r/n");	
                  cm.dispose();
                  return;			  
                  }	
				  if (cm.getMeso() < 5000000) {
				  cm.sendOk("金币不足！需要 #r5000000k 金币。");
				  cm.dispose();
				  return;
				  }
				  if (cm.getPlayer().hasEquipped(获得宠物)) {
                    cm.sendOk("你已经装备了宠物,请先卸下.");
                    cm.dispose();
                    return;
                  }
                  if(物品数量(获得宠物)>= 1){
				  cm.sendOk("对不起,你已经有宠物 #b#v"+获得宠物+"##z"+获得宠物+"##k,请先放生.\r\n");
				  cm.dispose();
				  return;
				  }					  
				  if(cm.getPlayer().getCSPoints(1)< 收费列表.点券){
				  cm.sendOk("你的点券不足,请检查点券数量再来");
				  cm.dispose();
				  return;
				  }
				  if(cm.getPlayer().getCSPoints(2)< 收费列表.抵用){
				  cm.sendOk("你的抵用券不足,请检查抵用券数量再来");
				  cm.dispose();
				  return;
				  }
			/*	  if (!cm.haveItem(消耗积分,消耗积分数量)) {
                 cm.sendOk("#r#e你没有足够的#v"+ 消耗积分 +"#*"+ 消耗积分数量 +" #l#n");
				 cm.dispose();
				 return;
			      }
				  */
                 if (!cm.haveItem(4001245,2)) {
                 cm.sendOk("#r#e你没有足够的蛋！！！！#l#n");
				 cm.dispose();
				 return;
			      }
			     for (var c = 0; c < 收费列表.材料.length; c = c + 2) {
                cm.gainItem(收费列表.材料[c], -收费列表.材料[c + 1])
			      }
			//	cm.gainItem(消耗积分,-消耗积分数量)
			    cm.gainMeso(-收费列表.金币);
				cm.gainNX(-收费列表.点券);
				cm.gainDY(-收费列表.抵用);
                }else {
                    cm.dispose();
                    return;
                }
                var 随机数字 = Math.floor(Math.random() * 10 + 1);
                属性获取1(1);
                cm.sendOk("#r#e" + 属性显示 + "#l#n\r\砸蛋成功,去看看您砸出的神宠吧!");
                cm.dispose();
                return;
              } else {
                cm.dispose();
                return;
       } 
    }
}
function 属性获取1(rs) {
	     for (var j = 0; j < rs; j++) {
         var r = Math.floor(Math.random() * 1000);
         var list = [];
		 var item = cm.getEquip(获得宠物).copy()
        for (var n = 0; n < 列表['length']; n++) {
            if (列表[n][15] >= r) {
                list.push(列表[n]);
            }
        }
        if (list.length != 0) {
            var rmd = new java.util.Random();
            var len = rmd.nextInt(list.length);
            var str = list[len][0];
		    分类 = list[len][16]
	if(item.getHp()==0){
			力量 = 力量 + parseInt(list[len][1]);
            敏捷 = 敏捷 + parseInt(list[len][2]);
            运气 = 运气 + parseInt(list[len][3]);
            智力 = 智力 + parseInt(list[len][4]);
            攻击 = 攻击 + parseInt(list[len][5]);
            魔力 = 魔力 + parseInt(list[len][6]);
            血量 = 血量 + parseInt(list[len][7]);
            蓝量 = 蓝量 + parseInt(list[len][8]);
            命中 = 命中 + parseInt(list[len][9]);
            回避 = 回避 + parseInt(list[len][10]);
            物防 = 物防 + parseInt(list[len][11]);
            魔防 = 魔防 + parseInt(list[len][12]);
            移速 = 移速 + parseInt(list[len][13]);
            跳跃 = 跳跃 + parseInt(list[len][14]);
            属性显示 += '' + str + '';
		    item.setFlag(0);
			item.setStr(力量);
			item.setDex(敏捷);
			item.setInt(智力);
			item.setLuk(运气);
			item.setHp(血量);
			item.setMp(蓝量);
			item.setWatk(攻击);
			item.setMatk(魔力);
			item.setWdef(物防);
		    item.setMdef(魔防);
			item.setAvoid(回避);	
			item.setAcc(命中);
			item.setJump(跳跃);		
			item.setSpeed(移速);	
			item.setOwner(属性显示);
			cm.addFromDrop(item);
			cm.itemlaba(砸蛋喇叭名称, ""+ 属性显示+"级别- 被玩家:" + cm.getPlayer().getName() + 砸蛋喇叭内容1 + "", item, 16);		
        }
      }
   }
}

function 物品数量(itemid) {
	return cm.getPlayer().getItemQuantity(itemid, false);
}
