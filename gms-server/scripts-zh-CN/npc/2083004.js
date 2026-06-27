/*
	NPC Name: 		Mark of the Squad
	Map(s): 		Entrance to Horned Tail's Cave
	Description: 		Horntail Battle starter
*/
var status = -1;
var ExpeditionType = Java.type("org.gms.server.expeditions.ExpeditionType");

function start() {
    if (cm.getPlayer().getLevel() < 120) {
        cm.sendOk("你必须高于120级的要求，试图挑战暗黑龙王.");
        cm.dispose();
        return;
    }
    if (cm.getPlayer().getBossLog("每日黑龙") > 3) {
        cm.sendOk("你今天已经完成挑战3次.");
        cm.dispose();
        return;
    }
    var em = cm.getEventManager("HorntailBattle");
    if (em == null) {
        cm.sendOk("该事件未启动，请联系GM.");
        cm.dispose();
        return;
    }
    var prop = em.getProperty("state");
    var marr = cm.getQuestRecord(160100);
    var data = marr.getCustomData();
    if (data == null) {
        marr.setCustomData("0");
        data = "0";
    }
    var time = parseInt(data);
    if (prop == null || prop.equals("0")) {
        var exped = cm.getExpedition(ExpeditionType.HORNTAIL);
        if (exped == null) {
            status = 0;
            cm.sendYesNo("你在成为远征队的队长感兴趣?每日可以打#r3#n#k次");
        } else if (cm.isLeaderExpedition(ExpeditionType.HORNTAIL)) {
            status = 10;
            cm.sendSimple("那你想做的事? \r\n#b#L0#查看队员#l \r\n#b#L1#删除成员#l \r\n#b#L2#编辑限制列表#l \r\n#r#L3#进入地图#l");
        } else {
            if (exped.getBanned().contains(cm.getPlayer().getId())) {
                cm.sendOk("从班长，你被禁止.");
                cm.dispose();
            } else if (exped.getMembers().containsKey(cm.getPlayer().getId())) {
                status = 5;
                cm.sendSimple("那你想做的事? \r\n#b#L0#查看队员#l \r\n#b#L1#加入队伍#l \r\n#b#L2#从队伍中撤离#l");
            } else {
                status = 5;
                cm.sendSimple("那你想做的事? \r\n#b#L0#查看队员#l \r\n#b#L1#加入队伍#l \r\n#b#L2#从队伍中撤离#l");
            }
        }
    } else {
        var eim = cm.getPlayer().getEventInstance();
        if (eim == null) {
            cm.sendOk("队内对抗boss战已经开始.");
            cm.safeDispose();
        } else {
            cm.sendYesNo("啊，你回来了。你想重新加入你的队伍在打?");
            status = 1;
        }
    }
}

function action(mode, type, selection) {
    switch (status) {
    case 0:
        if (mode == 1) {
            if (cm.getBossLog("每日黑龙") > 3) {
                cm.sendOk("你今天已经完成挑战3次.");
                cm.dispose();
                return;
            }
            var result = cm.createExpedition(ExpeditionType.HORNTAIL);
            if (result == 0) {
                cm.setBossLog("每日黑龙");
                cm.sendOk("你已经被任命为球队的队长。在接下来的5分钟，你可以添加探险队的成员.");
            } else if (result == 1) {
                cm.sendOk("你今天已经达到远征次数上限。");
            } else {
                cm.sendOk("发生错误加入你的队伍.");
            }
        }
        cm.dispose();
        break;
    case 1:
        if (mode == 1) {
            var eim = cm.getPlayer().getEventInstance();
            if (eim != null) {
                eim.registerPlayer(cm.getPlayer());
                cm.sendOk("你已重新加入远征战斗。");
            } else {
                cm.sendOk("错误...请重试.");
            }
        }
        cm.safeDispose();
        break;
    case 5:
        if (mode != 1) {
            cm.dispose();
            break;
        }
        var exped5 = cm.getExpedition(ExpeditionType.HORNTAIL);
        if (exped5 == null) {
            cm.sendOk("小队已经结束，请重新注册.");
            cm.dispose();
            break;
        }
        if (selection == 0) {
            showMembers(exped5);
        } else if (selection == 1) {
            if (cm.getBossLog("每日黑龙") > 3) {
                cm.sendOk("你今天已经完成挑战3次.");
                cm.dispose();
                return;
            }
            var joinResult = exped5.addMemberInt(cm.getPlayer());
            if (joinResult == 0) {
                cm.setBossLog("每日黑龙");
                cm.sendOk("您已成功加入了队伍");
            } else if (joinResult == 2) {
                cm.sendOk("从班长，你被禁止.");
            } else if (joinResult == 3) {
                cm.sendOk("队伍目前已满，请稍后再试.");
            } else {
                cm.sendOk("You are already part of the squad.");
            }
        } else {
            if (exped5.removeMember(cm.getPlayer())) {
                cm.sendOk("您已经从队伍成功及回收");
            } else {
                cm.sendOk("你是不是队伍的一部分.");
            }
        }
        cm.dispose();
        break;
    case 10:
        if (mode != 1) {
            cm.dispose();
            break;
        }
        var exped10 = cm.getExpedition(ExpeditionType.HORNTAIL);
        if (exped10 == null) {
            cm.sendOk("小队已经结束，请重新注册.");
            cm.dispose();
            break;
        }
        if (selection == 0) {
            showMembers(exped10);
            cm.dispose();
        } else if (selection == 1) {
            var memberList = exped10.getMemberList();
            if (memberList.size() <= 1) {
                cm.sendOk("没有可管理的队员.");
                cm.dispose();
            } else {
                status = 11;
                var list = "选择要删除的队员:\r\n";
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
            var dd = cm.getEventManager("HorntailBattle");
            dd.startInstance(exped10, cm.getMap(), 160100);
            cm.gainMeso(-1000000);
            cm.dispose();
        }
        break;
    case 11:
        if (mode == 1 && selection >= 0) {
            var exped11 = cm.getExpedition(ExpeditionType.HORNTAIL);
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
            var exped12 = cm.getExpedition(ExpeditionType.HORNTAIL);
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