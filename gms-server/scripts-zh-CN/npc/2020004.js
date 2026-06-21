var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status <= 1) {
	    cm.sendNext("需要去再来找我吧!");
	    cm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
    cm.sendSimple(" 你好我是仓库管理员，你需要什么，还是做跑商任务..\r\n#r#e\r\n#L0#跑商任务8环#n\r\n#d#e\r\n#L1#打开仓库#n");
    } else if (status == 1) {
	if (selection == 0) {
	    cm.openNpc(2020004, 2);
     }	
	if (selection == 1) {
	    cm.sendStorage();
	    cm.dispose();
     }		
	
	}
	}