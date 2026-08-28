var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            if (status == 0) {
                qm.dispose();
                return;
            } else if (status == 1) {
				
                qm.sendOk("真的没人来救我了吗？呜呜……");
                qm.dispose();
            }
            status--;
        }
        if (status == 0) {
            qm.sendNext("这个……？纸飞机？");
        } else if (status == 1) {
			qm.sendNext("…………");
		} else if (status == 2) {
			qm.sendNext("不，好像不是纸飞机……是从哪里飞来的呢？");
		} else if (status == 3) {
			qm.sendNext("里面好像写着什么字。要打开看看吗？");
		} else if (status == 4) {
			qm.sendNext("\r\n#e尖耳守备队招募新队员！#n\r\n\r\n收到这封信之后，马上到格兰蒂斯的尖耳狐狸村来！\r\n若想马上移动到这里，就闻一下这张纸的味道。\r\n\r\n#b-玛鲁-#k");
		} else if (status == 5) {
			qm.sendNext("尖耳狐狸村？格兰蒂斯有个叫尖耳狐狸村的地方吗？");
		} else if (status == 6) {
            qm.sendYesNo("去看看折了这架纸飞机的#b玛鲁#k到底有什么事，\r\n好像也没什么坏处……要去看看吗？\r\n\r\n#b（接受时会自动移动。使用#r万神殿的叶片飞机#k，可以随时移动。)");
        } else if (status == 7) {
                                qm.forceStartQuest(34740);
								//qm.updateInfoQuest(34770,
                                qm.forceStartQuest(34768);
								qm.forceCompleteQuest(34769);
								qm.dispose();
                                qm.warp(940204100, 0);
                                //qm.npc_LeaveField("oid=1");
                                //qm.npc_LeaveField("oid=1");
                                qm.dispose()
                            
        }
    }
}

function end(mode, type, selection) {
    qm.dispose();
}