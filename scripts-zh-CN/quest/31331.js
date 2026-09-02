/* Dawnveil
    [Ellinel Fairy Academy] Ivana's Misunderstanding
	Headmistress Ivana
    Made by Daenerys
*/
//接受拒绝任务：qm.sendAcceptDecline
//下一页任务： qm.sendNext
//自己对话只能用：sendNext
var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 3) {
            qm.sendOk("好吧，你在考虑考虑吧？");
            qm.dispose();
            return;
        }
        status--;
    }
	if (status == 0) {
	    qm.sendNext("米纳尔森林南部一直以来就以经常发生奇怪的事情而闻名。但是像这样奇怪的事还是第一次发生。石头山竟然活了，突然站了起来.....");
	} else if (status == 1) {
	    qm.sendNext("乍一听，你也没办法理解是怎么回事吧？但是这种事情确实发生了。");	
    } else if (status == 2) {	 
		qm.sendNext("唯一能解释这种超自然现象的人，应该只有一个人。那就是大精灵古瓦洛.....虽然他曾经被黑魔法师迷惑，成为了军团长，但现在已经不再是邪恶的人了。他正在某个地方休养生息。");	
    } else if (status == 3) {	 
	    qm.sendAcceptDecline("我们哈夫林代代都是天空,风和森林的朋友。使用部落代代相传的秘法，就能暂时和大精灵古瓦洛接触.....你想现在见见他吗？");
	} else if (status == 4) {
		qm.sendNext("好的请集中精神.....听到他的声音了吗？");
	} else if (status == 5) {
		qm.sendNext("岩壁巨人.....没想到造出了那种荒唐的东西。全都是我的错\r\n#b(听到了古瓦洛的声音。)");
	} else if (status == 6) {
		qm.sendNext("我也料到了可能会发生这种事。几百年前，我加入了黑魔法师的势力，后来被其中的某人背叛，被他吸收了力量.....一切都是从那时候开始的。我在很长的时间里丧失了对精灵的支配力，所以导致了奇怪事件的发生。");
	} else if (status == 7) {
		qm.sendNext("这是我的错误造成的，我本应该负起责任。但是我现在失去了力量.....请到米纳尔森林南部去调查一个叫岩壁巨人的巨人。");
	} else if (status == 8) {
		qm.sendNext("用普通的方法是无法和岩壁巨人对话的。不过我刚才已经把我的一部分力量分给了你，你现在应该可以和岩壁巨人对话了。如果我的推测没错的话.....\r\n#b(古瓦洛的神秘力量渗透进了身体。)");
	} else if (status == 9) {
		qm.sendNext("好的，再见。需要的时候，我会再找你的。");
	} else if (status == 10) {
		qm.forceStartQuest();
	    qm.dispose();		
	}
  }
  

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 2) {
            qm.sendOk("这是你的选择。");
            qm.dispose();
            return;
        }
        status--;
    }
	if (status == 0) {
	    qm.sendNext("你应该已经从大精灵古瓦洛那里听说了，这是非常严重的事件。不过对你这样的勇士来说，这件事一定也挺有趣的。怎么样？准好好了吗？\r\n\r\n#b怎么才能到岩壁巨人那里去呢？#k");
	} else if (status == 1) {
	    qm.sendNext("呵呵呵，你这就想走了吗？性子可真够急的。我们哈夫林中的几个探察队员已经去了哪里，他们可以帮助你\r\n\r\n#b哈夫林？#k");	
    } else if (status == 2) {	 
	    qm.sendYesNo("是的，我们种族大部分人都喜欢平静,和平,淳朴的生活，但是.....偶尔有些人生来就流淌着冒险家的血。我根本没办法阻止那些家伙。如果你想去的话，我可以马上送你过去。怎么样？");
	} else if (status == 3) {	 
	    qm.sendNext("好的，我马上就送你去。去了之后，顺便帮我看看那里的哈夫林的情况。");
	} else if (status == 4) {
		qm.forceCompleteQuest();
		qm.warp(240090000)
		qm.gainExp(1925763);
	    qm.dispose();		
	}
  }