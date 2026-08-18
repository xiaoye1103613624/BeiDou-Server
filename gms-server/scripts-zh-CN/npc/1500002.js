/* Andre
	Kerning Random Hair/Hair Color Change.
 */
var status = -1;
var beauty = 0;
var hair_Colo_new;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0) {
		cm.dispose();
		return;
	} else {
		status++;
	}

	if (status == 0) {
		var txt  = "";
			txt += "虽然这些孩子们非常的调皮，但是我却很喜爱他们。\r\n\r\n";
			if(cm.getQuestStatus(10107004) != 2 && cm.getQuestStatus(10107003) == 2 ){
				txt += "#L1##v4031025##b库迪真的是劫匪吗？#k#l\r\n";
			}else if(cm.getQuestStatus(10107015) != 2 && cm.getQuestStatus(10107014) == 2 ){
				txt += "#L2##v4031025##b孩子们在偷偷准备关于黑魔法师的演出#k#l\r\n";
			}else if(cm.getQuestStatus(10107015) == 2 ){
				//可以随意的进入后院
				txt += "#L3##v4031025##b进入后院#k#l\r\n";
			}else{
				cm.dispose();
			}
		cm.sendSimple(txt);
	} else if (status == 1) {
		if (selection == 1) {
			cm.sendOk("哼~又是一个外乡人。你是不是和他是一伙的？那个人前几天在附近鬼鬼祟祟的非常可疑，我不得不把他抓起来。" +
					"我不管你是什么人，总之如果你想证明你们是无辜的，就要拿出证据来。");
			cm.completeQuest(10107004);//完成对库迪的辩解
			cm.dispose();
		}else if(selection == 2){
			cm.sendOk("啊？哎呀....这完全怪我，当初我发现他们弄这些的时候，还在批评他们不要弄这些没用的事情。" +
					"\r\n他们一定是偷偷的跑到后院去了，那里有一个舞台，不过有一只很厉害的地鼠王....天呐!孩子们，老师来救你们啦。");
			cm.completeQuest(10107015);//完成孩子们在偷偷准备关于黑魔法师的演出
			cm.dispose();
		}else if(selection == 3){
			cm.warp(101073000);
			cm.dispose();
		}
	}
	
}
