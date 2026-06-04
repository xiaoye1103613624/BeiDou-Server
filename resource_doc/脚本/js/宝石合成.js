var 分割线 = "#fMap/Back/zerek/拍卖/标题1#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 红方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/0#";
var 蓝方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/1#";
var 绿方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/2#";
var 黄方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/3#";
var status = -1;
var 红星星 ="#fItem/Etc/0427/04270001/Icon8/5#";
var 黄星星 ="#fItem/Etc/0427/04270001/Icon9/0#";
var 宝石内容 =Array(
Array(0,Array(3991001,2),Array(3991002,1)),
Array(0,Array(3991002,2),Array(3991003,1)),
Array(0,Array(3991003,2),Array(3991004,1)),                  
Array(0,Array(3991004,2),Array(3991005,1)),
Array(0,Array(3991005,2),Array(3991006,1)),
Array(0,Array(3991006,2),Array(3991007,1)),
Array(0,Array(3991007,2),Array(3991008,1)),
Array(0,Array(3991008,2),Array(3991009,1)),


Array(1,Array(3991011,2),Array(3991012,1)),
Array(1,Array(3991012,2),Array(3991013,1)),
Array(1,Array(3991013,2),Array(3991014,1)),
/*
Array(1,Array(3991014,2),Array(3991015,1)),                 
Array(1,Array(3991015,2),Array(3991016,1)),
Array(1,Array(3991016,2),Array(3991017,1)),
Array(1,Array(3991017,2),Array(3991018,1)),
Array(1,Array(3991018,2),Array(3991019,1)),
*/

Array(3,Array(3991026,2),Array(3991027,1)),
Array(3,Array(3991027,2),Array(3991028,1)),
Array(3,Array(3991028,2),Array(3991029,1)),
Array(3,Array(3991029,2),Array(3991030,1)),       
Array(3,Array(3991030,2),Array(3991031,1)),
Array(3,Array(3991031,2),Array(3991032,1)),
Array(3,Array(3991032,2),Array(3991033,1)),
Array(3,Array(3991033,2),Array(3991034,1)),



/*
Array(2,Array(4010031,2),Array(4010032,1)),
Array(2,Array(4010032,2),Array(4010033,1)),
Array(2,Array(4010033,2),Array(4010034,1)),
Array(2,Array(4010034,2),Array(4010035,1)),
Array(2,Array(4010035,2),Array(4010036,1)),             
Array(2,Array(4010036,2),Array(4010037,1)),
Array(2,Array(4010037,2),Array(4010038,1)),*/
Array(2,Array(3991035,2),Array(3991036,1))




);


var 成功概率 =75;


var xz1,xz2,xz3;
function start() {
    action(1, 0, 0);
}


function action(mode, type, selection) {
	if (status >= 0 && mode == 0) {
		//cm.sendOk("感谢使用！");
		cm.dispose();
		return;
	}
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
		if (status == 0) {
			var text = "             #v3991035##e宝石合成 - #n[标准2合1]";
			text += ""+分割线+"\r\n"
			text +="    #L0##k"+红方+" 力量宝石 #l       #k#L1#"+绿方+" 敏捷宝石 #l\r\n\r\n";
			text +="    #L2##k"+蓝方+" 智力宝石 #l       #k#L3#"+黄方+" 运气宝石 #l\r\n\r\n";
			text += ""+分割线+"\r\n"
			cm.sendSimple(text);
		} else if (status == 1) {
			xz1 =selection;
			var text = "             #v3991035##e宝石合成 - #n[标准2合1]";
			text += ""+分割线+"\r\n"
				for(var a=0;a<宝石内容.length;a++){
				if(宝石内容[a][0]==xz1){	
				text +="    #L"+a+"##v"+宝石内容[a][1][0]+"##k#z"+宝石内容[a][1][0]+"# * #b"+宝石内容[a][1][1]+" #k= #v"+宝石内容[a][2][0]+"##z"+宝石内容[a][2][0]+"# * #b"+宝石内容[a][2][1]+"#l\r\n";
				}
			}
			text += "\r\n"+分割线+"\r\n"
			cm.sendSimple(text);
			
		}else if(status ==2){
			xz3=selection;
			var text = "";
			text +="\r\n\r\n      #v"+宝石内容[xz3][1][0]+"##k#z"+宝石内容[xz3][1][0]+"# * #b"+宝石内容[xz3][1][1]+" #k= #v"+宝石内容[xz3][2][0]+"##z"+宝石内容[xz3][2][0]+"# * #b"+宝石内容[xz3][2][1]+"\r\n";
			text += ""+分割线+"\r\n"
			text +="          #k请输入你要合成的数量，成功概率80%\r\n";
			text +="         #k失败不返还材料，一次最多可以合成100个\r\n";
			text += ""+分割线+"\r\n"
			cm.sendGetNumber(text,1,1,100);
		}else if(status ==3){
			xz2 =selection;
			if(cm.getPlayer().getItemQuantity(宝石内容[xz3][1][0],false)<宝石内容[xz3][1][1]*xz2){
			cm.sendOk("材料不足");
			cm.dispose();
			return;
			}
			cm.gainItem(宝石内容[xz3][1][0],-宝石内容[xz3][1][1]*xz2);
			var 随机 =Math.floor(Math.random()*100);
			if(随机<成功概率){
			cm.gainItem(宝石内容[xz3][2][0],xz2);
			cm.喇叭(3,"宝石合成","恭喜玩家 [" + cm.getPlayer().getName() + "] 合成了 ["+cm.getItemName(宝石内容[xz3][2][0])+"]* ["+(宝石内容[xz3][2][0],xz2)+"]");
			cm.sendOk("合成成功");
			status = -1;
			return;
			}else{
			cm.sendOk("合成失败");
			status = -1;
			return;	
			}
		}
}
}

