var status = -1;
var ExpeditionType = Java.type("org.gms.server.expeditions.ExpeditionType");

function start() {
    if (cm.getPlayer().getMapId() == 211070100 || cm.getPlayer().getMapId() == 211070101 || cm.getPlayer().getMapId() == 211070110) {
        cm.sendYesNo("你是否要出去?出去后无法继续挑战");
        status = 1;
        return;
    }
    if (cm.getPlayer().getLevel() < 120) {
        cm.sendOk("There is a level requirement of 120 to attempt Von Leon.");
        cm.dispose();
        return;
    }
    if (cm.getPlayer().getClient().getChannel() != 7 && cm.getPlayer().getClient().getChannel() != 8 && cm.getPlayer().getClient().getChannel() != 9) {
        cm.sendOk("Von Leon may only be attempted on channel 7,8,9.");
        cm.dispose();
        return;
    }
    var em = cm.getEventManager("VonLeonBattle");
    if (em == null) {
        cm.sendOk("The event isn't started, please contact a GM.");
        cm.dispose();
        return;
    }
    var eim_status = em.getProperty("state");
    var marr = cm.getQuestRecord(160107);
    var data = marr.getCustomData();
    if (data == null) {
        marr.setCustomData("0");
        data = "0";
    }
    var time = parseInt(data);
    if (eim_status == null || eim_status.equals("0")) {
        var exped = cm.getExpedition(ExpeditionType.VONLEON);
        if (exped == null) {
            status = 0;
            if (time + (12 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                cm.sendOk("You have already went to VonLeon in the past 12 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (12 * 3600000)));
                cm.dispose();
                return;
            }
            cm.sendYesNo("Are you interested in becoming the leader of the expedition Squad?");
        } else if (cm.isLeaderExpedition(ExpeditionType.VONLEON)) {
            if (time + (12 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                cm.sendOk("You have already went to VonLeon in the past 12 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (12 * 3600000)));
                cm.dispose();
                return;
            }
            status = 10;
            cm.sendSimple("What do you want to do, expedition leader? \r\n#b#L0#View expedition list#l \r\n#b#L1#Kick from expedition#l \r\n#b#L2#Remove user from ban list#l \r\n#r#L3#Select expedition team and enter#l");
        } else {
            if (time + (12 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                cm.sendOk("You have already went to VonLeon in the past 12 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (12 * 3600000)));
                cm.dispose();
                return;
            }
            if (exped.getBanned().contains(cm.getPlayer().getId())) {
                cm.sendOk("You been banned from the squad.");
                cm.dispose();
            } else if (exped.getMembers().containsKey(cm.getPlayer().getId())) {
                status = 5;
                cm.sendSimple("What would you like to do? \r\n#b#L0#Join the squad#l \r\n#b#L1#Leave the squad#l \r\n#b#L2#See the list of members on the squad#l");
            } else {
                status = 5;
                cm.sendSimple("What would you like to do? \r\n#b#L0#Join the squad#l \r\n#b#L1#Leave the squad#l \r\n#b#L2#See the list of members on the squad#l");
            }
        }
    } else {
        var eim = cm.getPlayer().getEventInstance();
        if (eim == null) {
            cm.sendOk("The squad's battle against the boss has already begun.");
            cm.safeDispose();
        } else {
            cm.sendYesNo("Ah, you have returned. Would you like to join your squad in the fight again?");
            status = 2;
        }
    }
}

function action(mode, type, selection) {
    switch (status) {
    case 0:
        if (mode == 1) {
            var result = cm.createExpedition(ExpeditionType.VONLEON);
            if (result == 0) {
                cm.sendOk("You have been named the Leader of the Squad. For the next 5 minutes, you can add the members of the Expedition Squad.");
            } else if (result == 1) {
                cm.sendOk("You have reached the daily limit for this expedition.");
            } else {
                cm.sendOk("An error has occurred adding your squad.");
            }
        }
        cm.dispose();
        break;
    case 1:
        if (mode == 1) {
            cm.warp(910000000, 0);
        }
        cm.dispose();
        break;
    case 2:
        if (mode == 1) {
            var eim = cm.getPlayer().getEventInstance();
            if (eim != null) {
                eim.registerPlayer(cm.getPlayer());
                cm.sendOk("You have rejoined the battle.");
            } else {
                cm.sendOk("Error... please try again.");
            }
        }
        cm.safeDispose();
        break;
    case 5:
        if (mode != 1) {
            cm.dispose();
            break;
        }
        var exped5 = cm.getExpedition(ExpeditionType.VONLEON);
        if (exped5 == null) {
            cm.sendOk("The squad has ended, please re-register.");
            cm.dispose();
            break;
        }
        if (selection == 0) {
            var joinResult = exped5.addMemberInt(cm.getPlayer());
            if (joinResult == 0) {
                cm.sendOk("You have joined the squad successfully");
            } else if (joinResult == 2) {
                cm.sendOk("You been banned from the squad.");
            } else if (joinResult == 3) {
                cm.sendOk("The squad is currently full, please try again later.");
            } else {
                cm.sendOk("You are already part of the squad.");
            }
        } else if (selection == 1) {
            if (exped5.removeMember(cm.getPlayer())) {
                cm.sendOk("You have withdrawed from the squad successfully");
            } else {
                cm.sendOk("You are not part of the squad.");
            }
        } else if (selection == 2) {
            showMembers(exped5);
        }
        cm.dispose();
        break;
    case 10:
        if (mode != 1) {
            cm.dispose();
            break;
        }
        var exped10 = cm.getExpedition(ExpeditionType.VONLEON);
        if (exped10 == null) {
            cm.sendOk("The squad has ended, please re-register.");
            cm.dispose();
            break;
        }
        if (selection == 0) {
            showMembers(exped10);
            cm.dispose();
        } else if (selection == 1) {
            var memberList = exped10.getMemberList();
            if (memberList.size() <= 1) {
                cm.sendOk("There are no members to remove.");
                cm.dispose();
            } else {
                status = 11;
                var list = "Select member to remove:\r\n";
                for (var i = 1; i < memberList.size(); i++) {
                    list += "#L" + (i - 1) + "#" + memberList.get(i).getValue() + "#l\r\n";
                }
                cm.sendSimple(list);
            }
        } else if (selection == 2) {
            var bannedList = exped10.getBanned();
            if (bannedList.isEmpty()) {
                cm.sendOk("The ban list is empty.");
                cm.dispose();
            } else {
                status = 12;
                var banListStr = "Select member to unban:\r\n";
                for (var i = 0; i < bannedList.size(); i++) {
                    var chr = cm.getPlayer().getMap().getWorldServer().getPlayerStorage().getCharacterById(bannedList.get(i));
                    var name = chr != null ? chr.getName() : "ID:" + bannedList.get(i);
                    banListStr += "#L" + i + "#" + name + "#l\r\n";
                }
                cm.sendSimple(banListStr);
            }
        } else if (selection == 3) {
            var dd = cm.getEventManager("VonLeonBattle");
            dd.startInstance(exped10, cm.getMap(), 160107);
            cm.dispose();
        }
        break;
    case 11:
        if (mode == 1 && selection >= 0) {
            var exped11 = cm.getExpedition(ExpeditionType.VONLEON);
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
            var exped12 = cm.getExpedition(ExpeditionType.VONLEON);
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
    var str = "#bMembers:#k\r\n";
    for (var i = 0; i < memberList.size(); i++) {
        var entry = memberList.get(i);
        var leaderMark = (i == 0) ? " (Leader)" : "";
        str += "#b" + entry.getValue() + "#k" + leaderMark + "\r\n";
    }
    cm.sendOk(str);
}