/* 
 *  NPC     Naosuke
 *  Maps ;  Ninja Castle Hallway
 *
 */
var status = -1

function start() {
    //cm.sendNext("Woah! Who are you?!");
    cm.sendNext("你是谁?!");
}

function action(mode, type, selection) {
    if (mode == 1) {
	status++
    } else {
	if (status == 0) {
	    cm.sendOk("……看到了吗?摆在面前的是一条危险的道路，众所周知，这条道路会吞噬并摧毁每一个敢于走上这条道路的人。如果我是你，我现在就会带着完好无损的生命转身离开");
	}
	cm.dispose();
	return;
    }
    if (status == 0) {
	//cm.sendYesNo("What? You want to proceed further from this? Are you saying that you know what's going out there?");
	cm.sendYesNo("怎么？你还想继续谈下去吗?你是说你知道外面发生了什么吗?");
    } else if (status == 1) {
	//cm.sendNext("...Okay. If you are going there knowing what's really out there, then I won't stop you. I really hope you safely reach Tenshu and... beat those guys!")
	cm.sendNext("…好吧。如果你要去那里，知道外面到底有什么，那我不会阻止你。我真的希望你能安全到达……打败那些家伙!")
    } else if (status == 2) {
	cm.warp(800040300, 0);
	cm.dispose();
    }
}