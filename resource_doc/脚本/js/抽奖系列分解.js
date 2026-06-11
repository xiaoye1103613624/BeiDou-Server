var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
var 爱心 = "#fEffect/CharacterEff/1032063/0/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var ttt = "#fUI/UIWindow.img/Quest/icon9/0#";
var xxx = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#";
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#";
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#";
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#";
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

///////////////////////////////////////////
var vipLevel = 0;
var itemList = new Array(); // 玩家背包
var item分解池 = [
	{ id: 1012752, 积分: 100 },//T5面饰
	{ id: 1012753, 积分: 200 },//T4面饰
	{ id: 1012754, 积分: 300 },//T3面饰
	{ id: 1012755, 积分: 400 },//T2面饰
	
	{ id: 1022313, 积分: 100 },//T5眼镜
	{ id: 1022314, 积分: 200 },//T4眼镜
	{ id: 1022315, 积分: 300 },//T3眼镜
	{ id: 1022316, 积分: 400 },//T2
	
	{ id: 1032327, 积分: 100 },//T5耳环
	{ id: 1032328, 积分: 200 },//T4耳环
	{ id: 1032329, 积分: 300 },//T3耳环
	{ id: 1032330, 积分: 400 },//T2
	
	{ id: 1122163, 积分: 100 },//T5项链
	{ id: 1122164, 积分: 200 },//T4项链
	{ id: 1122165, 积分: 300 },//T3项链
	{ id: 1122166, 积分: 400 },//T2
	
	{ id: 1092069, 积分: 10 },//战龙盾牌
	{ id: 1092035, 积分: 10 },//可乐盾牌
	{ id: 1092051, 积分: 10 },//啤酒杯盾牌
//	{ id: 1003843, 积分: 10 }, //奇怪的狐狸面具
	{ id: 1002850, 积分: 10 },
	{ id: 1102604, 积分: 10 },
//	{ id: 1032234, 积分: 10 }, //蓝色桃心耳环
	{ id: 1702472, 积分: 10 },
//	{ id: 1113189, 积分: 10 }, //天堂戒指
//	{ id: 1113190, 积分: 10 }, //天堂戒指
//	{ id: 1113191, 积分: 10 }, //天堂戒指
//	{ id: 1113192, 积分: 10 }, //天堂戒指
//	{ id: 1113193, 积分: 10 }, //天堂戒指
//	{ id: 1113194, 积分: 10 }, //天堂戒指
	{ id: 1112952, 积分: 10 },//希那的愤怒
	{ id: 1112951, 积分: 10 },//麦格纳斯的愤怒
	{ id: 1112666, 积分: 10 },//霸王的永恒戒指
//	{ id: 1113064, 积分: 60 },//狂战士的不朽戒指	
	{ id: 1402037, 积分: 10 },
	{ id: 1402063, 积分: 10 },
	{ id: 1442057, 积分: 10 },
	{ id: 1012020, 积分: 10 },
	{ id: 1012019, 积分: 10 },
	{ id: 1012018, 积分: 10 },
	{ id: 1012017, 积分: 10 },
	{ id: 1012016, 积分: 10 },
	{ id: 1012015, 积分: 10 },
	{ id: 1012014, 积分: 10 },
	{ id: 1012013, 积分: 10 },
	{ id: 1012012, 积分: 10 },
	{ id: 1012011, 积分: 10 },
	{ id: 1012056, 积分: 10 },
	{ id: 1012132, 积分: 10 },
	{ id: 1012309, 积分: 10 },
	{ id: 1012190, 积分: 10 },
	{ id: 1012189, 积分: 10 },
	{ id: 1012188, 积分: 10 },
	{ id: 1022021, 积分: 10 }, //晕呼呼眼镜
	{ id: 1022022, 积分: 10 }, //晕呼呼眼镜
	{ id: 1012373, 积分: 10 }, //休彼德曼的胡子
	{ id: 1132009, 积分: 10 },
	{ id: 1132008, 积分: 10 },
	{ id: 1132007, 积分: 10 },
	{ id: 1132006, 积分: 10 },
	{ id: 1132005, 积分: 10 },
	{ id: 1022047, 积分: 10 },
	{ id: 1022058, 积分: 10 },
	{ id: 1022060, 积分: 10 },
	{ id: 1022067, 积分: 10 },
	{ id: 1122028, 积分: 10 },
	{ id: 1122027, 积分: 10 },
	{ id: 1122026, 积分: 10 },
	{ id: 1122025, 积分: 10 },
	{ id: 1122024, 积分: 10 },
	{ id: 1050127, 积分: 10 },
	{ id: 1050100, 积分: 10 },
	{ id: 1051098, 积分: 10 },
	{ id: 1051140, 积分: 10 },
	{ id: 1002939, 积分: 10 },//安全帽	
	
	{ id: 1442046, 积分: 10},
	{ id: 1372038, 积分: 10},
	{ id: 1372037, 积分: 10},
	{ id: 1372036, 积分: 10},
	{ id: 1372035, 积分: 10},
	{ id: 1302105, 积分: 10 },
	{ id: 1312039, 积分: 10 },
	{ id: 1322065, 积分: 10 },
	{ id: 1332081, 积分: 10 },
	{ id: 1372046, 积分: 10 },
	{ id: 1382062, 积分: 10 },
	{ id: 1402053, 积分: 10 },
	{ id: 1412035, 积分: 10 },
	{ id: 1422039, 积分: 10 },
	{ id: 1432050, 积分: 10 },
	{ id: 1442071, 积分: 10 },
	{ id: 1452062, 积分: 10 },
	{ id: 1462056, 积分: 10 },
	{ id: 1472077, 积分: 10 },
	{ id: 1482029, 积分: 10 },
	{ id: 1492030, 积分: 10 },
	{ id: 1322026, 积分: 10 },
	{ id: 1322025, 积分: 10 },
	{ id: 1322024, 积分: 10 },
	{ id: 1322023, 积分: 10 },
	{ id: 1322022, 积分: 10 },
	{ id: 1322021, 积分: 10 },
	{ id: 1442018, 积分: 10 },
	{ id: 1312169, 积分: 10 },
	{ id: 1372033, 积分: 10 },//圣贤短杖
	{ id: 1372017, 积分: 10 },//领路灯
	{ id: 1332053, 积分: 10 },//野外烧烤串	
	{ id: 1402014, 积分: 10 },
	{ id: 1322027, 积分: 20 },
	{ id: 1402044, 积分: 10 },
	{ id: 1302063, 积分: 10 },
	{ id: 1302021, 积分: 10 },
	{ id: 1302022, 积分: 10 },
	{ id: 1302024, 积分: 10 },
	{ id: 1302031, 积分: 10 },
	{ id: 1302061, 积分: 10 },
	{ id: 1302013, 积分: 10 },
	{ id: 1322012, 积分: 10 },
	{ id: 1432015, 积分: 10 },
	{ id: 1432013, 积分: 10 },
	{ id: 1382041, 积分: 10 },
	{ id: 1382016, 积分: 10 },
	{ id: 1382015, 积分: 10 },
	{ id: 1432008, 积分: 10 },
	{ id: 1432039, 积分: 10 },
	{ id: 1442021, 积分: 10 },
	{ id: 1302016, 积分: 10 },
	{ id: 1302017, 积分: 10 },
	{ id: 1302025, 积分: 10 },
	{ id: 1302026, 积分: 10 },
	{ id: 1302027, 积分: 10 },
	{ id: 1302028, 积分: 10 },
	{ id: 1302029, 积分: 10 },
	{ id: 1092049, 积分: 10 },
	{ id: 1092050, 积分: 10 },
	{ id: 1092008, 积分: 10 },
	{ id: 1092030, 积分: 10 },
	{ id: 1092029, 积分: 10 },
	{ id: 1032025, 积分: 10 },
	{ id: 1032032, 积分: 10 },
	{ id: 1032035, 积分: 10 },
	{ id: 1032047, 积分: 10 },
	{ id: 1032058, 积分: 10 },
	{ id: 1032057, 积分: 10 },
	{ id: 1032056, 积分: 10 },
	{ id: 1032055, 积分: 10 },
	{ id: 1082149, 积分: 10 },
	{ id: 1082148, 积分: 10 },
	{ id: 1082147, 积分: 10 },
	{ id: 1082146, 积分: 10 },
	{ id: 1082150, 积分: 10 },
	{ id: 1082145, 积分: 10 },
//	{ id: 1082002, 积分: 10 },
	{ id: 1082175, 积分: 10 },//马绍尔手套
	{ id: 1082176, 积分: 10 },//马绍尔手套
	{ id: 1082177, 积分: 10 },//马绍尔手套
	{ id: 1082178, 积分: 10 },//马绍尔手套
	{ id: 1082179, 积分: 10 },//马绍尔手套	
	{ id: 1102041, 积分: 10 },
	{ id: 1102040, 积分: 10 },
	{ id: 1102042, 积分: 10 },
	{ id: 1102043, 积分: 10 },
	{ id: 1102163, 积分: 10 }
]; // 分解池中的物品及对应的积分
var 分解物品Array = new java.util.ArrayList();
var 分解物品数量Array = new java.util.ArrayList();
var 分解次数 = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            var pdd = "";
            pdd += "\t\t\t\t\t#b[装备分解机]#k\r\n\r\n";
            pdd += "此功能可以分解以下装备：\r\n";
            for (var i = 0; i < item分解池.length; i++) {
                pdd += "#v" + item分解池[i].id + "# ";
            }
            pdd += "\r\n\t\t\t\t   #r是否确认一键进行分解#k";
            cm.sendYesNo(pdd);
        } else if (status == 1) {
            var text = "";
            text += "  " + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + 蘑菇 + "\r\n\r\n";
        //    text += "尊敬的#r" + cm.getPlayer().getName() + "#k,您好!\r\n";
        //    text += "当前积分剩余：#r" + 查询当前积分() + "#k\r\n"; // 显示当前积分
            itemList = cm.getInventory(1).list().iterator();
            text += "本次分解列表:\r\n";
            while (itemList.hasNext()) {
                var item = itemList.next();
                for (var i = 0; i < item分解池.length; i++) {
                    if (item.getItemId() == item分解池[i].id) {
						var 积分 = item分解池[i].积分; // 获取该物品的积分
						text += "#v" + item.getItemId() + "##z" + item.getItemId() + "#   #r" + (item.getQuantity() * 积分) + "#b 积分#k\r\n";
                        分解次数 += item.getQuantity();
                        分解物品Array.add(item.getItemId());
                        分解物品数量Array.add(item.getQuantity());
                    }
                }
            }
            text += "\r\n总共分解#b:" + 分解次数 + "#k件\r\n";
            text += "\t\t\t\t\t#L0##d是否进行分解???";
            cm.sendSimple(text);
        } else if (status == 2) {
            if (selection == 0) {
                if (分解次数 < 1) {
                    cm.sendOk("分解次数不能是#r0#k件");
                    cm.dispose();
                } else {				
					// 根据累计赞助调整积分
                    var 累计赞助 = cm.getPlayer().getlpjf();
                    var 积分倍数 = 1.0; // 默认积分倍数
                    if (累计赞助 >= 100 && 累计赞助 < 300) {  //VIP.1
                        积分倍数 = 1.05;
                    } else if (累计赞助 >= 300 && 累计赞助 < 500) {  //VIP.2
                        积分倍数 = 1.1;
                    } else if (累计赞助 >= 500 && 累计赞助 < 1000) {  //VIP.3
                        积分倍数 = 1.15;
	                } else if (累计赞助 >= 1000 && 累计赞助 < 2000) {  //VIP.4
                        积分倍数 = 1.2;
	                } else if (累计赞助 >= 2000 && 累计赞助 < 3000) {  //VIP.5
                        积分倍数 = 1.3;
	                } else if (累计赞助 >= 3000 && 累计赞助 < 5000) {  //VIP.6
                        积分倍数 = 1.4;
	                } else if (累计赞助 >= 5000 && 累计赞助 < 7000) {  //VIP.7
                        积分倍数 = 1.5;
	                } else if (累计赞助 >= 7000 && 累计赞助 < 10000) {  //VIP.8
                        积分倍数 = 1.6;
	                } else if (累计赞助 >= 10000 && 累计赞助 < 15000) {  //VIP.9
                        积分倍数 = 1.7;
	                } else if (累计赞助 >= 15000 && 累计赞助 < 20000) {  //VIP.10
                        积分倍数 = 1.8;
	                } else if (累计赞助 >= 20000 && 累计赞助 < 100000) {  //VIP.11
                        积分倍数 = 2;
                    }
					
                    var 总积分 = 0;
                    for (var j = 0; j < 分解物品Array.size(); j++) {
                        var itemId = 分解物品Array.get(j);
                        var quantity = 分解物品数量Array.get(j);
                        var item = null;
                        for (var k = 0; k < item分解池.length; k++) {
                            if (item分解池[k].id == itemId) {
                                item = item分解池[k];
                                break;
                            }
                        }
                        if (item) {
                            cm.gainItem(itemId, -quantity); // 删除分解的装备
							总积分 += Math.floor(quantity * item.积分 * 积分倍数); // 取整避免小数
                        }
                    }
                    更改积分(总积分); // 增加积分
            //        cm.sendOk("分解成功！您获得了#r" + 总积分 + "#k积分\r\n当前积分剩余：#r" + 查询当前积分() + "#k");
                    cm.喇叭(3, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功分解 抽奖装备 并获得 " + 总积分 + " 积分！");
					cm.openNpc(9900004,"装备分解列表");
             //       cm.dispose();
                }
            }
        }
    }
}

function 更改积分(sum) {
    var chr = cm.getPlayer();
    sqlMultiPurpose("UPDATE characters SET ps = ps + " + sum + " WHERE id = " + chr.getId() + "");
    if (sum >= 1) {
        cm.getPlayer().dropMessage(5, "获得：" + sum + " 积分(分解抽奖系列装备)，当前积分剩余：" + 查询当前积分() + "");
    } else if (sum < 0) {
        cm.getPlayer().dropMessage(5, "消费：" + sum + " 积分(分解抽奖系列装备)，当前积分剩余：" + 查询当前积分() + "");
    }
}

function 查询当前积分() {
    var chr = cm.getPlayer();
    var con = cm.getConnection();
    var ps = con.prepareStatement("SELECT ps FROM characters WHERE id = ?");
    ps.setInt(1, chr.getId());
    var rs = ps.executeQuery();
    if (rs.next()) {
        var 当前积分 = rs.getInt("ps");
        rs.close();
        ps.close();
        con.close();
        return 当前积分;
    } else {
        rs.close();
        ps.close();
        con.close();
        return 0;
    }
}

function sqlMultiPurpose(sql) {
    var con = cm.getConnection();
    var ps = con.prepareStatement(sql);
    ps.executeUpdate();
    ps.close();
    con.close();
}