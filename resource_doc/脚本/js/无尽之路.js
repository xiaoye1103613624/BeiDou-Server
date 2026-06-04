var status;
var minLevel = 10;
var maxLevel = 250;
var minPlayers = 1;
var maxPlayers = 1;
var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR);
var month = ca.get(java.util.Calendar.MONTH) + 1;
var day = ca.get(java.util.Calendar.DATE);
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY);
var minute = ca.get(java.util.Calendar.MINUTE);
var second = ca.get(java.util.Calendar.SECOND);
var 星星 = "#fEffect/CharacterEff/1051294/1/0#";
var 星星1 = "#fEffect/CharacterEff/1051294/1/1#";
var 星星2 = "#fEffect/CharacterEff/1051294/1/2#";
var 星星3 = "#fEffect/CharacterEff/1051294/1/3#";
var 星星4 = "#fEffect/CharacterEff/1051294/1/4#";
var 星星5 = "#fEffect/CharacterEff/1051294/1/5#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 大水滴 = "#fItem/Etc/0427/04270001/Icon10/4#";
var 怪物代码 = 9600000;
var 怪物血量 = 30000000;
var 怪物x轴  = 12;
var 怪物y轴  = 144;
var 需要道具 = 3605006;
var 需要数量 = 88;
var 需要点券 = 3000;
var 需要金币 = 0;

function start() {

    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        var text = "";
        for (i = 0; i < 10; i++) {
            text += "";
        }
        text += "               #d欢迎来到活动地图huo'd！\r\n\r\n";
        text += "副本进入要求如下:\n\r\n#l";
        text += "1:人数限制:#r1 - 6 #k组队。 #r10级后方可进入#k\n\r\n";
        text += "2:队长需要枫叶x88。每天可进入10次\n\r\n";
        text += "3:如出现掉线，再次进入是接着挑战当前的关卡数\n\r\n";
        text += "4:只可在 全 线进行挑战。 \n\r\n";
        text += "5:可在每天全天可挑战。 挑战通关需第二天方可刷新。\n\r\n";
        text += "              #L1##r" + 星星2 + "开始执行活动地图" + 星星2 + "#l\r\n\r\n";
        cm.sendSimple(text);
    } else if (selection == 2) {
        cm.warp(910000000);
    } else if (status == 1) {
        if (selection == 2) {
            cm.openNpc(9050005, 5);
        } else if (selection == 1) {
            if (cm.getParty() == null) {
                cm.sendOk("你没有队伍无法进入！");
                cm.dispose();
            } else if (!cm.isLeader()) {
                cm.sendOk("请让你的队长和我说话~");
                cm.dispose();
            } else {
                var party = cm.getParty().getMembers();
                var inMap = cm.partyMembersInMap();
                var mapId = cm.getPlayer().getMapId();
                var it = party.iterator();
                var cPlayer = it.next();
                var victim = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                var levelValid = 0;
                for (var i = 0; i < party.size(); i++) {
                    if (party.get(i).getLevel() >= minLevel && party.get(i).getLevel() <= maxLevel)
                        levelValid++;
                }
                if (!cm.haveItem(4001126, 88)) {
                    cm.sendOk("作为队长需要准备门票#v4001126#x88个.");
                    cm.dispose();
                    return;
                } else if (cm.判断团队每日("无尽牛鼻") >= 10) { // 修正函数调用
                    cm.sendOk("你的队伍今日总挑战次数已达上限！");
                    cm.dispose();
                    return;
                // --- 修改结束 ---
                } else {
                    for each (var cPlayer in cm.getParty().getMembers()) {
                        if (getBossTime(cPlayer.getId(), '无尽完结') >= 9999999) {
                            cm.getPlayer().dropMessage(6, "#r" + cPlayer.getName() + "#k已经通关过了,请退出组队,或等维护后再进入！");
                            cm.dispose();
                            return;
                        }
                    }
                    var em = cm.getEventManager("refreshbossroom");
                    if (em == null) {
                        cm.sendOk("事件发生错误，请联系管理员.");
                        cm.dispose();
                    } else {
                        if (cm.getPlayerCount(910210000) <= 0) {
                            cm.gainItem(需要道具, -需要数量);
                            // --- 修改开始：正确调用给团队每日 ---
                            cm.给团队每日("无尽牛鼻"); // 修正函数调用
                            // --- 修改结束 ---
                            cm.dispose();
                            em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                            cm.warpParty(910210000, 0);
                            cm.全服漂浮喇叭("玩家[" + cm.getName() + "]进入活动地图地图，开始高难度漫长挑战！！", 5121001);
                            cm.喇叭(2, "玩家[" + cm.getName() + "]进入活动地图地图，开始高难度漫长挑战！！");
                            cm.喇叭(2, "玩家[" + cm.getName() + "]进入活动地图地图，开始高难度漫长挑战！！");
                            cm.喇叭(2, "玩家[" + cm.getName() + "]进入活动地图地图，开始高难度漫长挑战！！");
                            cm.喇叭(2, "玩家[" + cm.getName() + "]进入活动地图地图，开始高难度漫长挑战！！");
                            cm.喇叭(2, "玩家[" + cm.getName() + "]进入活动地图地图，开始高难度漫长挑战！！");
                        } else {
                            cm.sendOk("请换个线在来尝试,这个线已经有人了");
                        }
                    }
                    cm.dispose();
                }
            }
        }
    }
}

function getBossTime(id, bossid) //获得BOSS次数
{
    var con1 = Packages.database.DatabaseConnection.getConnection();
    ps1 = con1.prepareStatement("SELECT * FROM bosslog WHERE characterid = ? and bossid = ? ");
    ps1.setInt(1, id);
    ps1.setString(2, bossid);
    var rs1 = ps1.executeQuery();
    var count = 0;
    if (rs1.next()) {
        count = rs1.getInt("count");
    }
    con1.close();
    rs1.close();
    ps1.close();
    return count;
}