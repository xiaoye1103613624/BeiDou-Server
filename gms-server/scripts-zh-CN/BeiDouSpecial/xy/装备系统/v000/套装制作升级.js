//装备制作升级菜单
var acc = "#fEffect/CharacterEff/1112903/0/0#";//红桃心

var 皇冠白 = "#fUI/GuildMark/Mark/Etc/00009004/16#";
var 红爱心 = "#fEffect/CharacterEff/1112905/0/1#";
var 金币图标 = "#fUI/UIWindow.img/QuestIcon/7/0#";
var 小金币 = "#fUI/UIWindow.img/Item/BtCoin/normal/0#";
var 点券图标 = "#fUI/CashShop/CashItem/0#";

var 小烟花 = "#fMap/MapHelper/weather/squib/squib4/1#";
var 红枫叶 = "#fMap/MapHelper/weather/maple/1#";
var OldTitle = "\t\t\t\t\t#e欢迎来到#r装备制作升级菜单#k#n\t\t\t\t\r\n";
var status = -1;
var i = 0;
var prePath = 'xy/装备系统/v000/'

function start() {
    action(1, 0, 0)
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else if (mode === -1) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    if (status === 0) {
        let text = OldTitle;
        //text += "#b  套制作升级#k\r\n\r\n";

        text += "#L0# " + 小烟花 + "45级\t\t\t\t宝石装备制作#l\r\n\r\n";
        text += "#L1# " + 小烟花 + "45升级79 \t\t紫金装备制作#l\r\n";
        text += "#L2# " + 小烟花 + "79升级105 \t革命装备制作#l\r\n";
        text += "#L3# " + 小烟花 + "105升级140 \t女皇巨匠装备制作#l\r\n";
        text += "#L4# " + 小烟花 + "140升级160 \t埃苏装备制作#l\r\n";
        text += "#L5# " + 小烟花 + "160升级200 \t神秘之影装备制作#l\r\n\r\n";
        text += "#L6# " + 小烟花 + "戒指制作#l\r\n";
        text += "#L7# " + 小烟花 + "盾牌制作#l\r\n";
        text += "#L8# " + 小烟花 + "135布莱克缤武器装备兑换,不可升级#l\r\n";
        text += "#L9# " + 小烟花 + "155法弗纳武器兑换,不可升级#l\r\n";
        text += "#L10# " + 小烟花 + "200创世武器兑换,(未开放)#l\r\n";
        //text += "#L10# "+小烟花 +"血盟戒指升级#l\r\n";
        // =================================================================

        cm.sendSimple(text);
    } else if (status === 1) {
        doSelect(selection);
    } else {
        cm.dispose();
    }
}

function doSelect(selection) {
    switch (selection) {
        // ====================== 对应上面的L数字，修改跳转目标 ======================
        case 0:
            openNpc(prePath + "45级宝石套装制作");       //
            break;
        case 1:
            openNpc(prePath + "79级紫金装备制作");
            break;
        case 2:
            openNpc(prePath + "100级革命装备制作");
            break;
        case 3:
            openNpc(prePath + "140女皇装备");
            break;
        case 4:
            openNpc(prePath + "160埃苏装备");
            break;
        case 5:
            openNpc(prePath + "200神秘之影装备");
            break;
        case 6:
            openNpc(prePath + "戒指制作");
            break;
        case 7:
            openNpc(prePath + "盾牌制作");
            break;
        case 8:
            openNpc(prePath + "135布莱克装备");
            break;
        case 9:
            openNpc(prePath + "155法弗纳装备");
            break;
        // case 10:
        // openNpc("血盟戒指");
        // break;
        // ========================================================================

        default:
            cm.sendOk("功能未开放！");
            cm.dispose();
    }
}

//固定跳转函数
function openNpc(scriptName) {
    cm.dispose();
    cm.openNpc(9900001, scriptName);
}