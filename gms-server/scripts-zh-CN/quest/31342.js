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
            qm.sendOk("可以随时来找我");
            qm.dispose();
            return;
        }
        status--;
    }
	if (status == 0) {
	    qm.sendAcceptDecline("好的，现在出发吗？乘坐升降机，可以沿着岩壁巨人的身体爬上去。他的身体非常巨大，爬上去需要一些时间。请做好充分的准备。");
	} else if (status == 1) {	
		qm.forceStartQuest();
		qm.warp(240091600)
	    qm.dispose();		
	}
  }
  

function end(mode, type, selection) {
	qm.dispose();
}
