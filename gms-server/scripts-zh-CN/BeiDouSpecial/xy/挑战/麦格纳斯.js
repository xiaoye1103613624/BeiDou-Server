var status;
var fbmc = " 麦 格 纳 斯 "; //副本名称
var minLevel = 200; //最低等级
var maxLevel = 250; //最高等级
var minPartySize = 1; //最低人数
var maxPartySize = 6; //最高人数
var cishuxianzhi = 10; //限制次数
var maxjinbi = 50000; //判断征集令金币
var inmeso = 30000000; //入场金币
var 麦格纳斯 = "#fUI/UIWindow.img/MobGage/Mob/8880000#";

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
        var tex2 = "";
        var text = "";
        for (i = 0; i < 10; i++) {
            text += "";
        }
        //显示物品ID图片用的代码是  #v这里写入ID#
        // minPartySize = cm.getPlayer().isGM() ? 1 : 1;
        // cishuxianzhi = cm.getPlayer().isGM() ? 12 : 2;
        // minLevel = cm.getPlayer().isGM() ? 1 : 121;
        text += "#k\t\t\t#r 做好挑战 " + fbmc + "" + 麦格纳斯 + " 了么 #k\r\n\r\n进入要求如下：\r\n①人数限制:#r " + minPartySize + " #b- #r" + maxPartySize + "#k队员\t②等级限制：#r " + minLevel + " #b- #r" + maxLevel + "级 #k\r\n"
        text += "#k每天只能挑战:#b" + cishuxianzhi + "#k次 你今天已进入:#b" + cm.getPlayer().get每日记录("麦格纳斯次数") + "#k次#k\r\n"
        text += "挑战BOSS需要3000w金币\r\n"
        text += "#k战力高于:#b400万#k才可进入[当前战力:#b" + cm.getPlayer().getCombatPower() / 10000 + "万#k]#k\r\n"
        // text += "#k需要#v4001127#1个#k\r\n"
        if (!cm.getParty每日记录("麦格纳斯次数", cishuxianzhi)) { //判断组队是否2次
            text += "当前队伍状况:#r 队伍中队友挑战次数已经用完#k\r\n\r\n"

            //} else {
            //      text += "当前队伍状况:#g 符合要求   #k\r\n\r\n"
        }
        text += "#L1##r开始挑战#l      #L2##r副本征集令#k" + maxjinbi + "金币/次#l\r\n\r\n"
        cm.sendSimple(text);
    } else if (selection == 1) {

        if (cm.getParty() == null) {
            cm.sendOk("你没有队伍无法进入！");
            cm.dispose();
            return;
        } else if (!cm.getParty每日记录("麦格纳斯次数", cishuxianzhi)) { //判断组队是否2次
            cm.sendOk("队伍中队友挑战次数已经用完2次！");
            cm.dispose();
        } else if (cm.getPlayer().getMeso() < inmeso) {
            cm.sendOk("你的金币少于30000000，无法进入");
            cm.dispose();
            return;

        } else if (!cm.isLeader()) {
            cm.sendOk("请让你的队长和我说话~");
            cm.dispose();
            return;
        } else {
            var party = cm.getParty().getMembers();
            var inMap = cm.partyMembersInMap();
            var levelValid = 0;
            for (var i = 0; i < party.size(); i++) {
                if (party.get(i).getLevel() >= minLevel && party.get(i).getLevel() <= maxLevel)
                    levelValid++;
            }
            if (inMap < minPartySize || inMap > maxPartySize) {
                cm.sendOk("你的队伍人数不足" + minPartySize + "人.请把你的队伍人员召集到麦格纳斯门口再进入副本.");
                cm.dispose();
                return;
            } else if (levelValid != inMap) {
                cm.sendOk("请确保你的队伍里所有人员都在本地图，且最小等级在 " + minLevel + " 和 " + maxLevel + "之间.");
                cm.dispose();
                return;
                //   } else if (cm.getPlayer().getClient().getChannel()== 3 && cm.getPlayer().getClient().getChannel() == 4){
                //   cm.sendOk(" 亲，你不在3线或者4线呦！");
                //    cm.dispose();
                //   return;
            } else if (cm.getPlayer().getBossLog("麦格纳斯次数") >= cishuxianzhi) {
                cm.sendOk("您好,限定每天只能挑战" + cishuxianzhi + "次！");
                cm.dispose();
                return;
            } else {
                var msg = '';
                var notHere = '';
                var party = cm.getPlayer().getParty().getMembers();
                var it = party.iterator();
                while (it.hasNext()) {
                    var cPlayer = it.next();
                    var chr = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
                    if (chr != null) {
                        if (chr.getCombatPower() < 4000000) {
                            msg += chr.getName() + " "
                        }
                    } else {
                        notHere += cPlayer.getName() + " ";
                    }
                }
                if ('' !== notHere) {
                    cm.sendOk(notHere + '不在本地图.');
                    cm.dispose();
                    return;
                }
                if ('' !== msg) {
                    cm.sendOk(msg + '没有足够的"战力低于#b400万#k，无法进入！"');
                    cm.dispose();
                    return;
                }
                var em = cm.getEventManager("MGNS");
                if (em == null) {
                    cm.sendOk("这台电脑是当前不可用.");
                } else {
                    if (cm.getPlayerCount(401060100) <= 0) {//判定地图人数
                        em.startInstance(cm.getParty(), cm.getPlayer().getMap());
                        cm.giveParty每日记录("麦格纳斯次数");
                        cm.召唤怪物(8880000, 100000000000, 10000000, 1, 401060100, 2452, -1347);
                        //   AAA();//添加重返检测
                    } else {
                        cm.sendOk("请稍等...任务正在进行中.");
                    }

                }
                cm.dispose();
            }
        }
    } else if (selection == 2) {
        if (cm.getMeso() >= maxjinbi) { //判断多少金币
            cm.gainMeso(-maxjinbi); //扣除多少金币
            cm.喇叭(1, cm.getPlayer().getName() + " [征集令]" + " : " + "[" + fbmc + "Boss] 需要勇士一起完成");
            cm.dispose();
        } else {
            cm.sendOk("你的冒险币不足" + maxjinbi + "。无法发送征集令");
            cm.dispose();
        }

    }
}


function qianzhi() { //收取boss
    if (cm.getParty() == null) {
        cm.sendOk("你没有队伍无法进入！");
        cm.dispose();
        return false;
    }

    var leonys = cishuxianzhi;
    var party = cm.getParty().getMembers();
    var it = party.iterator();
    while (it.hasNext()) {
        var cPlayer = it.next();
        var teamID = cPlayer.getId();
        var curChar = cm.getMap().getCharacterById(teamID);
        if (curChar != null) {
            if (curChar.getQuestStatus(8534) == 0) {
                return false || cm.getPlayer().isGM();
            }

        }
    }
    return (true && cm.getPartyBosslog("huangdics", leonys));
}

function AAA() { //给队伍的bosslog 给远征军bosslog
    var party = cm.getParty().getMembers();
    var partyid = cm.getParty().getId();
    var bossid = "shaolin_l" + partyid;
    var mapId = cm.getMapId();
    var next = true;
    var levelValid = 0;
    var inMap = 0;
    var it = party.iterator();
    while (it.hasNext()) {
        var cPlayer = it.next();
        var teamID = cPlayer.getId();
        var curChar = cm.getMap().getCharacterById(teamID);
        if (curChar != null && cPlayer.getMapid() == mapId) {
            curChar.cm.giveParty每日记录("皇帝BOSS");
            //inMap += (cPlayer.getJobId() == 900 ? 3 : 1);
        }
    }
    //return;
}