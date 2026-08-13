var status = 0;
var co = 1000000;
var jilv = 0;
var costa;
var xx = -1;
var qty;
var cost;
jc1 = 2049100; //混沌卷轴

function start() {
    status = -1;
    action(1, 0, 0);
    var text = "搏一搏，单车能变摩托，赢了会所嫩模。\r\n";
    text += "本钱越多挣得越多哦，客官您要赌多少钱。\r\n\r\n";
    text += "底价100W，每100W一张混沌，请输入要赌的倍数";
    cm.sendGetNumber(text, 10, 1, 100);
}


function GetRandomNum(Min, Max) {
    var Range = Max - Min;
    var Rand = Math.random();
    return (Min + Math.round(Rand * Range));
}

function action(mode, type, selection) {
    status++;
    if (mode == -1) {
        cm.dispose();
        return;
    } else if (mode == 0) {
        cm.sendOk("穷B快滚!.");
        cm.dispose();
        return;
    }
    if (status == 1) {
        qty = (selection > 0) ? selection : (selection < 0 ? -selection : 1);
        cost = qty * co;
        var add = "欢迎你：#d" + cm.getName() + "#k,这里是本服混沌赌博系统#k,";
        add += "一个吃屎游戏,就要拥有最全面的吃屎选择,本服特色多多,时尚全面,";
        add += "为您打造一个冒险之家的感觉,喜欢的朋友记得带上朋友一起哦.\r\n ";
        add += "#r温馨提示您，小赌怡情，大赌伤身.\r\n ";
        add += "☆加倍赌博赔率由左到右赔率递增,奖金增加概率降低.\r\n\r\n";
        add += "☆#r当前下注押金: #b" + cost + " 金币\r\n";
        add += "#r☆#r当前您拥有金币：#b" + cm.getMeso() + " 金币\r\n";
        add += "#L1#" + "-[#b1:1倍赔率#k]#l";
        add += "#L2#" + "-[#b1:2倍赔率#k]#l";
        add += "#L3#" + "-[#b1:4倍赔率#k]#l";
        cm.sendSimple(add, 2);
    } else if (status == 2) {
        if (selection == 1) {
            qty = qty * 1;
            var add = "#b<#e#r 混沌赌博 #n#b>\r\n\r\n";
            add += "" + "-您选择的是[#r赔率1:1#b].\r\n";
            add += "" + "-您的押注为[#r" + cost + "金币#b].\r\n";
            add += "" + "-如果胜利将获取[" + qty + "张混沌#b]的奖励.\r\n";
            add += "" + "-点击[#r是#b]开始赌博,点击[#r不是#b]放弃赌博.";
            cm.sendYesNo(add);
            jilv = 1;
            xx = 0;
        } else if (selection == 2) {
            var add = "#b<#e#r 混沌赌博 #n#b>\r\n\r\n";
            qty = qty * 2;
            add += "" + "-您选择的是[#r赔率1:2#b].\r\n";
            add += "" + "-您的押注为[#r" + cost + "金币#b].\r\n";
            add += "" + "-如果胜利将获取[" + qty + "张混沌#b]的奖励.\r\n";
            add += "" + "-点击[#r是#b]开始赌博,点击[#r不是#b]放弃赌博.";
            cm.sendYesNo(add);
            jilv = 2;
            xx = 0;
        } else if (selection == 3) {
            var add = "#b<#e#r 混沌赌博 #n#b>\r\n\r\n";
            qty = qty * 4;
            add += "" + "-您选择的是[#r赔率1:3#b].\r\n";
            add += "" + "-您的押注为[#r" + cost + "金币#b].\r\n";
            add += "" + "-如果胜利将获取[" + qty + "张混沌#b]的奖励.\r\n";
            add += "" + "-点击[#r是#b]开始赌博,点击[#r不是#b]放弃赌博.";
            cm.sendYesNo(add);
            jilv = 3;
            xx = 0;
        }
    } else if (status == 3) {
        if (xx == 0) {
            if (jilv == 0) {
            } else if (jilv != 0) {
                if (cm.getMeso() < cost) {
                    cm.sendOk("#b您的金币不足,不能参加赌博.....");
                    cm.dispose();
                } else {
                    jiaru = GetRandomNum(0, jilv);
                    if (jiaru == 0) {
                        cm.gainItem(jc1, qty);
                        cm.gainMeso(-cost);
                        cm.sendOk("#b恭喜,您已经大获全胜...");
                        var text;
                        text = "[恭喜]" + cm.getPlayer().getName() + " : " + "在混沌赌博中赢得" + qty + "张混沌卷轴！";
                        //cm.message(text);
                        cm.getPlayer().getWorldServer().broadcastPacket(PacketCreator.serverNotice(6, text));
                        cm.dispose();
                    } else {
                        cm.gainMeso(-cost);
                        cm.sendOk("#b悲剧啊.你输了....");
                        cm.dispose();
                    }
                }
            }
        }
    }
}