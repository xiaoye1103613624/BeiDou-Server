
var status = 0;
var 黑水晶 = 4021008;
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 忠告 = "#k温馨提示：任何非法程序和外挂封号处理.封杀侥幸心理.";
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
		
	    var a1 = "#b#v1114204#魂之任务 #r【需要等级#b70级#r】需要道具\r\n#v4000144#*500 #v4001238#*50 #v4000022#*500 #v4021007#*5 \r\n\r\n 需要#b1000万金币#r\r\n";
	    var a2 = "#L2##k" + 正方箭头 + "开始任务\r\n";

            cm.sendSimple("这里是魂之任务中心，请确认你任务材料：\r\n"+a1+""+a2+"");
        } else if (status == 1) {
		
	    if (cm.getInventory(1).isFull()){
                        cm.sendOk("#b请保证装备栏位至少有2个空格,否则无法合成.");
                        cm.dispose();
          

	    } else if (selection == 2) {
		
		if (cm.getLevel() > 70  &&cm.haveItem(4000144,500)&&cm.haveItem(4001238,50)&&cm.haveItem(4000022,500)&&cm.haveItem(4021007,5)&&cm.getMeso()>=10000000) {

			
					
			cm.gainItem(1114204,5,5,5,5,0,0,5,5,0,0,0,0,0,0);//新手成长戒指		
			cm.gainItem(4000144, -500);
			cm.gainItem(4001238, -50);
			cm.gainItem(4000022, -500);
			cm.gainMeso(-10000000);
			cm.gainItem(4021007,-5);
			
			//cm.getPlayer().setBossLog("技能",1,1)
			cm.sendOk("已经完成了，去提升一下吧");
			cm.dispose();
			return;
		} else {
			cm.sendOk("你的材料不足 或者 等级没有70级 !!!");
			cm.dispose();
			return
		
		
				}
	
            }
        }
    }
}