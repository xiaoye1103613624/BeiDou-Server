
var 全局经验倍率 = 10000; // 最终获得经验是  环数  * 全局经验倍率  =获得经验
var 全局金币倍率 = 10000; // 最终获得金币是  环数  * 全局金币倍率  =获得金币
var status = -1;
var 定义任务道具 = [4000054, 4000436, 4000082, 4001006, 4000457, 4000454, 4000452, 4000449, 4000447, 4000444, 4000394, 4000402, 4000127, 4000028, 4000027, 4000163, 4000166, 4000240, 4000236, 4000233, 4000241, 4000268, 4000272, 4000049, 4000050, 4000085, 4000439, 4000440, 4000172, 4000118, 4000434, 4000432, 4000433];
var 固定物品材料 = Array(
		// (物品id, 固定数量)

		//这里的物品id  必须在 定义任务道具  有这个物品才生效
		Array(4000040, 1),
		Array(4000176, 1),
		Array(4001010, 1),
		Array(4000235, 1),
		Array(4000243, 1),
		Array(4001241, 1),
		Array(4000175, 1)
		);
var 当前环数 = 0;
var 当前环数状态 = 0;
var 环数物品 = 0;
var 环数物品数量 = 0;
var 物品数量 = 0;
var 物品id = 0;
var 获得额外经验百分比 = 0;
var itemList = Array(//环数奖励
		//Array(物品id,1物品数量,隶属多少环),

		//物品id  0  代表额外个哦金币
		//物品id  1  代表额外经验  百分比
		//隶属领取等级对应上面的
        Array(4001126, 100, 5),
		Array(4000313, 100, 5),
		Array(4000038, 100, 5),
		Array(0, 10000000, 5), //给金币100w
		Array(1, 10, 5),
		//Array(2022672,1,50),
		//------------------------------------60
        Array(4001126, 200, 10),
		Array(4000313, 100, 10),
		Array(4310108, 1, 10),
		Array(0, 20000000, 10), //给金币80w
		Array(1, 20, 10),
		//Array(2022672,1,60),
		//------------------------------------70
        Array(4001126, 400, 15),
		Array(4000313, 100, 15),
		Array(2022511, 10, 15),
		Array(0, 30000000, 15), //给金币80w
		Array(1, 30, 15),
		//Array(2022672,1,70),
		//--------------------------------------80
        Array(4001126, 800, 20),
		Array(4000313, 100, 20),
		Array(2022519, 1, 20),
		Array(0, 40000000, 20), //给金币80w
		Array(1, 40, 20)
		//Array(2022672,1,80),
		/*Array(0, 1000000, 0), //给金币 1w
		Array(1, 5, 0), //给金币 20w
		//Array(2022672,1,0),

		Array(0, 10000, 10), //给金币 20w
		Array(1, 5, 10), //给金币 20w
		//Array(2022672,1,10),
		//----------------------------------20

		Array(0, 10000, 20), //给金币 30w
		Array(1, 5, 20),
		//Array(2022672,1,20),
		//----------------------------------30

		Array(0, 10000, 30), //给金币 40w
		Array(1, 5, 30),
		//Array(2022672,1,30),
		//-------------------------------------40

		Array(0, 20000, 40), //给金币 60w
		Array(1, 10, 40),
		//Array(2022672,1,40),
		//------------------------------------------50
		Array(4001126, 400, 50),
		Array(0, 30000, 50), //给金币80w
		Array(2001001, 100, 50),
		Array(2001002, 100, 50),
		Array(1, 20, 50),
		//Array(2022672,1,50),
		//------------------------------------60
		Array(4001126, 200, 60),
		Array(0, 50000, 60), //给金币 100w
		Array(2001001, 100, 60),
		Array(1, 10, 60),
		//Array(2022672,1,60),
		//------------------------------------70
		Array(4001126, 200, 70),
		Array(0, 10000, 70), //给金币 200w
		//Array(2340000,1  ,70),//祝福
		Array(2000006, 50, 70),
		Array(1, 10, 70),
		//Array(2022672,1,70),
		//--------------------------------------80
		Array(4001126, 200, 80),
		Array(0, 10000, 80), //给金币 200w
		//Array(2340000,1  ,80),//祝福
		Array(2049100, 1, 80), //混沌
		Array(1, 10, 80),
		//Array(2022672,1,80),
		//--------------------------------------90
		Array(4001126, 200, 90),
		Array(0, 15000, 90), //给金币 250w
		//Array(2340000,1  ,90),//祝福
		Array(2049100, 1, 90), //混沌
		Array(1, 10, 90),
		//Array(2022672,1,90),
		//----------------------------------------100
		Array(4001126, 888, 100),
		Array(0, 15000, 100), //给金币 300w
		//Array(2340000, 1, 100), //祝福
		Array(2049100, 1, 100), //混沌

		Array(1, 100, 100),
		//Array(2022672,1,100),
		//---------------------------------------110
		Array(4001126, 200, 110),
		Array(0, 15000, 110), //给金币 300w
		//Array(2340000, 1, 110), //祝福
		Array(2049100, 1, 110), //混沌
		Array(1, 10, 110),
		//Array(2022672,1,110),
		//----------------------------------------120
		Array(4001126, 200, 120),
		Array(0, 15000, 120), //给金币 300w
		//Array(2340000,2  ,120),//祝福
		Array(2049100, 2, 120), //混沌
		Array(1, 10, 120),
		//----------------------------------------130
		Array(4001126, 200, 130),
		Array(0, 15000, 130), //给金币 300w
		//Array(2340000, 1, 130), //祝福
		Array(2049100, 3, 130), //混沌
		Array(1, 10, 130),
		//----------------------------------------------140
		Array(4001126, 200, 140),
		Array(0, 15000, 140), //给金币 300w
		//Array(2340000, 1, 140), //祝福
		Array(2049100, 3, 140), //混沌
		Array(1, 10, 140),
		//--------------------------------------------------150
		Array(4001126, 200, 150),
		Array(0, 30000, 150), //给金币 300w
		//Array(2340000, 1, 150), //祝福
		Array(2049100, 3, 150), //混沌
		Array(1, 10, 150),
		//-----------------------------------------------------160
		Array(4001126, 200, 160),
		Array(0, 30000, 160), //给金币 300w
		//Array(2340000, 1, 160), //祝福
		Array(2049100, 3, 160), //混沌
		Array(1, 10, 160),
		//-------------------------------------------------170
		Array(4001126, 200, 170),
		Array(0, 30000, 170), //给金币 300w
		Array(2049100, 3, 170), //混沌
		Array(1, 10, 170),
		//-----------------------------------------------180
		Array(4001126, 200, 180),
		Array(0, 30000, 180), //给金币 300w
		//Array(2340000, 1, 180), //祝福
		Array(2049100, 3, 180), //混沌
		Array(1, 10, 180),
		//-----------------------------------------------190
		Array(4001126, 200, 190),
		Array(0, 30000, 190), //给金币 300w
		//Array(2340000, 1, 190), //祝福
		Array(2049100, 3, 190), //混沌
		Array(1, 10, 190),
		//-------------------------------------------------200
		Array(4001126, 1000, 200),
		Array(2340000, 1, 200), //祝福
		Array(1, 100, 200),
		Array(0, 30000, 200), //给金币 300w
		Array(2022465, 1, 200), //祝福
		Array(2049100, 3, 200) //混沌*/
	);

function start() {
	action(1, 0, 0);
}
function 获取任务信息() {
	物品id = Number(cm.getQuestRecord(444440).getCustomData()); //读取任务物品
	物品数量 = Number(cm.getQuestRecord(444441).getCustomData()); //读取任务个数
	//cm.getPlayer().dropMessage(任务物品id + "您已经失去重返本战场资格" + 任务物品数量);

}

function 接任务(a, b) {

	cm.getQuestRecord(444440).setCustomData(a); //记录任务物品
	cm.getQuestRecord(444441).setCustomData(b); //记录任务个数
	 cm.getPlayer().set输出Log("跑环状态",1,1);
	cm.getPlayer().saveToDB(false, false);

}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		if (status >= 0) {
			cm.dispose();
			return;
		}
		status--;
	}
	if (status == 0) {
		当前环数 = cm.getPlayer().getBossLog("本周跑环")+1;
		当前环数状态 = cm.getPlayer(). get输出Log("跑环状态");
		var selStr = "\t\t\t\t#r#e每日跑环任务中心#n\r\n";
		if (当前环数 >= 21) {
			cm.sendOk("#b任务上限,晚上12点刷新");
			cm.dispose();
			return;
		}
		if (cm.getLevel() > 255) { //判断等级
			cm.sendOk("#b你当前等级已经超过了255级无法在接任务");
			cm.dispose();
			return;
		}
		
		if (cm.getLevel() < 30) { //判断等级
			cm.sendOk("#b你30级才可以跑");
			cm.dispose();
			return;
		}

		if (当前环数状态 <= 0) { //未接任务

			物品数量 = Math.floor(Math.random() * 50) + 50;
			物品id = 定义任务道具[Math.floor(Math.random() * 定义任务道具.length)];

			for (var jqa = 0; jqa < 固定物品材料.length; jqa++) {
				if (固定物品材料[jqa][0] == 物品id) {
					物品数量 = 固定物品材料[jqa][1];

				}

			}

			selStr += "#r成功接取 第 " + 当前环数 + " 环 任务#l#k\r\n\r\n"
			selStr += "需要：\r\n"
			selStr += "#v" + 物品id + "# X " + 物品数量 + "\r\n"
			接任务(物品id, 物品数量)
			selStr += "奖励：\r\n"
			selStr += "固定经验：" + 全局经验倍率 * 当前环数 + "\r\n"
			selStr += "固定金币：" + 全局金币倍率 * 当前环数 + "\r\n"
			for (var j = 0; j < itemList.length; j++) {

				if (itemList[j][2] == 当前环数) { //判断物品标识

					if (itemList[j][0] == 0) { //金币
						selStr += "#r额外金币：" + itemList[j][1] + "\r\n"
					} else if (itemList[j][0] == 1) {
						getlevelExp = Packages.constants.GameConstants.getExpNeededForLevel(cm.getPlayer().getLevel());
						获得额外经验百分比 = parseInt(getlevelExp/20000) * itemList[j][1];
						selStr += "#r额外百分比经验：" + 获得额外经验百分比 + "\r\n"

					} else { //不是金币
						selStr += "#v" + itemList[j][0] + "# X " + itemList[j][1] + "\r\n"
					}

				}

			}
		} else { //接了
			获取任务信息();

			if (物品id == 0) {
				/* for (var jb = 0; jb < 当前环数状态; jb++) {
				cm.killbossloga("跑环状态");
				} */

				cm.sendOk("#b任务异常！,请重新接任务吧b");
				cm.dispose();
				return;
			}

			selStr += "#r#L" + 当前环数 + "#提交 第 " + 当前环数 + " 环#l    \r\n\r\n"
			selStr += "--------------------------------------------------\r\n需要：#v" + 物品id + "#  [#r" + cm.getPlayer().getItemQuantity(物品id, false) + "#k/" + 物品数量 + "]\r\n--------------------------------------------------\r\n"
			selStr += "奖励：\r\n"
			selStr += "固定经验：" + 全局经验倍率 * 当前环数 + "\r\n"
			selStr += "固定金币：" + 全局金币倍率 * 当前环数 + "\r\n"
			for (var j = 0; j < itemList.length; j++) {

				if (itemList[j][2] == 当前环数) { //判断物品标识

					if (itemList[j][0] == 0) { //金币
						selStr += "#r额外金币：" + itemList[j][1] + "\r\n"
					} else if (itemList[j][0] == 1) {
						getlevelExp = Packages.constants.GameConstants.getExpNeededForLevel(cm.getPlayer().getLevel());
						获得额外经验百分比 = parseInt(getlevelExp/20000) * itemList[j][1];
						selStr += "#r额外百分比经验：" + 获得额外经验百分比 + "\r\n"

					} else { //不是金币
						selStr += "#v" + itemList[j][0] + "# X " + itemList[j][1] + "\r\n"
					}

				}

			}
			//selStr += "#b#L99999995#查该环物品出处#l\r\n";
			//selStr += "#b#L99999999#查看所有环奖励#l\r\n";
			selStr += "#b#L99999999#查看所有环奖励#l     #b#L99999997#重置环任务<500枫叶/次>#l\r\n";
			//selStr += "#b#L99999996#跳过环任务#l\r\n";
			
		}
		cm.sendSimple(selStr);

	} else if (status == 1) {
		if (selection == 99999999) {
			var txt = "";
			for (var j = 0; j < itemList.length; j++) {

				if (itemList[j][0] == 0) { //金币
					txt += itemList[j][2] + "环: #r金币：" + itemList[j][1] + " #k\r\n"
				} else if (itemList[j][0] == 1) {

					txt += itemList[j][2] + "环: #r经验：" + itemList[j][1] + "% #k\r\n\r\n"
				} else { //不是金币
					txt += itemList[j][2] + "环: #r#v" + itemList[j][0] + "##z" + itemList[j][0] + "# * " + itemList[j][1] + " #k\r\n"
				}

			}

			cm.sendOk(txt);
			cm.dispose();
			return;
		} else if (selection == 99999995) {
			cm.sendOk(cm.checkDropper(物品id));
			cm.dispose();
			return;
		} else if (selection == 99999998) {

			var txt2 = "";
			for (var jg = 0; jg < 定义任务道具.length; jg++) {

				txt2 += "#v" + 定义任务道具[jg] + "##z" + 定义任务道具[jg] + "#  "

			}

			cm.sendOk(txt2);
			cm.dispose();
			return;
		} else if (selection == 99999997) {
			if (cm.haveItem(4001126, 500) == false) {
				cm.sendOk("枫叶不足500无法重接取");
				cm.dispose();
				return;
			} else {
				 cm.getPlayer().set输出Log("跑环状态",1,-1);
				//cm.getPlayer().setBossLog("本周跑环");
				cm.gainItem(4001126, -500)
				cm.sendOk("重置任务成功");
				cm.dispose();
				return;
			}
		} else if (selection == 99999996) {
			if (cm.haveItem(4001126, 1000) == false) {
				cm.sendOk("枫叶不足1000无法重接取");
				cm.dispose();
				return;
			} else {
				 cm.getPlayer().set输出Log("跑环状态",1,-1);
				//cm.killbossloga("跑环状态");
				cm.getPlayer().setBossLog("本周跑环");
				cm.gainItem(4001126, -1000)
				cm.sendOk("重置任务成功");
				cm.dispose();
				return;
			}
		} else {

			当前环数 = selection;
			if (cm.getInventory(1).isFull(0)) { //判断第一个也就是装备栏的装备栏是否有一个空格
				cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法兑换.");
				cm.dispose();
			} else if (cm.getInventory(2).isFull(0)) { //判断第二个也就是消耗栏的装备栏是否有一个空格
				cm.sendOk("#b请保证消耗栏位至少有1个空格,否则无法兑换.");
				cm.dispose();
			} else if (cm.getInventory(3).isFull(0)) { //判断第三个也就是设置栏的装备栏是否有一个空格
				cm.sendOk("#b请保证设置栏位至少有1个空格,否则无法兑换.");
				cm.dispose();
			} else if (cm.getInventory(4).isFull(0)) { //判断第四个也就是其它栏的装备栏是否有一个空格
				cm.sendOk("#b请保证其它栏位至少有1个空格,否则无法兑换.");
				cm.dispose();
			} else if (cm.getInventory(5).isFull(0)) { //判断第五个也就是现金栏的装备栏是否有一个空格
				cm.sendOk("#b请保证现金栏位至少有1个空格,否则无法兑换.");
				cm.dispose();

			} else if (cm.haveItem(物品id, 物品数量) == false) { //判断是否领过
				cm.sendOk("#b物品不足！");
				cm.dispose();
			} else {
				cm.gainItem(物品id, -物品数量);
				cm.gainMeso(当前环数 * 全局金币倍率); //给金币全局经验倍率
				cm.gainExp(当前环数 * 全局经验倍率); //给经验

				for (var j = 0; j < itemList.length; j++) {

					if (itemList[j][2] == 当前环数) { //判断物品标识

						if (itemList[j][0] == 0) { //金币

							cm.gainMeso(itemList[j][1]); //给金币
						} else if (itemList[j][0] == 1) {

							cm.gainExp(获得额外经验百分比); //给经验
						} else { //不是金币

							cm.gainItem(itemList[j][0], itemList[j][1]);
						}

					}

				}
				//cm.killbossloga("跑环状态"); //删除数据库onetimelog表 符合自身的表
				 cm.getPlayer().set输出Log("跑环状态",1,-1);
				cm.getPlayer().setBossLog("本周跑环");
				cm.sendOk("#b成功领取！");
				cm.dispose();

			}

		}
	}
}
