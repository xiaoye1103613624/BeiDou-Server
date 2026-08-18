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
			txt += "你好，我是魔法师库迪，为了学习更多的魔法，我就来到了妖精学院做魔法研究。\r\n\r\n";
			if(cm.getQuestStatus(10107003) != 2){
				txt += "#L1##v4031025##b我想知道这里发生了什么事#k#l\r\n";
			}else if(cm.getQuestStatus(10107005) != 2 && cm.getQuestStatus(10107004) == 2 ){
				txt += "#L2##v4031025##b寻找证据#k#l";
			}else if(cm.getQuestStatus(10107006) != 2 && cm.getQuestStatus(10107005) == 2 ){
				txt += "#L3##v4031025##b被抢走的残页#k#l";
			}else if(cm.getQuestStatus(10107008) != 2 && cm.getQuestStatus(10107007) == 2 ){
				txt += "#L4##v4031025##b如何处理#k#l";
			}else if(cm.getQuestStatus(10107010) != 2 && cm.getQuestStatus(10107009) == 2 ){
				txt += "#L5##v4031025##b校长同意了#k#l";
			}else if(cm.getQuestStatus(10107011) != 2 && cm.getQuestStatus(10107010) == 2 &&cm.getMapId()==101072200){
				//二楼图书室
				txt += "#L6##v4031025##b展开行动吧#k#l";
			}else if(cm.getQuestStatus(10107012) != 2 && cm.getQuestStatus(10107011) == 2 &&cm.getMapId()==101072200){
				//二楼图书室
				txt += "#L7##v4031025##b妖精的剧本#k#l";
			}else if(cm.getQuestStatus(10107013) != 2 && cm.getQuestStatus(10107012) == 2 &&cm.getMapId()==101072700){
				//三楼楼道
				txt += "#L8##v4031025##b继续搜集线索#k#l";
			}else if(cm.getQuestStatus(10107014) != 2 && cm.getQuestStatus(10107013) == 2 &&cm.getMapId()==101072700){
				//三楼楼道
				txt += "#L9##v4031025##b妖精的戏服#k#l";
			}else{
				cm.dispose();
			}
			
			
		cm.sendSimple(txt);
	} else if (status == 1) {
		if (selection == 1) {				 
			cm.sendOk("事情是这样的，前几天我为了学习新的魔法来到这附近做一些研究，突然有人喊到：“把这个劫匪抓起来！”然后我就被他们抓到了这里。" +
					"他们说是我绑架了学院的小妖精，可是我真的没有做那种事情。可以请你帮我辩解一下吗？\r\n#r（请找副校长对话）#k");
			cm.completeQuest(10107003);//完成发生了什么事
			cm.dispose();
		}else if (selection == 2) {
			cm.openNpc(1500011,200);
		}else if (selection == 3) {
			cm.openNpc(1500011,300);
		}else if (selection == 4) {
			cm.sendOk("太好了，这下总算是自由了。诶？你说校长因为小妖精们不见了很难过。那我们来帮助他们寻找小精灵们好了。" +
					"我打算去孩子们经常生活的地方去寻找一些线索，不过请你先去征得校长的同意。\r\n#r（请找校长对话）#k");
			cm.completeQuest(10107008);//完成如何处理
			cm.dispose();
		}else if (selection == 5) {
			cm.sendOk("校长同意了，那我们就出发吧，我去二楼等你。");
			cm.completeQuest(10107010);//完成前往二楼
			cm.dispose();
		}else if (selection == 6) {
			cm.openNpc(1500011,600);
		}else if (selection == 7) {
			if(cm.haveItem(4462004,1)){
				cm.gainItem(4462004,-1);
				cm.gainExp(100000);
				cm.sendOk("恩，果然是很奇怪，我猜他们一定是在准备一场黑魔法师侵吞冒险岛的演出。我们再去三楼看看吧");
				cm.completeQuest(10107012);//完成获得剧本
				cm.dispose();
			}else{
				cm.sendOk("没有发现一些什么吗？再去找一找吧");
				cm.dispose();
			}
		}else if (selection == 8) {
			cm.openNpc(1500011,800);
		}else if (selection == 9) {
			if(cm.haveItem(4462005,1)){
				cm.gainItem(4462005,-1);
				cm.gainExp(100000);
				cm.sendOk("现在明白了一件事，孩子们在偷偷的准备一场关于黑魔法师的演出。\r\n#r现在我们去见副校长吧。#k");
				cm.completeQuest(10107014);//完成获得戏服
				cm.dispose();
			}else{
				cm.sendOk("没有发现一些什么吗？再去找一找吧");
				cm.dispose();
			}
		}
		
	} 
	
}
