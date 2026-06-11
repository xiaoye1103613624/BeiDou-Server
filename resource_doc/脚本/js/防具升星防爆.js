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
[1,"","1☆", 1,         1, 1000000,800,0,1],
[2,"1☆", "2☆",1,   2, 2000000,700,0,2],
[3,"2☆", "3☆",1,   3, 3000000,600,0,3],
[4,"3☆", "4☆",1,   4, 4000000,500,0,4],
[5,"4☆", "5☆",1,   5, 5000000,400,0,5],
[6,"5☆", "6☆",1,   6, 6000000,300,0,7],
[7,"6☆", "7☆",1,   7, 7000000,200,0,10],
[8,"7☆", "8☆",1,   8, 8000000,160,0,15],
[9,"8☆", "9☆",1,   9, 9000000,140,0,25],
[10,"9☆", "10☆",1,  10, 10000000,120,0,35],
[11,"10☆", "11☆",1, 11, 15000000,100,0,45],
[12,"11☆", "12☆",1, 12, 20000000,80,0,60],
[13,"12☆", "13☆",2, 13, 25000000,60,0,80],
[14,"13☆", "14☆",3, 14, 30000000,40,0,110],
[15,"14☆", "15☆",5, 15, 50000000,20,0,150],
]
var 强化材料1 = 4310108;
var 强化材料2 = 2531000;


function start() {
		var ii = Packages.server.MapleItemInformationProvider.getInstance();
	 if(cm.getInventory(1).getItem(1)==null){
		cm.sendOk("对不起,你的装备栏第一个格子里面没有防具");
		cm.dispose();	
		return;	
	} else if (cm.getInventory(1).getItem(1).getOwner()=="15☆") {
          cm.sendOk("你的装备第一格已经达到上限！");
          cm.dispose();
		  return;
	} else if (cm.getInventory(1).getItem(1).getItemId() > 1300000 && cm.getInventory(1).getItem(1).getItemId() < 1799999) {
          cm.sendOk("你的装备第一格并不是防具！");
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
				装备等级 = 1;
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
						textz += "#k[金币]：x#r"+金币总数/10000+"万 #k[元宝]：x#r"+元宝总数+" #k#v4310108#[点券币]：x#r"+材料总数1+"\r\n"
					// }
					
					textz += "#L"+i+"##b是否确定使用#v2531000##r本操作不可逆#l\r\n";
					// textz += "#L1000##r#v2531000#高级防爆#k\r\n";
					// textz += "#L1001##b强化说明#k";
					// textz += "#L1002##b强化介绍#k";
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
					if(!cm.haveItem(强化材料2,1)){
						cm.sendOk("#b你没有#v"+强化材料2+"#[#z"+强化材料2+"#]：x1；\r\n");
						cm.dispose();
						return;
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
					if(成功率 <= 强化[type][6]){
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
					
					item.setStr(装备力量+强化[type][8]);
			        item.setDex(装备敏捷+强化[type][8]);
					item.setLuk(装备运气+强化[type][8]);
			        item.setInt(装备智力+强化[type][8]);
					item.setWatk(装备攻击+强化[type][8]);
					item.setMatk(装备魔攻+强化[type][8]);
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
					cm.gainItem(强化材料2, -1);
					cm.setmoneyb(-元宝总数);
					cm.gainMeso(-金币总数);
					cm.喇叭(2,"[升星系统  " + cm.getPlayer().getName() + "]升为"+强化[up][2]+",大家为他鼓掌吧!");
					cm.sendOk("装备强化成功。");
					cm.dispose();
					}else{
                    var item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
                    var statup = new java.util.ArrayList();
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false); 
					cm.gainItem(强化材料1, -材料总数1);
					cm.gainItem(强化材料2, -1);
					cm.setmoneyb(-元宝总数);
					cm.gainMeso(-金币总数);
					cm.喇叭(2,"[升星系统  " + cm.getPlayer().getName() + "]升为"+强化[up][2]+",大家为他鼓掌吧!");
					cm.sendOk("装备强化失败。");
					cm.dispose();
					}
				}	
            }
			else if (selection == 1000) {
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
					if(!cm.haveItem(强化材料2,1)){
						cm.sendOk("#b你没有#v"+强化材料2+"#[#z"+强化材料2+"#]：x1；\r\n");
						cm.dispose();
						return;
					    }
				    }
				if(强化[type][4] > 0){
					if(cm.getmoneyb() < 元宝总数){
						cm.sendOk("元宝不足");
						cm.dispose();
						return;
					    }//cm.setmoneyb
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
					if(成功率 <= 强化[type][6]){
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
					
					item.setStr(装备力量+强化[type][8]);
			        item.setDex(装备敏捷+强化[type][8]);
					item.setLuk(装备运气+强化[type][8]);
			        item.setInt(装备智力+强化[type][8]);
					item.setWatk(装备攻击+强化[type][8]);
					item.setMatk(装备魔攻+强化[type][8]);
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
					cm.gainItem(强化材料2, -1);
					cm.setmoneyb(-元宝总数);
					cm.gainMeso(-金币总数);
					cm.sendOk("装备强化成功。");
					//cm.喇叭(2,"恭喜玩家：["+cm.getName()+"]装备强化成功！");
					cm.dispose();
					}else{
					cm.gainItem(强化材料1, -材料总数1);
					cm.gainItem(强化材料2, -1);
					cm.setmoneyb(-元宝总数);
					cm.gainMeso(-金币总数);
					cm.sendOk("装备强化失败。");
					cm.dispose();
					}
				}	
            }else if (selection == 1001) {
			var textz = "\r\n";
			    textz += "1 星[成功率80%, 失败没后果]\r\n";
			    textz += "2 星[成功率70%, 失败没后果]\r\n";
			    textz += "3 星[成功率60%, 失败没后果]\r\n";
			    textz += "4 星[成功率50%, 失败没后果]\r\n";
			    textz += "5 星[成功率40%, 失败-2星,防爆不减]\r\n";
			    textz += "6 星[成功率30%, 失败-2星,防爆不减]\r\n";
			    textz += "7 星[成功率20%, 失败-2星,防爆不减]\r\n";
			    textz += "8 星[成功率16%, 失败-2星,防爆不减]\r\n";
			    textz += "9 星[成功率14%, 失败-2星,防爆不减]\r\n";
			    textz += "10星[成功率12%, 失败-2星,防爆不减]\r\n";
			    textz += "11星[成功率10%, 失败-2星,防爆不减]\r\n";
			    textz += "12星[成功率 8%, 失败-2星,防爆不减]\r\n";
			    textz += "13星[成功率 6%, 失败-2星,防爆不减]\r\n";
			    textz += "14星[成功率 4%, 失败-2星,防爆不减]\r\n";
			    textz += "15星[成功率 2%, 失败-2星,防爆不减]\r\n";
					cm.sendOk(textz);
					cm.dispose();
			}else if (selection == 1002) {
			var textz = "\r\n";
			    textz += " 1星[增加四维、双攻:   1]\r\n";
			    textz += " 2星[增加四维、双攻:   2]\r\n";
			    textz += " 3星[增加四维、双攻:   3]\r\n";
			    textz += " 4星[增加四维、双攻:   4]\r\n";
			    textz += " 5星[增加四维、双攻:   5]\r\n";
			    textz += " 6星[增加四维、双攻:   7]\r\n";
			    textz += " 7星[增加四维、双攻:  10]\r\n";
			    textz += " 8星[增加四维、双攻:  15]\r\n";
			    textz += " 9星[增加四维、双攻:  25]\r\n";
			    textz += "10星[增加四维、双攻:  35]\r\n";
			    textz += "11星[增加四维、双攻:  45]\r\n";
			    textz += "12星[增加四维、双攻:  60]\r\n";
			    textz += "13星[增加四维、双攻:  80]\r\n";
			    textz += "14星[增加四维、双攻: 110]\r\n";
			    textz += "15星[增加四维、双攻: 150]\r\n";
					cm.sendOk(textz);
					cm.dispose();
			}

        }
    }
}