/*
 * ==================
 * 脚本类型: 玩具收集
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 收集各类玩具装备，包括耳环、盾牌、滑板、雨伞、手套、披风、抽奖装备
 *   2. 拥有装备可登记到收集册（消耗1件物品）
 *   3. 完成单个物品登记 = 2AP
 *   4. 完成整个类别收集 = 5AP
 * ==================
 */

var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');

// ===== 类别定义 =====
var categories = [
    {
        name: "耳环收集",
        icon: 1032013,
        items: [
            1032012, 1032013, 1032014, 1032017, 1032018, 1032019,
            1032023, 1032024, 1032025, 1032030, 1032032, 1032033,
            1032034, 1032038, 1032040, 1032044, 1032045, 1032046,
            1032048, 1032050, 1032051, 1032052, 1032053, 1032054,
            1032059, 1032063, 1032073, 1032074, 1032075
        ]
    },
    {
        name: "盾牌收集",
        icon: 1092008,
        items: [
            1092003, 1092004, 1092008, 1092018, 1092019, 1092020,
            1092022, 1092029, 1092030, 1092031, 1092032, 1092033,
            1092034, 1092035, 1092039, 1092040, 1092044, 1092045,
            1092046, 1092047, 1092056, 1092061
        ]
    },
    {
        name: "滑板收集",
        icon: 1442012,
        items: [
            1432015, 1432016, 1432017, 1432018,
            1442011, 1442012, 1442013, 1442014, 1442015, 1442016,
            1442017, 1442026, 1442027, 1442028, 1442029, 1442030,
            1442046, 1442054, 1442055, 1442056, 1442057, 1442065,
            1442066
        ]
    },
    {
        name: "雨伞收集",
        icon: 1302017,
        items: [
            1302016, 1302017, 1302025, 1302026, 1302027, 1302028,
            1302029, 1302058, 1702103
        ]
    },
    {
        name: "手套收集",
        icon: 1082156,
        items: [
            1082098, 1082099, 1082110, 1082156, 1082157, 1082158,
            1082159, 1082160, 1082162, 1082168, 1082169, 1082170,
            1082171, 1082172, 1082173, 1082228, 1082229, 1082230,
            1082231, 1082232, 1082233, 1082247, 1082249, 1082256,
            1082257, 1082258, 1082259, 1082260
        ]
    },
    {
        name: "披风收集",
        icon: 1102092,
        items: [
            1102005, 1102006, 1102007, 1102008, 1102009, 1102010,
            1102036, 1102037, 1102038, 1102039, 1102044, 1102045,
            1102049, 1102050, 1102051, 1102052, 1102055, 1102056,
            1102058, 1102059, 1102061, 1102063, 1102066, 1102067,
            1102068, 1102069, 1102072, 1102073, 1102091, 1102092,
            1102093, 1102094, 1102098, 1102107, 1102108, 1102110,
            1102111, 1102112, 1102137, 1102138, 1102141, 1102143,
            1102147, 1102150, 1102151, 1102152, 1102154, 1102155,
            1102157, 1102158, 1102159, 1102160, 1102164, 1102184,
            1102185, 1102186, 1102205, 1102210, 1102211, 1102212,
            1102215, 1102216, 1102218, 1102223, 1102224, 1102229,
            1102236
        ]
    },
    // 抽奖收集 - 按子类别分组
    {
        name: "抽奖收集",
        icon: 5220000,
        subCategories: [
            {
                name: "帽子",
                items: [
                    1002004, 1002006, 1002012, 1002013, 1002020, 1002021,
                    1002022, 1002023, 1002026, 1002028, 1002030, 1002033,
                    1002034, 1002035, 1002036, 1002037, 1002038, 1002041,
                    1002042, 1002048, 1002050, 1002056, 1002058, 1002059,
                    1002060, 1002063, 1002064, 1002065, 1002072, 1002073,
                    1002074, 1002083, 1002085, 1002088, 1002096, 1002097,
                    1002118, 1002119, 1002121, 1002128, 1002129, 1002137,
                    1002142, 1002144, 1002148, 1002152, 1002153, 1002155,
                    1002159, 1002160, 1002161, 1002162, 1002163, 1002164,
                    1002165, 1002166, 1002168, 1002169, 1002170, 1002171,
                    1002172, 1002173, 1002174, 1002175, 1002176, 1002179,
                    1002180, 1002182, 1002183, 1002209, 1002213, 1002215,
                    1002217, 1002218, 1002244, 1002245, 1002246, 1002247,
                    1002249, 1002252, 1002253, 1002254, 1002274, 1002340,
                    1002391, 1002392, 1002393, 1002394, 1002395, 1002418,
                    1002419, 1002585, 1002586, 1002587, 1002610, 1002613,
                    1002616, 1002619, 1002622, 1002625, 1002628, 1002631,
                    1002634, 1002637, 1002640, 1002643, 1002646
                ]
            },
            {
                name: "套服/上衣/裤子",
                items: [
                    1040003, 1040018, 1040019, 1040022, 1040023, 1040025,
                    1040029, 1040030, 1040032, 1040057, 1040059, 1040060,
                    1040067, 1040068, 1040069, 1040071, 1040080, 1040084,
                    1040086, 1040095, 1040096, 1040097, 1040100, 1041003,
                    1041004, 1041008, 1041020, 1041023, 1041024, 1041027,
                    1041029, 1041030, 1041031, 1041040, 1041041, 1041044,
                    1041048, 1041051, 1041053, 1041054, 1041062, 1041067,
                    1050002, 1050005, 1050011, 1050018, 1050025, 1050035,
                    1050039, 1050047, 1050053, 1050055, 1050056, 1050067,
                    1050068, 1050069, 1050072, 1050073, 1050074, 1051017,
                    1051024, 1051025, 1051027, 1051030, 1051031, 1051032,
                    1051033, 1051034, 1051039, 1051045, 1051047, 1051052,
                    1051053, 1051054, 1051055, 1052095, 1052098, 1052101,
                    1052104, 1052107, 1052110, 1052113, 1052116, 1052119,
                    1052122, 1052125, 1052128, 1052131,
                    1060008, 1060009, 1060011, 1060014, 1060018, 1060025,
                    1060028, 1060031, 1060046, 1060052, 1060056, 1060057,
                    1060063, 1060071, 1060074, 1060084, 1061006, 1061019,
                    1061027, 1061032, 1061034, 1061036, 1061054, 1061057,
                    1061063, 1061077, 1061088, 1062000, 1062001, 1062002
                ]
            },
            {
                name: "鞋子",
                items: [
                    1072262, 1072263, 1072264, 1072285, 1072288, 1072291,
                    1072294, 1072297, 1072300, 1072303, 1072306, 1072309,
                    1072312, 1072315, 1072318, 1072338
                ]
            },
            {
                name: "手套",
                items: [
                    1082074, 1082086, 1082087, 1082088, 1082117, 1082145,
                    1082146, 1082147, 1082148, 1082149, 1082150, 1082175,
                    1082178, 1082179, 1082180, 1082183, 1082186, 1082189,
                    1082192, 1082195, 1082198, 1082201, 1082204, 1082207,
                    1082210, 1082213
                ]
            },
            {
                name: "披风",
                items: [
                    1102000, 1102003, 1102011, 1102012, 1102013, 1102014,
                    1102015, 1102016, 1102017, 1102018, 1102028, 1102040,
                    1102041, 1102042, 1102043, 1102084, 1102085, 1102086
                ]
            },
            {
                name: "耳环/饰品",
                items: [
                    1032000, 1032001, 1032002, 1032003, 1032004, 1032005,
                    1032006, 1032007, 1032008, 1032009, 1032010, 1032011,
                    1032012, 1032013, 1032014, 1032015, 1032016, 1032017,
                    1032018, 1032019, 1032020, 1032021, 1032022, 1032023,
                    1032027, 1032028, 1032032,
                    1012056, 1022047
                ]
            },
            {
                name: "武器",
                items: [
                    // 单手剑
                    1302000, 1302002, 1302003, 1302004, 1302005, 1302007,
                    1302008, 1302009, 1302010, 1302012, 1302013, 1302014,
                    1302016, 1302017, 1302018, 1302019, 1302021, 1302022,
                    1302024, 1302025, 1302026, 1302027, 1302028, 1302029,
                    1302049, 1302056,
                    // 单手斧
                    1312000, 1312002, 1312004, 1312005, 1312006, 1312007,
                    1312008, 1312011, 1312012, 1312013, 1312014,
                    // 单手钝器
                    1322000, 1322002, 1322003, 1322007, 1322009, 1322010,
                    1322011, 1322012, 1322014, 1322015, 1322017, 1322019,
                    1322020, 1322021, 1322022, 1322023, 1322024, 1322025,
                    1322026, 1322027, 1322028,
                    // 短剑
                    1332002, 1332003, 1332004, 1332006, 1332009, 1332010,
                    1332011, 1332012, 1332013, 1332015, 1332016, 1332017,
                    1332018, 1332019, 1332020, 1332021, 1332022, 1332023,
                    1332024, 1332026, 1332027, 1332029, 1332030, 1332031,
                    1332032, 1332054,
                    // 短杖
                    1372000, 1372001, 1372002, 1372003, 1372004, 1372005,
                    1372006, 1372007, 1372008, 1372009, 1372011, 1372032,
                    // 长杖
                    1382000, 1382001, 1382003, 1382004, 1382006, 1382007,
                    1382008, 1382010, 1382011, 1382012, 1382014, 1382015,
                    1382036, 1382037,
                    // 双手剑
                    1402000, 1402001, 1402002, 1402003, 1402006, 1402007,
                    1402008, 1402010, 1402012, 1402013, 1402014, 1402015,
                    1402017, 1402037, 1402044, 1402048, 1402049,
                    // 双手斧
                    1412000, 1412003, 1412004, 1412005, 1412006, 1412007,
                    1412008,
                    // 双手钝器
                    1422000, 1422001, 1422003, 1422004, 1422005, 1422007,
                    1422008, 1422009, 1422011, 1422013,
                    // 枪
                    1432000, 1432001, 1432002, 1432004, 1432005, 1432006,
                    1432009, 1432010, 1432011, 1432013, 1432016, 1432017,
                    1432018, 1432030,
                    // 矛
                    1442000, 1442001, 1442003, 1442004, 1442005, 1442006,
                    1442007, 1442009, 1442010, 1442012, 1442013, 1442014,
                    1442015, 1442016, 1442017, 1442018, 1442021, 1442022,
                    1442025,
                    // 弓
                    1452000, 1452001, 1452002, 1452003, 1452004, 1452005,
                    1452006, 1452007, 1452008, 1452009, 1452010, 1452011,
                    1452012, 1452014, 1452015, 1452017, 1452018, 1452023,
                    1452026,
                    // 弩
                    1462000, 1462002, 1462003, 1462004, 1462005, 1462006,
                    1462007, 1462009, 1462010, 1462011, 1462012, 1462013,
                    1462018,
                    // 拳套
                    1472000, 1472001, 1472002, 1472003, 1472004, 1472005,
                    1472006, 1472007, 1472008, 1472009, 1472010, 1472011,
                    1472012, 1472013, 1472014, 1472015, 1472016, 1472017,
                    1472018, 1472019, 1472020, 1472021, 1472022, 1472023,
                    1472024, 1472025, 1472026, 1472027, 1472028, 1472029,
                    1472030, 1472031, 1472032, 1472033, 1472054,
                    // 指节
                    1482000, 1482001, 1482002, 1482003, 1482004, 1482005,
                    1482006, 1482007, 1482008, 1482009, 1482010, 1482011,
                    1482012,
                    // 短枪
                    1492000, 1492001, 1492002, 1492003, 1492004, 1492005,
                    1492006, 1492007, 1492008, 1492009, 1492010, 1492011,
                    1492012
                ]
            },
            {
                name: "盾牌",
                items: [
                    1092002, 1092004, 1092008, 1092011, 1092013, 1092014,
                    1092019, 1092020, 1092021, 1092022, 1092030
                ]
            }
        ]
    }
];

// 展开抽奖收集子类别的所有物品到一个集合,用于快速查找
var gachaponItemSet;
(function() {
    gachaponItemSet = new java.util.HashSet();
    var gachaCat = categories[categories.length - 1];
    for (var s = 0; s < gachaCat.subCategories.length; s++) {
        var items = gachaCat.subCategories[s].items;
        for (var i = 0; i < items.length; i++) {
            gachaponItemSet.add(items[i]);
        }
    }
})();

// ===== NPC脚本 =====
var status = -1;
var currentCategoryIdx = -1;
var currentSubCategoryIdx = -1;

function start() {
    status = -1;
    currentCategoryIdx = -1;
    currentSubCategoryIdx = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        if (currentSubCategoryIdx >= 0) {
            // 返回抽奖子类别列表
            currentSubCategoryIdx = -1;
            status = -1;
            showGachaponSubMenu();
            return;
        }
        if (currentCategoryIdx >= 0) {
            currentCategoryIdx = -1;
            status = -1;
            showMainMenu();
            return;
        }
        cm.dispose();
        return;
    }

    if (mode === 0) {
        cm.dispose();
        return;
    }

    status++;

    if (currentCategoryIdx === -1) {
        // 主菜单
        if (status === 0) {
            showMainMenu();
        } else {
            handleMainSelection(selection);
        }
    } else if (currentCategoryIdx === categories.length - 1) {
        // 抽奖收集 - 有子类别
        if (currentSubCategoryIdx === -1) {
            if (status === 0) {
                showGachaponSubMenu();
            } else {
                handleGachaponSubSelection(selection);
            }
        } else {
            if (status === 0) {
                showItemListForSubCategory();
            } else {
                handleItemAction(selection, true);
            }
        }
    } else {
        // 普通类别 - 直接显示物品
        if (status === 0) {
            showItemList();
        } else {
            handleItemAction(selection, false);
        }
    }
}

// ===== 主菜单 =====

function showMainMenu() {
    var collection = loadCollection();
    var text = "#e#b=== 玩具收集手册 ===#k#n\r\n\r\n";
    text += "#r登记1件玩具 = 2AP属性奖励#k\r\n";
    text += "#r完成整个类别收集 = 5AP属性奖励#k\r\n\r\n";
    text += "请选择要查看的类别：\r\n";

    for (var i = 0; i < categories.length; i++) {
        var cat = categories[i];
        var total = cat.subCategories ? getGachaponTotal() : cat.items.length;
        var completed = cat.subCategories ? getGachaponCompleted(collection) : getCategoryCompleted(i, collection);
        var pct = Math.floor(completed / total * 100);
        text += "#L" + i + "##b" + cat.name + "#k (";
        text += completed + "/" + total + ", " + pct + "%";
        text += ")#l\r\n";
    }

    status = 0;
    cm.sendSimple(text);
}

function getGachaponTotal() {
    var total = 0;
    var gachaCat = categories[categories.length - 1];
    for (var s = 0; s < gachaCat.subCategories.length; s++) {
        total += gachaCat.subCategories[s].items.length;
    }
    return total;
}

function handleMainSelection(selection) {
    if (selection >= 0 && selection < categories.length) {
        currentCategoryIdx = selection;
        if (selection === categories.length - 1) {
            currentSubCategoryIdx = -1;
            status = -1;
            showGachaponSubMenu();
        } else {
            status = -1;
            showItemList();
        }
    } else {
        cm.dispose();
    }
}

// ===== 抽奖子类别菜单 =====

function showGachaponSubMenu() {
    var gachaCat = categories[categories.length - 1];
    var collection = loadCollection();
    var catIdx = categories.length - 1;

    var text = "#e#b抽奖收集#k#n\r\n\r\n";
    text += "选择装备部位查看：\r\n";

    for (var s = 0; s < gachaCat.subCategories.length; s++) {
        var sub = gachaCat.subCategories[s];
        var total = sub.items.length;
        var completed = getSubCategoryCompleted(s, collection);
        var pct = Math.floor(completed / total * 100);
        text += "#L" + s + "##b" + sub.name + "#k (" + completed + "/" + total + ", " + pct + "%)#l\r\n";
    }

    text += "\r\n#L" + gachaCat.subCategories.length + "##r[返回主菜单]#l\r\n";

    // 抽奖收集整体完成奖励
    var gachaTotal = getGachaponTotal();
    var gachaCompleted = getGachaponCompleted(collection);
    if (gachaCompleted >= gachaTotal && !isCategoryClaimed(catIdx, collection)) {
        text += "#L" + (gachaCat.subCategories.length + 1) + "##d[领取抽奖收集完成奖励: 5AP]#l\r\n";
    }

    status = 1;
    cm.sendSimple(text);
}

function handleGachaponSubSelection(selection) {
    var gachaCat = categories[categories.length - 1];
    var catIdx = categories.length - 1;
    var collection = loadCollection();

    if (selection < gachaCat.subCategories.length) {
        currentSubCategoryIdx = selection;
        status = -1;
        showItemListForSubCategory();
    } else if (selection === gachaCat.subCategories.length) {
        currentCategoryIdx = -1;
        currentSubCategoryIdx = -1;
        status = -1;
        showMainMenu();
        return;
    } else if (selection === gachaCat.subCategories.length + 1) {
        if (!isCategoryClaimed(catIdx, collection)) {
            claimCategoryReward(catIdx, collection);
            cm.getPlayer().gainAp(5, true);
            cm.sendOk("恭喜！完成了 #b抽奖收集#k 所有装备收集！\r\n获得 #r5点AP#k 属性奖励！");
        } else {
            cm.sendOk("该类别奖励已经领取过了。");
        }
        status = -1;
        currentSubCategoryIdx = -1;
        showGachaponSubMenu();
        return;
    }
}

// ===== 物品列表 =====

function showItemList() {
    var cat = categories[currentCategoryIdx];
    var collection = loadCollection();
    var items = cat.items;
    var total = items.length;
    var completed = 0;

    var text = "#e#b" + cat.name + "#k#n\r\n\r\n";

    for (var i = 0; i < total; i++) {
        var itemId = items[i];
        var registered = isItemRegistered(itemId, collection);
        var hasItem = cm.haveItem(itemId, 1);

        if (registered) completed++;

        var icon = registered ? "#g[已登记]#k " : (hasItem ? "#r[可登记]#k " : "");
        text += "#L" + i + "#" + icon + " #i" + itemId + "# #v" + itemId + "# ";

        var itemName = ItemInformationProvider.getInstance().getName(itemId);
        text += (itemName ? itemName : ("#o" + itemId + "#"));

        if (registered) {
            text += " #g[已登记]#k";
        } else if (hasItem) {
            text += " #r[拥有,点击登记]#k";
        } else {
            text += " [未获得]";
        }
        text += "#l\r\n";
    }

    text += "\r\n#L" + total + "##r[返回主菜单]#l\r\n";

    if (completed >= total && !isCategoryClaimed(currentCategoryIdx, collection)) {
        text += "#L" + (total + 1) + "##d[领取类别完成奖励: 5AP]#l\r\n";
    }

    status = 1;
    cm.sendSimple(text);
}

function showItemListForSubCategory() {
    var gachaCat = categories[categories.length - 1];
    var sub = gachaCat.subCategories[currentSubCategoryIdx];
    var collection = loadCollection();
    var items = sub.items;
    var total = items.length;
    var completed = 0;

    var text = "#e#b抽奖收集 → " + sub.name + "#k#n\r\n\r\n";

    for (var i = 0; i < total; i++) {
        var itemId = items[i];
        var registered = isItemRegistered(itemId, collection);
        var hasItem = cm.haveItem(itemId, 1);

        if (registered) completed++;

        var icon = registered ? "#g[已登记]#k " : (hasItem ? "#r[可登记]#k " : "");
        text += "#L" + i + "#" + icon + " #i" + itemId + "# #v" + itemId + "# ";

        var itemName = ItemInformationProvider.getInstance().getName(itemId);
        text += (itemName ? itemName : ("#o" + itemId + "#"));

        if (registered) {
            text += " #g[已登记]#k";
        } else if (hasItem) {
            text += " #r[拥有,点击登记]#k";
        } else {
            text += " [未获得]";
        }
        text += "#l\r\n";
    }

    text += "\r\n#L" + total + "##r[返回抽奖类别列表]#l\r\n";

    status = 1;
    cm.sendSimple(text);
}

// ===== 物品操作 =====

function handleItemAction(selection, isGachapon) {
    var cat = categories[currentCategoryIdx];
    var items;
    if (isGachapon) {
        var sub = cat.subCategories[currentSubCategoryIdx];
        items = sub.items;
    } else {
        items = cat.items;
    }

    var total = items.length;
    var collection = loadCollection();

    if (selection < total) {
        var itemId = items[selection];
        var registered = isItemRegistered(itemId, collection);
        var hasItem = cm.haveItem(itemId, 1);

        if (registered) {
            var itemName = ItemInformationProvider.getInstance().getName(itemId);
            cm.sendOk("#i" + itemId + "# " + (itemName || "") + "\r\n该装备已经登记过了！");
        } else if (hasItem) {
            // 登记装备（消耗1件）
            cm.gainItem(itemId, -1);
            registerItem(itemId, collection);
            cm.getPlayer().gainAp(2, true);
            var itemName = ItemInformationProvider.getInstance().getName(itemId);
            cm.sendOk("登记成功！#i" + itemId + "# " + (itemName || "") + "\r\n获得 #r2点AP#k 属性奖励！\r\n物品已消耗1件。");
        } else {
            var itemName = ItemInformationProvider.getInstance().getName(itemId);
            cm.sendOk("#i" + itemId + "# " + (itemName || "") + "\r\n你还没有获得该装备。\r\n请先获得该装备后再来登记。");
        }
    } else if (selection === total) {
        // 返回
        if (isGachapon) {
            currentSubCategoryIdx = -1;
            status = -1;
            showGachaponSubMenu();
            return;
        } else {
            currentCategoryIdx = -1;
            status = -1;
            showMainMenu();
            return;
        }
    } else if (selection === total + 1) {
        if (!isCategoryClaimed(currentCategoryIdx, collection)) {
            claimCategoryReward(currentCategoryIdx, collection);
            cm.getPlayer().gainAp(5, true);
            cm.sendOk("恭喜！完成了 #b" + cat.name + "#k 所有玩具收集！\r\n获得 #r5点AP#k 属性奖励！");
        } else {
            cm.sendOk("该类别奖励已经领取过了。");
        }
    }

    status = -1;
    if (isGachapon) {
        showItemListForSubCategory();
    } else {
        showItemList();
    }
}

// ===== 数据持久化 =====

function loadCollection() {
    var data = cm.getCharacterExtendValue("toyCollection");
    if (data == null || data === "") {
        return { registered: [], categoryRewards: [] };
    }
    try {
        var c = JSON.parse(data);
        if (!c.registered) c.registered = [];
        if (!c.categoryRewards) c.categoryRewards = [];
        return c;
    } catch (e) {
        return { registered: [], categoryRewards: [] };
    }
}

function saveCollection(data) {
    cm.saveOrUpdateCharacterExtendValue("toyCollection", JSON.stringify(data));
}

// ===== 辅助函数 =====

function isItemRegistered(itemId, collection) {
    return collection.registered.indexOf(String(itemId)) >= 0;
}

function isCategoryClaimed(catIdx, collection) {
    return collection.categoryRewards.indexOf(String(catIdx)) >= 0;
}

function registerItem(itemId, collection) {
    var key = String(itemId);
    if (collection.registered.indexOf(key) < 0) {
        collection.registered.push(key);
    }
    saveCollection(collection);
}

function claimCategoryReward(catIdx, collection) {
    var key = String(catIdx);
    if (collection.categoryRewards.indexOf(key) < 0) {
        collection.categoryRewards.push(key);
    }
    saveCollection(collection);
}

function getCategoryCompleted(catIdx, collection) {
    var cat = categories[catIdx];
    if (cat.subCategories) return getGachaponCompleted(collection);
    var count = 0;
    for (var i = 0; i < cat.items.length; i++) {
        if (isItemRegistered(cat.items[i], collection)) count++;
    }
    return count;
}

function getSubCategoryCompleted(subIdx, collection) {
    var sub = categories[categories.length - 1].subCategories[subIdx];
    var count = 0;
    for (var i = 0; i < sub.items.length; i++) {
        if (isItemRegistered(sub.items[i], collection)) count++;
    }
    return count;
}

function getGachaponCompleted(collection) {
    var count = 0;
    var gachaCat = categories[categories.length - 1];
    for (var s = 0; s < gachaCat.subCategories.length; s++) {
        var items = gachaCat.subCategories[s].items;
        for (var i = 0; i < items.length; i++) {
            if (isItemRegistered(items[i], collection)) count++;
        }
    }
    return count;
}
