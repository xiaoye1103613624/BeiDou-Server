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

    {id: 1112764, 积分: 40},  // A级宝石戒指
    {id: 1112768, 积分: 40},  // A级宝石戒指
    {id: 1112772, 积分: 40},  // A级宝石戒指
    {id: 1112776, 积分: 40}   // A级宝石戒指
	
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
            text += "尊敬的#r" + cm.getPlayer().getName() + "#k,您好!\r\n";
            text += "当前积分剩余：#r" + 查询当前积分() + "#k\r\n"; // 显示当前积分
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
               //     cm.sendOk("分解成功！您获得了#r" + 总积分 + "#k积分\r\n当前积分剩余：#r" + 查询当前积分() + "#k");
                    cm.喇叭(3, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功分解 A级宝石戒指 并获得 " + 总积分 + " 积分！");
                    cm.openNpc(9900004,"装备分解列表");
                }
            }
        }
    }
}

function 更改积分(sum) {
    var chr = cm.getPlayer();
    sqlMultiPurpose("UPDATE characters SET ps = ps + " + sum + " WHERE id = " + chr.getId() + "");
    if (sum >= 1) {
        cm.getPlayer().dropMessage(5, "获得：" + sum + " 积分(分解A级宝石戒指)，当前积分剩余：" + 查询当前积分() + "");
    } else if (sum < 0) {
        cm.getPlayer().dropMessage(5, "消费：" + sum + " 积分(分解A级宝石戒指)，当前积分剩余：" + 查询当前积分() + "");
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