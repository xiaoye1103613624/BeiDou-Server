/*
 * 任务 32109 - 妖精学院艾利涅（寻找线索）
 * 由 Kmst/HeavenMS 写法(cm) 改写为 北斗GMS083 风格(qm)
 *
 * 本脚本特殊点：
 *   1. start 无对话，直接 forceStartQuest + dispose
 *   2. end 包含选择菜单(askMenu→sendSimple)，选错回退循环
 *   3. 选错选项后 status -= 2 回退到菜单，选对才继续后续对话链
 *
 * 改写对照：
 *   cm.askMenu(msg)        → qm.sendSimple(msg)    选择菜单
 *   cm.sendNext(msg)       → qm.sendNext(msg)      首句/独立消息
 *   cm.sendNextPrev(msg,npcId) → qm.sendNextPrev(msg)  npcId参数省略
 *   cm.forceStartQuest()   → qm.forceStartQuest()   start无参数版本
 *   cm.forceCompleteQuest(32109) → qm.forceCompleteQuest()
 *   cm.gainExp(3600)       → qm.gainExp(3600)
 *   #h # (玩家名)          → #h0#  GMS083标准玩家名标签
 *   selectionLog[1]        → selection 参数直接使用
 *
 * 注意：end 函数不使用 mode==0&&type>0||selection==1 的拒绝检查，
 *       因为 selection==1 会与菜单选项1(湖中寻找)冲突。
 *       此脚本无 sendAcceptDecline，不需要拒绝分支。
 */

var status = -1;

function start(mode, type, selection) {
    // 无对话，直接启动任务
    qm.forceStartQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        // 选择菜单：3个选项，只有选项3是正确答案
        var msg = "呜呜……你们是来帮忙找孩子们吗？你们打算怎么帮忙？\r\n\r\n";
        msg += "#L1##b想在湖中寻找。#l\r\n";
        msg += "#L2##b想用魔法来搜寻孩子们的行踪。#l\r\n";
        msg += "#L3##b想在孩子们生活过的地方寻找线索。#l\r\n";
        qm.sendSimple(msg);
    } else if (status == 1) {
        // 根据菜单选择处理
        if (selection == 1) {
            // 错误选项1：湖中寻找 → 提示后回退到菜单
            qm.sendOk("你们刚刚没有仔细观察湖泊吗？很显然，水上很难留下什么证据。什么也无法找到。");
            status -= 2; // 回退：下次调用 mode=1 → status++ → status=0 → 重新显示菜单
        } else if (selection == 2) {
            // 错误选项2：魔法搜寻 → 提示后回退到菜单
            qm.sendOk("艾利涅周围的森林里，魔法气息非常强。魔法粒子的浓度非常强，所以用普通的探索魔法反而无法找到孩子们。");
            status -= 2; // 回退到菜单
        } else if (selection == 3) {
            // 正确选项3：寻找线索 → 继续后续对话链
            qm.sendNext("你打算以此来推断孩子们消失去了哪里吗？这个方案好像不错……");
        }
    } else if (status == 2) {
        qm.sendNextPrev("你还打算偷东西？校长先生，没必要听这个异邦人的话。");
    } else if (status == 3) {
        qm.sendNextPrev("虽然我也是半信半疑。可毕竟，孩子们的安全才是首位的，因此现在必须得试试。");
    } else if (status == 4) {
        // 原文"妖精字院艾利里"为笔误，已修正为"妖精学院艾利涅"
        // #h ##k → #h0##k (GMS083玩家名标签)
        qm.sendNextPrev("你叫#b#h0##k吧？我将允许你在妖精学院艾利涅的建筑内部进行搜索。2楼是男生宿舍，3楼是女生宿舍。但是这个建筑从一开始就是以防止外人入侵为目的进行设计的。所以，你得注意的是，除了艾利涅的老师或学生，其他人进入的话，建筑就会自动发动攻击。");
    } else if (status == 5) {
        qm.sendNextPrev("哼，你可别要花样，我会盯着你的！");
    } else if (status == 6) {
        // #h # → #h0# (GMS083玩家名标签)
        qm.sendOk("一定能够找到孩子们的，请不用担心。#h0#，我先到上面一层等你。\r\n#b(前往艾利涅2楼，与库迪见面。)");
        qm.gainExp(3600);
        qm.forceCompleteQuest();
        qm.dispose();
    }
}
