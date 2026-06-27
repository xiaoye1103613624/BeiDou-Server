/*
	NPC 名字: 		Adobis
	所在地图: 		扎昆的祭台入口
	脚本名字: 		扎昆远征队
*/

var status = 0;
var ExpeditionType = Java.type("org.gms.server.expeditions.ExpeditionType");

function action(mode, type, selection) {
    if (cm.getPlayer().getMapId() == 211042200) { //艰苦洞穴Ⅲ
        if (selection < 100) {
            cm.sendSimple("#r#L100#扎昆#l\r\n#L101#进阶扎昆#l");
        } else {
            if (selection == 100) {
                cm.warp(211042300, 0); //扎昆入口
            } else if (selection == 101) {
                cm.warp(211042301, 0); //进阶扎昆入口
            }
            cm.dispose();
        }
        return;
    } else if (cm.getPlayer().getMapId() == 211042401) { //进阶扎昆的祭台入口
        switch (status) {
        case 0:
            if (cm.getPlayer().getLevel() < 100) {
                cm.sendOk("你的等级小于 100 级，无法挑战进阶扎昆。");
                cm.dispose();
                return;
            }
            var em = cm.getEventManager("ChaosZakum");
            if (em == null) {
                cm.sendOk("配置清单为空，请联系管理员。");
                cm.safeDispose();
                return;
            }
            var prop = em.getProperty("state");
            var marr = cm.getQuestRecord(160102);
            var data = marr.getCustomData();
            if (data == null) {
                marr.setCustomData("0");
                data = "0";
            }
            var time = parseInt(data);
            if (prop == null || prop.equals("0")) {
                var exped = cm.getExpedition(ExpeditionType.CHAOS_ZAKUM);
                if (exped == null) {
                    status = 1;
                    if (time + (12 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                        cm.sendOk("You have already went to Chaos Zakum in the past 12 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (12 * 3600000)));
                        cm.dispose();
                        return;
                    }
                    cm.sendYesNo("现在可以申请远征队，你想成为远征队队长吗？");
                } else if (cm.isLeaderExpedition(ExpeditionType.CHAOS_ZAKUM)) {
                    if (time + (12 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                        cm.sendOk("You have already went to Chaos Zakum in the past 12 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (12 * 3600000)));
                        cm.dispose();
                        return;
                    }
                    status = 10;
                    cm.sendSimple("你现在想做什么？\r\n#b#L0#查看远征队成员。#l \r\n#b#L1#管理远征队成员。#l \r\n#b#L2#编辑限制列表。#l \r\n#r#L3#进入地图。#l");
                } else {
                    if (time + (12 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                        cm.sendOk("You have already went to Chaos Zakum in the past 12 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (12 * 3600000)));
                        cm.dispose();
                        return;
                    }
                    if (exped.getBanned().contains(cm.getPlayer().getId())) {
                        cm.sendOk("在远征队的制裁名单。");
                        cm.safeDispose();
                    } else if (exped.getMembers().containsKey(cm.getPlayer().getId())) {
                        status = 5;
                        cm.sendSimple("你现在想做什么？\r\n#b#L0#查看远征队成员。#l \r\n#b#L1#加入远征队。#l \r\n#b#L2#退出远征队。#l");
                    } else {
                        status = 5;
                        cm.sendSimple("你现在想做什么？ \r\n#b#L0#查看远征队成员。#l \r\n#b#L1#加入远征队。#l \r\n#b#L2#退出远征队。#l");
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
            break;
        case 1:
            if (mode == 1) {
                var result = cm.createExpedition(ExpeditionType.CHAOS_ZAKUM);
                if (result == 0) {
                    cm.sendOk("你已经成为了远征队队长。接下来的5分钟，请等待队员们的申请。");
                } else if (result == 1) {
                    cm.sendOk("你今天已经达到远征次数上限。");
                } else {
                    cm.sendOk("An error has occurred adding your squad.");
                }
            } else {
                cm.sendOk("如果你想申请远征队的话，那么就来找我吧。")
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
            cm.dispose();
            break;
        case 5:
            if (mode != 1) {
                cm.dispose();
                break;
            }
            var exped5 = cm.getExpedition(ExpeditionType.CHAOS_ZAKUM);
            if (exped5 == null) {
                cm.sendOk("已经结束了申请。");
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
                    cm.sendOk("制裁指定的成员成功。");
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
            var exped10 = cm.getExpedition(ExpeditionType.CHAOS_ZAKUM);
            if (exped10 == null) {
                cm.sendOk("已经结束了申请。");
                cm.dispose();
                break;
            }
            if (selection == 0) {
                showMembers(exped10);
                cm.safeDispose();
            } else if (selection == 1) {
                var memberList = exped10.getMemberList();
                if (memberList.size() <= 1) {
                    cm.sendOk("没有可管理的队员。");
                    cm.safeDispose();
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
                    cm.sendOk("限制列表为空。");
                    cm.safeDispose();
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
                var dd = cm.getEventManager("ChaosZakum");
                dd.startInstance(exped10, cm.getMap(), 160102);
                cm.dispose();
            }
            break;
        case 11:
            if (mode == 1 && selection >= 0) {
                var exped11 = cm.getExpedition(ExpeditionType.CHAOS_ZAKUM);
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
                var exped12 = cm.getExpedition(ExpeditionType.CHAOS_ZAKUM);
                if (exped12 != null) {
                    var bannedList = exped12.getBanned();
                    if (selection < bannedList.size()) {
                        exped12.unban(bannedList.get(selection));
                    }
                }
            }
            cm.dispose();
            break;
        }
    } else {
        switch (status) {
        case 0:
            if (cm.getPlayer().getLevel() < 120) {
                cm.sendOk("你的等级小于 120 级，无法挑战扎昆。");
                cm.dispose();
                return;
            }

            if (cm.getMap(280030000).getCharactersSize() > 0) {
                cm.sendOk("里面有人正在挑战中...请稍后...");
                cm.dispose();
                return;
            }

            var em = cm.getEventManager("ZakumBattle");
            if (em == null) {
                cm.sendOk("配置清单为空，请联系管理员。");
                cm.safeDispose();
                return;
            }
            var prop = em.getProperty("state");
            var marr = cm.getQuestRecord(160101);
            var data = marr.getCustomData();
            if (data == null) {
                marr.setCustomData("0");
                data = "0";
            }
            var time = parseInt(data);
            if (prop == null || prop.equals("0")) {
                var exped = cm.getExpedition(ExpeditionType.ZAKUM);
                if (exped == null) {
                    status = 1;
                    if (cm.getBossLog("挑战扎昆") >= 3) {
                        cm.sendOk("对不起，扎昆每日每人只进3次哦！参与挑战也计算次数哦！");
                        cm.dispose();
                        return;
                    }
                    cm.sendYesNo("现在可以申请远征队，你想成为远征队队长吗？\r\n今日挑战：[#r" + cm.getBossLog("挑战扎昆") + "#k/#b3#k]");
                } else if (cm.isLeaderExpedition(ExpeditionType.ZAKUM)) {
                    status = 10;
                    cm.sendSimple("你现在想做什么？\r\n#b#L0#查看远征队成员。#l \r\n#b#L1#管理远征队成员。#l \r\n#b#L2#编辑限制列表。#l \r\n#r#L3#进入地图。#l");
                } else {
                    if (exped.getBanned().contains(cm.getPlayer().getId())) {
                        cm.sendOk("在远征队的制裁名单。");
                        cm.safeDispose();
                    } else if (exped.getMembers().containsKey(cm.getPlayer().getId())) {
                        status = 5;
                        cm.sendSimple("你现在想做什么？\r\n#b#L0#查看远征队成员。#l \r\n#b#L1#加入远征队。#l \r\n#b#L2#退出远征队。#l");
                    } else {
                        status = 5;
                        cm.sendSimple("你现在想做什么？\r\n#b#L0#查看远征队成员。#l \r\n#b#L1#加入远征队。#l \r\n#b#L2#退出远征队。#l");
                    }
                }
            } else {
                var eim = cm.getPlayer().getEventInstance();
                if (eim == null) {
                    cm.sendOk("小队与扎昆的战斗已经开始了。");
                    cm.safeDispose();
                } else {
                    cm.sendYesNo("啊，你回来了。你想再加入你的队伍吗？");
                    status = 1;
                }
            }
            break;
        case 1:
            if (mode == 1) {
                if (cm.getBossLog("挑战扎昆") >= 3) {
                    cm.sendOk("对不起，扎昆每日每人只进3次哦！参与挑战也计算次数哦！");
                    cm.dispose();
                    return;
                }
                var result = cm.createExpedition(ExpeditionType.ZAKUM);
                if (result == 0) {
                    cm.sendOk("你已经成为了远征队队长。接下来的5分钟，请等待队员们的申请。");
                    cm.setBossLog("挑战扎昆", cm.getBossLog("挑战扎昆") + 1);
                } else if (result == 1) {
                    cm.sendOk("你今天已经达到远征次数上限。");
                } else {
                    cm.sendOk("添加队时出错..");
                }
            } else {
                cm.sendOk("如果你想申请远征队的话，那么就来找我吧。")
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
            var exped5 = cm.getExpedition(ExpeditionType.ZAKUM);
            if (exped5 == null) {
                cm.sendOk("已经结束了申请。");
                cm.safeDispose();
                break;
            }
            if (selection == 0) {
                showMembers(exped5);
                cm.dispose();
            } else if (selection == 1) {
                if (cm.getBossLog("挑战扎昆") >= 3) {
                    cm.sendOk("对不起，扎昆每日每人只可挑战3次哦！参与挑战也计算次数哦！");
                    cm.dispose();
                    return;
                }
                var joinResult = exped5.addMemberInt(cm.getPlayer());
                if (joinResult == 0) {
                    cm.sendOk("申请加入远征队成功，请等候队长指示。");
                    cm.setBossLog("挑战扎昆", cm.getBossLog("挑战扎昆") + 1);
                } else if (joinResult == 2) {
                    cm.sendOk("在远征队的制裁名单。");
                } else if (joinResult == 3) {
                    cm.sendOk("远征队员已经达到30名，请稍后再试。");
                } else {
                    cm.sendOk("你已经参加了远征队，请等候队长指示。");
                }
            } else {
                if (exped5.removeMember(cm.getPlayer())) {
                    cm.sendOk("制裁指定的成员成功。");
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
            var exped10 = cm.getExpedition(ExpeditionType.ZAKUM);
            if (exped10 == null) {
                cm.sendOk("已经结束了申请。");
                cm.dispose();
                break;
            }
            if (selection == 0) {
                showMembers(exped10);
                cm.safeDispose();
            } else if (selection == 1) {
                var memberList = exped10.getMemberList();
                if (memberList.size() <= 1) {
                    cm.sendOk("没有可管理的队员。");
                    cm.safeDispose();
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
                    cm.sendOk("限制列表为空。");
                    cm.safeDispose();
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
                var dd = cm.getEventManager("ZakumBattle");
                dd.startInstance(exped10, cm.getMap(), 160101);
                cm.dispose();
            }
            break;
        case 11:
            if (mode == 1 && selection >= 0) {
                var exped11 = cm.getExpedition(ExpeditionType.ZAKUM);
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
                var exped12 = cm.getExpedition(ExpeditionType.ZAKUM);
                if (exped12 != null) {
                    var bannedList = exped12.getBanned();
                    if (selection < bannedList.size()) {
                        exped12.unban(bannedList.get(selection));
                    }
                }
            }
            cm.dispose();
            break;
        }
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