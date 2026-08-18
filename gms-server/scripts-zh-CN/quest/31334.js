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
            qm.sendOk("没什么事别来烦我。");
            qm.dispose();
            return;
        }
        status--;
    }
	if (status == 0) {
	    qm.sendNext("我从珂娜那里听说了。哎呀真是麻烦你了。哈哈哈！\r\n总之谢谢你。我刚好因为怪物而头疼呢。对了，听珂娜说，你是来这里探险的.....你想找什么东西呢？");
	} else if (status == 1) {
	    qm.sendNextPrev("你想听听故事吗？我可以随时告诉你");	
    } else if (status == 2) {	 
		qm.sendNextPrev("相信你应该挺村长说过了，这里是我们哈夫林的探查现场。原来这里聚集着很多哈夫林，比现在多好几倍");	
    } else if (status == 3) {	 
		qm.sendNextPrev("在探查什么东西？你和别吃惊。这个的山在动！在几百年的时间里，一点一点地在动。知道这一点的话，任何人都会感到好奇，不是吗？我从六岁开始一直到现在，从白色的毛变成灰色，都一直带领探查团员们在这里进行研究。");
	} else if (status == 4) {
		qm.sendNextPrev("但是有一天，那件事突然发生了。我们一直以为是山的东西，其实不是山。");
	} else if (status == 5) {
		qm.sendNextPrev("我当时想这下完了。我还以为会发生大灾难，但没想到谁也没受伤。因为那个岩壁巨人动了一下之后，就再也没动了。他好像在张嘴说话，但是我们根本听不懂他在说什么。");
	} else if (status == 6) {
		qm.sendNextPrev("还有，那天之后出现了很多危险的怪物，探查队员们一个个的离开了，现在就只剩下这么几个人.....怎么样？现在你明白了吗？");
	} else if (status == 7) {	
		qm.forceStartQuest();
	    qm.dispose();		
	}
  }
  

function end(mode, type, selection) {
	qm.dispose();
}
