
var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE); //获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE); //获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);
var Q = " Q ";
var W = " W ";
var E = " E ";
var A = " A ";
var S = " S ";
var D = " D ";
var M = " M ";
var X = " X ";
var H = " H ";
var M = " M ";
var C = " C ";
var Y = " Y ";
var Gf1 = " 1 ";
var Gf2 = " 2 ";
var Gf3 = " 3 ";
var Gf4 = " 4 ";
var Gf5 = " 5 ";
var Gf6 = " 6 ";
var Gf7 = " 7 ";
var Gf8 = " 8 ";
var Gf9 = " 9 ";
var Gf0 = " 0 ";

var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";

var status = -1;
var ysss = 6;

var questions = Array(
        Array("" + Gf3 + "" + Gf3 + "" + Gf3 + "" + Gf3 + "", "3333"),
        Array("" + Gf6 + "" + Gf6 + "" + Gf6 + "" + Gf6 + "", "6666"),
        Array("" + A + "" + W + "" + A + "" + W + "", "AWAW"),
        Array("" + E + "" + S + "" + A + "" + D + "", "ESAD"),
        Array("" + E + "" + Gf1 + "" + Gf9 + "" + Gf3 + "", "E193"),
        Array("" + Q + "" + E + "" + Gf3 + "" + Gf6 + "", "QE36"),
        Array("" + A + "" + Gf6 + "" + A + "" + Gf3 + "", "A6A3"),
        Array("" + A + "" + D + "" + A + "" + D + "", "ADAD"),
        Array("" + W + "" + W + "" + W + "" + D + "", "WWWD"),
        Array("" + Q + "" + E + "" + W + "" + S + "", "QEWS"),
        Array("" + D + "" + D + "" + D + "" + S + "", "DDDS"),
        Array("" + W + "" + S + "" + W + "" + S + "", "WSWS"),
        Array("" + E + "" + E + "" + E + "" + E + "", "EEEE"),
        Array("" + Q + "" + Q + "" + Q + "" + E + "", "QQQE"),
        Array("" + Q + "" + W + "" + Q + "" + W + "", "QWQW"),
        Array("" + W + "" + Q + "" + W + "" + Q + "", "WQWQ"),
        Array("" + S + "" + S + "" + S + "" + S + "", "SSSS"),
        Array("" + S + "" + S + "" + S + "" + D + "", "SSSD"),
        Array("" + S + "" + S + "" + S + "" + Q + "", "SSSQ"),
        Array("" + S + "" + Q + "" + Q + "" + S + "", "SQQS"),
        Array("" + S + "" + W + "" + W + "" + W + "", "SWWW"),
        Array("" + S + "" + W + "" + W + "" + S + "", "SWWS"),
        Array("" + S + "" + A + "" + A + "" + S + "", "SAAS"),
        Array("" + S + "" + E + "" + E + "" + S + "", "SEES"),
        Array("" + S + "" + W + "" + E + "" + D + "", "SWED"),
        Array("" + S + "" + Q + "" + W + "" + Q + "", "SQWQ"),
        Array("" + Q + "" + W + "" + A + "" + W + "", "QWAW"),
        Array("" + Q + "" + S + "" + S + "" + Q + "", "QSSQ"),
        Array("" + Q + "" + Q + "" + W + "" + D + "", "QQWD"),
        Array("" + Q + "" + E + "" + Q + "" + S + "", "QEQS"),
        Array("" + Q + "" + D + "" + D + "" + S + "", "QDDS"),
        Array("" + Q + "" + S + "" + S + "" + S + "", "QSSS"),
        Array("" + E + "" + S + "" + S + "" + E + "", "ESSE"),
        Array("" + Q + "" + A + "" + Q + "" + E + "", "QAQE"),
        Array("" + Q + "" + S + "" + Q + "" + W + "", "QSQW"),
        Array("" + W + "" + A + "" + W + "" + Q + "", "WAWQ"),
        Array("" + Gf5 + "" + Gf2 + "" + S + "" + H + "", "52SH"),
        Array("" + Gf1 + "" + Gf3 + "" + Gf7 + "" + Gf9 + "", "1379"),
        Array("" + Gf2 + "" + Gf4 + "" + Gf6 + "" + Gf8 + "", "2468"),
        Array("" + A + "" + W + "" + A + "" + W + "" + W + "", "AWAWW"),
        Array("" + Gf3 + "" + Gf1 + "" + Gf3 + "" + Gf7 + "" + Gf9 + "", "31379"),
        Array("" + Gf1 + "" + Gf3 + "" + Gf7 + "" + Gf8 + "" + Gf9 + "", "13789"),
        Array("" + Gf1 + "" + E + "" + Gf2 + "" + D + "" + Gf9 + "", "1E2D9"),
        Array("" + Gf1 + "" + A + "" + Gf3 + "" + E + "" + Gf7 + "", "1A3E7"),
        Array("" + Q + "" + A + "" + Gf3 + "" + E + "" + Gf7 + "", "QA3E7"),
        Array("" + Q + "" + Q + "" + Gf3 + "" + Q + "" + E + "", "QQ3QE"),
        Array("" + A + "" + Gf3 + "" + Gf3 + "" + Q + "" + E + "", "A33QE"),
        Array("" + S + "" + H + "" + Gf5 + "" + Gf2 + "" + Gf0 + "", "SH520"),
        Array("" + Gf5 + "" + Gf2 + "" + Gf0 + "" + S + "" + H + "", "520SH"),
        Array("" + D + "" + E + "" + A + "" + Q + "" + Q + "", "DEAQQ"),
        Array("" + S + "" + H + "" + M + "" + X + "" + D + "", "SHMXD"),
        Array("" + A + "" + D + "" + A + "" + D + "" + W + "", "ADADW"),
        Array("" + W + "" + W + "" + W + "" + D + "" + W + "", "WWWDW"),
        Array("" + Q + "" + E + "" + W + "" + S + "" + W + "", "QEWSW"),
        Array("" + D + "" + D + "" + D + "" + S + "" + W + "", "DDDSW"),
        Array("" + W + "" + S + "" + W + "" + S + "" + W + "", "WSWSW"),
        Array("" + E + "" + E + "" + E + "" + E + "" + W + "", "EEEEW"),
        Array("" + Q + "" + Q + "" + Q + "" + E + "" + W + "", "QQQEW"),
        Array("" + Q + "" + W + "" + Q + "" + W + "" + W + "", "QWQWW"),
        Array("" + S + "" + W + "" + W + "" + W + "" + Q + "", "SWWWQ"),
        Array("" + S + "" + W + "" + W + "" + S + "" + Q + "", "SWWSQ"),
        Array("" + S + "" + A + "" + A + "" + S + "" + Q + "", "SAASQ"),
        Array("" + S + "" + E + "" + E + "" + S + "" + Q + "", "SEESQ"),
        Array("" + S + "" + W + "" + E + "" + D + "" + Q + "", "SWEDQ"),
        Array("" + S + "" + Q + "" + W + "" + Q + "" + Q + "", "SQWQQ"),
        Array("" + Q + "" + W + "" + A + "" + W + "" + Q + "", "QWAWQ"),
        Array("" + Q + "" + S + "" + S + "" + Q + "" + Q + "", "QSSQQ"),
        Array("" + Q + "" + Q + "" + W + "" + D + "" + Q + "", "QQWDQ"),
        Array("" + Q + "" + E + "" + Q + "" + S + "" + Q + "", "QEQSQ"),
        Array("" + Q + "" + D + "" + D + "" + S + "" + Q + "", "QDDSQ"),
        Array("" + Q + "" + S + "" + S + "" + S + "" + Q + "", "QSSSQ"),
        Array("" + E + "" + S + "" + S + "" + E + "" + Q + "", "ESSEQ"),
        Array("" + Q + "" + A + "" + Q + "" + E + "" + Q + "", "QAQEQ"),
        Array("" + Q + "" + S + "" + Q + "" + W + "" + Q + "", "QSQWQ"),
        Array("" + W + "" + A + "" + W + "" + Q + "" + Q + "", "WAWQQ"),
        Array("" + Q + "" + W + "" + E + "" + A + "" + S + "", "QWEAS"),
        Array("" + W + "" + S + "" + W + "" + S + "" + Q + "", "WSWSQ"),
        Array("" + W + "" + S + "" + W + "" + S + "" + W + "", "WSWSW"),
        Array("" + W + "" + S + "" + W + "" + S + "" + D + "", "WSWSD"),
        Array("" + W + "" + S + "" + W + "" + S + "" + A + "", "WSWSA"),
        Array("" + W + "" + S + "" + W + "" + S + "" + E + "", "WSWSE"),
        Array("" + Q + "" + S + "" + Q + "" + S + "" + Q + "", "QSQSQ"),
        Array("" + Q + "" + W + "" + Q + "" + W + "" + Q + "", "QWQWQ"),
        Array("" + W + "" + S + "" + W + "" + S + "" + D + "", "WSWSD"),
        Array("" + E + "" + S + "" + E + "" + S + "" + Q + "", "ESESQ"),
        Array("" + A + "" + W + "" + D + "" + S + "" + Q + "", "AWDSQ"),
        Array("" + S + "" + S + "" + Q + "" + S + "" + Q + "", "SSQSQ"),
        Array("" + A + "" + S + "" + D + "" + S + "" + Q + "", "ASDSQ"),
        Array("" + Q + "" + W + "" + E + "" + Gf3 + "" + Gf2 + "" + Gf1 + "", "QWE321"),
        Array("" + A + "" + S + "" + D + "" + Gf2 + "" + Gf1 + "" + Gf3 + "", "ASD213"),
        Array("" + E + "" + Q + "" + D + "" + Gf6 + "" + Gf8 + "" + Gf9 + "", "EQD689"),
        Array("" + S + "" + A + "" + D + "" + Gf7 + "" + Gf4 + "" + Gf1 + "", "SAD741"),
        Array("" + A + "" + S + "" + D + "" + S + "" + Q + "" + Q + "", "ASDSQQ"),
        Array("" + W + "" + W + "" + W + "" + S + "" + Q + "" + Q + "", "WWWSQQ"),
        Array("" + E + "" + S + "" + S + "" + S + "" + Q + "" + Q + "", "ESSSQQ"),
        Array("" + Q + "" + W + "" + E + "" + A + "" + S + "" + D + "", "QWEASD"),
        Array("" + A + "" + E + "" + W + "" + S + "" + S + "" + W + "", "AEWSSW"),
        Array("" + E + "" + Gf6 + "" + E + "" + Gf0 + "" + E + "" + Gf2 + "", "E6E0E2"),
        Array("" + E + "" + Gf6 + "" + X + "" + Gf7 + "" + E + "" + Gf2 + "", "E6X7E2"),
        Array("" + A + "" + Gf4 + "" + Gf3 + "" + Gf2 + "" + A + "" + A + "", "A432AA"),
        Array("" + S + "" + Gf3 + "" + S + "" + Gf2 + "" + Gf1 + "" + S + "", "S3S21S"),
        Array("" + M + "" + W + "" + E + "" + M + "" + A + "" + M + "", "MWEMAM"),
        Array("" + X + "" + Q + "" + E + "" + S + "" + X + "" + X + "", "XQESXX"),
        Array("" + D + "" + S + "" + S + "" + D + "" + A + "" + D + "", "DSSDAD"),
        Array("" + H + "" + E + "" + E + "" + D + "" + S + "" + H + "", "HEEDSH")
		);
var qid = -1;
var time1;
var time2;
var count = 0;

function start() {
    if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
		cm.sendOk("请保证背包所有栏位至少保留3个空格！");
        cm.dispose();
        return;
    }

    action(1, 0, 0);
}

function action(mode, type, selection) {
	if ((hour == 8 && minute >= 30) || hour >= 8 && hour < 22 || (hour == 22 && minute <= 30)) {
        if (mode == 1) {
            status++;
        } else {
            if (status >= 0) {
                cm.dispose();
                return;
            }

            status--;
        }
        if (status == 0) {

            time1 = cm.getCurrentTime();
            count++;
            qid = Math.floor(Math.random() * questions.length);
            cm.sendGetText("#w#r第 - " + count + " - 节拍。#k\r\n#b#e" + questions[qid][0] + "#n#k\r\n目前难度 ；" + ysss + "秒/节拍;#r★★★★★#k");

        } else if (status == 1) {

            time2 = cm.getCurrentTime();
            var myasked = cm.getText();
            var answer = questions[qid][1];
            if ((time2 - time1) / 1000 > ysss) {
                cm.getPlayer().dropMessage(6,"连击超时,连击中断");
                cm.dispose();
                return;
            }

            if (myasked == answer) {

                status = -1;
                if (count == 10) {
                    //var itemList = Array(4001245,4000463,4310060,4001165,3992025,2510418); //
                    //var itemIdx = Math.floor(Math.random() * itemList.length);
                    cm.setBossLog("活动节奏点");
                    cm.gainItem(4001165, 99);
                    cm.gainItem(3605006, 1);
                //    cm.getPlayer().dropMessage(6,"获得节奏点,继续游戏获得更多节奏点,#b可兑换更多奖励");
					cm.sendOk("获得节奏点,继续游戏获得更多节奏点,#b可兑换更多奖励！！！");
                    cm.worldMessage(6, "[节奏大师]：玩家 " + cm.getName() + " 10连击获得节奏点，获得树喜欢的阳光99个、女神的赐福1个。");
                    cm.dispose();

                } else if (count < 11) {
                    cm.sendSimple("#w#r G O M B O [完美节奏] #kx #r" + count + " 获得一个#v3994720#");
                    cm.gainItem(3994720, 1);
                } else {
					cm.sendOk("获得节奏点,继续游戏获得更多节奏点,#b可兑换更多奖励！！！");
                    cm.dispose();

                }

            } else {
                status = -1;
                count = 0;

                cm.sendSimple("节奏错误,连击中断");

            }
        }
    } else {
		cm.sendOk("当前服务器时间:" + hour + "点" + minute + "分\r\n活动时间：\r\n节奏大师每晚的#r8点30分 - 22点30分#k才可进行，请不要错过！");
        cm.dispose();
    }
}
