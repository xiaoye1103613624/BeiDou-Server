var mi0 = "┏━━━━━━━━━━━┓";
var mi1 = "┃     - XiaoMiMS -     ┃";
var mi2 = "┃ 脚本仿制  　定制脚本 ┃";
var mi3 = "┃ 技术支持 　 游戏顾问 ┃";
var mi4 = "┃ ＷＺ添加　  地图制作 ┃";
var mi5 = "┣━━━━━━━━━━━┫";
var mi6 = "┃　唯一QQ:526703257    ┃";
var mi7 = "┗━━━━━━━━━━━┛";

// 脚本参数设定
var xiaomi = { 
    装备: 1113077,
    主题变量: "境界突破",
    合成常量: "mi境界合成"
};

var xmxsz = new Array(
    { 等级: 1, 几率: 100, 强化物品: 1112601, 名称: "炼精化气", 时装称号等级: 1, 金币: 100000000, 四维: 10, 双攻: 10, 材料: [ [4440300,100],[4310088,100],[4310143,100] ] },
    { 等级: 2, 几率: 90, 强化物品: 1112602, 名称: "炼气化神", 时装称号等级: 4, 金币: 100000000, 四维: 20, 双攻: 20, 材料: [ [4440200,100],[4310088,100],[4310143,100] ] },
    { 等级: 3, 几率: 80, 强化物品: 1112603, 名称: "炼神返虚", 时装称号等级: 7, 金币: 100000000, 四维: 30, 双攻: 30, 材料: [ [4440101,100],[4310088,100],[4310143,100] ] },
    { 等级: 4, 几率: 70, 强化物品: 1112604, 名称: "炼虚合道", 时装称号等级: 10, 金币: 100000000, 四维: 40, 双攻: 40, 材料: [ [4440001,100],[4310088,100],[4310143,100] ] },
    { 等级: 5, 几率: 60, 强化物品: 1112610, 名称: "道胎初结", 时装称号等级: 13, 金币: 100000000, 四维: 50, 双攻: 50, 材料: [ [4443300,100],[4310088,300],[4310143,300] ] },
    { 等级: 6, 几率: 50, 强化物品: 1112611, 名称: "脱胎换骨", 时装称号等级: 14, 金币: 100000000, 四维: 60, 双攻: 60, 材料: [ [4443200,100],[4310088,300],[4310143,300] ] },
    { 等级: 7, 几率: 40, 强化物品: 1112612, 名称: "不坠轮回", 时装称号等级: 15, 金币: 100000000, 四维: 70, 双攻: 70, 材料: [ [4443101,100],[4310088,300],[4310143,300] ] },
    { 等级: 8, 几率: 30, 强化物品: 1112613, 名称: "举世无双", 时装称号等级: 16, 金币: 100000000, 四维: 80, 双攻: 80, 材料: [ [4443101,100],[4310088,300],[4310143,300] ] },
    { 等级: 9, 几率: 20, 强化物品: 1114200, 名称: "天道圣人", 时装称号等级: 17, 金币: 100000000, 四维: 100, 双攻: 100, 材料: [ [4443001,100],[4310088,300],[4310143,300] ] }
);

var xmxszch = new Array(
    { 等级: 1, 名称: "一阶●旋照★赵客缦胡缨★", 称号ID: 1112577 },
    { 等级: 2, 名称: "二阶●开光★吴钩霜雪明★", 称号ID: 1112578 },
    { 等级: 3, 名称: "三阶●融合★银鞍照白马★", 称号ID: 1112579 },
    { 等级: 4, 名称: "四阶●心动★飒沓如流星★", 称号ID: 1112580 },
    { 等级: 5, 名称: "五阶●灵寂★十步杀一人★", 称号ID: 1112581 },
    { 等级: 6, 名称: "六阶●金丹★千里不留行★", 称号ID: 1112582 },
    { 等级: 7, 名称: "七阶●元婴★事了拂衣去★", 称号ID: 1112587 },
    { 等级: 8, 名称: "八阶●出窍★深藏身与名★", 称号ID: 1112588 },
    { 等级: 9, 名称: "九阶●分神★闲过信陵饮★", 称号ID: 1112589 },
    { 等级: 10, 名称: "十阶●合体★脱剑膝前横★", 称号ID: 1112590 },
    { 等级: 11, 名称: "十一阶●渡劫★谁能书阁下★", 称号ID: 1112599 },
    { 等级: 12, 名称: "十二阶●大乘★白首太玄经★", 称号ID: 1112600 },
    { 等级: 13, 名称: "道●天仙★君不悟★", 称号ID: 1112605 },
    { 等级: 14, 名称: "道●真仙★钗头凤★", 称号ID: 1112606 },
    { 等级: 15, 名称: "道●金仙★照红梅★", 称号ID: 1112607 },
    { 等级: 16, 名称: "道●大罗★醉花间★", 称号ID: 1112608 },
    { 等级: 17, 名称: "道●红尘★如梦令★", 称号ID: 1112609 }
);

var 粉爱心 = "#fItem/Etc/0427/04270005/Icon8/1#";
var 首页标题 = "#b┣━━━━━━━ " + 粉爱心 + " #e境界·#r突破#n " + 粉爱心 + " ━━━━━━━┫#k\r\n\r\n";
var shouscft = "#fUI/UIWindow.img/Maker/GaugeBar/bar#";
var 星星 = "#fEffect/CharacterEff/1003393/0/0#";	
var 蓝加 = "#fUI/Basic.img/BtMax/mouseOver/0#";	
var 蓝杠 = "#fUI/Basic.img/BtMin/mouseOver/0#";	
var 表情高兴 = "#fUI/GuildBBS/GuildBBS/Emoticon/Basic/2#";
var 小黄星 = "#fItem/Etc/0427/04270001/Icon9/0#";
var status = -1;
var selection;
var 临时1;
var 临时2;      
var 金币 = "#fItem/Special/0900.img/09000001/iconRaw/1#";
var 钞票 = "#fItem/Special/0900.img/09000002/iconRaw/0#";
var 经验值 = "#fUI/UIWindow.img/QuestIcon/8/0#";
var M9 = "#fEffect/CharacterEff/1112905/0/1#";//小红心			
var XMcscs1;
var xmstr1;
var i;
var 当前等级;
var 经验值库;
var xmml1;
var xmml2;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        status--;
        cm.dispose();
        return;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
        var text = "";
        text += 首页标题;

        text +=
            "  " +
            M9 +
            "星之大陆世界是一个高等世界，宗门林立，修真者需要提升自己的境界才能在这个世界生存下去。#n\r\n";
        text +=
            "  " +
            M9 +
            "而突破境界的方法只能不断的获取灵石来修炼，才能一步步达到那大道的彼岸！#n\r\n";

        text +=
            " #b#L1#" +
            小黄星 +
            "<<<境界突破>>>" +
            小黄星 +
            "#l  #L2#" +
            小黄星 +
            "<<<修炼境界>>>" +
            小黄星 +
            "#l\r\n";

        cm.sendYesNo(text);
    } else if (status == 1) {
        xmml1 = selection;
        if (selection == 1) {
            // 玩家选择了 "<<<境界突破>>>"
            if (cm.getInventory(1).getItem(1) == null) {
                cm.sendOk("如果要强化，请把物品放在背包第一格!");
                cm.dispose();
                return;
            }

            item = cm
                .getChar()
                .getInventory(Packages.client.inventory.MapleInventoryType.EQUIP)
                .getItem(1)
                .copy();
            当前装备 = item.getItemId();
            if (!getpdky(当前装备)) {
                var list = "";
                for (var i = 0; i < xmxsz.length; i++) {
                    list += "#v" + xmxsz[i].强化物品 + "##z" + xmxsz[i].强化物品 + "#";
                }
                cm.sendOk("#r请把以下装备放到装备栏第一格：#k\r\n" + list);
                cm.dispose();
                return;
            }
            当前等级 = getjdid(当前装备);

            需求装备 = xmxsz[当前等级 - 1].强化物品;
            if (当前等级 >= xmxsz.length) {
                cm.sendOk("抱歉，当前装备已经最高等级！");
                cm.dispose();
                return;
            }

            进阶装备 = xmxsz[当前等级].强化物品;
            if (需求装备 != 当前装备) {
                cm.sendOk("请把装备：#v" + 需求装备 + "##t" + 需求装备 + "#放入第一格");
                cm.dispose();
                return;
            }

            XMcscs1 = 当前等级;
            i = 当前等级;
            var text = 首页标题;

            text +=
                "#e#b#v" +
                当前装备 +
                "##t" +
                当前装备 +
                "##k #k突破为→ #r【#t" +
                进阶装备 +
                "#】#n\r\n"; //#v"+进阶装备+"#

            text +=
                "#b进阶属性增为：#k" +
                M9 +
                "四维：" +
                xmxsz[i].四维 +
                " " +
                M9 +
                "双攻：" +
                xmxsz[i].双攻 +
                "\r\n";

            text += "#d需要：[成功率：" + xmxsz[i].几率 + "]\r\n";
			text +="\r\n  " + shouscft + "\r\n";
            for (var c = 0; c < xmxsz[i].材料.length; c++) {
                text +=
                    "#k#v" +
                    xmxsz[i].材料[c][0] +
                    "##t" +
                    xmxsz[i].材料[c][0] +
                    "# x #b[#c" +
                    xmxsz[i].材料[c][0] +
                    "#/" +
                    xmxsz[i].材料[c][1] +
                    "]\r\n";
            }
            text += "" + 金币 + "冒险币 x #b" + xmxsz[i].金币 + "\r\n";

            cm.sendYesNo(text);
        } else if (selection == 2) {
            // 玩家选择了 "<<<修炼境界>>>"
            i = 0;
            var text = "";
            text += 首页标题;
            text += "  " + M9 + "修炼境界需要从天地间收集各种灵力灵石来突破！#n\r\n";
            text +=
                "  " +
                M9 +
                "突破境界为：#k" +
                M9 +
                "四维：" +
                xmxsz[i].四维 +
                " " +
                M9 +
                "双攻：" +
                xmxsz[i].双攻 +
                "\r\n";
            text +=
                "#e#b#v" + xmxsz[i].强化物品 + "##t" + xmxsz[i].强化物品 + "##k#n\r\n";

            text += "#d需要收集：[成功率：" + xmxsz[i].几率 + "]\r\n";
            for (var c = 0; c < xmxsz[i].材料.length; c++) {
                text +=
                    "#k#v" +
                    xmxsz[i].材料[c][0] +
                    "##t" +
                    xmxsz[i].材料[c][0] +
                    "# x #b[#c" +
                    xmxsz[i].材料[c][0] +
                    "#/" +
                    xmxsz[i].材料[c][1] +
                    "]\r\n";
            }
            text += "" + 金币 + "冒险币 x #b" + xmxsz[i].金币 + "\r\n";

            cm.sendYesNo(text);
        }
    } else if (status == 2) {
        xmml2 = selection;
        i = 当前等级;
        if (xmml1 == 1) {
            if (!getszchdj(xmxsz[XMcscs1].时装称号等级)) {
                cm.sendOk(
                    "请放入时装称号进装备栏第二格以验证您的实力！\r\n把称号脱下来，放到第二格就行了，这都不明白吗？\r\n#e#r并且需要：" +
                    xmxsz[XMcscs1].时装称号等级 +
                    " 阶段以上级别的\r\n#i" +
                    xmxszch[xmxsz[XMcscs1].时装称号等级 - 1].称号ID +
                    ":##z" +
                    xmxszch[xmxsz[XMcscs1].时装称号等级 - 1].称号ID +
                    "#"
                );
                cm.dispose();
                return;
            }

            if (当前等级 >= xmxsz.length) {
                cm.sendOk("抱歉，当前装备已经最高等级！");
                cm.dispose();
                return;
            }
            for (var c = 0; c < xmxsz[i].材料.length; c++) {
                if (!cm.haveItem(xmxsz[i].材料[c][0], xmxsz[i].材料[c][1])) {
                    cm.sendOk(
                        "材料不足：#v" +
                        xmxsz[i].材料[c][0] +
                        "##t" +
                        xmxsz[i].材料[c][0] +
                        "#"
                    );
                    cm.dispose();
                    return;
                }
            }

            if (cm.getMeso() < xmxsz[XMcscs1].金币) {
                cm.sendOk("金币不足无法强化！需要：" + xmxsz[XMcscs1].金币);
                cm.dispose();
                return;
            }

            for (var c = 0; c < xmxsz[i].材料.length; c++) {
                cm.gainItem(xmxsz[i].材料[c][0], -xmxsz[i].材料[c][1]);
            }
            cm.gainMeso(-xmxsz[XMcscs1].金币);

            if (Math.floor(Math.random() * 100) <= xmxsz[XMcscs1].几率) {
                item = cm.getEquip(xmxsz[XMcscs1].强化物品).copy();
                item.setFlag(1);
                item.setStr(xmxsz[XMcscs1].四维);
                item.setDex(xmxsz[XMcscs1].四维);
                item.setInt(xmxsz[XMcscs1].四维);
                item.setLuk(xmxsz[XMcscs1].四维);
                item.setWatk(xmxsz[XMcscs1].双攻);
                item.setMatk(xmxsz[XMcscs1].双攻);

                Packages.server.MapleInventoryManipulator.removeFromSlot(
                    cm.getC(),
                    Packages.client.inventory.MapleInventoryType.EQUIP,
                    1,
                    1,
                    false
                );
                Packages.server.MapleInventoryManipulator.addFromDrop(
                    cm.getC(),
                    item,
                    false
                );
                cm.sendOk("#e#b恭喜你！顺利进阶一次！\r\n");
                cm.道具喇叭(
                    "【" + xiaomi.主题变量 + "】",
                    "恭喜玩家 " +
                    cm.getPlayer().getName() +
                    " 成功进阶一次！属性得到了提升！",
                    1,
                    item.getPosition()
                );
                cm.dispose();
            } else {
                cm.sendOk(
                    "本次升级过程中遇到遇到了黑魔法副作用\r\n#e#r抱歉！升级失败！装备安全！"
                );
                cm.dispose();
            }
        } else if (xmml1 == 2) {
            // 第二排合成
            i = 0;
            XMcscs1 = 0;
            if (cm.getPlayer().getxmwnjlc(xiaomi.合成常量) >= 1) {
                cm.sendOk("一人一生只能创造一个属于自己的" + xiaomi.主题变量 + "哦！");
                cm.dispose();
                return;
            }

            if (!getszchdj(xmxsz[0].时装称号等级)) {
                cm.sendOk(
                    "请放入时装称号进装备栏第二格以验证您的实力！\r\n把称号脱下来，放到第二格就行了，这都不明白吗？\r\n#e#r并且需要：" +
                    xmxsz[0].时装称号等级 +
                    " 阶段以上级别的\r\n#i" +
                    xmxszch[xmxsz[0].时装称号等级 - 1].称号ID +
                    ":##z" +
                    xmxszch[xmxsz[0].时装称号等级 - 1].称号ID +
                    "#"
                );
                cm.dispose();
                return;
            }

            if (getpdch()) {
                cm.sendOk(
                    "你已经拥有一个 #v" +
                    xmxsz[0].强化物品 +
                    "##t" +
                    xmxsz[0].强化物品 +
                    "# 无需重新制作"
                );
                cm.dispose();
                return;
            }

            if (cm.getInventory(1).isFull(1)) {
                cm.sendOk("背包空间不足，无法合成！");
                cm.dispose();
                return;
            }

            for (var c = 0; c < xmxsz[i].材料.length; c++) {
                if (!cm.haveItem(xmxsz[i].材料[c][0], xmxsz[i].材料[c][1])) {
                    cm.sendOk(
                        "材料不足：#v" +
                        xmxsz[i].材料[c][0] +
                        "##t" +
                        xmxsz[i].材料[c][0] +
                        "#"
                    );
                    cm.dispose();
                    return;
                }
            }

            if (cm.getMeso() < xmxsz[XMcscs1].金币) {
                cm.sendOk("金币不足无法强化！需要：" + xmxsz[XMcscs1].金币);
                cm.dispose();
                return;
            }

            for (var c = 0; c < xmxsz[i].材料.length; c++) {
                cm.gainItem(xmxsz[i].材料[c][0], -xmxsz[i].材料[c][1]);
            }
            cm.gainMeso(-xmxsz[XMcscs1].金币);

            if (Math.floor(Math.random() * 100) <= xmxsz[XMcscs1].几率) {
                cm.getPlayer().setxmwnjlc(xiaomi.合成常量, 1);
                item = cm.getEquip(xmxsz[XMcscs1].强化物品).copy();
                item.setFlag(1);
                item.setStr(xmxsz[XMcscs1].四维);
                item.setDex(xmxsz[XMcscs1].四维);
                item.setInt(xmxsz[XMcscs1].四维);
                item.setLuk(xmxsz[XMcscs1].四维);
                item.setWatk(xmxsz[XMcscs1].双攻);
                item.setMatk(xmxsz[XMcscs1].双攻);

                Packages.server.MapleInventoryManipulator.addFromDrop(
                    cm.getC(),
                    item,
                    false
                );
                cm.sendOk("#e#b恭喜你成功合成了\r\n");

                cm.道具喇叭(
                    "【" + xiaomi.主题变量 + "】",
                    "恭喜玩家 " +
                    cm.getPlayer().getName() +
                    " 历经千辛万苦终于成功锻造！",
                    15
                );

                cm.dispose();
            } else {
                cm.sendOk("修仙也会出现失败哦！再接再厉吧！");
                cm.dispose();
            }
        }
    }
}

function xiaomisq() {
    if (cm.getChannelServer().getServerName() == "079冒险岛") {
        return false;
    }
    return true;
}

function getjdid(id) {
    var fhz = 0;
    for (var c = 0; c < xmxsz.length; c++) {
        if (id == xmxsz[c].强化物品) {
            fhz = xmxsz[c].等级;
            break;
        }
    }
    return fhz;
}

function getpdky(id) {
    var fhz = false;
    for (var c = 0; c < xmxsz.length; c++) {
        if (id == xmxsz[c].强化物品) {
            fhz = true;
            break;
        }
    }
    return fhz;
}

function getPlayes() {
    em = cm.getEventManager("Visitormi");
    if (em == null || !em.getProperty("m")) {
        cm.dispose();
        return true;
    }
    return false;
}

function getpdch() {
    var fhz = false;
    if (
        cm.getPlayer().hasEquipped(xmxsz[0].强化物品) ||
        cm.haveItem(xmxsz[0].强化物品, 1)
    ) {
        fhz = true;
    }
    return fhz;
}

function getszchdj(level) {
    var fhz = false;
    var itemch = cm.getInventory(1).getItem(2);
    if (itemch == null) {
        return false;
    }
    var itemid = itemch.getItemId();
    for (var c = 0; c < xmxszch.length; c++) {
        if (xmxszch[c].称号ID == itemid) {
            if (xmxszch[c].等级 >= level) {
                fhz = true;
                break;
            }
        }
    }
    return fhz;
}