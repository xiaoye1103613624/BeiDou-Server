var status = -1;
var ExpeditionType = Java.type("org.gms.server.expeditions.ExpeditionType");

function start() {
    cm.removeAll(4001256);
    cm.removeAll(4001257);
    cm.removeAll(4001258);
    cm.removeAll(4001259);
    cm.removeAll(4001260);
    if (cm.getPlayer().getLevel() < 90) {
        cm.sendOk("There is a level requirement of 90 to attempt Crimsonwood Keep.");
        cm.dispose();
        return;
    }
    var em = cm.getEventManager("CWKPQ");
    if (em == null) {
        cm.sendOk("The event isn't started, please contact a GM.");
        cm.dispose();
        return;
    }
    var prop = em.getProperty("state");
    var exped = cm.getExpedition(ExpeditionType.CWKPQ);

    if (prop == null || prop.equals("0")) {
        if (exped == null) {
            status = 0;
            cm.sendYesNo("Are you interested in becoming the leader of the expedition Squad?");
        } else if (cm.isLeaderExpedition(ExpeditionType.CWKPQ)) {
            status = 10;
            cm.sendSimple("What do you want to do? \r\n#b#L0#Check out members#l \r\n#b#L1#Remove member#l \r\n#b#L3#Check out jobs#l \r\n#r#L4#Enter map#l");
        } else {
            status = 5;
            cm.sendSimple("What do you want to do? \r\n#b#L0#Check out members#l \r\n#b#L1#Join the squad#l \r\n#b#L2#Withdraw from squad#l \r\n#b#L3#Check out jobs#l");
        }
    } else {
        var eim = cm.getPlayer().getEventInstance();
        if (eim == null) {
            cm.sendOk("The battle against the boss has already begun.");
            cm.safeDispose();
        } else {
            cm.sendYesNo("Ah, you have returned. Would you like to join your squad in the fight again?");
            status = 1;
        }
    }
}

function action(mode, type, selection) {
    switch (status) {
    case 0:
        if (mode == 1) {
            if (!cm.haveItem(4032012, 1)) {
                cm.sendOk("You need 1 Crimson Heart to apply.");
            } else {
                var result = cm.createExpedition(ExpeditionType.CWKPQ);
                if (result == 0) {
                    cm.sendOk("You have been named the Leader of the Squad. For the next 5 minutes, you can add the members of the Expedition Squad.");
                } else if (result == 1) {
                    cm.sendOk("You have reached the daily limit for this expedition.");
                } else {
                    cm.sendOk("An error has occurred adding your squad.");
                }
            }
        }
        cm.dispose();
        break;
    case 1:
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
        var exped5 = cm.getExpedition(ExpeditionType.CWKPQ);
        if (exped5 == null) {
            cm.sendOk("The squad has ended, please re-register.");
            cm.dispose();
            break;
        }
        if (selection == 0) {
            showMembers(exped5, false);
        } else if (selection == 3) {
            showMembers(exped5, true);
        } else if (selection == 1) {
            if (exped5.getMembers().containsKey(cm.getPlayer().getId())) {
                cm.sendOk("You are already part of the squad.");
            } else {
                var joinResult = exped5.addMemberInt(cm.getPlayer());
                if (joinResult == 0) {
                    cm.sendOk("You have joined the squad successfully");
                } else if (joinResult == 2) {
                    cm.sendOk("You been banned from the squad.");
                } else if (joinResult == 3) {
                    cm.sendOk("The squad is currently full, please try again later.");
                } else {
                    cm.sendOk("The squad has ended, please re-register.");
                }
            }
        } else if (selection == 2) {
            var removed = exped5.removeMember(cm.getPlayer());
            if (removed) {
                cm.sendOk("You have withdrawed from the squad successfully");
            } else {
                cm.sendOk("You are not part of the squad.");
            }
        }
        cm.dispose();
        break;
    case 10:
        if (mode != 1) {
            cm.dispose();
            break;
        }
        var exped10 = cm.getExpedition(ExpeditionType.CWKPQ);
        if (exped10 == null) {
            cm.sendOk("The squad has ended, please re-register.");
            cm.dispose();
            break;
        }
        if (selection == 0) {
            showMembers(exped10, false);
            cm.dispose();
        } else if (selection == 3) {
            showMembers(exped10, true);
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
        } else if (selection == 4) {
            if (cm.haveItem(4032012, 1)) {
                cm.gainItem(4032012, -1);
                var dd = cm.getEventManager("CWKPQ");
                dd.startInstance(exped10);
            } else {
                cm.sendOk("Where is my Crimson Heart?");
            }
            cm.dispose();
        }
        break;
    case 11:
        if (mode == 1 && selection >= 0) {
            var exped11 = cm.getExpedition(ExpeditionType.CWKPQ);
            if (exped11 != null) {
                var mList = exped11.getMemberList();
                if (selection + 1 < mList.size()) {
                    exped11.ban(mList.get(selection + 1));
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

function showMembers(exped, showJobs) {
    var memberList = exped.getMemberList();
    var str = "";
    if (showJobs) {
        str += "#bJobs:#k\r\n";
        for (var i = 0; i < memberList.size(); i++) {
            var entry = memberList.get(i);
            var chr = cm.getPlayer().getMap().getWorldServer().getPlayerStorage().getCharacterById(entry.getKey());
            var jobName = "Unknown";
            if (chr != null) {
                jobName = chr.getJob().name();
            }
            str += "#b" + entry.getValue() + "#k : " + jobName + "\r\n";
        }
    } else {
        str += "#bMembers:#k\r\n";
        for (var i = 0; i < memberList.size(); i++) {
            var entry = memberList.get(i);
            var leaderMark = (i == 0) ? " (Leader)" : "";
            str += "#b" + entry.getValue() + "#k" + leaderMark + "\r\n";
        }
    }
    cm.sendOk(str);
}