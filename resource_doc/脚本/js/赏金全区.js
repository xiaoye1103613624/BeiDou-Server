var status = -1;

// 赏金物品的数组
var sjwpArr = [
    4000000,4000001,4000002,4000003,4000004,4000005,4000006,4000007,4000008,4000009,
    4000010,4000011,4000012,4000013,4000014,4000015,4000016,4000017,4000018,4000019,
    4000020,4000021,4000022,4000023,4000024,4000025,4000026,4000027,4000028,4000029,
    4000030,4000031,4000032,4000033,4000034,4000035,4000036,4000037,4000039,
    4000041,4000042,4000043,4000044,4000045,4000046,4000048,4000049,
    4000050,4000051,4000052,4000053,4000054,4000055,4000056,4000057,4000058,4000059,
    4000060,4000061,4000062,4000063,4000064,4000065,4000068,4000069,
    4000070,4000071,4000072,4000073,4000074,4000075,4000076,4000077,4000078,4000079,
    4000080,4000082,4000083,4000084,4000085,4000086,4000087,4000088,4000089,
    4000090,4000091,4000092,4000093,4000094,4000095,4000096,4000097,4000098,4000099
];
// 奖励物品的数组
var itemSet = new Array(4000313,4020000,4020001,4020002,4020003,4020004,4020005,4020006,4020007,4020008,4004000,4004001,4004002,4004003,4004004,4010000,4010001,4010002,4010003,4010004,4010005,4010006,5150040,4001136,5201004,5201005,4001245,3010000,3010001,3010002,3010003,3010004,3010005,3010006,3010007,3010008,3010009,3010010,3010012,3010013,3010014,3010016,3010017,3010018,2040807,2040709,2040710,2040711,2040806,2044103,2044203,2040006,4310149,2040303,2040403,2040506,2040507,2040603,2043003,2043103,2043703,2043803,2044003,2044303,2044403,2044503,2044603,2044703,2044815,2044908,2460005,2022511,4310036,4310023,4310024,1302030,1332025,1382012,1432012,1442024,1452022,1462019,1472032,1422014,1412011,1482020,1492020);


// 赏金物品需要个最多个数
var sjwpgs = 100;

var sjrwflag;
var wpindex;
var wpid;
var wpgs;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == 1) {
        status++;
    } else if (mode == 0 && status != 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
        var text = "\t\t  #e#r#v4000038#欢迎来到赏金猎人任务#v4000038##k#n\r\n\r\n";
        for (i = 0; i < 10; i++) {
            text += "";
        }
        text += "#L1##e#b我要领取赏金任务#l     #L2#我要查看赏金任务#l\r\n\r\n\r\n";
		text += "            #e#r今日完成赏金次数:"+cm.getPlayer().getBossLog('赏金次数')+"#l\r\n";
		 text += "       #L3##e#b我要领取今日赏金任务奖励#v4310149##l\r\n\r\n\r\n";
       text += "  #e#k赏金任务介绍:每小时0到10分可以领取,完成任务！#l\r\n";
        cm.sendSimple(text); 
    }
    else if (status == 1) {
        if (selection == 1) {
            sjrwflag = cm.getPlayer().getBossLog('赏金任务flag');
            if (sjrwflag % 2 != 0) {
                cm.sendOk("你有正在进行中的赏金任务，请放弃后再来领取。");
                cm.dispose();
                return;
           
			} else if (cm.getMin() > 10) {
				cm.sendOk("赏金任务开启时间,每小时0分-010分可以进行。");
                cm.dispose();
                 return;
               
                  
            }
            sjrwflag ++;
            wpindex = Math.floor(Math.random()*(sjwpArr.length));
            wpid = sjwpArr[wpindex];
            wpgs = Math.floor(Math.random()*sjwpgs) + 1;
            var wpindex2 = wpindex.toString(2);
            var wpgs2 = wpgs.toString(2);
            // 记录物品的数组索引
            var j;
            j = 0;
            for (var i = wpindex2.length-1; i >= 0; i--) {
                if (wpindex2[i] == '1') cm.getPlayer().setBossLog(sjrwflag+'赏金物品'+j);
                j++;
            }
            // 记录物品的个数
            j = 0;
            for (var i = wpgs2.length-1; i >= 0; i--) {
                if (wpgs2[i] == '1') cm.getPlayer().setBossLog(sjrwflag+'赏金物品个数'+j);
                j++;
            }
            // 标记已经接了赏金任务
            cm.getPlayer().setBossLog('赏金任务flag');
            cm.sendOk("恭喜你成功接取赏金任务，需要物品#v"+ wpid + "#个数：" + wpgs);
            cm.dispose();
			 } else if (selection == 3) { 
                if (cm.getPlayer().getBossLog("赏金次数") > 4)  {
                cm.gainItem(4310149, 1);
                cm.getPlayer().setBossLog("赏金次数", 0, -5);
				 cm.sendOk("恭喜您用5次赏金任务兑换#v4310149#");
				cm.喇叭(1, "恭喜玩家[" + cm.getPlayer().getName() + "]使用赏金任务次数兑换1亿金币！！");
                cm.dispose();
                } else {
                    cm.sendOk("#e#k你今天还没有完成5次赏金猎人任务");
                    cm.dispose();
                }
        }
        else if (selection == 2) {
            sjrwflag = cm.getPlayer().getBossLog('赏金任务flag');
            if (sjrwflag % 2 == 0) {
                cm.sendOk("你当前没有进行中的赏金任务，请领取后再来");
                cm.dispose();
                return;
            }
            wpindex = 0;
            for (var k = 10; k >= 0; k--) {
                wpindex *= 2;
                if (cm.getPlayer().getBossLog(sjrwflag+'赏金物品'+k) > 0) wpindex += 1;
            }
            wpgs = 0;
            for (var k = 10; k >= 0; k--) {
                wpgs *= 2;
                if (cm.getPlayer().getBossLog(sjrwflag+'赏金物品个数'+k) > 0) wpgs += 1;
            }
            wpid = sjwpArr[wpindex];
            var zttext = "\t\t  #e#r#v4000038#你当前的赏金任务状态#v4000038#\r\n";;
            zttext += "你需要带来物品：#v"+wpid+"#,个数：#e#k"+wpgs+"。\r\n";
            zttext += "#e#r放弃赏金任务需要200个枫叶\r\n";
            zttext += "赏金获得随机#v1382012#,#v2460005#,#v2022511#,#v2044503#,#v4310023#,#v4310024#\r\n";
            zttext += "#L5##g我要放弃赏金任务#l	     #L6##b我要领取赏金奖励#k\r\n";
            cm.sendSimple(zttext);
        }
        else {
            cm.dispose();
        }
		
    }
    else if (status == 2) {
        if (selection == 5) {
            sjrwflag = cm.getPlayer().getBossLog('赏金任务flag');
            if (sjrwflag % 2 == 0) {
                cm.sendOk("你当前没有进行中的赏金任务。");
                cm.dispose();
                return;
            }
            // 判断下放弃要的物品够不够
            if (cm.haveItem(4001126, 200) == false) {
                cm.sendOk("你没有足够的材料放弃赏金任务");
                cm.dispose();
                return;
            }
            // 放弃赏金任务 扣除东西
            cm.gainItem(4001126, -200);
            cm.getPlayer().setBossLog('赏金任务flag');
            cm.sendOk("恭喜你成功放弃赏金任务。");
            cm.dispose();
        }
        else if (selection == 6) {
            sjrwflag = cm.getPlayer().getBossLog('赏金任务flag');
            if (sjrwflag % 2 == 0) {
                cm.sendOk("你当前没有进行中的赏金任务，请领取后再来");
                cm.dispose();
				return;
             } else if (cm.getMin() > 5) {
				cm.sendOk("赏金提交任务开启时间,每小时0分-05分可以进行。");
                cm.dispose();
				return;
            }
            wpindex = 0;
            for (var k = 10; k >= 0; k--) {
                wpindex *= 2;
                if (cm.getPlayer().getBossLog(sjrwflag+'赏金物品'+k) > 0) wpindex += 1;
            }
            wpgs = 0;
            for (var k = 10; k >= 0; k--) {
                wpgs *= 2;
                if (cm.getPlayer().getBossLog(sjrwflag+'赏金物品个数'+k) > 0) wpgs += 1;
            }
            wpid = sjwpArr[wpindex];
            // 判断物品够不够
            if (cm.haveItem(wpid, wpgs) == false) {
                cm.sendOk("你没有带来足够的物品");
                cm.dispose();
                return;
            }
            var rand = Math.floor(Math.random() * itemSet.length);
              cm.gainItem(itemSet[rand],1);
            // 扣除物品 结束赏金任务
            cm.gainItem(wpid, -wpgs);
            cm.sendOk("#e#r恭喜您额外获得：#b#v"+itemSet[rand]+"#.");
            cm.getPlayer().setBossLog('赏金任务flag');
			cm.getPlayer().setBossLog('赏金次数');
            // 领取奖励...cm.getItemName(itemSet[rand])
cm.喇叭(1, "恭喜玩家[" + cm.getPlayer().getName() + "]完成本阶段赏金任务获得随机奖励！！");


            cm.dispose();
        }
        else {
            cm.dispose();
        }
    }
}