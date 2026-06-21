var status = 0;
var minLevel = 8;
var maxLevel = 200;
var minPartySize = 1;
var maxPartySize = 6;
var cishuxianzhi = 10; //限制次数
var maxjinbi = 50000; //判断征集令金币
var 邪恶小兔2 = "#fEffect/CharacterEff/1112960/3/1#";
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            var yhms = "";
            yhms += "                " + 邪恶小兔2 + "#r#e月秒副本#n" + 邪恶小兔2 + "\r\n\r\n";
            yhms += "副本进入要求如下：\r\n";
            yhms += "①人数限制: " + minPartySize + " #b- #r" + maxPartySize + "#k队员\t②等级限制：#r " + minLevel + " #b- #r" + maxLevel + "级 #k\r\n"
            yhms += "每天只能挑战:#b" + cishuxianzhi + "#k次 你今天已进入:#b" + cm.getPlayer().get每日记录("月秒副本") + "#k次#k\r\n"
            yhms += "等级限制：" + minLevel + " - " + maxLevel + "  人数限制：" + minPartySize + " - " + maxPartySize + " 经验指数：#r适中#k\r\n";
            // yhms += "2、限制次数:每天可进行" + maxenter + "次\r\n";
            //yhms += "2、今日已进行: #b" + cm.getBossLog(Log) + " #k次       \r\n"
            yhms += "#L0##b开始 月秒副本 #l    \r\n\r\n";
          //  yhms += "#L2##b#r#v4031560#30张 兑换 #v1142817#年糕汤狂爱者#l\r\n\r\n";
          //  yhms += "#L4##b#r#v4031560#5张 兑换 #v1142816#尝过年糕的人#l\r\n\r\n";
			//yhms += "#L6##b#r#v4031560#20张 兑换 #v3800670##z3800670##l\r\n\r\n";
            cm.sendSimple(yhms);
        } else if (status == 1) {
            if (selection == 0) {
                if (cm.getParty() == null) { // 没有组队
                    cm.sendOk("请组队后和我谈话。");
                    cm.dispose();
                } else if (!cm.isLeader()) { // 不是队长
                    cm.sendOk("请叫队长和我谈话。");
                    cm.dispose();
                } else if (!cm.getParty每日记录("月秒副本", 10)) { //判断组队是否2次
                    cm.sendOk("队伍中队友挑战次数已经用完10次！");
                    cm.dispose();
                    return;
                    // }else if( cm.getPlayer().getBossLog("月秒副本") >= cishuxianzhi) {
                    // cm.sendOk("您好,限定每天只能挑战"+ cishuxianzhi +"次！");
                    // cm.dispose();
                    // return;
                } else {
                    cm.givePartyItems(4001095, -1, true);
                    cm.givePartyItems(4001096, -1, true);
                    cm.givePartyItems(4001097, -1, true);
                    cm.givePartyItems(4001098, -1, true);
                    cm.givePartyItems(4001099, -1, true);
                    cm.givePartyItems(4001101, -1, true);
                    var party = cm.getParty().getMembers();
                    var mapId = cm.getPlayer().getMapId();
                    var next = true;
                    var levelValid = 0;
                    var inMap = 0;
                    var it = party.iterator();
                    while (it.hasNext()) {
                        var cPlayer = it.next();
                        if ((cPlayer.getLevel() >= minLevel) && (cPlayer.getLevel() <= maxLevel)) {
                            levelValid += 1;
                        } else {
                            next = false;
                        }
                        if (cPlayer.getMapid() == mapId) {
                            inMap += 1;
                        }
                    }
                    if (party.size() < minPartySize || party.size() > maxPartySize || inMap < minPartySize) {
                        next = false;
                    }
                    if (next) {
                        var em = cm.getEventManager("HenesysPQ");
                        if (em == null) {
                            cm.sendOk("此任务正在建设当中。");
                        } else {
                            var prop = em.getProperty("state");
                            if (prop.equals("0") || prop == null) {
                                em.startInstance(cm.getParty(), cm.getMap());
                                //cm.getPlayer().setBossLog("月秒副本");//给团队次数
                                cm.giveParty每日记录("月秒副本");
                                // cm.setPartyBosslog("月秒副本");//给团队次数
                                //cm.喇叭(2, "[" + cm.getPlayer().getName() + "]开始带领他的队伍挑战【月妙副本】，让我们祝福他们！！");
                                cm.removeAll(4001022);
                                cm.removeAll(4001023);
                                cm.dispose();
                                return;
                            } else {
                                cm.sendOk("任务正在进行中...请稍等!");
                            }
                        }
                        cm.dispose();
                    } else {
                        cm.sendOk("请确认你的组队员：\r\n\r\n#b1、组队员必须要" + minPartySize + "人及以上，" + maxPartySize + "人及以下。\r\n2、组队员等级必须要在" + minLevel + "级以上" + maxLevel + "级以下。\r\n\r\n（#r如果仍然错误, 重新下线,再登陆 或者请重新组队。#k#b）");
                        cm.dispose();
                    }
                } //判断组队
            } else if (selection == 1) {
                cm.sendOk("请确认你的组队员：\r\n\r\n#b1、组队员必须要" + minPartySize + "人以上，" + maxPartySize + "人以下。\r\n2、组队员等级必须要在" + minLevel + "级以上" + maxLevel + "级以下。\r\n\r\n（#r如果仍然错误, 重新下线,再登陆 或者请重新组队。#k#b）");
                cm.dispose();
            } else if (selection == 2) {
                if (cm.haveItem(4031560, 30)) {
                    cm.gainItem(4031560, -30);
                    cm.给属性装备(1142817, 0, 0, 4, 4, 4, 4, 50, 50, 4, 10, 2, 2, 2, 2, 2, 2);
                    cm.sendOk("恭喜你兑换成功！");
                    cm.worldMessage(6, "[" + cm.getName() + "] : 使用30张达克鲁的邮票在月妙NPC兑换了【年糕汤狂爱者勋章】大家快恭喜他！");
                    cm.dispose();
                } else {
                    cm.sendOk("#v4031560# 不足30张无法兑换！");
                    cm.dispose();
                }
            } else if (selection == 4) {
                if (cm.haveItem(4031560, 5)) {
                    cm.gainItem(4031560, -5);
                    cm.给属性装备(1142816, 0, 0, 2, 2, 2, 2, 30, 30, 3, 6, 1, 1, 1, 1, 1, 1);
                    cm.sendOk("恭喜你兑换成功！");
                    cm.worldMessage(6, "[" + cm.getName() + "] : 使用5张达克鲁的邮票在月妙NPC兑换了【年糕勋章】大家快恭喜他！");
                    cm.dispose();
                } else {
                    cm.sendOk("#v4031560# 不足5张无法兑换！");
                    cm.dispose();
                }
            } else if (selection == 6) {
				
                if (cm.haveItem(4031560, 20)) {
                    cm.gainItem(4031560, -20);
                    cm.gainItem(3800670, 1);
                    cm.sendOk("恭喜你兑换成功！");
                    cm.worldMessage(6, "[" + cm.getName() + "] : 使用20张达克鲁的邮票在月妙NPC兑换了【月妙的幸运年糕】大家快恭喜他！");
                    cm.dispose();
                } else {
                    cm.sendOk("#v4031560# 不足20张无法兑换！");
                    cm.dispose();
                }				
            } else if (selection == 3) {
                if (cm.getMeso() >= maxjinbi) { //判断多少金币
                    cm.gainMeso( - maxjinbi); //扣除多少金币
                    cm.全服黄色喇叭(cm.getPlayer().getName() + " [副本征集令]" + " : " + "[月秒副本]需要勇士一起完成,我已在副本门口!");
                    cm.dispose();
                } else {
                    cm.sendOk("你的冒险币不足" + maxjinbi + "。无法发送征集令");
                    cm.dispose();
                }
            }
        }
    }
}
