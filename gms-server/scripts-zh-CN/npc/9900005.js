/*小黑屋脚本
作者：枫叶    QQ：1848350048    
 */
var 点券图标 = "#fUI/CashShop/CashItem/0#";
//var 充值网站 = "http://pay.5566pay.com/Pay/5b8c39815c0f60d7d4020ea6f32e387c";
var 金币消费 = 1000000000;
var 点卷消费 = 1000000;
//传送地点，消耗金币
var maps = Array(
		[910000000, 1],
		[104000000, 1],
		[101000000, 1],
        [120000000, 1],
        [102000000, 1],
        [100000000, 1]

        );
var status = 0;
var show;
var sCost;
var selectedMap = -1;
function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status >= 2) {
            cm.说明文字(" 等你想去哪里了，记得找我哦！");
            cm.对话结束();
            return;
        }
        status--;
    }
    if (status == 0) {
    } else if (status == 1) {		
		
		var selStr="";
		if(cm.getPlayer().getNowdaylog()<1)
		{
			cm.getPlayer().setNowdaylog();
		}
		金币消费=金币消费*cm.getPlayer().getNowdaylog();
		点卷消费=点卷消费*cm.getPlayer().getNowdaylog();
            selStr += "  #d#e欢迎来到小黑屋监狱，请遵守游戏规则，禁止开挂，开辅助和人不在游戏的挂机刷怪行为：#k#n#b\r\n";
		    selStr +="  #r#e每次出去将消费不同额度的点卷或金币,且每次消费的额度会随着次数提升而提升!#k#n#b\r\n"
			selStr += "  #r#e请选择你要去的地方：#k#n#b\r\n";
            selStr +="  (目前消费为:[#r"+金币消费+"#k#b]金币 或 [#k#r"+点卷消费+"#k#b]点卷!#n)\r\n"
			selStr +=" #L1000#"+点券图标+"#d点卷余额："+cm.getPlayer().getCSPoints(1)+"  [#b点击充值点卷#d]#l\r\n"//平台网站充值
			//selStr +=" "+点券图标+"#d点卷余额："+cm.getPlayer().getCSPoints(1)+"  #l\r\n"
		for (var i = 0; i < maps.length; i++) {
			 selStr += "\r\n#L" + i + "# #m" + maps[i] + "##l";
        }
        cm.sendSimple(selStr);
    } else if (status == 2) {
				if (selection == 1000) {
			//cm.openWeb(充值网站); //打开网页
			//cm.playerMessage(5, "正在打开充值网站！如果没有弹出充值网站请联系GM1"); //个人看见的对话 5红色字 6蓝色字 1为弹窗
			cm.dispose();
			cm.openNpc(3003385,71);
			return;
			
		}
		        
        show = maps[selection][1];
		var text ="\t传送目标: #b#m" + maps[selection] + "##k\r\n";
		text +=" #L1#消费[#r"+金币消费+"#k]金币传送至:#b#m" + maps[selection] + "##k#l\r\n"
		text +=" #L2#消费[#r"+点卷消费+"#k]点卷传送至:#b#m" + maps[selection] + "##k#l\r\n"
        cm.sendYesNo(text);
        selectedMap = selection;
    } else if (status == 3) {
		if(selection==1)
		{
			if(cm.getPlayer().getMeso()<金币消费)
			{
			  cm.sendOk("您所拥有的金币不足,请联系相关人员进行操作!");
			  cm.dispose();
			  return;
			}
			else   
			{
			cm.getPlayer().setNowdaylog();
			cm.gainMeso(-金币消费);
            cm.warp(maps[selectedMap][0]);
            cm.dispose();
			return;
			}
		}
		else if(selection==2)
		{
			if(cm.getPlayer().getCSPoints(1) < 点卷消费)
			{
			  cm.sendOk("您所拥有的点卷不足,请联系相关人员进行操作!");
			  cm.dispose();
			  return;
			}
			else{
				  cm.getPlayer().setNowdaylog();
                  cm.getPlayer().modifyCSPoints(1, -点卷消费, true);				  
                  cm.warp(maps[selectedMap][0]);
                  cm.dispose();
			return;
			}
		}
		else 
		{
			cm.sendOk("非法操作,请联系相关人员进行操作!");
			  cm.dispose();
			  return;
		}

            
        
    }
}