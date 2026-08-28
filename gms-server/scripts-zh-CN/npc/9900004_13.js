
var status = -1;
var 红星星 ="#fItem/Etc/0427/04270001/Icon8/5#";
var 黄星星 ="#fItem/Etc/0427/04270001/Icon9/0#";
var 宝石内容 =Array(
// 力量宝石
Array(0,Array(4251202,3),Array(4440300,1)),
Array(0,Array(4440300,3),Array(4440200,1)),
Array(0,Array(4440200,3),Array(4440101,1)),
Array(0,Array(4440101,3),Array(4440100,1)),                  
Array(0,Array(4440100,3),Array(4440000,1)),

//敏捷
Array(1,Array(4251202,3),Array(4443300,1)),
Array(1,Array(4443300,3),Array(4443200,1)),
Array(1,Array(4443200,3),Array(4443101,1)),
Array(1,Array(4443101,3),Array(4443100,1)),                 
Array(1,Array(4443100,3),Array(4443000,1)),

//智慧
Array(3,Array(4251202,3),Array(4442300,1)),
Array(3,Array(4442300,3),Array(4442200,1)),
Array(3,Array(4442200,3),Array(4442101,1)),
Array(3,Array(4442101,3),Array(4442100,1)),       
Array(3,Array(4442100,3),Array(4442000,1)),


//运气
Array(2,Array(4251202,3),Array(4441300,1)),
Array(2,Array(4441300,3),Array(4441200,1)),
Array(2,Array(4441200,3),Array(4441101,1)),
Array(2,Array(4441101,3),Array(4441100,1)),
Array(2,Array(4441100,3),Array(4441000,1))




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
			var text = "";
			    text +="            "+红星星+" #b欢迎来到 #r宝石合成 #b功能 "+红星星+"\r\n\r\n";
			text +="    #L0##k"+黄星星+" 力量宝石 "+黄星星+"#l       #k#L1#"+黄星星+" 敏捷宝石 "+黄星星+"#l\r\n\r\n";
			text +="    #L2##k"+黄星星+" 智力宝石 "+黄星星+"#l       #k#L3#"+黄星星+" 运气宝石 "+黄星星+"#l\r\n\r\n";
			
			cm.sendSimple(text);
		} else if (status == 1) {
			xz1 =selection;
			var text = "";
			    text +="            "+红星星+" #b欢迎来到 #r宝石合成 #b功能 "+红星星+"\r\n\r\n";
			for(var a=0;a<宝石内容.length;a++){
			if(宝石内容[a][0]==xz1){	
			text +="#L"+a+"# "+黄星星+" #v"+宝石内容[a][1][0]+"##k#z"+宝石内容[a][1][0]+"# * #b"+宝石内容[a][1][1]+" #k= #v"+宝石内容[a][2][0]+"##z"+宝石内容[a][2][0]+"# * #b"+宝石内容[a][2][1]+"\r\n";
			}
			}
			cm.sendSimple(text);
			
		}else if(status ==2){
			xz3=selection;
			var text = "";
			    text +="            "+红星星+" #b欢迎来到 #r宝石合成 #b功能 "+红星星+"\r\n\r\n";
				text +=""+黄星星+" #v"+宝石内容[xz3][1][0]+"##k#z"+宝石内容[xz3][1][0]+"# * #b"+宝石内容[xz3][1][1]+" #k= #v"+宝石内容[xz3][2][0]+"##z"+宝石内容[xz3][2][0]+"# * #b"+宝石内容[xz3][2][1]+"\r\n";
			text +=""+红星星+" #k请输入你要合成的数量，成功概率80% 失败不返还材料\r\n";
			text +=""+红星星+" #k一次最多可以合成100个\r\n";
			text +=""+红星星+" #k注意：成功概率只判定一次\r\n如批量合成如50个成功则都成功，50个失败则损失50个材料\r\n";
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
			//cm.喇叭(3,"宝石合成","恭喜玩家 [" + cm.getPlayer().getName() + "] 合成了 ["+cm.getItemName(宝石内容[xz3][2][0])+"]* ["+(宝石内容[xz3][2][0],xz2)+"]");
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

