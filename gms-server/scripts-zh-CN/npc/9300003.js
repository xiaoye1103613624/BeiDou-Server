/*
 PEAK冒险脚本
 脚本：结婚殿堂
 */
var jt = "#fUI/Basic/BtHide3/mouseOver/0#";
var 箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var victim;
var ring = 1112800; // 结婚戒指的ID
var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status <= 0 && mode <= 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    var MC = cm.getServerName();
    var 性别 = cm.getPlayer().getGender();
    var 结婚开关 = cm.GetPiot("结婚开关", "1");
    var 结婚价格 = cm.GetPiot("结婚价格", "1");
    var 离婚价格 = cm.GetPiot("离婚价格", "1");
    var 点券 = cm.getPlayer().getCSPoints(1);

    if (cm.GetPiot("自由传送开关", "2") == 100) {
        cm.showInstruction("", 200, 3);
        cm.dispose();
    } else if (status <= 0) {
        var selStr = "我是月老，你需要伴侣吗？我可是专门帮人介绍对象的，看你人不错，要不要介绍一个给你呢？\r\n#b";
        selStr += "#L3##b我要结婚[#r男女#k]#b#l\r\n";
        selStr += "#L4##b我要离婚[#r双方在场情况#k#b]#l\r\n";
        selStr += "\r\n_________________________________________________\r\n";
        selStr += "#L1##b我们要结拜为好兄弟[#r男男#k#b]#l\r\n";
        selStr += "#L2##b我们要结拜为好姐妹[#r女女#k#b]#l\r\n";

        if (cm.getPlayer().getGMLevel() == 6) {
            selStr += "\r\n#L100##d" + 箭头 + " 结婚手术费用#r[GM]#k#l";
            selStr += "\r\n#L101##d" + 箭头 + " 离婚手术费用#r[GM]#k#l";
            if (cm.GetPiot("结婚开关", "1") <= 0) {
                selStr += "\r\n#L500#" + 箭头 + " #b结婚#g[开启中]#r[GM]#k#l";
            }
            if (cm.GetPiot("结婚开关", "1") >= 1) {
                selStr += "\r\n#L501#" + 箭头 + " #b结婚#r[关闭中]#r[GM]#k#l";
            }
        }
        cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
            case 100:
                cm.dispose();
                cm.openNpc(9300003, 1);
                break;
            case 101:
                cm.dispose();
                cm.openNpc(9300003, 2);
                break;

            case 4: // 离婚
    if (点券 < 离婚价格) {
        cm.sendNext("  #b离婚需要 #r" + 离婚价格 + "#k#b 点券。");
        cm.dispose();
        return;
    }
    if (cm.getPlayer().getMarriageId() == 0) {
        cm.sendNext("你还没结婚呢？单身狗");
        cm.dispose();
        return;
    }
    if (cm.getParty() == null) {
        cm.sendNext("请和你的配偶组队哦！");
        cm.dispose();
        return;
    }
    if (!cm.isLeader()) {
        cm.sendNext("让队长与我对话。");
        cm.dispose();
        return;
    }

    var gender = cm.getPlayer().getGender();
    var mapId = cm.getPlayer().getMapId();
    var party = cm.getPlayer().getParty().getMembers();
    var it = party.iterator();
    var next = false; // 初始化 next 变量
    var spouseId = cm.getPlayer().getMarriageId(); // 获取配偶的 ID

    while (it.hasNext()) {
        var cPlayer = it.next();
        victim = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
        if (victim == null) {
            continue; // 如果 victim 为 null，跳过当前循环
        }
        if (victim.getId() == spouseId && 
            victim.getMapId() == mapId) {
            next = true;
            break;
        }
    }

    if (!next) {
        cm.sendNext("请确认您的配偶在同一张地图上，并且在队伍中。");
        cm.dispose();
        return;
    }

    // 更新婚姻状态
    cm.getPlayer().setMarriageId(0); // 清空玩家的婚姻 ID
    victim.setMarriageId(0); // 清空配偶的婚姻 ID
    cm.gainNX(-离婚价格); // 扣除点券
    cm.sendNext("离婚成功，恭喜你恢复单身生活。");
    cm.worldMessage(6, "[月老] : 恭喜 [" + cm.getChar().getName() + "] 和 [" + victim.getName() + "] 解除了婚姻关系。祝福你们各自幸福！");
	cm.worldMessage(6, "[月老] : 恭喜 [" + cm.getChar().getName() + "] 和 [" + victim.getName() + "] 解除了婚姻关系。祝福你们各自幸福！");
	cm.worldMessage(6, "[月老] : 恭喜 [" + cm.getChar().getName() + "] 和 [" + victim.getName() + "] 解除了婚姻关系。祝福你们各自幸福！");
    cm.dispose();
    break;
            case 3: // 男女结婚
                if (点券 < 结婚价格) {
                    cm.sendNext("  #b结婚需要 #r" + 结婚价格 + "#k#b 点券。");
                    cm.dispose();
                    return;
                }
                if (cm.getPlayer().getMarriageId() > 0) {
                    cm.sendNext("你已经结过婚，想离婚吗？");
                    cm.dispose();
                    return;
                }
                if (cm.getParty() == null) {
                    cm.sendNext("请和你的对象组队哦！");
                    cm.dispose();
                    return;
                }
                if (!cm.isLeader()) {
                    cm.sendNext("让队长与我对话。");
                    cm.dispose();
                    return;
                }
                var gender = cm.getPlayer().getGender();
                var mapId = cm.getPlayer().getMapId();
                var party = cm.getPlayer().getParty().getMembers();
                var it = party.iterator();
                var next = false; // 初始化 next 变量
                while (it.hasNext()) {
                    var cPlayer = it.next();
                    victim = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                    if (victim == null) {
                        continue; // 如果 victim 为 null，跳过当前循环
                    }
                    if (victim.getId() != cm.getPlayer().getId() && 
                        party.size() <= 2 && 
                        victim.getMarriageId() == 0 && 
                        victim.getMapId() == mapId && 
                        victim.getGender() != gender) {
                        next = true;
                        break;
                    }
                }
                if (!next) {
                    cm.sendNext("请确认您跟您的另一半在同一张地图、不同性别、并且都在线以及队伍中没有其他人");
                    cm.dispose();
                    return;
                }
                if (!victim.hasEquipped(ring) || !cm.getPlayer().hasEquipped(ring)) {
                    cm.sendNext("您或您的另一半没有装备#v" + ring + "##z" + ring + "#？");
                    cm.dispose();
                    return;
                }
                if (!cm.canHold(1112804) || !victim.canHold(1112804)) {
                    cm.sendNext("您或您的另一半背包空间不足");
                    cm.dispose();
                    return;
                }
                cm.getPlayer().setMarriageId(victim.getId());
                victim.setMarriageId(cm.getPlayer().getId());
                cm.givePartyItems(1112804, 1, false);
                cm.gainNX(-结婚价格);
                cm.worldMessage(6, "[月老] : 恭喜 [" + cm.getChar().getName() + "] 和 [" + victim.getName() + "] 结为夫妻！祝福二位新婚快乐！");
				cm.worldMessage(6, "[月老] : 恭喜 [" + cm.getChar().getName() + "] 和 [" + victim.getName() + "] 结为夫妻！祝福二位新婚快乐！");
				cm.worldMessage(6, "[月老] : 恭喜 [" + cm.getChar().getName() + "] 和 [" + victim.getName() + "] 结为夫妻！祝福二位新婚快乐！");
                cm.dispose();
                break;

            case 1: // 男男结婚
    if (点券 < 结婚价格) {
        cm.sendNext("  #b结婚需要 #r" + 结婚价格 + "#k#b 点券。");
        cm.dispose();
        return;
    }
    if (cm.getPlayer().getMarriageId() > 0) {
        cm.sendNext("你已经结过婚，想离婚吗？");
        cm.dispose();
        return;
    }
    if (cm.getParty() == null) {
        cm.sendNext("请和你的对象组队哦！");
        cm.dispose();
        return;
    }
    if (!cm.isLeader()) {
        cm.sendNext("让队长与我对话。");
        cm.dispose();
        return;
    }

    var gender = cm.getPlayer().getGender(); // 获取玩家性别
    if (gender != 0) { // 如果玩家不是男性
        cm.sendNext("你不是男的，无法选择男男结婚！");
        cm.dispose();
        return;
    }

    var mapId = cm.getPlayer().getMapId();
    var party = cm.getPlayer().getParty().getMembers();
    var it = party.iterator();
    var next = false; // 初始化 next 变量
    while (it.hasNext()) {
        var cPlayer = it.next();
        victim = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
        if (victim == null) {
            continue; // 如果 victim 为 null，跳过当前循环
        }
        if (victim.getId() != cm.getPlayer().getId() && 
            party.size() <= 2 && 
            victim.getMarriageId() == 0 && 
            victim.getMapId() == mapId && 
            victim.getGender() == gender) { // 同性结婚
            next = true;
            break;
        }
    }
    if (!next) {
        cm.sendNext("请确认您跟您的另一半在同一张地图、同性别、并且都在线以及队伍中没有其他人");
        cm.dispose();
        return;
    }
    if (!victim.hasEquipped(ring) || !cm.getPlayer().hasEquipped(ring)) {
        cm.sendNext("您或您的另一半没有装备#v" + ring + "##z" + ring + "#？");
        cm.dispose();
        return;
    }
    if (!cm.canHold(1112804) || !victim.canHold(1112804)) {
        cm.sendNext("您或您的另一半背包空间不足");
        cm.dispose();
        return;
    }
    cm.getPlayer().setMarriageId(victim.getId());
    victim.setMarriageId(cm.getPlayer().getId());
    cm.givePartyItems(1112804, 1, false);
    cm.gainNX(-结婚价格);
    cm.worldMessage(6, "[月老] : 恭喜 [" + cm.getChar().getName() + "] 和 [" + victim.getName() + "] 结为兄弟！一生兄弟，永不分离！");
	cm.worldMessage(6, "[月老] : 恭喜 [" + cm.getChar().getName() + "] 和 [" + victim.getName() + "] 结为兄弟！一生兄弟，永不分离！");
	cm.worldMessage(6, "[月老] : 恭喜 [" + cm.getChar().getName() + "] 和 [" + victim.getName() + "] 结为兄弟！一生兄弟，永不分离！");
    cm.dispose();
    break;

case 2: // 女女结婚
    if (点券 < 结婚价格) {
        cm.sendNext("  #b结婚需要 #r" + 结婚价格 + "#k#b 点券。");
        cm.dispose();
        return;
    }
    if (cm.getPlayer().getMarriageId() > 0) {
        cm.sendNext("你已经结过婚，想离婚吗？");
        cm.dispose();
        return;
    }
    if (cm.getParty() == null) {
        cm.sendNext("请和你的对象组队哦！");
        cm.dispose();
        return;
    }
    if (!cm.isLeader()) {
        cm.sendNext("让队长与我对话。");
        cm.dispose();
        return;
    }

    var gender = cm.getPlayer().getGender(); // 获取玩家性别
    if (gender != 1) { // 如果玩家不是女性
        cm.sendNext("你不是女的，无法选择女女结婚！");
        cm.dispose();
        return;
    }

    var mapId = cm.getPlayer().getMapId();
    var party = cm.getPlayer().getParty().getMembers();
    var it = party.iterator();
    var next = false; // 初始化 next 变量
    while (it.hasNext()) {
        var cPlayer = it.next();
        victim = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
        if (victim == null) {
            continue; // 如果 victim 为 null，跳过当前循环
        }
        if (victim.getId() != cm.getPlayer().getId() && 
            party.size() <= 2 && 
            victim.getMarriageId() == 0 && 
            victim.getMapId() == mapId && 
            victim.getGender() == gender) { // 同性结婚
            next = true;
            break;
        }
    }
    if (!next) {
        cm.sendNext("请确认您跟您的另一半在同一张地图、同性别、并且都在线以及队伍中没有其他人");
        cm.dispose();
        return;
    }
    if (!victim.hasEquipped(ring) || !cm.getPlayer().hasEquipped(ring)) {
        cm.sendNext("您或您的另一半没有装备#v" + ring + "##z" + ring + "#？");
        cm.dispose();
        return;
    }
    if (!cm.canHold(1112804) || !victim.canHold(1112804)) {
        cm.sendNext("您或您的另一半背包空间不足");
        cm.dispose();
        return;
    }
    cm.getPlayer().setMarriageId(victim.getId());
    victim.setMarriageId(cm.getPlayer().getId());
    cm.givePartyItems(1112804, 1, false);
    cm.gainNX(-结婚价格);
    cm.worldMessage(6, "[月老] : 恭喜 [" + cm.getChar().getName() + "] 和 [" + victim.getName() + "] 结为姐妹！情比金坚，永不分离！");
	cm.worldMessage(6, "[月老] : 恭喜 [" + cm.getChar().getName() + "] 和 [" + victim.getName() + "] 结为姐妹！情比金坚，永不分离！");
	cm.worldMessage(6, "[月老] : 恭喜 [" + cm.getChar().getName() + "] 和 [" + victim.getName() + "] 结为姐妹！情比金坚，永不分离！");
    cm.dispose();
    break;
            case 500: // 开启结婚功能
                cm.GainPiot("结婚开关", "1", -结婚开关);
                cm.GainPiot("结婚开关", "1", 1);
                cm.dispose();
                cm.openNpc(9300003, 0);
                break;

            case 501: // 关闭结婚功能
                cm.GainPiot("结婚开关", "1", -结婚开关);
                cm.dispose();
                cm.openNpc(9300003, 0);
                break;
        }
    }
}