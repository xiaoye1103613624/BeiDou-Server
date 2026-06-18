/*
 * 野外 Boss 手动召唤（GM工具）
 * 功能：展示Boss列表（含图标），选择后直接在当前地图玩家位置召唤
 */
// Boss图标定义
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

// Boss数据：[图标, 名称, 怪物ID]
var bossList = [
    [蜗牛王, "[蜗 牛 王]", 2220000],
    [蘑菇王, "[蘑 菇 王]", 6130101],
    [蓝蘑菇王, "[蓝蘑菇王]", 9400205],
    [僵蘑菇王, "[僵蘑菇王]", 6300005],
    [树妖王, "[树 妖 王]", 3220000],
    [仙人掌, "[仙 人 掌]", 3220001],
    [贝壳精, "[贝 壳 精]", 4220000],
    [僵尸猴, "[僵 尸 猴]", 5220002],
    [贝壳王, "[贝 壳 王]", 5220001],
    [猫头鹰, "[猫 头 鹰]", 5220003],
    [流浪熊, "[流 浪 熊]", 7220000],
    [艾力杰, "[艾 力 杰]", 8220000],
    [吉米啦, "[吉 米 啦]", 8220002],
    [大妙仙, "[大 妙 仙]", 7220002],
    [九尾狐, "[九 尾 狐]", 7220001],
    [喷火龙, "[喷 火 龙]", 8180000],
    [格瑞芬, "[格 瑞 芬]", 8180001],
    [蝙蝠怪, "[蝙 蝠 怪]", 8130100],
    [大多尔, "[大 多 尔]", 6220000],
    [海兽, "[ 海  兽 ]", 8220003],
    [多多, "[ 多  多 ]", 8220004],
    [独角兽, "[独 角 兽]", 8220005],
    [雷卡, "[ 雷  卡 ]", 8220006],
    [鱼王, "[ 鱼  王 ]", 8510000],
    [树精, "[ 树  精 ]", 9420521],
    [妖僧, "[ 妖  僧 ]", 9600025],
    [闹钟, "[ 闹  钟 ]", 8500001],
    [熊狮, "[ 熊  狮 ]", 9420542],
    [扎昆, "[ 扎  昆 ]", 8800001],
    [黑龙, "[ 黑  龙 ]", 8810018],
    [品克宾, "[品 克 宾]", 8820001]
];

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) status++;
    else status--;

    if (status == 0) {
        // 展示Boss选择列表（带图标和名称）
        var text = "\t\t#r#e< 野外BOSS召唤 >#k#n\r\n\r\n";
        text += "#b请选择要在当前地图召唤的野外BOSS：#k\r\n\r\n";
        // 横向每行展示4个Boss
        for (var i = 0; i < bossList.length; i++) {
            text += "#L" + i + "#" + bossList[i][0] + " " + bossList[i][1] + "#l";
            if ((i + 1) % 3 == 0) {
                text += "\r\n"; // 每4个换行
            } else {
                text += "\t";   // 同列之间用tab分隔
            }
        }
        // 最后一行不足4个时补一个换行
        if (bossList.length % 3 != 0) {
            text += "\r\n";
        }
        cm.sendSimple(text);
    } else if (status == 1) {
        // 选择后直接在当前地图玩家位置召唤
        var boss = bossList[selection];
        var mobId = boss[2];
        var pos = cm.getPlayer().getPosition();
        // 在玩家位置生成怪物
        cm.spawnMonster(mobId, pos.x, pos.y);
        // 地图公告
        cm.mapMessage(6, "[野外 Boss] 玩家 " + cm.getPlayer().getName() + " 召唤了 " + boss[1]);
        cm.dispose();
    }
}