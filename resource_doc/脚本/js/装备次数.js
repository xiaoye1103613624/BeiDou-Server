// true 开启
// false 关闭
				
var 星星 = "#fEffect/CharacterEff/1003393/0/0#";	
		


var 表情高兴 = "#fUI/GuildBBS/GuildBBS/Emoticon/Basic/2#";
var status = -1;
var selection;
var 彩虹 ="#fEffect/ItemEff/1071085/effect/walk1/2#";
var 积分 = new Array(1,2);
var 随机积分 = 积分[Math.floor(Math.random() * 积分.length)];

function start() {
    
	status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
		cm.dispose();
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
    if (cm.getInventory(1).getItem(1) == null) {
            cm.sendOk("如果要强化，请吧物品放在背包第一格!");
            cm.dispose();
            return;
        }
	item = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getItem(1).copy();
			
	金锤子 = item.getViciousHammer();
	
	当前等级 = item.getLevel();
	
	可升次数 = item.getUpgradeSlots();
	
	总次数 = 可升次数+当前等级;
	
	最高级 = 50+金锤子;
	
	剩余次数 = 最高级-总次数;

	当前装备 = item.getItemId();
	提示说明 = "";
	if (总次数 < 18) {
	需要数量 = 1;
	提示说明 = "";
	进阶强化 = false;
	概率 = 2;
	
	} else {
	需要数量 = parseInt(1+(总次数-17));	
	进阶强化 = true;
    if (cm.haveItem(2340000,1)) {
    var mface = Array(1,1,2,2,2,2,2,2,2,2);
    概率 = mface[Math.floor(Math.random() * mface.length)];
	概率显示 = 80;
	祝福卷 = "#r当前使用祝福卷已提升30%";
	祝福开关 = true;
	} else {	
    var mface = Array(1,1,1,1,1,2,2,2,2,2);
    概率 = mface[Math.floor(Math.random() * mface.length)];	
    概率显示 = 50;
	祝福卷 = "#b使用祝福卷可以提升30成率哦！";
	祝福开关 = false;
	}
	}

      	
	if (当前装备 == 1122076 || 当前装备 == 1122278 ) {
        cm.sendOk("龙王的力量格外强大，无法破解其中的奥秘！暂时无法升级黑龙项链！");
        cm.dispose();
        return;
	} 

	//if (可升次数 == 0  && 当前等级 <= 0) {
    //    cm.sendOk("此装备无法升级！");
   //     cm.dispose();
   //     return;
	//}	

	
	if (总次数 >= 最高级  ) {
        cm.sendOk("抱歉，当前装备升级次数已经封顶！");
        cm.dispose();
        return;
	}	
	var text = "";
	text += "\t" + 星星 + 星星 +  "#v2022359# #e#d冒险险装备升级#k#n #v2022359#"  +  星星 + 星星 +  " \r\n"
	text += "\t\t#e#r#v"+ 当前装备 +"##t"+ 当前装备 +"#\r\n";
	text += "\t\t#e#r目前等级："+当前等级+" 级 可升次数："+剩余次数+"#k#n#b\r\n";
	
	text += "\t\t #d当前需要：#t3700288#[#r#c3700288##d/#b"+需要数量+"#d]+3元宝\r\n";
	text += "\t\t #d当前需要：#t4021009#[#r#c4021009##d/#b"+需要数量+"#d]+#t4011007#[#r#c4011007##d/#b"+需要数量+"#d]\r\n";
	if (进阶强化){
	text += "\t\t #d进阶强化：\r\n";	
	text += "\t\t #d当前成率为："+概率显示+"%\r\n";	
	text += "\t\t #d"+祝福卷+"\r\n";
	}	
	cm.sendYesNo(text);

    } else if (status == 1) {	
            if (cm.getmoneyb() < 3) {
		    cm.sendOkS("元宝不足!",2);
		    cm.对话结束();
			return;	
           } else if (cm.haveItem(3700288,需要数量)) { 
			    if (进阶强化){
				if (祝福开关){
				cm.gainItem(2340000,-1);	
				}					
				}
				cm.gainItem(3700288,-需要数量);
				cm.gainItem(4021009,-需要数量);
				cm.gainItem(4011007,-需要数量);
				cm.gainzb(-3);
				if (概率 == 2 ){
                item.setUpgradeSlots((item.getUpgradeSlots() + 1));
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);	
                cm.sendOk("恭喜你！运气真好，强化成功了一次！");					
                cm.喇叭(1,"[装备强化中心] "+cm.getName(),"装备的总升级次数得到了提升！");		
                //cm.worldMessage(6,"【装备强化中心】["+cm.getName()+"]在自由市场相框处！成功提升装备强化一次！"); 				
				} else {
				cm.sendOk("本次强化遇到黑暗力量阻挡！失败了！");
				//cm.worldMessage(6,"【装备强化中心】["+cm.getName()+"]装备升级失败！"); 
				}
				cm.dispose(); 
			}else{ 
				cm.sendOk("您的金锤子不足！\r\n需要："+需要数量+"个金锤子"); 
				cm.dispose(); 
			} 
	    
			 
	 }	 
	} 
	  