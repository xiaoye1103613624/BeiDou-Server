/*
 * 世界 Boss 手动召唤 NPC
 */
// 定义美化变量
var 传送中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/6#";
var 蜗牛王 = "#fUI/UIWindow.img/MobGage/Mob/2220000#";
var 蘑菇王 = "#fUI/UIWindow.img/MobGage/Mob/6130101#";
var 蓝蘑菇王 = "#fUI/UIWindow.img/MobGage/Mob/9400205#";
var 僵蘑菇王 = "#fUI/UIWindow.img/MobGage/Mob/6300005#";
var 树妖王 = "#fUI/UIWindow.img/MobGage/Mob/3220000#";
var 仙人掌 = "#fUI/UIWindow.img/MobGage/Mob/3220001#";
var 贝壳精 = "#fUI/UIWindow.img/MobGage/Mob/4220000#";
var 僵尸猴 = "#fUI/UIWindow.img/MobGage/Mob/5220002#";
var 猫头鹰 = "#fUI/UIWindow.img/MobGage/Mob/5220003#";
var 贝壳王 = "#fUI/UIWindow.img/MobGage/Mob/5220000#";
var 流浪熊 = "#fUI/UIWindow.img/MobGage/Mob/7220000#";
var 艾力杰 = "#fUI/UIWindow.img/MobGage/Mob/8220000#";
var 吉米啦 = "#fUI/UIWindow.img/MobGage/Mob/8220002#";
var 大妙仙 = "#fUI/UIWindow.img/MobGage/Mob/7220002#";
var 九尾狐 = "#fUI/UIWindow.img/MobGage/Mob/7220001#";
var 喷火龙 = "#fUI/UIWindow.img/MobGage/Mob/8180000#";
var 格瑞芬 = "#fUI/UIWindow.img/MobGage/Mob/8180001#";
var 蝙蝠怪 = "#fUI/UIWindow.img/MobGage/Mob/8130100#";
var 大多尔 = "#fUI/UIWindow.img/MobGage/Mob/6220000#";
var 海兽 = "#fUI/UIWindow.img/MobGage/Mob/8220003#";
var 多多 = "#fUI/UIWindow.img/MobGage/Mob/8220004#";
var 独角兽 = "#fUI/UIWindow.img/MobGage/Mob/8220005#";
var 雷卡 = "#fUI/UIWindow.img/MobGage/Mob/8220006#";
var 鱼王 = "#fUI/UIWindow.img/MobGage/Mob/8510000#";
var 树精 = "#fUI/UIWindow.img/MobGage/Mob/9420521#";
var 妖僧 = "#fUI/UIWindow.img/MobGage/Mob/9600025#";
var 品克宾 = "#fUI/UIWindow.img/MobGage/Mob/8820001#";
var 黑龙 = "#fUI/UIWindow.img/MobGage/Mob/8810018#";
var 闹钟 = "#fUI/UIWindow.img/MobGage/Mob/8500001#";
var 熊狮 = "#fUI/UIWindow.img/MobGage/Mob/9420542#";
var 扎昆 = "#fUI/UIWindow.img/MobGage/Mob/8800001#";

// 定义世界BOSS地图数组
// 新增第 6、7 列：spawnX、spawnY
var bossmaps = [
    [蜗牛王, "[蜗 牛 王]", 104000400, 10, 2220000, 266, -355],
    [蘑菇王, "[蘑 菇 王]", 100000005, 10, 6130101,  -754, 215],
    [蓝蘑菇王, "[蓝蘑菇王]", 800010100, 10, 9400205, 158, 95],
    [僵蘑菇王, "[僵蘑菇王]", 105070002, 10, 6300005, 456, 77],
    [树妖王, "[树 妖 王]", 101030404, 10, 3220000,  660, 1573],
    [仙人掌, "[仙 人 掌]", 260010201, 10, 3220001,  177, 275],
    [贝壳精, "[贝 壳 精]", 230020100, 10, 4220000, -461, 640],
    [僵尸猴, "[僵 尸 猴]", 100040106, 10, 5220002,  456, 209],
    [贝壳王, "[贝 壳 王]", 110040000, 10, 5220001, -221, -113],
    [猫头鹰, "[猫 头 鹰]", 220050100, 15, 5220003,  278, 792],
    [流浪熊, "[流 浪 熊]", 250010304, 15, 7220000, -361, 393],
    [艾力杰, "[艾 力 杰]", 200010300, 15, 8220000,  206, 83],
    [吉米啦, "[吉 米 啦]", 261030000, 15, 8220002, -615, -405],
    [大妙仙, "[大 妙 仙]", 250010503, 15, 7220002,  135, 543],
    [九尾狐, "[九 尾 狐]", 222010310, 15, 7220001, -250, 35],
    [喷火龙, "[喷 火 龙]", 240020401, 20, 8180000, -313, 452],
    [格瑞芬, "[格 瑞 芬]", 240020101, 20, 8180001,  239, 452],
    [蝙蝠怪, "[蝙 蝠 怪]", 105090900, 20, 8130100,  127, 130],
    [大多尔, "[大 多 尔]", 107000300, 20, 6220000,  825, -119],
    [海兽, "[ 海  兽 ]", 240040401, 20, 8220003, 15, 1149],
    [多多, "[ 多  多 ]", 270010500, 20, 8220004, 43, -913],
    [独角兽, "[独 角 兽]", 270020500, 20, 8220005, -242, -915],
    [雷卡, "[ 雷  卡 ]", 270030500, 20, 8220006, 848, -914],
    [鱼王, "[ 鱼  王 ]", 230040420, 30, 8510000, 597, 168]
/*    [树精, "[ 树  精 ]", 541020700, 30000, 9420521,    0, 35],
    [妖僧, "[ 妖  僧 ]", 702070400, 30000, 9600025,  100, 35],
    [闹钟, "[ 闹  钟 ]", 220080000, 30000, 8500001,  -50, 35],
    [熊狮, "[ 熊  狮 ]", 551030100, 30000, 9420542,  150, 35],
    [扎昆, "[ 扎  昆 ]", 211042300, 30000, 8800001, -100, 35],
    [黑龙, "[ 黑  龙 ]", 240050400, 30000, 8810018,  200, 35],
    [品克宾, "[品 克 宾]", 270050000, 30000, 8820001,    0, 35]*/
];

var status = 0;
var cost;          // 召唤费用
var mobId;         // 目标怪物 ID
var mapId;         // 当前地图 ID
var spawnX, spawnY;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0 && status > 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) status++;
    else status--;

    var currentMapId = cm.getMapId();
    var info = null;

    // 根据当前地图找出对应 Boss 信息
    for (var i = 0; i < bossmaps.length; i++) {
        if (bossmaps[i][2] == currentMapId) {
            info = bossmaps[i];
            break;
        }
    }

    if (info == null) {
        cm.sendOk("这张地图没有可召唤的 Boss。");
        cm.dispose();
        return;
    }

    mobId   = info[4];
    cost    = info[3];
    spawnX  = info[5];
    spawnY  = info[6];

    if (status == 0) {
        if (isMonsterInMap(mobId, currentMapId)) {
            cm.sendOk("当前地图中已经存在 #b" + info[1] + "#k，无需再次召唤。");
            cm.dispose();
            return;
        }
        cm.sendYesNo("是否花费 #r" + cost + "#k 元宝召唤 #b" + info[1] + "#k？");
    } else if (status == 1) {
        if (cm.getChar().getmoneyb() < cost) {
            cm.sendOk("元宝不足！需要 #r" + cost + "#k 元宝。");
            cm.dispose();
            return;
        }
        cm.setmoneyb(-cost);
        var map = cm.getMap(currentMapId);
    //    map.spawnMonsterOnGroundBelow(mobId, spawnX, spawnY);
		cm.召唤怪物(mobId, 1000000, 1000000, 1, currentMapId, spawnX, spawnY);
		cm.喇叭(1, "[野外 Boss] 玩家 " + cm.getPlayer().getName() + " 在 " + info[1] + " 地图召唤了 野外Boss");
        cm.dispose();
    }
}

// 与传送脚本共用，直接拷过来
function isMonsterInMap(mobId, mapId) {
    var map = cm.getMap(mapId);
    if (!map) return false;
    var monsters = map.getAllMonstersThreadsafe();
    if (!monsters) return false;
    for (var i = 0; i < monsters.size(); i++) {
        if (monsters.get(i).getId() == mobId) return true;
    }
    return false;
}