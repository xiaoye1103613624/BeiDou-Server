/* Dawnveil
    [Ellinel Fairy Academy] Ivana's Misunderstanding
	Headmistress Ivana
    Made by Daenerys
*/
//接受拒绝任务：qm.sendAcceptDecline
//下一页任务： qm.sendNextPrev
//自己对话只能用：sendNextPrev
var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 3) {
            qm.sendOk("等你需要去的时候可以随时来找我。");
            qm.dispose();
            return;
        }
        status--;
    }
	if (status == 0) {
	    qm.sendNext("怎么你是我的粉丝吗？看我的骑士卡布的名声终于在冒险岛世界中传扬开来了。我要事先声明，我的签名可是很贵的，大概1亿金币？");
	} else if (status == 1) {
	    qm.sendNextPrev("真是个没有幽默感的朋友。我来自我介绍一下。我叫卡布！注意发音。如果说成是“卡普”或“卡波”的话，小心屁股被我踢，哈哈哈！");	
    } else if (status == 2) {	 
		qm.sendNextPrev("哎呀，长话短说，直奔主题？我喜欢像你这样的人。人生中最重要的是速度，到岩壁巨人那里去的路很高,很遥远，也很险峻，靠自己的两条腿是没办法过去的。不过有我卡布的骑宠的话，就另说了！当然，如果你能好好当我的助手的话。");	
    } else if (status == 3) {	 
		qm.sendAcceptDecline("很简单。往前走的事情交给我！既然你是第一次来我就不收你钱了。要试试看吗，朋友？");
	} else if (status == 4) {	
	    qm.sendNextPrev("#b(摸了摸卡布的坐骑，试着坐上去。)");	
	} else if (status == 5) {	
		qm.forceStartQuest();
	    qm.dispose();		
	}
  }
  

function end(mode, type, selection) {
	qm.dispose();
}
