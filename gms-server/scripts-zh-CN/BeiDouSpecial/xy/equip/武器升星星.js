/*
 079 085脚本
QQ:870074996
 */
var nx;
var status = 0;
var 装备力量,装备敏捷,装备运气,装备智力,装备血量,装备蓝量,装备攻击,装备魔攻,装备物防,装备魔防,装备回避,装备命中,装备跳跃,装备移动,装备等级,装备次数,强化等级,材料总数1,材料总数2,材料总数3,金币总数;
var 强化 = [
//排序 名字 等级 点券币  元宝 金币 概率 防爆
// [1,"","1☆", 0,         0, 0,1000],
[1,"","1☆", 1,         1, 200000,10000,0],
[2,"1☆", "2☆",1,   2, 300000,10000,0],
[3,"2☆", "3☆",1,   3, 400000,10000,0],
[4,"3☆", "4☆",1,   4, 500000,10000,0],
[5,"4☆", "5☆",2,   5, 8000,10000,0],
[6,"5☆", "6☆",2,   6, 120000,10000,1],
[7,"6☆", "7☆",2,   7, 15000,10000,1],
[8,"7☆", "8☆",4,   10, 20000,10000,2],
[9,"8☆", "9☆",6,   15, 25000,10000,3],
[10,"9☆", "10☆",1,  2, 30000,10000,4],
[11,"10☆", "11☆",1, 3, 500000,10000,1],
[12,"11☆", "12☆",2, 4, 80000,10000,1],
[13,"12☆", "13☆",4, 5, 200000,10000,1],
[14,"13☆", "14☆",2, 10, 500000,10000,2],
[15,"14☆", "15☆",5, 20, 1000000,10000,1],
]
var 强化材料1 = 4310108;
var 强化材料2 = 2531000;


function start() {
		var ii = Packages.server.MapleItemInformationProvider.getInstance();
	 if(cm.getInventory(1).getItem(1)==null){
		cm.sendOk("对不起,你的装备栏第一个格子里面没有武器");
		cm.dispose();	
		return;	
	}else if (cm.getInventory(1).getItem(1).getOwner()=="15☆") {
          cm.sendOk("你的装备第一格已经达到上限！");
          cm.dispose();
		  return;
	} else if (cm.getInventory(1).getItem(1).getItemId() < 1300000 || cm.getInventory(1).getItem(1).getItemId() > 1799999) {
          cm.sendOk("你的装备第一格并不是武器！");
          cm.dispose();
		  return;
	}else if (ii.isCash(cm.getInventory(1).getItem(1).getItemId()) == true) {
                    cm.sendOk("商城点卷物品暂不支持.");
                    cm.dispose();
					return;
		} else {
		status = -1;
		action(1, 0, 0);
	}
}

function action(mode, i, selection) {
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
			var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
			var 装备ID = cm.getInventory(1).getItem(1).getItemId();
			var 装备名称 = item.getOwner();
			
			装备攻击=item.getWatk();
            装备魔攻=item.getMatk();
			// if (装备攻击<=50 || 装备魔攻<=50){
				装备等级 = 1;
			// }
			// if (装备攻击>50 && 装备攻击<=100 || 装备魔攻>50 && 装备魔攻<=100){
				// 装备等级 = 2;
			// }
			// if (装备攻击>100 && 装备攻击<=150 || 装备魔攻>100 && 装备魔攻<=150){
				// 装备等级 = 3;
			// }
			// if (装备攻击>150 && 装备攻击<=200 || 装备魔攻>150 && 装备魔攻<=200){
				// 装备等级 = 4;
			// }
			// if (装备攻击>200 && 装备攻击<=999 || 装备魔攻>200 && 装备魔攻<=999){
				// 装备等级 = 5;
			// }
			var textz = "当前所选强化装备：“#b#i" + 装备ID + "##t" + 装备ID + "##k”最高可强化15☆\r\n\r\n";
			for(var i = 0 ; 装备名称 != 强化[i][1] ;i++){				
				    }
				    textz += "#v"+装备ID+"#第["+强化[i][0]+"]次强化\r\n"
					textz += "需要：\r\n"
					材料总数2 = 强化[i][7] * 装备等级;
					金币总数  = 强化[i][5] * 装备等级;
					元宝总数  = 强化[i][4] * 装备等级;
					材料总数1 = 强化[i][3] * 装备等级;
					// if(强化[i][4] > 0){
						textz += "#k[金币]：x#r"+金币总数/10000+"万 #k[元宝]：x#r"+元宝总数+"\r\n#k#v4310108#[点券币]：x#r"+材料总数1+" #k#v2531000#[防爆]：x#r"+材料总数2+"\r\n"
					// }
					
					textz += "#L"+i+"##r开始强化#r[成功率"+(强化[i][6]*0.6)/10+"%]#k\r\n";
					textz += "#L1001##b强化说明#k";
					textz += "#L1002##b强化介绍#k";
					textz +="\r\n\r\n"
					cm.sendSimple(textz);
					
			} else if (status == 1) {
			if (selection <= 999) {
			type = selection
			up = type  ;
			down  = type  ;	
				if(强化[type][3] > 0){
					if(!cm.haveItem(强化材料1,材料总数1)){
						cm.sendOk("#b你没有#v"+强化材料1+"#[#z"+强化材料1+"#]：x"+材料总数1+"；\r\n");
						cm.dispose();
						return;
					    }
				    }
				if(强化[type][7] > 0){
					if(!cm.haveItem(强化材料2,材料总数2)){
						cm.sendOk("#b你没有#v"+强化材料2+"#[#z"+强化材料2+"#]：x"+材料总数2+"；\r\n");
						cm.dispose();
						return;
					    }
				    }
				if(强化[type][4] > 0){
					if(cm.getmoneyb() < 元宝总数){
						cm.sendOk("元宝不足");
						cm.dispose();
						return;
					    }
				    }	
				if(强化[type][5] > 0){
					if(cm.getMeso() < 金币总数){
						cm.sendOk("金币不足");
						cm.dispose();
						return;
					    }
				    }	
				if(强化[type][0] <= 15){
					var 成功率 = Math.floor(Math.random() * 999+1);
					if(成功率 <= (强化[type][6]*0.6)){
					var 加成 = 0.025;
                    var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
                    var statup = new java.util.ArrayList();
					装备力量=item.getStr();
                    装备敏捷=item.getDex();
                    装备运气=item.getLuk();
                    装备智力=item.getInt();
                    装备血量=item.getHp();
                    装备蓝量=item.getMp();
                    装备攻击=item.getWatk();
                    装备魔攻=item.getMatk();
                    装备物防=item.getWdef();
                    装备魔防=item.getMdef();
                    装备回避=item.getAvoid();
                    装备命中=item.getAcc();
                    装备跳跃=item.getJump();
                    装备移动=item.getSpeed();
					if(强化[type][1] == "4☆" || 强化[type][1] == "5☆" ||强化[type][1] == "6☆" ){
						加成 = 0.05;
					}if(强化[type][1] == "7☆" || 强化[type][1] == "8☆" ||强化[type][1] == "9☆" ){
						加成 = 0.10;
					}if(强化[type][1] == "10☆"){
						加成 = 0.20;
					}if(强化[type][1] == "11☆"){
						加成 = 0.30;
					}if(强化[type][1] == "12☆"){
						加成 = 0.45;
					}if(强化[type][1] == "13☆"){
						加成 = 0.60;
					}if(强化[type][1] == "14☆"){
						加成 = 0.100;
					}
					
					item.setStr(装备力量+装备力量*加成);
			        item.setDex(装备敏捷+装备敏捷*加成);
					item.setLuk(装备运气+装备运气*加成);
			        item.setInt(装备智力+装备智力*加成);
					item.setWatk(装备攻击+装备攻击*加成);
					item.setMatk(装备魔攻+装备魔攻*加成);
		            item.setHp(装备血量);	
					item.setMp(装备蓝量);	
					item.setWdef(装备物防);	
					item.setMdef(装备魔防);	
					item.setAvoid(装备回避);	
					item.setAcc(装备命中);	
					item.setJump(装备跳跃);	
					item.setSpeed(装备移动);		
					
					//item.setLocked(1);
				    item.setOwner(强化[up][2]);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false); 
					cm.gainItem(强化材料1, -材料总数1);
					cm.gainItem(强化材料2, -材料总数2);
					cm.setmoneyb(-元宝总数);
					cm.gainMeso(-金币总数);
					cm.sendOk("装备强化成功。");
					// cm.getItemMegaphone("升为"+强化[up][2]+",大家为他鼓掌吧!",item);
					// cm.getPlayer().itemlaba("[升星系统  " + cm.getPlayer().getName() + "]", "升为"+强化[up][2]+",大家为他鼓掌吧!", item, 1);
					cm.dispose();
					}else{
                    var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
                    var statup = new java.util.ArrayList();
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false); 
					cm.gainItem(强化材料1, -材料总数1);
					cm.gainItem(强化材料2, -材料总数2);
					cm.setmoneyb(-元宝总数);
					cm.gainMeso(-金币总数);
					// cm.getPlayer().itemlaba("[升星系统  " + cm.getPlayer().getName() + "]", "升"+强化[up][2]+"失败,不要气馁!", item, 1);
					cm.sendOk("装备强化失败。");
					cm.dispose();
					}
				}	
            }else if (selection == 1001) {
			var textz = "\r\n";
			    textz += "1 星[成功率 60%,失败没后果]\r\n";
			    textz += "2 星[成功率 54%, 失败没后果]\r\n";
			    textz += "3 星[成功率 48%, 失败没后果]\r\n";
			    textz += "4 星[成功率 42%, 失败没后果]\r\n";
			    textz += "5 星[成功率 30%, 失败没后果]\r\n";
			    textz += "6 星[成功率 24%, 失败没后果]\r\n";
			    textz += "7 星[成功率 21%, 失败没后果]\r\n";
			    textz += "8 星[成功率 15%, 失败没后果]\r\n";
			    textz += "9 星[成功率 20%, 失败没后果]\r\n";
			    textz += "10星[成功率 12%, 失败没后果]\r\n";
			    textz += "11星[成功率  6%, 失败没后果]\r\n";
			    textz += "12星[成功率4.8%, 失败没后果]\r\n";
			    textz += "13星[成功率3.6%, 失败没后果]\r\n";
			    textz += "14星[成功率2.4%, 失败没后果]\r\n";
			    textz += "15星[成功率1.2%, 失败没后果]\r\n";
					cm.sendOk(textz);
					cm.dispose();
			}else if (selection == 1002) {
			var textz = "\r\n";
			    textz += "1-4  星[增加四维、双攻:2.5%]\r\n";
			    textz += "5-7  星[增加四维、双攻:  5%]\r\n";
			    textz += "8-10 星[增加四维、双攻: 10%]\r\n";
			    textz += "   11星[增加四维、双攻: 20%]\r\n";
			    textz += "   12星[增加四维、双攻: 30%]\r\n";
			    textz += "   13星[增加四维、双攻: 45%]\r\n";
			    textz += "   14星[增加四维、双攻: 60%]\r\n";
			    textz += "   15星[增加四维、双攻:100%]\r\n";
					cm.sendOk(textz);
					cm.dispose();
			}

        }
    }
}