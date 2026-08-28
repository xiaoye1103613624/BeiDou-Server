

var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            if (status == 0) {
                qm.sendOk("这可是非常好的机会，真遗憾。如果你改变了注意，就跟我说。我会再次给你到美丽的度假村去的机会.....");
                qm.dispose();
            }
            status--;
        }
        if (status == 0) {
            if (qm.getMapId() == 180000001) {
                qm.sendOk("很遗憾，您因为违反用户守则被禁止游戏活动，如有异议请联系管理员.");
                qm.dispose();
            } else {
	    qm.sendAcceptDecline("你好？我是大贸易商金利奇。我想给你一个特别的机会。冒险岛最好的度假胜地金海滩度假村即将开业，在开业之前我想给你一次体验的机会。你想现在就去吗？\r\n#b（#r金海滩#b是特殊主体副本。提供#r50#b级以下和勇士等级对应的怪物和任务。）");
            }
        } else if (status == 1) {
	    qm.sendOk("好的，我现在就把你送到金海滩度假村去。");	
        } else if (status == 2) {
		qm.forceCompleteQuest();
            qm.warp(120040300);
            qm.dispose();
        }
    }

 } 
  
  
  
  
  
function end(mode, type, selection) {
	qm.dispose();
}


 



