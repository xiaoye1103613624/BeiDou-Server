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
var 贝伦 = "#fUI/UIWindow.img/MobGage/Mob/8930000#";
var 混沌血腥女王 = "#fUI/UIWindow.img/MobGage/Mob/8920000#";
var 觉醒希拉的幻影 = "#fUI/UIWindow.img/MobGage/Mob/8880400#";
var 威尔 = "#fUI/UIWindow.img/MobGage/Mob/8880301#";
var 威尔 = "#fUI/UIWindow.img/MobGage/Mob/8880302#";

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
    // 8800001=扎昆本体（带血条）；祭坛正式召唤是假身8800000+手臂8800003~8800010
    [扎昆, "[ 扎  昆 ]", 8800001],
    [黑龙, "[ 黑  龙 ]", 8810018],
    [品克宾, "[品 克 宾]", 8820001],
    [贝伦, "[ 贝 伦 ]", 8930000],
    [混沌血腥女王, "[血腥女王]", 8920000],
    [觉醒希拉的幻影, "[希拉幻影]", 8880400],
    [威尔, "[ 威 尔 ]", 8880301],
    [威尔, "[狂暴威尔]", 8880302],
];

var searchMode = "";         // 搜索模式："id"=按ID搜索, "name"=按名称搜索（区分sendGetNumber/sendGetText的回调）
var searchResults = null;    // 存储名称搜索结果列表（ArrayList<Pair<Integer,String>>）
var status = 0;

function start() {
    status = -1;
    searchMode = "";
    searchResults = null;
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
        // ===================== 主菜单：Boss列表 + 额外功能选项 =====================
        searchMode = "";
        searchResults = null;
        var text = "\t\t#r#e< 野外BOSS召唤 >#k#n\r\n\r\n";
        text += "#b请选择要在当前地图召唤的野外BOSS：#k\r\n\r\n";
        // 横向每行展示3个Boss
        for (var i = 0; i < bossList.length; i++) {
            text += "#L" + i + "#" + bossList[i][0] + " " + bossList[i][1] + "#l";
            if ((i + 1) % 3 == 0) {
                text += "\r\n";
            } else {
                text += "\t";
            }
        }
        if (bossList.length % 3 != 0) {
            text += "\r\n";
        }
        // 额外功能选项
        text += "\r\n#L" + bossList.length + "##r#e[输入ID直接召唤]#k#n#l\r\n";
        text += "#L" + (bossList.length + 1) + "##r#e[按名称搜索召唤]#k#n#l\r\n";
        cm.sendSimple(text);

    } else if (status == 1) {
        // ===================== 处理主菜单选择 =====================
        if (selection == bossList.length) {
            // 输入ID召唤
            searchMode = "id";
            cm.sendGetNumber("请输入要召唤的怪物ID：", 1, 1, 999999999);
        } else if (selection == bossList.length + 1) {
            // 按名称搜索召唤
            searchMode = "name";
            cm.sendGetText("请输入怪物名称关键词进行搜索：\r\n#b（支持模糊搜索，输入部分名称即可）#k");
        } else {
            // 选择预设Boss，直接在当前地图玩家位置召唤
            var boss = bossList[selection];
            var mobId = boss[2];
            var pos = cm.getPlayer().getPosition();
            cm.spawnMonster(mobId, pos.x, pos.y);
            cm.mapMessage(6, "[野外 Boss] 玩家 " + cm.getPlayer().getName() + " 召唤了 " + boss[1]);
            cm.dispose();
        }

    } else if (status == 2) {
        // ===================== 处理搜索输入结果（ID或名称文本） =====================
        if (searchMode == "id") {
            // ---------- ID输入结果 ----------
            var inputBossId = selection; // sendGetNumber的回调，selection即为用户输入的数字
            var pos = cm.getPlayer().getPosition();
            var monster = cm.getMonsterLifeFactory(inputBossId);
            if (monster == null) {
                // 不存在或WZ损坏/技能未支持，回到ID输入页重新输入
                cm.sendGetNumber("#r怪物ID [" + inputBossId + "] 无法召唤（不存在或数据异常），请重新输入！#k\r\n\r\n请输入要召唤的怪物ID：", 1, 1, 999999999);
                status = 1; // 回退，下次递增仍到status 2
            } else {
                // 存在则召唤
                var mobName = cm.getMobNameFromId(inputBossId);
                cm.spawnMonster(inputBossId, pos.x, pos.y);
                cm.mapMessage(6, "[野外 Boss] 玩家 " + cm.getPlayer().getName() + " 召唤了 [" + mobName + "] ID:" + inputBossId);
                cm.dispose();
            }
        } else if (searchMode == "name") {
            // ---------- 名称搜索文本输入结果 ----------
            var searchText = cm.getText().trim(); // sendGetText的回调，通过getText()获取
            if (searchText == "") {
                // 空输入，回到名称搜索页（错误信息嵌入prompt，避免sendOk+sendGetText冲突导致客户端异常）
                cm.sendGetText("#r搜索关键词不能为空，请重新输入！#k\r\n\r\n请输入怪物名称关键词进行搜索：\r\n#b（支持模糊搜索，输入部分名称即可）#k");
                status = 1; // 回退
            } else {
                var results = cm.getMobsIDsFromName(searchText);
                if (results.isEmpty()) {
                    // 未找到结果，回到名称搜索页（错误信息嵌入prompt）
                    cm.sendGetText("#r未找到包含 [" + searchText + "] 的怪物，请重新输入！#k\r\n\r\n请输入怪物名称关键词进行搜索：\r\n#b（支持模糊搜索，输入部分名称即可）#k");
                    status = 1; // 回退
                } else {
                    // 有结果，展示选择列表
                    searchResults = results;
                    var resultText = "\t\t#r#e< 名称搜索结果 >#k#n\r\n\r\n";
                    resultText += "#b搜索关键词：[" + searchText + "]，共找到 " + results.size() + " 个怪物：#k\r\n\r\n";
                    for (var i = 0; i < results.size(); i++) {
                        var pair = results.get(i);
                        resultText += "#L" + i + "##b" + pair.getRight() + "#k（ID：" + pair.getLeft() + "）\r\n#l";
                    }
                    resultText += "\r\n#L" + results.size() + "##r[返回重新搜索]#k#l\r\n";
                    cm.sendSimple(resultText);
                }
            }
        }

    } else if (status == 3) {
        // ===================== 处理名称搜索结果选择 =====================
        if (selection == searchResults.size()) {
            // 选择了"返回重新搜索"，回到名称搜索输入页
            searchMode = "name";
            cm.sendGetText("请输入怪物名称关键词进行搜索：\r\n#b（支持模糊搜索，输入部分名称即可）#k");
            status = 1; // 回退，下次递增到status 2处理文本输入
        } else {
            // 选择了具体怪物，召唤
            var selectedPair = searchResults.get(selection);
            var mobId = selectedPair.getLeft();
            var mobName = selectedPair.getRight();
            var pos = cm.getPlayer().getPosition();
            cm.spawnMonster(mobId, pos.x, pos.y);
            cm.mapMessage(6, "[野外 Boss] 玩家 " + cm.getPlayer().getName() + " 召唤了 [" + mobName + "] ID:" + mobId);
            cm.dispose();
        }
    }
}