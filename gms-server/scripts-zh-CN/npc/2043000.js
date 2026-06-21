/*
 * Papulatus
 */

var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status >= 1) {
	    status--;
	} else {
	    cm.dispose();
	    return;
	}
    }
    if (status == 0) {
	cm.sendNext("您准备好返回到原始时间段了吗？维度裂缝目前处于打开状态。请记住，这是我第一次这样做，因此有可能失败。也就是说，我是 非常有信心，它将成功！我将确保您能回到原来的时间！");
    } else if (status == 1) {
	cm.sendNextPrev("现在，在我们开始之前，请考虑一下您过去所居住的时间和地点。“裂缝的维度”将认清想法，并将您带到那个地方。 以后见！");
    } else if (status == 2) {
	cm.teachSkill(5121010, 0, 10);
	cm.forceCompleteQuest(6363);
	cm.warp(120000200, 0);
	cm.dispose();
    }
}