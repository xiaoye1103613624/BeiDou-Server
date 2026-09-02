var status = -1;

function end(mode,type,selection) {
	if(mode == -1){
		status--;
	} else {
		status++;
	}
	if(status == 0){
		qm.forceStartQuest();
		qm.dispose();
	} else{
		qm.dispose();
	}
}