var status = 0;
//var co = 5000000;
var co = 500000;
var jilv = 0;
var costa;
var xx = -1;
var qty;
var cost;

function start() {
    status = -1;
    action(1, 0, 0);
    var text = "搏一搏，单车能变摩托，赢了会所嫩模。\r\n";
    text += "本钱越多挣得越多哦，客官您要赌多少钱。\r\n\r\n";
    text += "底价50W哦，请输入要赌的倍数";
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
        var add = "欢迎你：#d" + cm.getName() + "#k,这里是本服金币赌博系统#k,";
        add += "一个吃屎游戏,就要拥有最全面的吃屎选择,本服特色多多,时尚全面,";
        add += "为您打造一个冒险之家的感觉,喜欢的朋友记得带上朋友一起哦.\r\n ";
        add += "#r温馨提示您，小赌怡情，大赌伤身.\r\n\r\n ";
        add += "☆加倍赌博赔率由左到右赔率递增,奖金增加概率降低.\r\n ";
        add += "☆当前下注押金:#b<#b 金币赌博 #n#b>#b<#b 默认下注：" + cost + " 金币#n#b >#k\r\n";
        add += "☆#r当前您拥有金币：" + cm.getMeso() + "#k\r\n";
        //add += "#L0#" +  "-[#r加注#k]#l\r\n\r\n";
        add += "#L1#" + "-[#b1:1倍赔率#k]#l";
        add += "#L2#" + "-[#b1:2倍赔率#k]#l";
        add += "#L3#" + "-[#b1:3倍赔率#k]#l";
        cm.sendSimple(add, 2);
    } else if (status == 2) {
        if (selection == 0) {
            //cm.sendNextPrev("#b成功加注#r10000金币#b,请点确定后查看.");
            cost = cost + 10000;
            status = 2;
            //cm.sendPrev("#b成功加注#r10000金币#b,请点确定后查看.");
        } else if (selection == 1) {
            var add = "#b<#e#r 金币赌博 #n#b>\r\n\r\n";
            add += "" + "-您选择的是[#r赔率1:1#b].\r\n";
            add += "" + "-您的押注为[#r" + cost + "金币#b].\r\n";
            add += "" + "-如果胜利将获取[#r除本金外" + cost * 1 + "金币#b]的奖励.\r\n";
            add += "" + "-点击[#r是#b]开始赌博,点击[#r不是#b]放弃赌博.";
            cm.sendYesNo(add);
            jilv = 1;
            xx = 0;
        } else if (selection == 2) {
            var add = "#b<#e#r 金币赌博 #n#b>\r\n\r\n";
            add += "" + "-您选择的是[#r赔率1:2#b].\r\n";
            add += "" + "-您的押注为[#r" + cost + "金币#b].\r\n";
            add += "" + "-如果胜利将获取[#r除本金外" + cost * 2 + "金币#b]的奖励.\r\n";
            add += "" + "-点击[#r是#b]开始赌博,点击[#r不是#b]放弃赌博.";
            cm.sendYesNo(add);
            jilv = 2;
            xx = 0;
        } else if (selection == 3) {
            var add = "#b<#e#r 金币赌博 #n#b>\r\n\r\n";
            add += "" + "-您选择的是[#r赔率1:3#b].\r\n";
            add += "" + "-您的押注为[#r" + cost + "金币#b].\r\n";
            add += "" + "-如果胜利将获取[#r除本金外" + cost * 3 + "金币#b]的奖励.\r\n";
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
                    //status = -1;
                    cm.dispose();
                } else {
                    jiaru = GetRandomNum(0, jilv);
                    if (jiaru == 0) {
                        costa = cost * jilv
                        cm.gainMeso(costa)
                        cm.sendOk("#b恭喜,您已经大获全胜...");
                        var text;
                        text = "[恭喜]" + cm.getPlayer().getName() + " : " + "在博彩中赢得" + costa + "金币。";
                        cm.message(text);
                        //cm.getPlayer().getMap().broadcastMessage(serverNotice(6, " has ended the expedition."));
                        status = -1;
                        cm.dispose();
                    } else {
                        cm.gainMeso(-cost)
                        cm.sendOk("#b悲剧啊.你输了....");
                        status = 2;
                        cm.dispose();
                    }
                }
            }
        }
    }
}