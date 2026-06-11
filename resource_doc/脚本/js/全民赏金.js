var status = -1;

// 赏金物品的数组
var sjwpArr = [
4000016, //红蜗牛壳
4000008, //道符
4000002, //蝴蝶结
4000007, //火独眼
4000029, //香蕉
4000023, //冰独眼	
4000011, //蘑菇孢芽
4000009, //蓝蘑菇
4000037, //蓝水灵
4000042, //蝙蝠翅膀
4000035, //桌布
4000036, //奇妙的药		
4000032, //鳄鱼皮
4000039, //铁甲猪蹄
4000043, //红螃蟹
4000045, //乌龟		
4000052, //白狼尾
4000060, //月光精灵
4000069, //僵尸
4000074, //黑色飞狮尾
4000102, //黑积木
4000106, //玩具熊猫棉花
4000108, //熊猫娃娃
4000115, //齿轮
4000128, //黄小丑
4000143, //僵尸娃娃
4000162, //华丽鳞片
4000180, //鲨鱼假牙	
4000182, //石灰粉瓶
4000183, //墨汁瓶		
4000188, //鸭蛋
4000191, //黑山羊角
4000192, //鼻环
4000197, //石板
4000205, //骷髅犬绑带	
4000207, //骨盆
4000373, //墨汁
4000292, //人参汤
4000294, //百年桔梗
4000329, //仙人掌球
4000330, //仙人掌刺
4000331, //仙人掌花
4000354, //烧杯
4000089, //流氓A的徽章
4000066, //云狐尾巴
4000075, //三角头巾
4000080, //火焰猎犬的项链
4000281, //蛇纹皮
4000298, //白纸张
4000282, //蟠桃核
4000465, //椰子壳
4000472, //蛇笛
4000469, //围巾
4000375, //轮胎
4000433, //油罐
4000432, //青苔岩石
4000434, //大花草
4000118, //小太空船
4000172, //三尾狐的尾巴
4000440, //粗糙的皮革
4000439, //青苔石
4000085, //火石球的石片
4000050, //企鹅王的嘴
4000049, //白雪人角
4000272, //蛋壳碎片
4000268, //飞龙的翅膀
4000241, //邪恶绵羊嚼过的草
4000233, //半人马的净水	
4000236, //橡木甲虫角
4000240, //小火花羽毛
4000166, //虾肉
4000163, //海马的角
4000027, //怪猫的眼
4000028, //月牙牛魔王的角
4000127, //玩具鼓
4000402, //银人心
4000394, //白虎尾巴
4000444, //绿色衣襟
4000447, //绿色头盔
4000449, //蓝色衣襟
4000452, //蓝色头盔
4000454, //红色衣襟
4000457, //红色头盔
4001006, //火焰羽毛
4000082, //金牙
4000436, //烧杯
4000054, //白角齿
4000053 //黑角齿
];
// 奖励物品的数组
var itemSet = new Array(4000313,4001226,4001227,4001228,4001229,4001230,4000038,3605015,3605016,3605017,3605018,3605019,3994730,4170016,2022309,2022517,4021009,4011007,2614012,3605006,2022530);

var ca = java.util.Calendar.getInstance();
var year = ca.get(java.util.Calendar.YEAR); //获得年份
var month = ca.get(java.util.Calendar.MONTH) + 1; //获得月份
var day = ca.get(java.util.Calendar.DATE);//获取日
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE);//获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒
// 赏金物品需要个最多个数
var sjwpgs = 2000;

var sjrwflag;
var wpindex;
var wpid;
var wpgs;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    // 获取当前时间
    var ca = java.util.Calendar.getInstance();
    var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); // 24小时制的小时数

    // 检查是否在20:00至22:00之间
    if (hour < 10 || hour >= 20) {
        cm.sendOk("整点任务活动开启时间为#r每天10:00至20:00#k。\r\n服务器当前时间：#r" + hour +":" + minute + ":" + second + "#n#k");
        cm.dispose();
        return;
    }


    if (mode == 1) {
        status++;
    } else if (mode == 0 && status != 0) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
        var text = "\t\t\t\t#e#r#v3700247#整点赏金任务#v3700247##k#n\r\n\r\n";
        for (i = 0; i < 10; i++) {
            text += "";
        }
        text += "#b任务说明：\r\n#k每个整点开始#r10分钟内#k可领取赏金任务\r\n按照任务要求20分钟内把物品交给我就能获得奖励\r\n";
        text += "#k别想提前准备好物品再来找我做任务哦\r\n因为我会#r一次性拿走你拥有且我需要的全部物品#k\r\n";
		text += "每完成1次赏金任务获得#r1元宝#k奖励\r\n";
        text += "每完成5次赏金任务还能额外获得一份超级奖励\r\n\r\n";
		text += "              #d您当前已完成 #r "+cm.getPlayer().getBossLog('赏金次数')+" 次 #d赏金任务\r\n\r\n";
		
        text += "     #L1##e#r领取赏金任务#l      #L2#查看与提交当前任务#l\r\n\r\n";

		text += "           #L3#兑换5次任务额外奖励#l\r\n";

        cm.sendSimple(text); 
    }
    else if (status == 1) {
        if (selection == 1) {
            sjrwflag = cm.getPlayer().getBossLog('赏金任务flag');
            if (sjrwflag % 2 != 0) {
                cm.sendOk("你有正在进行中的赏金任务，请放弃后再来领取。");
                cm.dispose();
                return;
           
			} else if (cm.getMin() > 9) {
				cm.sendOk("赏金任务只会在每天10:00至22:00每个整点的10分内进行。");
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
		var dailyRewardLimit = 2; // 每日最大领取次数
		var dailyRewardCount = cm.getPlayer().getBossLog("赏金每日奖励次数") || 0; // 获取玩家当天的领取次数，如果没有记录则默认为0

		if (dailyRewardCount >= dailyRewardLimit) {
			cm.sendOk("你今天已经领取了最大次数的额外奖励，明天再来！。");
			cm.dispose();
			return;
		}

		if (cm.getPlayer().getBossLog("赏金次数") >= 5) {
			cm.gainItem(3605006, 100); //女神赐福
			cm.gainItem(3994731, 1); //一亿金币
			cm.gainItem(2022309, 20); //点券或抵用置换卡
			cm.getPlayer().setBossLog("赏金次数", 0, -5); // 重置赏金次数
			cm.getPlayer().setBossLog("赏金每日奖励次数", (dailyRewardCount + 1)); // 更新每日领取次数
			cm.sendOk("恭喜您完成5次赏金任务\r\n\r\n奖励女神赐福*100、一亿金币*1、点券或抵用置换卡*20");
			cm.喇叭(1, "恭喜玩家 [" + cm.getPlayer().getName() + "] 完成了5次赏金任务，奖励女神赐福*100、一亿金币*1、点券或抵用置换卡*20！");
			cm.dispose();
		} else {
			cm.sendOk("#e#k你还没有完成5次赏金任务，完成后将获得以下奖励：\r\n#r#v3605006:##z3605006# * 100\r\n#v2022309:# #z2022309# * 20\r\n#v3994731:# #z3994731# * 1 ");
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
            var zttext = "\t\t\t\t#e#r#v3700247#赏金任务状态#v3700247##n#k\r\n\r\n";;
            zttext += "你需要在30分钟内带 #r"+wpgs+"个 #k#v"+wpid+"#交给我\r\n";
            //zttext += "你当前拥有 #r#c"+wpgs+"#个 #k#v"+wpid+"#(#r多出的我也会全部没收#k)\r\n";
            zttext += "完成任务我会给您一份不错的随机奖励\r\n";
            zttext += "#k如果您觉得任务有难度也可以给我#r 555个#v4001126#\r\n#k我可以帮你取消当前赏金任务\r\n\r\n";
            zttext += "   #e#r#L6#提交物品领取奖励#l    #L5##b交枫叶放弃任务#l\r\n";
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
            if (cm.haveItem(4001126, 555) == false) {
                cm.sendOk("你没有足够的材料放弃赏金任务");
                cm.dispose();
                return;
            }
            // 放弃赏金任务 扣除东西
            cm.gainItem(4001126, -555);
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
             } else if (cm.getMin() > 19) {
				cm.sendOk("20分钟都过了你才来，我早都不需要了");
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
			// 获取物品名称
			var itemName = cm.getItemName(itemSet[rand]);
				cm.gainItem(itemSet[rand],1);
				cm.setmoneyb(+1);
				cm.playerMessage("奖励:恭喜获得 1元宝");
			//	cm.gainItem(wpid, -30000);
			//	cm.gainItem(wpid, -30000);
				cm.removeAll(wpid); //扣除次道具所有
				cm.sendOk("#e#r恭喜您额外获得：#b#v"+itemSet[rand]+"#.");
				cm.getPlayer().setBossLog('赏金任务flag');
				cm.getPlayer().setBossLog('赏金次数');
				// 领取奖励...cm.getItemName(itemSet[rand])
				cm.喇叭(1, "恭喜玩家 [" + cm.getPlayer().getName() + "] 完成赏金任务，获得 1元宝 和 " + itemName + " ！！");
		//		cm.喇叭(1, "恭喜玩家 [" + cm.getPlayer().getName() + "] 完成赏金任务，获得 3元宝 和 " + itemName + " ！！");
		//		cm.喇叭(1, "恭喜玩家 [" + cm.getPlayer().getName() + "] 完成赏金任务，获得 3元宝 和 " + itemName + " ！！");

				cm.dispose();
        }
        else {
            cm.dispose();
        }
    }
}
/*
function 影藏福利() {
if (Math.floor(Math.random() * 5) <= 1) {//   
		奖励 = Math.floor(Math.random() * 3+1);// 5+1 5的范围 取 1个数字 随机 
        //cm.getPlayer().setwzcz(cm.getPlayer().getwzcz()+奖励);
		cm.setmoneyb(+奖励);
		//cm.gainmoney(奖励);
		cm.playerMessage("奖励:"+奖励+" 元宝奖励！");	
		//cm.worldMessage(12, cm.getC().getChannel(),"【签到隐藏福利】" + " : " + " [" + cm.getPlayer().getName() + "] 签到时欧皇附体，额外获得了"+奖励+"元赞助奖励！");
	}
}*/