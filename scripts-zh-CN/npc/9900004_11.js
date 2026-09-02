var status = 0;
var 红星星 ="#fItem/Etc/0427/04270001/Icon8/5#";
var 黄星星 ="#fItem/Etc/0427/04270001/Icon9/0#";

var 力量宝石代码 =[4440300,4440200,4440101,4440100,4440000];
var 敏捷宝石代码 =[4443300,4443200,4443101,4443100,4443000];
var 智力宝石代码 =[4442300,4442200,4442101,4442100,4442000];
var 运气宝石代码 =[4441300,4441200,4441101,4441100,4441000];

var 力量宝石提高属性 =[10,20,30,40,50];
var 敏捷宝石提高属性 =[10,20,30,40,50];
var 智力宝石提高属性 =[10,20,30,40,50];
var 运气宝石提高属性 =[10,20,30,40,50];


var 宝石提高攻击属性 = [2,3,5,7,10];
var 宝石提高魔攻属性 = [2,3,5,7,10];


var 宝石确定选择 =[1,2,3,4,5];


var 力量宝石1=0,敏捷宝石1=0,智力宝石1=0,运气宝石1=0;
var nrwb="";
var nrpd3;
var xz1,xz2,xz3;
var 宝石选择;
var 宝石属性;
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
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
          var text = "";
		   text += "           "+红星星+" #b欢迎来到 #r宝石镶嵌 #b功能 "+红星星+"\r\n\r\n";
		   text += "#k#L1#"+黄星星+"选择装备#l\r\n";
		   cm.sendSimple(text);
        } else if (status == 1) {
		var text ="";
			text += "           "+红星星+" #b欢迎来到 #r宝石镶嵌 #b功能 "+红星星+"\r\n\r\n";
			text += "#k#e- 请选择你要进行操作的装备-#n\r\n\r\n#b";
			for (var i = 0; i < 96; i++) {
			if (cm.getInventory(1).getItem(i) != null) {
				var 力量宝石=0,敏捷宝石=0,智力宝石=0,运气宝石=0;
				var 点数 =cm.getInventory(1).getItem(i).getHpR();
				nrwb ="[力:0敏:0智:0运:0]";
				if(cm.getInventory(1).getItem(i).getHpR()>0&&cm.getInventory(1).getItem(i).getHpR()<10000){
				力量宝石 = Math.floor(点数/1000);
				敏捷宝石 = Math.floor(点数/100)%10;
				智力宝石 = Math.floor(点数/10)%10;
				运气宝石 = Math.floor(点数/1)%10;
				nrwb ="[力:"+力量宝石+"敏:"+敏捷宝石+"智:"+智力宝石+"运:"+运气宝石+"]";
				}
				if(Packages.server.MapleItemInformationProvider.getInstance().isCash(cm.getInventory(1).getItem(i).getItemId()) != true&&cm.getInventory(1).getItem(i).getExpiration() == -1){
				text += "#L" + i + "##k第#b"+(i)+"#k格:#v" + cm.getInventory(1).getItem(i).getItemId() + "##b#z" + cm.getInventory(1).getItem(i).getItemId() + "#  #r"+nrwb+"#l\r\n";
				nrpd3++;
			}
			}
			}
			if(nrpd3<1){
			text +="#r很抱歉你貌似没有什么东西可以强化";	
			cm.sendOk(text);
			cm.dispose();
			return;	
			}else{
			cm.sendSimple(text);
			}	
        }else if(status ==2){
			xz1 =selection;
			var text ="";
			text += "           "+红星星+" #b欢迎来到 #r宝石镶嵌 #b功能 "+红星星+"\r\n\r\n";
			text += "#k#e选择:[第#r"+xz1+"#k格][#v"+cm.getInventory(1).getItem(xz1).getItemId()+"##t"+cm.getInventory(1).getItem(xz1).getItemId()+"#]#n\r\n\r\n#b";
			var 点数 =cm.getInventory(1).getItem(xz1).getHpR();
			力量宝石1 = Math.floor(点数/1000);
			敏捷宝石1 = Math.floor(点数/100)%10;
			智力宝石1 = Math.floor(点数/10)%10;
			运气宝石1 = Math.floor(点数/1)%10;
			nrwb ="[力:"+力量宝石1+"敏:"+敏捷宝石1+"智:"+智力宝石1+"运:"+运气宝石1+"]";
			text +="#k当前力量宝石等级:#r"+力量宝石1+"\t";
			text +="#k当前敏捷宝石等级:#r"+敏捷宝石1+"\r\n";
			text +="#k当前智力宝石等级:#r"+智力宝石1+"\t";
			text +="#k当前运气宝石等级:#r"+运气宝石1+"\r\n";
			text +="#b#L0#镶嵌力量宝石#l\t";
			text +="#b#L1#镶嵌敏捷宝石#l\r\n";
			text +="#b#L2#镶嵌智力宝石#l\t";
			text +="#b#L3#镶嵌运气宝石#l\r\n";
			cm.sendSimple(text);
		}else if(status ==3){
			xz2 =selection;
			var text ="";
				text += "           "+红星星+" #b欢迎来到 #r宝石镶嵌 #b功能 "+红星星+"\r\n\r\n";
				switch(xz2){
				case 0:
				if(力量宝石1>0){
				cm.sendOk("当前装备已经上过力量宝石了,如果要改换先拆卸");
				cm.dispose();
				return;
				}
				宝石选择 =力量宝石代码;
				宝石属性 =力量宝石提高属性;
				break;
				case 1:
				if(敏捷宝石1>0){
				cm.sendOk("当前装备已经上过敏捷宝石了,如果要改换先拆卸");
				cm.dispose();
				return;
				}
				宝石选择 =敏捷宝石代码;
				宝石属性 =敏捷宝石提高属性;
				break;
				case 2:
				if(智力宝石1>0){
				cm.sendOk("当前装备已经上过智力宝石了,如果要改换先拆卸");
				cm.dispose();
				return;
				}
				宝石选择 =智力宝石代码;
				宝石属性 =智力宝石提高属性;
				break;
				case 3:
				if(运气宝石1>0){
				cm.sendOk("当前装备已经上过运气宝石了,如果要改换先拆卸");
				cm.dispose();
				return;
				}
				宝石选择 =运气宝石代码;
				宝石属性 =运气宝石提高属性;
				break;				
				}
				text += "#k#e- 请选择你要进行镶嵌的宝石-#n\r\n\r\n#b";
				for(var a=0;a<宝石选择.length;a++){
				text +="#L"+a+"##v"+宝石选择[a]+"##z"+宝石选择[a]+"##l";	
				if((a+1)%2==0){
				text +="\r\n";	
				}else {
				text +="\t";	
				}
				}
				cm.sendSimple(text);
		}else if(status ==4){
			xz3 =selection;
			var text ="";
				text += "           "+红星星+" #b欢迎来到 #r宝石镶嵌 #b功能 "+红星星+"\r\n\r\n";
				text += "#k#e选择:[第#r"+xz1+"#k格][#v"+cm.getInventory(1).getItem(xz1).getItemId()+"##t"+cm.getInventory(1).getItem(xz1).getItemId()+"#]#n\r\n\r\n#b";
				text +="当前选择提交的宝石为#v"+宝石选择[xz3]+"#,提高属性+"+宝石属性[xz3]+"\r\n\r\n";
				if(cm.getPlayer().getItemQuantity(宝石选择[xz3],false)<1){
				text +="#r材料不足无法镶嵌";
				cm.sendOk(text);	
				cm.dispose();
				return;
				}else{
				text +="#r请问你是否要进行镶嵌";
				cm.sendYesNo(text);	
				}
				
		}else if(status ==5){
			cm.gainItem(宝石选择[xz3],-1);
			var ItemCopy = cm.getInventory(1).getItem(xz1).copy();
			switch(xz2){
			case 0:
			ItemCopy.setStr(ItemCopy.getStr()+宝石属性[xz3]);
			ItemCopy.setWatk(ItemCopy.getWatk()+宝石提高攻击属性[xz3]);
			ItemCopy.setMatk(ItemCopy.getMatk()+宝石提高魔攻属性[xz3]);
			ItemCopy.setHpR(ItemCopy.getHpR()+宝石确定选择[xz3]*1000);
			break;
			case 1:
			ItemCopy.setDex(ItemCopy.getDex()+宝石属性[xz3]);
			ItemCopy.setWatk(ItemCopy.getWatk()+宝石提高攻击属性[xz3]);
			ItemCopy.setMatk(ItemCopy.getMatk()+宝石提高魔攻属性[xz3]);
			ItemCopy.setHpR(ItemCopy.getHpR()+宝石确定选择[xz3]*100);
			break;
			case 2:
			ItemCopy.setInt(ItemCopy.getInt()+宝石属性[xz3]);
			ItemCopy.setWatk(ItemCopy.getWatk()+宝石提高攻击属性[xz3]);
			ItemCopy.setMatk(ItemCopy.getMatk()+宝石提高魔攻属性[xz3]);
			ItemCopy.setHpR(ItemCopy.getHpR()+宝石确定选择[xz3]*10);
			break;
			case 3:
			ItemCopy.setLuk(ItemCopy.getLuk()+宝石属性[xz3]);
			ItemCopy.setWatk(ItemCopy.getWatk()+宝石提高攻击属性[xz3]);
			ItemCopy.setMatk(ItemCopy.getMatk()+宝石提高魔攻属性[xz3]);
			ItemCopy.setHpR(ItemCopy.getHpR()+宝石确定选择[xz3]*1);
			break;
			}	
			ItemCopy.setOwner(ItemCopy.getOwner());
			Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getClient(), Packages.client.inventory.MapleInventoryType.EQUIP, xz1, 1, false);
			Packages.server.MapleInventoryManipulator.addFromDrop(cm.getClient(), ItemCopy, false);
			cm.sendSimple("镶嵌成功");
			cm.dispose();
			return;
			
			
			
			
			
			
			
			
			
			
		} 
        }
    }

