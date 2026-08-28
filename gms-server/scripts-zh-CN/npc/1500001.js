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
			txt += "真希望孩子们能够健康快乐的长大~呵呵。\r\n\r\n";
			if(cm.getQuestStatus(10107007) != 2 && cm.getQuestStatus(10107006) == 2){
				txt += "#L1##v4031025##b提交库迪的证据#k#l\r\n";
			}else if(cm.getQuestStatus(10107009) != 2 && cm.getQuestStatus(10107008) == 2){
				txt += "#L2##v4031025##b让我们来帮忙一起寻找小妖精们吧#k#l\r\n";
			}else if(cm.getQuestStatus(10107020) != 2 && cm.getQuestStatus(10107019) == 2){
				txt += "#L3##v4031025##b教训地鼠王#k#l\r\n";
			}else{
				cm.dispose();
			}
		cm.sendSimple(txt);
	} else if (status == 1) {
		if (selection == 1) {			
			cm.sendOk("喔，抱歉。看来真的是冤枉你们了。真是太对不起了，你们现在就可以离开这里了..........哎，孩子们~你们在哪里呀？\r\n\r\n" +
					"#r（看校长的样子真的很难过，先去找库迪吧。）#k");
			cm.completeQuest(10107007);//完成小精灵们怎么办
			cm.dispose();
		}else if (selection == 2) {
			cm.sendOk("你说要帮助我们寻找孩子们？是真的吗？太好了，之前我们还冤枉了你们，真是太抱歉了，库迪说要去孩子们经常生活的地方寻找线索真是个好主意，请你们上二楼看一看吧。");
			cm.completeQuest(10107009);//完成慷概相助
			cm.dispose();
		}else if (selection == 3) {
			cm.openNpc(1500001, 100);
		}
		
	} 
	
}
