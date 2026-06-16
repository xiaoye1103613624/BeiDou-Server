/******************
 079 085脚本
 QQ:870074996
 作者:小猫
 ********************/

var 图标 = "#fEffect/CharacterEff/1112905/0/1#";//红心
var JT = "#fUI/Basic/BtHide3/mouseOver/0#";//小箭头
var 心 = "#fUI/GuildMark.img/Mark/Etc/00009001/14#";//大红心
var 闹钟图标 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#";
var 完成 = "#fUI/UIWindow/Quest/Tab/enabled/2#";
var 正在进行中 = "#fUI/UIWindow/Quest/Tab/enabled/1#";
var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR);
var month = ca.get(java.util.Calendar.MONTH) + 1;
var day = ca.get(java.util.Calendar.DATE);
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY);
var minute = ca.get(java.util.Calendar.MINUTE);
var second = ca.get(java.util.Calendar.SECOND);
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);
var items = [10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150]
var 任务物品 = Array(
    Array(10, 4000016, 500),
    Array(10, 4000019, 500),
    Array(10, 4310143, 1),
    Array(10, 4000038, 10),
    Array(10, 1, 500),
    Array(10, 2, 500),
//``````````````````
    Array(20, 1122210, 1),
    Array(20, 4000001, 500),
    Array(20, 4000002, 500),
    Array(20, 4310143, 10),
    Array(20, 4000038, 40),
    Array(20, 1, 5000),
    Array(20, 2, 5000),
//``````````````````
    Array(30, 1122210, 1),
    Array(30, 4000024, 500),
    Array(30, 4000021, 500),
    Array(30, 4000313, 100),
    Array(30, 4000038, 100),
    Array(30, 1, 1000000),
    Array(30, 2, 10000),
//``````````````````
    Array(40, 1122210, 1),
    Array(40, 4000037, 500),
    Array(40, 4000043, 500),
    Array(40, 4310143, 20),
    Array(40, 4001126, 1000),
    Array(40, 1, 2000000),
    Array(40, 2, 20000),
//``````````````````
    Array(50, 1122210, 1),
    Array(50, 4001111, 5),
    Array(50, 4000464, 2),
    Array(50, 4170016, 2),
    Array(50, 4310143, 40),
    Array(50, 4000038, 40),
    Array(50, 4000313, 40),
    Array(50, 1, 3000000),
    Array(50, 2, 30000),
//``````````````````
    Array(60, 1122210, 1),
    Array(60, 4000264, 300),
    Array(60, 4000271, 300),
    Array(60, 4170016, 5),
    Array(60, 4000464, 5),
    Array(60, 4310143, 50),
    Array(60, 4000038, 50),
    Array(60, 4000313, 50),
    Array(60, 1, 5000000),
    Array(60, 2, 50000),
//``````````````````
    Array(70, 1122210, 1),
    Array(70, 4000082, 500),
    Array(70, 4170016, 5),
    Array(70, 4000464, 5),
    Array(70, 4310143, 50),
    Array(70, 4000038, 50),
    Array(70, 4000313, 50),
    Array(70, 4310097, 10),
    Array(70, 4310098, 10),
    Array(70, 4310156, 10),
    Array(70, 1, 10000000),
    Array(70, 2, 80000),
//``````````````````
    Array(80, 1122210, 1),
    Array(80, 4000067, 30),
    Array(80, 4001084, 1),
    Array(80, 4310143, 50),
    Array(80, 4000038, 50),
    Array(80, 4000313, 50),
    Array(80, 4310097, 10),
    Array(80, 4310098, 10),
    Array(80, 4310156, 10),
    Array(80, 1, 20000000),
    Array(80, 2, 100000),
//``````````````````
    Array(90, 1122210, 1),
    Array(90, 4000136, 200),
    Array(90, 4000045, 200),
    Array(90, 4021009, 40),
    Array(90, 4011007, 40),
    Array(90, 1, 50000000),
    Array(90, 2, 100000),
//``````````````````
    Array(100, 1122210, 1),
    Array(100, 4000282, 200),
    Array(100, 4000028, 200),
    Array(100, 4000046, 200),
    Array(100, 4310143, 50),
    Array(100, 4000038, 50),
    Array(100, 4000313, 50),
    Array(100, 1, 100000000),
    Array(100, 2, 100000),
//``````````````````
    Array(110, 1122210, 1),
    Array(110, 4001111, 10),
    Array(110, 4000053, 200),
    Array(110, 4000054, 200),
    Array(110, 4310143, 50),
    Array(110, 4000038, 50),
    Array(110, 4000313, 50),
    Array(110, 1, 200000000),
    Array(110, 2, 100000),
//``````````````````
    Array(120, 1122210, 1),
    Array(120, 4170016, 5),
    Array(120, 4000464, 5),
    Array(120, 4001085, 1),
    Array(120, 4001083, 1),
    Array(120, 4310097, 20),
    Array(120, 4310098, 20),
    Array(120, 4310156, 20),
    Array(120, 1, 300000000),
    Array(120, 2, 100000),
//``````````````````
    Array(130, 1122210, 1),
    Array(130, 4000244, 500),
    Array(130, 4000245, 500),
    Array(130, 4000151, 100),
    Array(130, 4000152, 100),
    Array(130, 4310143, 50),
    Array(130, 4000038, 50),
    Array(130, 4000313, 50),
    Array(130, 1, 500000000),
    Array(130, 2, 100000),
//``````````````````
    Array(140, 1122210, 1),
    Array(140, 4001241, 5),
    Array(140, 4001242, 5),
    Array(140, 4001085, 1),
    Array(140, 4001083, 1),
    Array(140, 4310143, 50),
    Array(140, 4000038, 50),
    Array(140, 4000313, 50),
    Array(140, 4310097, 20),
    Array(140, 4310098, 20),
    Array(140, 4310156, 20),
    Array(140, 1, 1000000000),
    Array(140, 2, 100000),
//``````````````````
    Array(150, 1122210, 1),
    Array(150, 4001080, 1),
    Array(150, 4001083, 1),
    Array(150, 1122076, 1),
    Array(150, 4310088, 500),
    Array(150, 1, 2000000000),
    Array(150, 2, 200000)
//``````````````````
)
// 赫拉的钥匙,083没有
var 奖励物品 = Array(
    Array(10, 1122210, 5),
    Array(20, 1122210, 10),
    Array(30, 1122210, 15),
    Array(40, 1122210, 20),
    Array(50, 1122210, 25),
    Array(60, 1122210, 30),
    Array(70, 1122210, 35),
    Array(80, 1122210, 40),
    Array(90, 1122210, 45),
    Array(100, 1122210, 50),
    Array(110, 1122210, 55),
    Array(120, 1122210, 60),
    Array(130, 1122210, 70),
    Array(140, 1122210, 85),
    Array(150, 1122210, 110)
)
var s

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    } else if (mode == 0 && selection == -1) {
        cm.dispose();
        return;
    }

    if (mode == 1) {
        status++;
    } else {
        status--;
        cm.dispose();
        return;
    }

    if (status == 0) {

        var dj = cm.getPlayer().getLevel()
        var text = "\r\n   " + 心 + " " + 心 + "  " + 心 + "   #r#e < 萧曳冒险岛 > #k#n " + 心 + "  " + 心 + "  " + 心 + " \r\n\r\n";
        // text += " #k" + 闹钟图标 + "时间:#r" + hour + "#k#b:#r" + minute + "#k#b:#r" + second + "\t";
        text += "满阶:#r15阶#b	#bLv150满级#k#r" + dj + "#k\r\n\r\n";
        var ss = true;
        for (var i = 0; i < items.length; i++) {
            if (dj > items[i]) {
                var c = cm.getBossRankCount("主线任务2" + items[i]);
                if (c < 1 && ss) {
                    text += "\t\t#L" + i + "#" + 图标 + "#r主线任务(#rLv." + items[i] + "#b)" + 正在进行中 + "#k#l\r\n\r\n"
                    ss = false;
                } else if (c > 0) {
                    text += "\t\t   " + 图标 + "#g主线任务(#rLv." + items[i] + "#b)" + 完成 + "#k\r\n"
                }
            } else {
                text += "\t\t   " + 图标 + "#b主线任务(#rLv." + items[i] + "可开始#b)" + 图标 + "#k\r\n"
            }
        }
        text += "\r\n" + 心 + "  " + 心 + "  " + 心 + "  " + 心 + "  " + 心 + "  " + 心 + "  " + 心 + "  " + 心 + "  " + 心 + "  " + 心 + "  " + 心 + "  " + 心;


        cm.sendOk(text);
        if (ss) {
            cm.dispose()
            return
        }
    } else if (status == 1) {
        s = selection
        var dj = cm.getPlayer().getLevel()
        var text = "\r\n   " + 心 + " " + 心 + "  " + 心 + "   #r#e < 萧曳冒险岛 > #k#n " + 心 + "  " + 心 + "  " + 心 + " \r\n\r\n";
        text += " #k" + 闹钟图标 + "时间:#r" + hour + "#k#b:#r" + minute + "#k#b:#r" + second + "\r\n";
        text += "#d任务需要:\r\n"
        for (var i = 0; i < 任务物品.length; i++) {
            if (任务物品[i][0] == items[s]) {
                if (任务物品[i][1] == 2) {
                    text += "#b需要点券 x #r" + 任务物品[i][2] + "  #b当前:#r" + cm.getPlayer().getCSPoints(1) + "#k\r\n"
                } else if (任务物品[i][1] == 1) {
                    text += "#b需要金币 x #r" + 任务物品[i][2] + "  #b当前:#r" + cm.getPlayer().getMeso() + "#k\r\n"
                } else {
                    text += "#b#v" + 任务物品[i][1] + "##t" + 任务物品[i][1] + "# x #r" + 任务物品[i][2] + "  #b当前:#r#c" + 任务物品[i][1] + "##k\r\n"
                }
            }
        }
        text += "#r任务奖励:\r\n"
        for (var i = 0; i < 奖励物品.length; i++) {
            var ii = 奖励物品[i]
            if (ii[0] == items[s]) {
                switch (ii[1]) {
                    case 0:
                        text += "#b金币:#r" + ii[2] + "#k\r\n"
                        break;
                    case 1:
                        text += "#b点券:#r" + ii[2] + "#k\r\n"
                        break;
                    case 2:
                        text += "#b抵用券:#r" + ii[2] + "#k\r\n"
                        break;
                    case 1122210:
                        text += "#b#v1122210#全属性+" + ii[2] + "#k\r\n"
                        break;
                    default:
                        text += "#b#z" + ii[1] + "#  x #r" + ii[2] + "#k\r\n"
                }
            }
        }
        text += "\t\t#b点击下项提交物品#r背包记得留足够的空格"
        cm.sendNext(text)
    } else if (status == 2) {
        var next = false;
        if (cm.getSpace(1) < 5 || cm.getSpace(2) < 5 || cm.getSpace(4) < 5 || cm.getSpace(3) < 5) {
            cm.sendOk("请把装备栏,消耗栏,其他栏，空出5个格子，设置栏空出5个格子出来");
            cm.dispose();
            return;
        }
        for (var i = 0; i < 任务物品.length; i++) {
            var iii = 任务物品[i]
            if (iii[0] == items[s]) {
                if (iii[1] == 2) {
                    if (cm.getPlayer().getCSPoints(1) < iii[2]) {
                        next = true;
                        break;
                    }
                } else if (iii[1] == 1) {
                    if (cm.getPlayer().getMeso() < iii[2]) {
                        next = true;
                        break;
                    }
                } else {
                    if (!cm.haveItem(iii[1], iii[2])) {
                        next = true;
                        break;
                    }
                }
            }
        }
        if (next) {
            cm.sendOk("你的任务材料准备的不充分,请检查")
            cm.dispose()
            return
        }
        for (var i = 0; i < 任务物品.length; i++) {
            var iii = 任务物品[i]
            if (iii[0] == items[s]) {
                if (iii[1] == 2) {
                    cm.gainNX(-iii[2]);
                } else if (iii[1] == 1) {
                    cm.gainMeso(-iii[2]);
                } else {
                    cm.gainItem(iii[1], -iii[2])
                }
            }
        }
        for (var i = 0; i < 奖励物品.length; i++) {
            var iii = 奖励物品[i]
            if (iii[0] == items[s]) {
                switch (iii[1]) {
                    case 0:
                    case 1:
                    case 2:
                        cm.getPlayer().modifyCSPoints(iii[1], iii[2], true)
                        break
                    default:
                        cm.给属性装备(iii[1], 0, 0, iii[2], iii[2], iii[2], iii[2], 0, 0, iii[2], iii[2], 0, 0, 0, 0, 0, 0, 0);
                        break;
                }
            }
        }
        cm.setBossRankCount("主线任务2" + items[s]);
        cm.getPlayer().指定喇叭("高质地喇叭", "系统公告", "恭喜[" + cm.getPlayer().getName() + "]完成了主线任务" + items[s] + "获得了丰厚的奖励!");
        cm.sendOk("恭喜你完成了主线任务" + items[s] + "获得了丰厚的奖励")
        cm.dispose()
        return
    }
}







