var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 小黄星 = "#fItem/Etc/0427/04270001/Icon9/0#";
var 正方箭头 = "#fUI/Basic/icon/arrow#"; // → 大箭头
var status = -1;
var ii = Packages.server.MapleItemInformationProvider.getInstance();
var 标题 = "时装属性转移";
var 道具 = 3991027;
var ronghe = 0;
var snew0="";
var snew4="UR级";
function start() {
		a = -1;
		action(1, 0, 0);
	}



function action(mode, type, secm.gainItem(id, str, dex, int_, luk, hp, mp, watk, matk, wdef, mdef, hb, mz, ty, yd); //id,力量,敏捷,智力,运气,血量,蓝量,攻击,魔攻,防御,魔防,回避,命中,跳跃,移动,时间lection)
	
	if (mode == 1)
		status++;
	else
		status--;
	if (status == 0) {
		var text = "#b时装属性转移#k\r\n";
		text = "\r\n"
		text += "              "+小黄星+小黄星+"#r "+标题+" #k"+小黄星+小黄星+"\r\n";
		text += "       ┏━━━━━━━━━━━━━━━━━┓\r\n";
		text += "       ┃ #b第一格时装属性继承到第二格时装上#k ┃\r\n";
		text += "       ┃ #b  只可用相同部位的时装进行转移  #k ┃\r\n";
		text += "       ┗━━━━━━━━━━━━━━━━━┛\r\n";
		text += "                #b#e#L0#时装属性转移#l#k";
		cm.sendSimple(text);
	} else if (status == 1) {
		var text = "你目前选择的是#r时装属性置换#k\r\n";
		text += "┏━━━━━━━━━━━━━━━━━━┓\r\n"
		text += "┃这项功能目前需要手续费用#r20000#d点券#k   ┃\r\n";
		text += "#r┃切勿低属性转高属性，出问题自己负责！┃\r\n";
		text += "┃"+圆形+" 继承后第一格时装会消失           ┃\r\n"
		text += "┗━━━━━━━━━━━━━━━━━━┛\r\n"
//		text += "#k#e点击下一步即可开始转移，一切问题自行承担。\r\n";
		cm.sendSimple(text);
	} else if (status == 2) {
		var ItemID = cm.getInventory(1).getItem(1).getItemId();
		var ItemID1 = cm.getInventory(1).getItem(2).getItemId();
		var 力量 = cm.getInventory(1).getItem(1).getStr();//cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
		var 敏捷 = cm.getInventory(1).getItem(1).getDex();
		var 智力 = cm.getInventory(1).getItem(1).getInt();
		var 运气 = cm.getInventory(1).getItem(1).getLuk();
		var 物攻 = cm.getInventory(1).getItem(1).getWatk();
		var 魔攻 = cm.getInventory(1).getItem(1).getMatk();
		var 物防 = cm.getInventory(1).getItem(1).getWdef();
		var 魔防 = cm.getInventory(1).getItem(1).getMdef();
		var 血量 = cm.getInventory(1).getItem(1).getHp();
		var 蓝量 = cm.getInventory(1).getItem(1).getMp();
		var 命中 = cm.getInventory(1).getItem(1).getAcc();
		var 回避 = cm.getInventory(1).getItem(1).getAvoid();
		var 手技 = cm.getInventory(1).getItem(1).getHands();
		var 潜能 = cm.getInventory(1).getItem(1).getOwner();
		var 已升级次数 = cm.getInventory(1).getItem(1).getLevel();
		if (cm.getPlayer().getCSPoints(1) < 20000) {
			cm.sendOk("点券不足 20000，无法转移~");
			cm.dispose();
		} else if (cm.canHold(ItemID, 1) == false) {
			cm.sendOk("背包空间不足，请确认。");
			cm.dispose();
		} else if (cm.getInventory(1).getItem(1) == null || cm.getInventory(1).getItem(2) == null) {
				cm.sendOk("请将你所要转移的装备放置在装备栏的第一、二格。");
				cm.dispose();
			} else if (cm.isCash(cm.getInventory(1).getItem(1).getItemId()) == false && cm.isCash(cm.getInventory(1).getItem(2).getItemId()) == false) {
				cm.sendOk("请保证装备栏第一、二格的装备为点装。");
				cm.dispose();
			} else if (parseInt(cm.getInventory(1).getItem(1).getItemId() / 10000) != parseInt(cm.getInventory(1).getItem(2).getItemId() / 10000)) {
				cm.sendOk("请保证第一、二格装备为同一类型装备。");
				cm.dispose();
			}else if (cm.getInventory(1).getItem(1).getExpiration() != -1) {
					cm.sendOk("#r抱歉，有时间限制的装备不能进行转移！");
					cm.dispose();	
		} else {
			var equip = ii.randomizeStats(ii.getEquipById(ItemID1)).copy();
			var type = ii.getEquipById(ItemID1);
			equip.setPosition(1);
			equip.setStr(力量);
			equip.setDex(敏捷);
			equip.setInt(智力);
			equip.setLuk(运气);
			equip.setHp(血量);
			equip.setMp(蓝量);
			equip.setWatk(物攻);
			equip.setMatk(魔攻);
			equip.setWdef(物防);
			equip.setMdef(魔防);
			equip.setAcc(命中);
			equip.setAvoid(回避);
			equip.setHands(手技);
			equip.setOwner(潜能);
			equip.setLevel(已升级次数);
		//	Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, true);
		//	Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, true);
	     	Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), equip, false);
			Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice(11, cm.getClient().getChannel(), "时装转移" + " : " + "恭喜"+ cm.getChar().getName() +"成功使用了时装转移功能转移了"+ cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).getOwner() +"时装，他/她的战斗力获得大幅提升！"));
		//	Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(),item,false);
			cm.gainItem(ItemID1, -1);
			cm.gainItem(ItemID, -1);
			cm.gainNX(-20000);
			cm.sendOk("属性转移成功~");
			cm.dispose();
			//	var ii = Packages.server.MapleItemInformationProvider.getInstance();
          //          var toDrop = ii.randomizeStats(ii.getEquipById(1122017)).copy();

		//			toDrop.setStr(888);	
		//			toDrop.setDex(888);	
		//			toDrop.setInt(888);		
		//			toDrop.setLuk(888);	
		//			toDrop.setWatk(888);	
		//			toDrop.setMatk(888);
		//			toDrop.setHp(5000);	
		//			toDrop.setMp(5000);
			//		toDrop.setWdef(100);
		//			toDrop.setMdef(100);
		//			 var 时间 = 60000 * 60 * 24 * 30;
		//			toDrop.setExpiration(java.lang.System.currentTimeMillis()+时间);
		//			Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), toDrop, false);
		}

	}

}




