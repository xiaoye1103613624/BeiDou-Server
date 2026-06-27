/* Mu Young
	Boss Balrog
*/

var status = -1;
var ExpeditionType = Java.type("org.gms.server.expeditions.ExpeditionType");

function action(mode, type, selection) {
    switch (status) {
    case -1:
        status = 0;
        switch (cm.getChannelNumber()) {
        default:
            cm.sendNext("目前模式为 #i3994116# 如果你想加入这个模式请按下一步  条件是 等级 50 ~ 等级 120 / 远征队人数 1 ~ 30 个");
            break;
        }
        break;
    case 0:
        var em = cm.getEventManager("BossBalrog");
        if (em == null) {
            cm.sendOk("目前副本出了一点问题，请联系GM！");
            cm.safeDispose();
            return;
        }
        var prop = em.getProperty("state");
        if (prop == null || prop.equals("0")) {
            var exped = cm.getExpedition(ExpeditionType.BOSS_BALROG);
            if (exped == null) {
                status = 1;
                cm.sendYesNo("现在可以申请远征队，你想成为远征队队长吗？");
            } else if (cm.isLeaderExpedition(ExpeditionType.BOSS_BALROG)) {
                status = 10;
                cm.sendSimple("你现在想做什么？\r\n#b#L0#查看远征队成员。#l \r\n#b#L1#管理远征队成员。#l \r\n#b#L2#编辑限制列表。#l \r\n#r#L3#进入地图。#l");
            } else {
                if (exped.getBanned().contains(cm.getPlayer().getId())) {
                    cm.sendOk("在远征队的制裁名单。");
                    cm.safeDispose();
                } else if (exped.getMembers().containsKey(cm.getPlayer().getId())) {
                    status = 5;
                    cm.sendSimple("你要做什么? \r\n#b#L0#查看远征队名单#l \r\n#b#L1#加入远征队#l \r\n#b#L2#退出远征队#l");
                } else {
                    status = 5;
                    cm.sendSimple("你要做什么? \r\n#b#L0#查看远征队名单#l \r\n#b#L1#加入远征队#l \r\n#b#L2#退出远征队#l");
                }
            }
        } else {
            var eim = cm.getPlayer().getEventInstance();
            if (eim == null) {
                cm.sendOk("远征队的挑战已经开始.");
                cm.safeDispose();
            } else {
                cm.sendYesNo("你要继续进行远征任务吗？");
                status = 2;
            }
        }
        break;
    case 1:
        if (mode == 1) {
            var lvl = cm.getPlayerStat("LVL");
            if (lvl >= 50 && lvl <= 250) {
                var result = cm.createExpedition(ExpeditionType.BOSS_BALROG);
                if (result == 0) {
                    cm.sendOk("你已经成为了远征队队长。接下来的5分钟，请等待队员们的申请。");
                } else if (result == 1) {
                    cm.sendOk("你今天已经达到远征次数上限。");
                } else {
                    cm.sendOk("未知错误.");
                }
            } else {
                cm.sendNext("有一个远征队成员的等级不是50到120之间。");
            }
        } else {
            cm.sendOk("如果你想再次申请远征队的话请告诉我。")
        }
        cm.safeDispose();
        break;
    case 2:
        if (mode == 1) {
            var eim = cm.getPlayer().getEventInstance();
            if (eim != null) {
                eim.registerPlayer(cm.getPlayer());
                cm.sendOk("你已重新加入远征战斗。");
            } else {
                cm.sendOk("由于未知的错误，操作失败。");
            }
        }
        cm.safeDispose();
        break;
    case 5:
        if (mode != 1) {
            cm.dispose();
            break;
        }
        var exped5 = cm.getExpedition(ExpeditionType.BOSS_BALROG);
        if (exped5 == null) {
            cm.sendOk("远征队已经注销，请重新发起。");
            cm.safeDispose();
            break;
        }
        if (selection == 0) {
            showMembers(exped5);
            cm.dispose();
        } else if (selection == 1) {
            var joinResult = exped5.addMemberInt(cm.getPlayer());
            if (joinResult == 0) {
                cm.sendOk("申请加入远征队成功，请等候队长指示。");
            } else if (joinResult == 2) {
                cm.sendOk("在远征队的制裁名单。");
            } else if (joinResult == 3) {
                cm.sendOk("远征队员已经达到30名，请稍后再试。");
            } else {
                cm.sendOk("你已经参加了远征队，请等候队长指示。");
            }
        } else {
            if (exped5.removeMember(cm.getPlayer())) {
                cm.sendOk("成功退出远征队。");
            } else {
                cm.sendOk("你没有参加远征队。");
            }
        }
        cm.safeDispose();
        break;
    case 10:
        if (mode != 1) {
            cm.dispose();
            break;
        }
        var exped10 = cm.getExpedition(ExpeditionType.BOSS_BALROG);
        if (exped10 == null) {
            cm.sendOk("远征队已经注销，请重新发起。");
            cm.dispose();
            break;
        }
        if (selection == 0) {
            showMembers(exped10);
            cm.dispose();
        } else if (selection == 1) {
            var memberList = exped10.getMemberList();
            if (memberList.size() <= 1) {
                cm.sendOk("没有可管理的队员。");
                cm.dispose();
            } else {
                status = 11;
                var list = "选择要制裁的队员:\r\n";
                for (var i = 1; i < memberList.size(); i++) {
                    list += "#L" + (i - 1) + "#" + memberList.get(i).getValue() + "#l\r\n";
                }
                cm.sendSimple(list);
            }
        } else if (selection == 2) {
            var bannedList = exped10.getBanned();
            if (bannedList.isEmpty()) {
                cm.sendOk("限制列表为空.");
                cm.dispose();
            } else {
                status = 12;
                var banListStr = "选择要解除限制的队员:\r\n";
                for (var i = 0; i < bannedList.size(); i++) {
                    var chr = cm.getPlayer().getMap().getWorldServer().getPlayerStorage().getCharacterById(bannedList.get(i));
                    var name = chr != null ? chr.getName() : "ID:" + bannedList.get(i);
                    banListStr += "#L" + i + "#" + name + "#l\r\n";
                }
                cm.sendSimple(banListStr);
            }
        } else if (selection == 3) {
            var dd = cm.getEventManager("BossBalrog");
            dd.startInstance(exped10, cm.getMap());
            cm.dispose();
        }
        break;
    case 11:
        if (mode == 1 && selection >= 0) {
            var exped11 = cm.getExpedition(ExpeditionType.BOSS_BALROG);
            if (exped11 != null) {
                var mList = exped11.getMemberList();
                if (selection + 1 < mList.size()) {
                    exped11.ban(mList.get(selection + 1));
                }
            }
        }
        cm.dispose();
        break;
    case 12:
        if (mode == 1 && selection >= 0) {
            var exped12 = cm.getExpedition(ExpeditionType.BOSS_BALROG);
            if (exped12 != null) {
                var bannedList = exped12.getBanned();
                if (selection < bannedList.size()) {
                    exped12.unban(bannedList.get(selection));
                }
            }
        }
        cm.dispose();
        break;
    default:
        cm.dispose();
        break;
    }
}

function showMembers(exped) {
    var memberList = exped.getMemberList();
    var str = "#b远征队成员:#k\r\n";
    for (var i = 0; i < memberList.size(); i++) {
        var entry = memberList.get(i);
        var leaderMark = (i == 0) ? " (队长)" : "";
        str += "#b" + entry.getValue() + "#k" + leaderMark + "\r\n";
    }
    cm.sendOk(str);
}