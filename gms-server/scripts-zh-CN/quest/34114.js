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
            qm.sendNext("虽然泉水所映射出的只是记忆片段。。。。但多亏于此！");
        } else if (status == 1) {
            qm.sendYesNo("既然已经找回了记忆，现在没理由继续在这里了！等火焰鸟一到，我会骑乘他回到无名村？");
        } else if (status == 2) {
            qm.forceCompleteQuest();
            //qm.forceCompleteQuest(56201);
            //qm.forceCompleteQuest(56211);
            qm.forceCompleteQuest(34114);
            qm.warp(450001200, 0); //703100010
            qm.dispose();
        }
    }
}

function end(mode, type, selection) {
    qm.dispose();
}
