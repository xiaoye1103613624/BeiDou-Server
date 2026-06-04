/* ==================
脚本类型: 兑换正义混沌卷轴, 不让玩家输入几次, 直接对话1/5/max次
脚本作者：野原广志
联系方式qq：279934747 承接079-204脚本
=====================
*/

//UI素材导入
var 皇冠白 ="#fUI/GuildMark/Mark/Etc/00009004/16#";
var 获取 = "#fUI/UIWindow.img/QuestIcon/4/0#"
var unchecked = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var checked = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var 香水 = "#fUI/GuildMark/Mark/Pattern/00004008/15#";
var 金币图标 = "#fUI/UIWindow/QuestIcon/7/0#";
var xxx = "#fUI/UIWindow.img/Quest/icon8/0#";

//变量定义
var reqMeso = 1000000;//单次合成需求的金币
var item = 4170016;//需要合成什么? 这里直接定义成了中国心
var status = -1;
var exchangeNum = 0;//玩家兑换的个数
/*/
单次合成需要的材料数组: 物品id, 物品数量
如果需要增加别的需求材料,直接按照 "[物品id,物品数量]," 这样的格式在数组最后增加
*/
var reqItemList = [
  [4000038, 50],
  [4000313, 50],
  [4001126, 300],

];


function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	}
	else if (mode == 0) {
		status--;
	}
	else {
		cm.dispose();
		return;
	}
	if (status == 0) {
		var text = "";
		text += "你选择的是#b#e#v" + item + "##z" + item + "##k#n兑换\r\n";
		text += "#k#n---------------------------------------------------\r\n"
		text += "#r每兑换一次需要以下物品:\r\n";
		for (var i = 0; i < reqItemList.length; i++) {
			text += "#k#n" + 香水 + " #i" + reqItemList[i][0] + "# #z" + reqItemList[i][0] + "#  [#r" + cm.getPlayer().getItemQuantity(reqItemList[i][0], false) + "#k/" + reqItemList[i][1] + "] " + (!cm.haveItem(reqItemList[i][0], reqItemList[i][1]) ? ("#r未满足 " + unchecked + "#k") : ("#g已满足 " + checked + "#k")) + "\r\n";
		}
		text += "\r\n" + 金币图标  + " * 100W" + " [#r" + cm.getMeso() + "#k/" + reqMeso + "] " + (cm.getMeso() < reqMeso ? ("#r未满足 " + unchecked + "#k") : ("#g已满足 " + checked + "#k")) + "\r\n";
		text += "\r\n" + "根据你背包内材料与金币,本次最多可以兑换 #r#e" + maxCanBuy(reqItemList) + "#k#n 个 #v" + item + "#\r\n";
		text += "#k#n---------------------------------------------------\r\n\r\n"
		text += " #L0#" + xxx + "兑换 #d#e1#k#n 个#l #L1#" + xxx + "兑换 #d#e10#k#n 个#l #L2#" + xxx + "兑换 #b#e100#k#n 个#k#n#l\r\n"
		cm.sendSimple(text);
	}
	else if (status == 1) {
		var selStr = "";
		se10 = selection;
		if (selection == 0) {
			exchangeNum = 1;
		}
		else if (selection == 1) {
			exchangeNum = 10;
		}
		else if (selection == 2) {
			exchangeNum = 100;
		}
		else {
			cm.sendOk("出错拉! 请联系GM修改");
			cm.dispose();
			return;
		}
		//无法持有(背包空间不足)
		if(!cm.canHold(item, exchangeNum)) {
			cm.sendNext("#r背包空间不足, 请先清理背包后再来!");
			cm.dispose();
			return;
		}
		//遍历数组, 根据兑换个数判断背包内需求材料的数量是否足够
		for(var i = 0; i < reqItemList.length; i++) {
			if (!cm.haveItem(reqItemList[i][0],reqItemList[i][1] * exchangeNum)) {
				cm.sendOk("背包内 #v" + reqItemList[i][0] + "# 不足#b#e " + reqItemList[i][1] * exchangeNum + "#k#n 个!");
				cm.dispose();
				return;
			}
		}

		//遍历数组, 根据兑换个数扣除对应需求材料, 获取合成道具
		for(var j = 0; j < reqItemList.length; j++) {
			cm.gainItem(reqItemList[j][0], (-reqItemList[j][1] * exchangeNum));
		}
		//扣除金币
		cm.gainMeso(-reqMeso * exchangeNum)
		cm.gainItem(item,exchangeNum);
		selStr += "#k#n" + 获取 + " 恭喜你, 成功合成了#b#e" + exchangeNum + "#k#n个 #v" + item + "#, 请检查背包!";
		cm.sendOk(selStr);
		cm.worldMessage(6,"[系统公告] 玩家 [" + cm.getName() + "] 在合成中心兑换了" + exchangeNum + "个" + cm.getItemName(item));
		cm.dispose();
	}
}

/*
*定义一个函数, 判断最大能兑换几个物品
*@param {*} arr 需求材料数组
*/
function maxCanBuy (arr){
	var max = Infinity; //先把最大兑换数量赋值为无穷大, 在下面每一次遍历都取最小值, 从而达到效果
	var playerMaterial = [];
	//根据传入的需求材料数组来获取角色身上的道具数量
	for (var i = 0; i < arr.length; i++){
		playerMaterial.push([arr[i][0],cm.getPlayer().getItemQuantity(arr[i][0], false)]);
	}
	//再次遍历传入的数组, 来判断最大兑换个数, 先判断材料
	for (var j = 0; j < arr.length; j++) {
		if(playerMaterial[j][1] >= arr[j][1]) {
			var quantityBasedOnMaterial = Math.floor(playerMaterial[j][1]/arr[j][1]);//每一次循环先根据当前需求数量来判断最大兑换
			max = Math.min(max, quantityBasedOnMaterial);
		}
		else {
			max = 0;
			break;//任何一个材料不足, 都直接跳出循环, 返回0
		}
	}
	var quantityBasedOnMeso = Math.floor(cm.getMeso() / reqMeso);
	max = Math.min(max, quantityBasedOnMeso);
	return max;
}