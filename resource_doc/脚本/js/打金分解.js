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
var item分解池 = new Array(
1112766,  //C级宝石戒指
1112770,  //C级宝石戒指
1112774,  //C级宝石戒指
1112778,  //C级宝石戒指

1122174,  //  运动套
1032121,  //
1082401,  //
1042231,  //
1062148,  //
1072618,  //
1002357, //扎昆头盔
1002926, //暴力熊帽
1002927, //心疤狮王头
1092046, //冒险岛战士盾牌
1003364, //传说冒险岛帽子
1052405, //传说冒险岛套服
1082391, //传说冒险岛手套
1072610, //传说冒险岛靴子
1102322, //传说冒险岛披风
1132110, //传说冒险岛腰带
1302192, //传说冒险岛单手剑
1402129, //传说冒险岛双手剑
1432117, //传说冒险岛枪
1442154, //传说冒险岛矛
1382142, //传说冒险岛长杖
1452147, //传说冒险岛弓
1462136, //传说冒险岛弩
1472159, //传说冒险岛拳套
1332168, //传说冒险岛短刀
1482120, //传说冒险岛指节
1492119, //传说冒险岛短枪

1003552,  //  T5
1052461,  //
1102441,  //
1082433,  //
1132154,  //
1072666,  //
1302227,  //  T5武器
1402151,  //
1432138,  //
1442173,  //
1382168,  //
1452170,  //
1462159,  //
1472179,  //
1332193,  //
1482140,  //
1492152,  //
1092022,  // T5盾牌

1003561,  //  风暴
1052467,  //
1102467,  //
1082438,  //
1132161,  //
1072672,  //

1003740,  // 终极
1052569,  //
1102506,  //
1082498,  //
1132182,  //
1072768,  //

1002939,  // 抽奖
1050127,  //
1012251,  //
1082149,  //
1102163,  //
1092049,  //

1003540,  // 外星人
1052460,  //
1032142,  //
1072664,  //
1082432,  //
1112738,  //
1122197,  //
1132152); // 假设这是分解池的ID
var 分解积分 = 1; // 每分解一个装备获得的积分
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
			pdd += "#v" + item分解池[i] + "#";
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
                    if (item.getItemId() == item分解池[i]) {
                        text += "#v" + item.getItemId() + "##z" + item.getItemId() + "# x " + item.getQuantity() + "个\r\n";
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
                    var 总积分 = 0;
                    for (var j = 0; j < 分解物品Array.size(); j++) {
                        var itemId = 分解物品Array.get(j);
                        var quantity = 分解物品数量Array.get(j);
                        cm.gainItem(itemId, -quantity); // 删除分解的装备
                        总积分 += quantity * 分解积分; // 计算总积分
                    }
                    更改积分(总积分); // 增加积分
                    cm.sendOk("分解成功！您获得了#r" + 总积分 + "#k积分\r\n当前积分剩余：#r" + 查询当前积分() + "#k");
                    cm.喇叭(3, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功分解一批装备并获得 " + 总积分 + " 积分！");
                    cm.dispose();
                }
            }
        }
    }
}

function 更改积分(sum) {
    var chr = cm.getPlayer();
    sqlMultiPurpose("UPDATE characters SET ps = ps + " + sum + " WHERE id = " + chr.getId() + "");
    if (sum >= 1) {
        cm.getPlayer().dropMessage(5, "获得：" + sum + " 积分(装备分解专属)，当前积分剩余：" + 查询当前积分() + "");
    } else if (sum < 0) {
        cm.getPlayer().dropMessage(5, "消费：" + sum + " 积分(装备分解专属)，当前积分剩余：" + 查询当前积分() + "");
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