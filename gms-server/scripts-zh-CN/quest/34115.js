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
            qm.sendNext("#ho#，你醒过来啦。哈哈… 所幸你掉在了沙子上…");
        } else if (status == 1) {
			qm.sendNext("额，坠落时受的伤… 啊，没什么，不用在意。现在只要考虑怎么从这个洞穴里出去就好了。");
		} else if (status == 2) {
			qm.sendNext("#b(虽然担心卡奥的事，但现在好像没时间管这些了。)#k");
		} else if (status == 3) {
			qm.sendNext("这里就是消亡旅途的最后一站… 安息洞穴。这洞穴的终点就是消亡旅途的终点。现在基本上到了。");
		} else if (status == 4) {
			qm.forceCompleteQuest(34109);
            qm.sendYesNo("虽然这里像迷宫一样复杂… 但是我知道通向洞穴出口的捷径。好了，跟我来吧。");
        } else if (status == 5) {
                                qm.forceStartQuest(34115);
                                qm.forceCompleteQuest(34115);
                                //qm.npc_LeaveField("oid=1");
                                //qm.npc_LeaveField("oid=1");
                                qm.dispose()
                            
        }
    }
}

function end(mode, type, selection) {
    qm.dispose();
}