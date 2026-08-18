






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
        if (status == 0) {
            qm.sendOk("唉，快远离我吧。");
            qm.dispose();
            return;
        }
        status--;
    }
	if (status == 0) {
	    qm.sendNext(".....刚才我说什么来着？\r\n\r\n不行，我不善良，我也许会伤害你.....你快走开.....我害怕我自己。");
	} else if (status == 1) {
	    qm.sendNext("#b#h，##k你能听到我说话吗？能和岩壁巨人对话吗？\r\n\r\n#b(是古瓦洛的声音。把和岩壁巨人的对话告诉他吧。)#k");	
    } else if (status == 2) {	 
		qm.sendNext("岩壁巨人是大地精灵长期聚集在一起形成的新的生命体，看上去像是黏土组成的一样。实际上是成百上千个小生命体结合在一起后诞生出来的东西。");	
    } else if (status == 3) {	 
		qm.sendNext("因为我在几百年前遭到了军团长麦格纳斯的背叛，被吸收了力量。因此精灵们本能地感觉到了危机，就像受了伤之后，身体的哥哥组织会迅速运作起来恢复身体一样，精灵们决定通过“合体”来应对“我的力量消失”的危机。这一过程，经历了几百年的时间。");
	} else if (status == 4) {	
	    qm.sendNext("但这很明显是违背自然规律的.....原来精灵们不应该组成巨大的生命体，而是应该分散在各地,组成世界才对。如果精灵们分散在世界各地，即使某个地方的精灵收到了污染，他们也能自我净化。但是现在他们合成了一个生命体，就产生了很大的问题。");	
	} else if (status == 5) {
		qm.sendNext("如果岩壁巨人被黑暗力量污染，那该怎么办呢？那样的话，一定会发生大灾难。从岩壁巨人说的话来看，好像已经出现了污染的征兆。这一切全都是我的错。");	
	} else if (status == 6) {
		qm.sendNext("你能帮助岩壁巨人吗？必须阻止大灾难的发生。");	
	} else if (status == 7) {		
		qm.forceStartQuest();
	    qm.dispose();		
	}
  }
  

function end(mode, type, selection) {
	qm.dispose();
}
