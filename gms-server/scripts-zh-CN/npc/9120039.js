/*
	NPC Name: 		Old Fox Flagship Al
	Map(s): 		2102 Old Fox Flagship Deck : Zipangu
	Description: 		Nibergen Battle starter
*/
var status = -1;
var ExpeditionType = Java.type("org.gms.server.expeditions.ExpeditionType");

function start() {
    if (cm.getMapId() == 802000610) {
        if (cm.getPlayer().getClient().getChannel() != 2) {
            cm.sendOk("参加远征任务请到 2 频道.");
            cm.dispose();
            return;
        }
        var em = cm.getEventManager("Nibergen");
        if (em == null) {
            cm.sendOk("脚本出错，请联系管理员.");
            cm.dispose();
            return;
        }
        var prop = em.getProperty("state");
        if (prop == null || prop.equals("0")) {
            var exped = cm.getExpedition(ExpeditionType.NIBERGEN);
            if (exped == null) {
                status = 0;
                cm.sendYesNo("你想成为远征队长吗？");
            } else if (cm.isLeaderExpedition(ExpeditionType.NIBERGEN)) {
                status = 10;
                cm.sendSimple("你想做什么?远征队长。 \r\n#b#L0#查看远征队#l \r\n#b#L1#制裁远征队员#l \r\n#b#L2#查看制裁名单#l \r\n#r#L3#开始远征任务#l");
            } else {
                if (exped.getBanned().contains(cm.getPlayer().getId())) {
                    cm.sendOk("你被加入制裁名单，不能进行远征任务.");
                    cm.dispose();
                } else {
                    status = 5;
                    cm.sendSimple("你想干什么? \r\n#b#L0#查看远征队#l \r\n#b#L1#加入远征队#l \r\n#b#L2#离开远征队#l");
                }
            }
        } else {
            var eim = cm.getPlayer().getEventInstance();
            if (eim == null) {
                cm.sendOk("远征任务已经开始");
                cm.safeDispose();
            } else {
                cm.sendYesNo("你要继续远征任务吗?");
                status = 2;
            }
        }
    } else {
        status = 25;
        cm.sendNext("你想退出远征队吗?");
    }
}

function action(mode, type, selection) {
    switch (status) {
    case 0:
        if (mode == 1) {
            var result = cm.createExpedition(ExpeditionType.NIBERGEN);
            if (result == 0) {
                cm.sendOk("你已经成为远征队长，请在5分钟内整理好你的远征队伍，并开始远征任务。");
            } else if (result == 1) {
                cm.sendOk("你今天已经达到远征次数上限。");
            } else {
                cm.sendOk("未知错误。成为远征队长失败");
            }
        }
        cm.dispose();
        break;
    case 2:
        if (mode == 1) {
            var eim = cm.getPlayer().getEventInstance();
            if (eim != null) {
                eim.registerPlayer(cm.getPlayer());
                cm.sendOk("你已重新加入远征战斗。");
            } else {
                cm.sendOk("错误。。请再试一次");
            }
        }
        cm.safeDispose();
        break;
    case 5:
        if (mode != 1) {
            cm.dispose();
            break;
        }
        var exped5 = cm.getExpedition(ExpeditionType.NIBERGEN);
        if (exped5 == null) {
            cm.sendOk("远征队已经注销，请重新发起。");
            cm.dispose();
            break;
        }
        if (selection == 0) {
            showMembers(exped5);
        } else if (selection == 1) {
            if (exped5.getMembers().containsKey(cm.getPlayer().getId())) {
                cm.sendOk("你已经加入远征队了.");
            } else {
                var joinResult = exped5.addMemberInt(cm.getPlayer());
                if (joinResult == 0) {
                    cm.sendOk("加入远征队成功");
                } else if (joinResult == 2) {
                    cm.sendOk("你被加入制裁名单，不能进行远征任务.");
                } else if (joinResult == 3) {
                    cm.sendOk("远征队人数已经足够。请稍后再试");
                } else {
                    cm.sendOk("远征队已经注销，请重新发起。");
                }
            }
        } else if (selection == 2) {
            if (exped5.removeMember(cm.getPlayer())) {
                cm.sendOk("退出远征队成功");
            } else {
                cm.sendOk("你还没有加入远征队.");
            }
        }
        cm.dispose();
        break;
    case 10:
        if (mode != 1) {
            cm.dispose();
            break;
        }
        var exped10 = cm.getExpedition(ExpeditionType.NIBERGEN);
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
                cm.sendOk("没有可制裁的队员。");
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
                cm.sendOk("制裁名单为空。");
                cm.dispose();
            } else {
                status = 12;
                var list = "选择要解除制裁的队员:\r\n";
                for (var i = 0; i < bannedList.size(); i++) {
                    var chr = cm.getPlayer().getMap().getWorldServer().getPlayerStorage().getCharacterById(bannedList.get(i));
                    var name = chr != null ? chr.getName() : "ID:" + bannedList.get(i);
                    list += "#L" + i + "#" + name + "#l\r\n";
                }
                cm.sendSimple(list);
            }
        } else if (selection == 3) {
            if (exped10 != null) {
                var dd = cm.getEventManager("Nibergen");
                dd.startInstance(exped10);
            } else {
                cm.sendOk("由于未知的错误，对远征队的要求被拒绝。");
            }
            cm.dispose();
        }
        break;
    case 11:
        if (mode == 1 && selection >= 0) {
            var exped11 = cm.getExpedition(ExpeditionType.NIBERGEN);
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
            var exped12 = cm.getExpedition(ExpeditionType.NIBERGEN);
            if (exped12 != null) {
                var bannedList = exped12.getBanned();
                if (selection < bannedList.size()) {
                    exped12.unban(bannedList.get(selection));
                }
            }
        }
        cm.dispose();
        break;
    case 25:
        cm.warp(802000610, 0);
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