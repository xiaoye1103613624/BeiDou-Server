var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
var 爱心 = "#fEffect/CharacterEff/1032063/0/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#";
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#";
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#";
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#";
var 月卡系统 = "#fEffect/CharacterEff1.img/QQ1408745/2/3#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 音符3 = "#fEffect/CharacterEff.img/1112949/2/0#";
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#"; 
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#"; 
var 任务简介 = "#fUI/UIWindow.img/Quest/summary#"; 
var 任务提示 = "#fUI/UIWindow.img/Quest/BtAlert/mouseOver/0#"; 
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#"; 
var 橙条 = "#fUI/UIWindow.img/Minigame/Common/barTeamA#"; 
var 蘑菇 = "#fUI/UIWindow.img/Minigame/Common/mark#";
var aa = "#fUi/ChatBalloon.img/pet/12/nw#"
var bb = "#fUi/ChatBalloon.img/pet/5/nw#"
var cc = "#fUi/ChatBalloon.img/pet/5/ne#"
var dd = "#fUi/ChatBalloon.img/pet/5/s#"
var ff = "#fUi/ChatBalloon.img/pet/5/n#"
var ss = "#fUi/ChatBalloon.img/pet/5/sw#"
var gg = "#fUi/ChatBalloon.img/pet/5/se#"
///////////////////////////////////////////
var vipLevel = 5010019;
// 赠送材料
var 必成 = 2022615;
var 冒险岛卷 = 2022465;
var 高贵防具自选箱 = 2022613;
var 翅膀自选 = 2022613;
var 精灵吊坠 = 1122017;
var 红武自选 = 2022355;
var 网吧勋章 = 1142145;
var 枫叶 = 4001126;
var 黄金武器 = 2022503;
var S级魔方 = 4000463;

// 月卡工资材料
var x11卷轴 = 2049345;
var x12卷轴 = 2049346;
var x13卷轴 = 2049347;
var x14卷轴 = 2049348;
var x15卷轴 = 2049349;
var 祝福 = 2340000;
var 放大镜 = 2460005;
var 防爆 = 2531000;
var 高等五彩 = 4251202;
var 白嫖 = 5252001;
var 月卡币 = 2022511;
var D片 = 4031179;
var 枫叶球 = 4031456;
var 中国心 = 4000464;
var 白银月卡 = 5010019

// 存储用户选择的办理数量
var cardCount = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            var text = "";
            text += "\t\t"+月卡系统+"\r\n"+群粉心+"";
            text += "\t\t\t" + 星星 + " #r#e#L0#理财卡介绍#l "+星星+"\r\n\r\n";
            text += "\t\t\t#L1##bVIP理财卡办理#l \r\n\r\n";
			text += "\t\t\t#L2##b每日福利领取#l\r\n\r\n";
            text += "\t\t\t" + 小红星 + 小蓝星 + 小红星 + 小蓝星 + 小红星 +  小红星 + 小红星 + 小红星 +"\r\n";

            cm.sendSimple(text);
        } else if (status == 1) {
            if(selection == 0) {
                var pdd = "";
                pdd += "\t\t\t"+月卡系统+"\r\n"+群粉心+"";
                pdd += "\t\t#d购买理财卡#v3700045#[30日]特权需要 #r188赞助/张\r\n\r\n";
                // 同步修改介绍文案：明确每次办理均送100个/张必成破攻
                pdd += "\t#d每次购买立刻获得#r [30日]VIP特权#k 必成破攻#v2614000# #r100个/张 加188累计积分/张#k\r\n\r\n";
                pdd += "#d#v2022509#*20 #v4000487#*5 #v3605006#*10 #v3994742#*500 #v4310144#*5000 #v2022531#*2 #v3994731#*1 #v2710002#*1 #v3994731#*1 #v4310108#*1 #v4001126#*999 \r\n\r\n";
                pdd += "#e※ 持有理财卡每日可领取1次福利，奖励数量与持有卡数量成正比！#n";
                cm.sendOk(pdd);
                cm.dispose();
            } else if(selection == 1) {
                // 询问用户要办理几张VIP理财卡
                var inputText = "";
                inputText += "\t\t" + 星星 + 星星 + 星星 + "\r\n";
                inputText += "#e请输入要办理的VIP理财卡数量：#n\r\n\r\n";
                inputText += "#d每张VIP理财卡价格：#r188赞助#k\r\n";
                inputText += "#d办理福利：#r每张赠送#v2614000#×100 + 188积分#k\r\n";
                inputText += "#d您当前拥有的赞助：#r" + cm.getPlayer().getmoney() + "#k\r\n";
                inputText += "\t\t" + 星星 + 星星 + 星星 + "\r\n";
                
                // 发送输入框，让用户输入数量
                cm.sendGetText(inputText);
            } else if(selection == 2) {
                // 每日福利领取逻辑（无修改，保留原有所有功能）
                var itemCount = cm.itemQuantity(3700045); // 获取当前持有的卡数量
                
                // 第一步：检查是否持有理财卡（无卡则提示办理）
                if(itemCount <= 0) {
                    cm.sendOk("#e您没有#v3700045##n，无法领取每日福利！\r\n\r\n请先#b办理VIP理财卡#k");
                    cm.dispose();
                    return;
                }
                
                // 第二步：检查今日是否已领取（无论有多少卡，仅允许1次）
                var claimedToday = cm.getPlayer().getBossLog("理财卡福利") || 0;
                if(claimedToday >= 1) {
                    cm.sendOk("您今天已经领取过每日福利啦！\r\n\r\n#e无论持有多少理财卡，每日仅可领取1次，明日再来吧！#n");
                    cm.dispose();
                    return;
                }
                
                // 奖励内容：根据卡数量倍增（保留超30000拆分发放逻辑）
                var baseRewards = [ // 基础奖励模板（单张卡的奖励）
                    [2022509, 20],
                    [4000487, 5],
                    [3605006, 10],
                    [3994742, 500],
                    [4310144, 5000],
                    [2022531, 2],
                    [3994731, 2],
                    [2710002, 1],
                    [4310108, 1],
                    [4001126, 999]
                ];
                
                // 计算实际奖励（基础奖励 × 卡数量），并处理超30000拆分发放
                for(var i = 0; i < baseRewards.length; i++) {
                    var itemId = baseRewards[i][0];
                    var baseAmount = baseRewards[i][1];
                    var actualAmount = baseAmount * itemCount; // 按卡数量计算总奖励数
                    
                    if (actualAmount > 30000) {
                        var fullTimes = Math.floor(actualAmount / 30000);
                        var remainAmount = actualAmount % 30000;
                        for (var j = 0; j < fullTimes; j++) {
                            cm.gainItem(itemId, 30000);
                        }
                        if (remainAmount > 0) {
                            cm.gainItem(itemId, remainAmount);
                        }
                    } else {
                        cm.gainItem(itemId, actualAmount);
                    }
                }
                // 金币奖励按原逻辑保留
                cm.gainMeso(1000000 * itemCount);
                
                // 标记今日已领取
                cm.getPlayer().setBossLog("理财卡福利", claimedToday + 1);
                
                // 领取成功提示（无修改）
                var successMsg = "";
                successMsg += "\t\t" + 成功了 + "\r\n";
                successMsg += "#e成功领取每日VIP福利#n\r\n\r\n";
                successMsg += "#b持有理财卡:#k #r" + itemCount + "#k 张\r\n";
                successMsg += "#b奖励已按卡数量倍增:#k 基础奖励 × " + itemCount + "\r\n";
                successMsg += "#b超30000道具已自动拆分发放#k（单次最大30000）\r\n";
                successMsg += "#b今日已领:#k #r1#k / #r1#k 次（每日仅1次）\r\n";
                successMsg += "\t\t" + 星星 + 星星 + 星星 + "\r\n";
                
                cm.sendOk(successMsg);
                cm.喇叭(2, "VIP玩家 [" + cm.getPlayer().getName() + "] 领取了" + itemCount + "张理财卡每日福利！");
                cm.dispose();
            }
        } else if (status == 2) {
            // 处理用户输入的办理数量
            var input = cm.getText();
            cardCount = parseInt(input);
            
            if (isNaN(cardCount) || cardCount <= 0) {
                cm.sendOk("#r输入错误！请输入有效的正整数数量。#k");
                cm.dispose();
                return;
            }
            
            var totalCost = cardCount * 188;
			var totalCost1 = cardCount * 100; // 2614000道具发放数量：办理数量×100
            
            if (cm.getPlayer().getmoney() >= totalCost) {
                // 核心修改：移除所有首次办理BossLog判定，每次办理均执行以下4行核心操作
                cm.gainItem(3700045, cardCount, 30); // 发放理财卡
                cm.gainItem(2614000, totalCost1);     // 每次均按比例发放必成破攻
                cm.getPlayer().setmoney(cm.getPlayer().getmoney() - totalCost); // 扣除赞助
                cm.getPlayer().setlpjf(cm.getPlayer().getlpjf() + totalCost);   // 增加积分
                
                // 办理成功提示：移除首次福利标识，改为通用福利提示
                var successText = "";
                successText += "\t\t" + 成功了 + "\r\n";
                successText += "#e成功办理 #r" + cardCount + "#k 张VIP理财卡！#n\r\n\r\n";
                successText += "#b消耗赞助：#r" + totalCost + "#k\r\n";
                successText += "#b剩余赞助：#r" + cm.getPlayer().getmoney() + "#k\r\n";
                successText += "#b获得积分：#r" + totalCost + "#k\r\n";
                successText += "#b办理福利：#r获得#v2614000#×" + totalCost1 + "#k\r\n"; // 显示实际发放数量
                successText += "\t\t" + 星星 + 星星 + 星星 + "\r\n";
                
                cm.sendOk(successText);
                // 记录办理日志
                cm.getItemLog("理财卡明细", "\r\n【"+cm.getName()+"】 开通 "+cardCount+"张理财卡，消耗"+totalCost+"赞助，剩余："+cm.getPlayer().getmoney()+"，获得#v2614000#×"+totalCost1+"\r\n");
                // 喇叭通知三连发
                for(var i = 0; i < 3; i++) {
                    cm.喇叭(2, "恭喜玩家 [" + cm.getPlayer().getName() + "] 成功办理 " + cardCount + " 张VIP理财卡特权！");
                }
                cm.dispose();
            } else {
                // 赞助不足提示（无修改）
                cm.sendOk("#e办理失败！#n\r\n\r\n#r您需要拥有 " + totalCost + " 个赞助币才能办理 " + cardCount + " 张#i3700045#\r\n当前仅有 " + cm.getPlayer().getmoney() + " 个赞助币");
                cm.dispose();
            }
        }
    }
}