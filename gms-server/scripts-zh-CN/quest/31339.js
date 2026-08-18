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
	    qm.sendAcceptDecline("好了，这么多的话，燃料应该够了。已经全部准备好了，现在出发吗？");
	} else if (status == 1) {	
		qm.forceStartQuest();
		qm.warp(240091000)
	    qm.dispose();		
	}
  }
  

function end(mode, type, selection) {
	qm.dispose();
}
