var MapleInventoryManipulator = Java.type('server.MapleInventoryManipulator');
var MapleItemInformationProvider = Java.type('server.MapleItemInformationProvider');
var DatabaseConnection = Java.type('database.DatabaseConnection');
var 点券图标 = "#fUI/CashShop/CashItem/0#";
var status = 0;
var ringnum = 0;
var id = 0;
var itemIndex;
var sweepLogKey = "一键扫荡跑环";     // 扫荡记录键名
var itemList = Array(
Array(4000016, 188),  // 红蜗牛壳
Array(4000008, 188),  // 道符
Array(4000002, 188),  // 蝴蝶结
Array(4000007, 188),  // 火独眼
Array(4000029, 188),  // 香蕉
Array(4000023, 188),  // 冰独眼
Array(4000011, 188),  // 蘑菇孢芽
Array(4000009, 188),  // 蓝蘑菇
Array(4000037, 188),  // 蓝水灵
Array(4000042, 188),  // 蝙蝠翅膀
Array(4000035, 188),  // 桌布
Array(4000036, 188),  // 奇妙的药
Array(4000032, 188),  // 鳄鱼皮
Array(4000039, 188),  // 铁甲猪蹄
Array(4000043, 188),  // 红螃蟹
Array(4000045, 188),  // 乌龟
Array(4000052, 188),  // 白狼尾
Array(4000060, 188),  // 月光精灵
Array(4000069, 188),  // 僵尸
Array(4000074, 188),  // 黑色飞狮尾
Array(4000102, 188),  // 黑积木
Array(4000106, 188),  // 玩具熊猫棉花
Array(4000108, 188),  // 熊猫娃娃
Array(4000115, 188),  // 齿轮
Array(4000128, 188),  // 黄小丑
Array(4000143, 188),  // 僵尸娃娃
Array(4000162, 188),  // 华丽鳞片
Array(4000180, 188),  // 鲨鱼假牙
Array(4000182, 188),  // 石灰粉瓶
Array(4000183, 188),  // 墨汁瓶
Array(4000188, 188),  // 鸭蛋
Array(4000191, 188),  // 黑山羊角
Array(4000192, 188),  // 鼻环
Array(4000197, 188),  // 石板
Array(4000205, 188),  // 骷髅犬绑带
Array(4000207, 188),  // 骨盆
Array(4000373, 188),  // 墨汁
Array(4000292, 188),  // 人参汤
Array(4000294, 188),  // 百年桔梗
Array(4000329, 188),  // 仙人掌球
Array(4000330, 188),  // 仙人掌刺
Array(4000331, 188),  // 仙人掌花
Array(4000354, 188),  // 烧杯
Array(4000089, 188),  // 流氓A的徽章
Array(4000066, 188),  // 云狐尾巴
Array(4000075, 188),  // 三角头巾
Array(4000080, 188),  // 火焰猎犬的项链
Array(4000281, 188),  // 蛇纹皮
Array(4000298, 188),  // 白纸张
Array(4000282, 188),  // 蟠桃核
Array(4000465, 188),  // 椰子壳
Array(4000472, 188),  // 蛇笛
Array(4000469, 188),  // 围巾
Array(4000375, 188),  // 轮胎
Array(4000433, 188),  // 油罐
Array(4000432, 188),  // 青苔岩石
Array(4000434, 188),  // 大花草
Array(4000118, 188),  // 小太空船
Array(4000172, 188),  // 三尾狐的尾巴
Array(4000440, 188),  // 粗糙的皮革
Array(4000439, 188),  // 青苔石
Array(4000085, 188),  // 火石球的石片
Array(4000050, 188),  // 企鹅王的嘴
Array(4000049, 188),  // 白雪人角
Array(4000272, 188),  // 蛋壳碎片
Array(4000268, 188),  // 飞龙的翅膀
Array(4000241, 188),  // 邪恶绵羊嚼过的草
Array(4000233, 188),  // 半人马的净水
Array(4000236, 188),  // 橡木甲虫角
Array(4000240, 188),  // 小火花羽毛
Array(4000166, 188),  // 虾肉
Array(4000163, 188),  // 海马的角
Array(4000027, 188),  // 怪猫的眼
Array(4000028, 188),  // 月牙牛魔王的角
Array(4000127, 188),  // 玩具鼓
Array(4000402, 188),  // 银人心
Array(4000394, 188),  // 白虎尾巴
Array(4000444, 188),  // 绿色衣襟
Array(4000447, 188),  // 绿色头盔
Array(4000449, 188),  // 蓝色衣襟
Array(4000452, 188),  // 蓝色头盔
Array(4000454, 188),  // 红色衣襟
Array(4000457, 188),  // 红色头盔
Array(4001006, 188),  // 火焰羽毛
Array(4000082, 188)  // 金牙
//Array(4000436, 188)  // 烧杯
//Array(4000054, 188),  // 白角齿
//Array(4000053, 188)  // 黑角齿
);

var 随机道具 = Array(
    2340000, 4000464, 2049122, 2048708, 4001226, 4001227, 4001228, 4001229, 4001230, 2711003
);
var 最终道具 = Array(
    2022699, 2049104, 2460007, 4310088, 2511127, 1092069, 1092035, 1092051
);
var myDate = new Date();
var year = myDate.getFullYear();
var month = myDate.getMonth() + 1;
var days = myDate.getDate();

function start() {
    // 一键扫荡检测
    if (hasSweepLogToday(sweepLogKey)) {
        cm.sendOk("#r你今天已经使用过“一键扫荡跑环”，无法再执行任务！");
        cm.dispose();
        return;
    }

    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            id = cm.getPlayer().getId();
            ringnum = getBossLog1("paoshang1");

            var strlen = "任务所需材料均为随机\r\n#e#r【每环随机奖励】\r\n";
            for (var i = 0; i < 随机道具.length; i++) {
                strlen += "#v" + 随机道具[i] + "# ";
            }
            strlen += "\r\n完成30环随机奖励:\r\n";
            for (var j = 0; j < 最终道具.length; j++) {
                strlen += "#v" + 最终道具[j] + "# ";
            }

            if (ringnum <= 0) {
                strlen += "\r\n#r玩法详情： \r\n#n#b每次完成必得#r#z3994789#一张#b,\r\n#n#b全部完成必得#r#z3994789#20张#b,\r\n您需要将我需要的材料拿来,满足我的条件后,\r\n我会给你一个巨大的奖品哦,\r\n每天我都会提供给你20次任务,做完就能领取巨额大奖!!";
                cm.sendNext(strlen); // 确保玩家可以点击下一步
            } else if (ringnum <= 30) {
                strlen += "\r\n\r\n#n您已经准备好完成我的委托了么？";
                cm.sendNext(strlen); // 确保玩家可以点击下一步
            } else {
                strlen = "\r\n\r\n真厉害！您已经完成了当日所有跑商！";
                cm.sendOk(strlen);
                cm.dispose();
                return;
            }
        } else if (status == 1) {
            if (cm.getInventory(4).isFull()) {
                cm.sendOk("#b请保证其他栏位至少有2个空格,否则无法继续.");
                cm.dispose();
                return;
            }
            if (cm.getInventory(2).isFull()) {
                cm.sendOk("#b请保证消耗栏位至少有2个空格,否则无法继续.");
                cm.dispose();
                return;
            }

            if (ringnum <= 0) {
                // 如果 ringnum <= 0，直接分配第一个任务
                var ran = Math.floor(Math.random() * itemList.length);
                var itemid = itemList[ran][0];
                var itemnum = itemList[ran][1];
                setOneTimeLog(ran, id);
                ringnum = 1; // 设置为第一个任务
                setBossLog1("paoshang1"); // 初始化任务日志
                var strlen = "您当前跑环环数为： " + ringnum + "\r\n\r\n";
                strlen += "这次您需要帮我搜集" + itemnum + "个#v" + itemid + "#\r\n期待您的好消息";
                cm.sendOk(strlen);
                cm.dispose();
                return;
            }

            itemIndex = getOneTimeLog(id);
            if (ringnum > 0 && ringnum < 30) { // 每轮奖励
                if (cm.haveItem(itemList[itemIndex][0], itemList[itemIndex][1])) {
                    cm.gainItem(itemList[itemIndex][0], -itemList[itemIndex][1]);
                    var sel = Math.floor(Math.random() * 随机道具.length);
                    cm.gainItem(随机道具[sel], 1);
                    cm.gainItem(3994789, 1);
                    cm.getPlayer().指定喇叭("高质地喇叭", "每日跑商", "玩家 [" + cm.getPlayer().getName() + "] 完成了第" + ringnum + "环跑环任务, 获得了奖励!");
                    cm.sendOk("恭喜您完成了这次跑商，本环奖励: #v" + 随机道具[sel] + "#");
                    updateTask();
                } else {
                    cm.sendOk("#r当前第" + ringnum + "环\r\n#k对不起，您还没有拿来我需要的材料，加油哦！\r\n当前需要您帮我搜集#r " + itemList[itemIndex][1] + " #b个#v" + itemList[itemIndex][0] + "#\r\n\r\n期待您的好消息");
                    cm.dispose();
                    return;
                }
            } else if (ringnum == 30) { // 最终奖励
                if (cm.haveItem(itemList[itemIndex][0], itemList[itemIndex][1])) {
                    cm.gainItem(itemList[itemIndex][0], -itemList[itemIndex][1]);
                    var sel = Math.floor(Math.random() * 最终道具.length);
                    cm.gainItem(最终道具[sel], 1);
                    cm.gainItem(3994789, 20);
                    var rewardName = "#z" + 最终道具[sel] + "#";
                    cm.sendOk("恭喜您完成了这次跑商，本环奖励: #v" + 最终道具[sel] + "#");
                    cm.getPlayer().指定喇叭("高质地喇叭", "每日跑商", "玩家 [" + cm.getPlayer().getName() + "] 完成全部跑环任务, 获得了最终奖励!");
                    if (noData("paoshang1")) {
                        setBossLog1("paoshang1");
                    } else {
                        updateBossLog1("paoshang1");
                    }
                } else {
                    cm.sendOk("" + ringnum + "对不起，您还没有拿来我需要的材料，加油哦！\r\n\r\n这次您需要帮我搜集" + itemList[itemIndex][1] + "个#v" + itemList[itemIndex][0] + "#\r\n期待您的好消息");
                    cm.dispose();
                    return;
                }
            }
        }
    }
}

function updateTask() {
    var ran = Math.floor(Math.random() * itemList.length);
    var itemid = itemList[ran][0];
    var itemnum = itemList[ran][1];
    changeOneTimeLog(ran, id);
    var strlen = "您当前跑环环数为： " + (ringnum + 1) + "\r\n\r\n";
    strlen += "这次您需要帮我搜集" + itemnum + "个#v" + itemid + "#\r\n期待您的好消息";
    if (noData("paoshang1")) {
        setBossLog1("paoshang1");
    } else {
        updateBossLog1("paoshang1");
    }
    cm.sendOk(strlen);
    cm.dispose();
}

function getBossLog1(log) {
    var id = cm.getPlayer().getId();
    var con = DatabaseConnection.getConnection();
    var count = 0;
    var ps;
    ps = con.prepareStatement("SELECT * FROM bosslog1 WHERE characterid = ? and bossid = ? and time = CURDATE()");
    ps.setInt(1, id);
    ps.setString(2, log);
    var rs = ps.executeQuery();
    if (rs.next()) {
        count = rs.getInt("count");
    } else {
        count = 0;
    }
    rs.close();
    ps.close();
    return count;
}

function setBossLog1(log) {
    var id = cm.getPlayer().getId();
    var con1 = DatabaseConnection.getConnection();
    var day = "" + year + "-" + month + "-" + days + "";
    var ps = con1.prepareStatement("insert into bosslog1 (characterid, bossid, count, time) values (?,?,?,?)");
    ps.setInt(1, id);
    ps.setString(2, log);
    ps.setInt(3, 1);
    ps.setString(4, day);
    ps.executeUpdate();
    ps.close();
}

function updateBossLog1(log) {
    var id = cm.getPlayer().getId();
    var con1 = DatabaseConnection.getConnection();
    var ps = con1.prepareStatement("update bosslog1 set count = count + 1 where characterid = ? and bossid = ? and time = CURDATE()");
    ps.setInt(1, id);
    ps.setString(2, log);
    ps.executeUpdate();
    ps.close();
}

function getOneTimeLog(id) {
    var con = DatabaseConnection.getConnection();
    var count = -1;
    var ps;
    ps = con.prepareStatement("SELECT log FROM onetimelog WHERE characterid = ? and log like '%跑环任务%'");
    ps.setInt(1, id);
    var rs = ps.executeQuery();
    if (rs.next()) {
        count = rs.getString("log").replace("跑环任务_", "");
    }
    rs.close();
    ps.close();
    return count;
}

function setOneTimeLog(bossid, id) {
    var con1 = DatabaseConnection.getConnection();
    var ps = con1.prepareStatement("insert into onetimelog (characterid, log) values (?,?)");
    ps.setInt(1, id);
    ps.setString(2, "跑环任务_" + bossid);
    ps.executeUpdate();
    ps.close();
}

function changeOneTimeLog(bossid, id) {
    var con1 = DatabaseConnection.getConnection();
    var ps = con1.prepareStatement("update onetimelog set log = ? where characterid = ? and log like '%跑环任务%'");
    ps.setString(1, "跑环任务_" + bossid);
    ps.setInt(2, id);
    ps.executeUpdate();
    ps.close();
}

function noData(log) {
    var id = cm.getPlayer().getId();
    var con = DatabaseConnection.getConnection();
    var create = false;
    var ps;
    ps = con.prepareStatement("SELECT * FROM bosslog1 WHERE characterid = ? and bossid = ? and time = CURDATE()");
    ps.setInt(1, id);
    ps.setString(2, log);
    var rs = ps.executeQuery();
    if (rs.next()) {
        create = false;
    } else {
        create = true;
    }
    rs.close();
    ps.close();
    return create;
}

function hasSweepLogToday(log) {
    var id = cm.getPlayer().getId();
    var con = DatabaseConnection.getConnection();
    var count = 0;
    var ps = con.prepareStatement(
        "SELECT count FROM bosslog1 WHERE characterid = ? AND bossid = ? AND time = CURDATE()"
    );
    ps.setInt(1, id);
    ps.setString(2, log);
    var rs = ps.executeQuery();
    if (rs.next()) {
        count = rs.getInt("count");
    }
    rs.close();
    ps.close();
    return count > 0;
}