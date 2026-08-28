/* ==================
 脚本类型:  任务	    
 脚本版权：游戏盒团队
 联系扣扣：297870163    609654666
 =====================
 */
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
			qm.forceStartQuest(34107);
            qm.forceCompleteQuest(34107);
            qm.sendNext("…我正好要去死亡火焰地带…… 如果你也要去的话，我可以载你一程……\r\n\r\n#b（接受时坐船，移动到忘却之湖）");
        } else if (status == 1) {
            qm.sendYesNo("…那么，出发…");
        } else if (status == 2) {
            qm.forceCompleteQuest();
            //qm.forceCompleteQuest(56201);
            //qm.forceCompleteQuest(56211);
            //qm.forceCompleteQuest(56203);
            qm.warp(450001007); //703100010
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    qm.dispose();
}