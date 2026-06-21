/*
 * ==================
 * 脚本类型: NPC
 * 脚本作者：北斗项目组
 * 功能说明：武器进阶系统（仅限已拥有初始武器的玩家）
 *   1. 武器逐级进阶（兑换下一级），每次消耗上一级武器+其他材料
 *   2. 不可领取初始武器（请通过武器中心→购买初始武器获取）
 *   3. 初始武器四维+20，攻击力+1，魔力+1，攻速+6（基础属性，购买时赋予）
 *   4. 进阶后按职业增加二维属性+10/级：
 *      战士: 力量+敏捷 | 弓箭手: 敏捷+力量 | 法师: 智力+运气
 *      飞侠: 运气+敏捷 | 海盗: 力量+敏捷
 *   5. 进阶后物理职业攻击力+10/级，法师魔力+15/级
 *   6. 倍率变量(RATE)控制材料消耗，除上一级武器外所有材料乘以该倍率
 *   7. 链式配置(CHAIN_NODES)：按武器类型(itemId前3位)过滤出该类型专属进阶链，
 *      预留节点(items为空)会被所有类型自动跳过，便于后续随时插入新节点
 * ==================
 */

// ===== 职业群常量与方法（内联自职业群.js，避免load()兼容问题） =====
var JOB_GROUP = {
    BEGINNER: 0,  // 新手
    WARRIOR:  1,  // 战士
    ARCHER:   2,  // 弓箭手
    MAGICIAN: 3,  // 法师
    THIEF:    4,  // 飞侠
    PIRATE:   5   // 海盗
};

/**
 * 根据 jobId 获取职业群编号 0~5
 */
function getJobGroup(jobId) {
    if (jobId === 0 || jobId === 1000 || jobId === 2000 || jobId === 800 || jobId === 900 || jobId === 910) {
        return JOB_GROUP.BEGINNER;
    }
    var rawGroup = Math.floor(jobId / 100) % 10;
    var mapping = {};
    mapping[0] = JOB_GROUP.BEGINNER;
    mapping[1] = JOB_GROUP.WARRIOR;
    mapping[2] = JOB_GROUP.MAGICIAN;
    mapping[3] = JOB_GROUP.ARCHER;
    mapping[4] = JOB_GROUP.THIEF;
    mapping[5] = JOB_GROUP.PIRATE;
    return mapping[rawGroup] !== undefined ? mapping[rawGroup] : JOB_GROUP.BEGINNER;
}

function getJobGroupName(groupId) {
    var names = {};
    names[JOB_GROUP.BEGINNER] = "新手";
    names[JOB_GROUP.WARRIOR]  = "战士";
    names[JOB_GROUP.ARCHER]   = "弓箭手";
    names[JOB_GROUP.MAGICIAN] = "法师";
    names[JOB_GROUP.THIEF]    = "飞侠";
    names[JOB_GROUP.PIRATE]   = "海盗";
    return names[groupId] || "未知";
}

function getStatKeysByGroup(groupId) {
    if (groupId == JOB_GROUP.WARRIOR)  return ["str", "dex"]; // 战士: 力量+敏捷
    if (groupId == JOB_GROUP.ARCHER)   return ["dex", "str"]; // 弓箭手: 敏捷+力量
    if (groupId == JOB_GROUP.MAGICIAN) return ["int", "luk"]; // 法师: 智力+运气
    if (groupId == JOB_GROUP.THIEF)    return ["luk", "dex"]; // 飞侠: 运气+敏捷
    if (groupId == JOB_GROUP.PIRATE)   return ["str", "dex"]; // 海盗: 力量+敏捷
    return ["str", "dex"]; // 新手默认战士
}

function getStatName(key) {
    if (key == "str") return "力量";
    if (key == "dex") return "敏捷";
    if (key == "int") return "智力";
    if (key == "luk") return "运气";
    return key;
}

function isPhysicalJobGroup(groupId) {
    return groupId == JOB_GROUP.WARRIOR || groupId == JOB_GROUP.ARCHER ||
           groupId == JOB_GROUP.THIEF || groupId == JOB_GROUP.PIRATE ||
           groupId == JOB_GROUP.BEGINNER; // 新手按物理处理
}

function getAtkInfo(groupId) {
    if (groupId == JOB_GROUP.MAGICIAN) {
        return {label: "魔力", key: "matk"};
    }
    return {label: "攻击力", key: "watk"};
}

// ===== 倍率配置（默认1，修改此处可整体调整材料消耗） =====
var RATE = 1;

// ===== Java类型导入 =====
var InventoryType = Java.type('org.gms.client.inventory.InventoryType');

// ===== 武器类型前缀 → 中文名 =====
var WEAPON_TYPE_NAME = {
    130: "单手剑", 131: "单手斧", 132: "单手钝器", 133: "短剑",
    137: "短杖",   138: "长杖",   140: "双手剑",   141: "双手斧",
    142: "双手钝器", 143: "枪",   144: "矛",       145: "弓",
    146: "弩",     147: "拳套",  148: "指节",     149: "手枪"
};

// ===== 武器进阶链配置（链式结构，按节点顺序排列，便于后续插入新节点） =====
// items: { 武器类型前缀: 该类型对应的武器itemId, ... }，类型未覆盖则该类型自动跳过此节点
// otherMaterials: [[材料ID, 基础数量], ...]，除上一级武器外的其他材料（应用RATE倍率），默认为空，仅消耗上一级武器
var CHAIN_NODES = [
    {
        key: "chrissy", name: "圣诞六翼天使武器", weaponLevel: 10, otherMaterials: [],
        items: {
            130: 1302105, 131: 1312039, 132: 1322065, 133: 1332081, 137: 1372046, 138: 1382062,
            140: 1402053, 141: 1412035, 142: 1422039, 143: 1432050, 144: 1442071, 145: 1452062,
            146: 1462056, 147: 1472077, 148: 1482029, 149: 1492030
        }
    },
    {
        key: "maple", name: "枫叶武器系列", weaponLevel: 30,
        otherMaterials: [[4011007, 1], [4021009, 1], [4000313, 1], [4001126, 10]],
        items: {
            130: 1302030, 133: 1332025, 138: 1382012, 141: 1412011, 142: 1422014, 143: 1432012,
            144: 1442024, 145: 1452022, 146: 1462019, 147: 1472032
        }
    },
    {
        key: "mapleQs", name: "枫叶青涩武器系列", weaponLevel: 40,
        otherMaterials: [[4011007, 5], [4021009, 5], [4000313, 5], [4001126, 50]],
        items: {
            133: 1332142, 137: 1372094, 138: 1382118, 140: 1402104, 144: 1442130, 145: 1452123,
            146: 1462111, 147: 1472134, 148: 1482096, 149: 1492095
        }
    },
    {
        key: "adGem", name: "冒险岛宝石武器系列", weaponLevel: 50,
        otherMaterials: [[4011007, 10], [4021009, 10], [4000313, 10], [4001126, 10]],
        items: {
            130: 1302169, 131: 1312068, 132: 1322099, 133: 1332144, 137: 1372096, 138: 1382120,
            140: 1402106, 141: 1412067, 142: 1422069, 143: 1432095, 144: 1442132, 145: 1452125,
            146: 1462113, 147: 1472136, 148: 1482098, 149: 1492097
        }
    },
    {
        key: "maple4th", name: "4周年枫叶武器系列", weaponLevel: 60,
        otherMaterials: [[4011007, 20], [4021009, 20], [4000313, 20], [4001126, 200]],
        items: {
            130: 1302064, 131: 1312032, 132: 1322054, 133: 1332055, 137: 1372034, 138: 1382039,
            141: 1412027, 142: 1422029, 143: 1432040, 144: 1442051, 145: 1452045, 146: 1462040,
            147: 1472055, 148: 1482022, 149: 1492022
        }
    },
    {
        key: "adPlat", name: "冒险岛铂金武器系列", weaponLevel: 70,
        otherMaterials: [[4011007, 30], [4021009, 30], [4000313, 30], [4001126, 300]],
        items: {
            130: 1302170, 131: 1312069, 132: 1322101, 133: 1332145, 137: 1372097, 138: 1382121,
            140: 1402107, 141: 1412068, 142: 1422070, 143: 1432096, 144: 1442133, 145: 1452126,
            146: 1462114, 147: 1472137, 148: 1482099, 149: 1492098
        }
    },
    {
        key: "newGold", name: "新黄金枫叶武器系列", weaponLevel: 80,
        otherMaterials: [[4011007, 40], [4021009, 40], [4000313, 40], [4001126, 400]],
        items: {
            130: 1302172, 131: 1312071, 132: 1322105, 133: 1332147, 137: 1372099, 138: 1382123,
            140: 1402109, 141: 1412070, 142: 1422072, 143: 1432098, 144: 1442135, 145: 1452128,
            146: 1462116, 147: 1472139, 148: 1482101, 149: 1492100
        }
    },
    {
        key: "purpleGold", name: "紫金枫叶武器系列", weaponLevel: 90,
        otherMaterials: [[4011007, 50], [4021009, 50], [4000313, 50], [4001126, 500]],
        items: {
            130: 1302212, 131: 1312114, 132: 1322154, 133: 1332186, 137: 1372131, 138: 1382160,
            140: 1402145, 141: 1412102, 142: 1422105, 143: 1432135, 144: 1442173, 145: 1452165,
            146: 1462156, 147: 1472177, 148: 1482138, 149: 1492138
        }
    },
    {
        key: "exclPurpleGold", name: "专属紫金枫叶武器系列", weaponLevel: 100,
        otherMaterials: [[4011007, 60], [4021009, 60], [4000313, 60], [4001126, 600]],
        items: {
            130: 1302227, 131: 1312116, 132: 1322162, 133: 1332193, 137: 1372139, 138: 1382168,
            140: 1402151, 141: 1412104, 142: 1422107, 143: 1432138, 144: 1442182, 145: 1452170,
            146: 1462159, 147: 1472179, 148: 1482140, 149: 1492152
        }
    },
    {
        key: "mapleSuper", name: "枫叶超级武器系列", weaponLevel: 110,
        otherMaterials: [[4011007, 70], [4021009, 700], [4000313, 70], [4001126, 700]],
        items: {
            133: 1332143, 137: 1372095, 138: 1382119, 140: 1402105, 144: 1442131, 145: 1452124,
            146: 1462112, 147: 1472135, 148: 1482097, 149: 1492096
        }
    },
    {
        key: "revolution", name: "革命武器系列", weaponLevel: 120,
        otherMaterials: [[4011007, 80], [4021009, 8], [4000313, 80], [4001126, 800]],
        items: {
            130: 1302289, 131: 1312165, 132: 1322215, 133: 1332238, 137: 1372188, 138: 1382222,
            140: 1402210, 141: 1412147, 142: 1422152, 143: 1432178, 144: 1442234, 145: 1452216,
            146: 1462204, 147: 1472226, 148: 1482179, 149: 1492190
        }
    },
    {
        key: "zakum", name: "扎昆泊伊兹尼武器系列", weaponLevel: 130,
        otherMaterials: [[4011007, 90], [4021009, 90], [4000313, 90], [4001126, 900]],
        items: {
            130: 1302312, 131: 1312182, 132: 1322233, 133: 1332257, 137: 1372204, 138: 1382242,
            140: 1402233, 141: 1412161, 142: 1422168, 143: 1432197, 144: 1442251, 145: 1452235,
            146: 1462222, 147: 1472244, 148: 1482199, 149: 1492209
        }
    },
    {
        key: "adTreasure", name: "冒险岛寻宝武器系列", weaponLevel: 140,
        otherMaterials: [[4011007, 100], [4021009, 100], [4000313, 100], [4001126, 100]],
        items: {
            130: 1302336, 131: 1312201, 132: 1322253, 133: 1332277, 137: 1372225, 138: 1382263,
            140: 1402253, 141: 1412180, 142: 1422187, 143: 1432216, 144: 1442270, 145: 1452255,
            146: 1462241, 147: 1472263, 148: 1482218, 149: 1492233
        }
    },
    // {
    //     key: "justice", name: "<正义>枫叶武器系列", weaponLevel: 145,
    //     otherMaterials: [[4011007, 120], [4021009, 120], [4000313, 120], [4001126, 1200]],
    //     items: {
    //         130: 1302200, 131: 1312106, 132: 1322146, 133: 1332177, 137: 1372126, 138: 1382152,
    //         140: 1402138, 141: 1412094, 142: 1422097, 143: 1432126, 144: 1442164, 145: 1452156,
    //         146: 1462146, 147: 1472168, 148: 1482129, 149: 1492129
    //     }
    // },
    {
        key: "royalBaron", name: "皇家班·雷昂武器系列", weaponLevel: 150,
        otherMaterials: [[4011007, 140], [4021009, 140], [4000313, 140], [4001126, 1400]],
        items: {
            130: 1302316, 131: 1312186, 132: 1322237, 133: 1332261, 137: 1372208, 138: 1382246,
            140: 1402237, 141: 1412179, 142: 1422186, 143: 1432201, 144: 1442255, 145: 1452239,
            146: 1462226, 147: 1472248, 148: 1482203, 149: 1492213
        }
    },
    {
        key: "master", name: "巨匠武器系列", weaponLevel: 160,
        otherMaterials: [[4011007, 160], [4021009, 160], [4000313, 160], [4001126, 1600]],
        items: {
            130: 1302285, 131: 1312162, 132: 1322213, 133: 1332235, 137: 1372186, 138: 1382220,
            140: 1402204, 141: 1412144, 142: 1422149, 143: 1432176, 144: 1442232, 145: 1452214,
            146: 1462202, 147: 1472223, 148: 1482177, 149: 1492188
        }
    },
    {
        key: "esrabuse", name: "埃苏莱布斯武器系列", weaponLevel: 180,
        otherMaterials: [[4011007, 180], [4021009, 180], [4000313, 180], [4001126, 1800]],
        items: {
            130: 1302333, 131: 1312199, 132: 1322250, 133: 1332274, 137: 1372222, 138: 1382259,
            140: 1402251, 141: 1412177, 142: 1422184, 143: 1432214, 144: 1442268, 145: 1452252,
            146: 1462239, 147: 1472261, 148: 1482216, 149: 1492231
        }
    },
    {
        key: "terminus", name: "特米纳斯武器系列", weaponLevel: 190,
        otherMaterials: [[4011007, 200], [4021009, 200], [4000313, 200], [4001126, 2000]],
        items: {
            130: 1302290, 131: 1312166, 132: 1322216, 133: 1332239, 137: 1372189, 138: 1382223,
            140: 1402211, 141: 1412148, 142: 1422153, 143: 1432179, 144: 1442235, 145: 1452217,
            146: 1462205, 147: 1472227, 148: 1482180, 149: 1492191
        }
    },
    {
        key: "mysticShadow", name: "神秘之影武器系列", weaponLevel: 200,
        otherMaterials: [[4011007, 250], [4021009, 250], [4000313, 250], [4001126, 2500]],
        items: {
            130: 1302343, 131: 1312203, 132: 1322255, 133: 1332279, 137: 1372228, 138: 1382265,
            140: 1402259, 141: 1412181, 142: 1422189, 143: 1432218, 144: 1442274, 145: 1452257,
            146: 1462243, 147: 1472265, 148: 1482221, 149: 1492235
        }
    },
    {
        // 预留节点：创世武器系列（WZ数据暂缺，items留空待后续补充武器ID，所有类型自动跳过；材料已预先配置）
        key: "placeholder2", name: "(预留-创世武器系列)", weaponLevel: 200,
        otherMaterials: [[4011007, 300], [4021009, 300], [4000313, 300], [4001126, 3000]],
        items: {}
    }
];

// ===== 当前会话状态 =====
var status = -1;
var curWeaponType = -1;     // 当前武器类型前缀（130/131/.../149）
var curChainIndex = -1;     // 当前武器在该类型链中的索引（-1表示尚未领取）
var pendingTargetIndex = -1; // 待兑换的目标链索引（该类型链内的索引）
var 返回图标 = "#fUI/UIWindow.img/itemSearch/BtBack/normal/0#";

// ===== 入口 =====

function start() {
    status = -1;
    curWeaponType = -1;
    curChainIndex = -1;
    pendingTargetIndex = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    if (status == 0) {
        showMainMenu();
    } else if (status == 1) {
        handleSelection(selection);
    } else if (status == 2) {
        if (type == 1) {
            doExchange();
        } else {
            cm.sendOk("已取消进阶。");
            cm.dispose();
        }
    }
}

// ===== 武器类型/链查找 =====

/**
 * 检查背包装备栏第一格是否为指定物品
 * @param itemId 物品ID
 * @returns true=第一格是该物品
 */
function hasItemInEquip(itemId) {
    var equipInv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    var item = equipInv.getItem(1); // 只取第一格
    return item != null && item.getItemId() == itemId;
}

/**
 * 按武器类型前缀过滤出该类型专属的进阶链（预留节点自动跳过）
 * @returns [{itemId, name, weaponLevel, otherMaterials, globalIndex}, ...]
 */
function buildTypeChain(weaponType) {
    var chain = [];
    for (var i = 0; i < CHAIN_NODES.length; i++) {
        var node = CHAIN_NODES[i];
        var itemId = node.items[weaponType];
        if (itemId != null) {
            chain.push({
                itemId: itemId,
                name: node.name,
                weaponLevel: node.weaponLevel,
                otherMaterials: node.otherMaterials,
                globalIndex: i
            });
        }
    }
    return chain;
}

/**
 * 在指定类型链中查找玩家当前拥有的最高级武器索引
 */
function findCurrentChainIndex(typeChain) {
    for (var i = typeChain.length - 1; i >= 0; i--) {
        if (hasItemInEquip(typeChain[i].itemId)) {
            return i;
        }
    }
    return -1;
}

/**
 * 扫描所有武器类型链，找到玩家当前拥有的链节点（用于自动识别玩家武器类型）
 * @returns {weaponType, chainIndex} 或 null
 */
// 所有支持的武器类型前缀列表（显式声明，避免for...in的兼容问题）
var ALL_WEAPON_TYPES = [130, 131, 132, 133, 137, 138, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149];

function detectOwnedChain() {
    for (var wi = 0; wi < ALL_WEAPON_TYPES.length; wi++) {
        var weaponType = ALL_WEAPON_TYPES[wi];
        var typeChain = buildTypeChain(weaponType);
        var idx = findCurrentChainIndex(typeChain);
        if (idx >= 0) {
            return {weaponType: weaponType, chainIndex: idx};
        }
    }
    return null;
}

/**
 * 推断武器类型前缀：只取背包装备栏第一格
 * @returns 武器类型前缀，第一格非武器时返回-1
 */
function detectEquippedWeaponType() {
    var equipInv = cm.getPlayer().getInventory(InventoryType.EQUIP);
    var equipItem = equipInv.getItem(1); // 只取第一格
    if (equipItem == null) {
        return -1;
    }
    var eqType = Math.floor(equipItem.getItemId() / 10000);
    if (WEAPON_TYPE_NAME[eqType] != null) {
        return eqType;
    }
    return -1;
}

// ===== 菜单显示 =====

function showMainMenu() {
    // 优先识别玩家已拥有的链武器（不论是否装备），否则识别当前装备的武器类型
    var owned = detectOwnedChain();
    if (owned != null) {
        curWeaponType = owned.weaponType;
        curChainIndex = owned.chainIndex;
    } else {
        curWeaponType = detectEquippedWeaponType();
        curChainIndex = -1;
    }

    if (curWeaponType < 0) {
        cm.sendOk("#r未能识别你的武器类型！#k\r\n请先装备任意一把武器后再来。");
        cm.dispose();
        return;
    }

    var typeChain = buildTypeChain(curWeaponType);
    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getJobGroup(jobId);
    var jobName = getJobGroupName(jobGroup);
    var typeName = WEAPON_TYPE_NAME[curWeaponType];

    var text = "#e武器进阶#n\r\n\r\n";
    text += "武器类型：#b" + typeName + "#k    职业：#b" + jobName + "#k\r\n";

    if (curChainIndex < 0) {
        // 未拥有进阶武器 → 提示先购买初始武器
        text += "#r你尚未拥有进阶武器！#k\r\n\r\n";
        text += "请先通过 #b武器中心 → 购买初始武器#k 获取\r\n";
        text += "#i" + typeChain[0].itemId + "# #b" + typeChain[0].name + "#k 后再来进阶。\r\n";
        text += "\r\n#L0#返回武器中心#l\r\n";
    } else if (curChainIndex >= typeChain.length - 1) {
        // 已达该类型当前最高节点
        var maxNode = typeChain[curChainIndex];
        text += "当前武器：#i" + maxNode.itemId + "# #b" + maxNode.name + "#k\r\n\r\n";
        text += "#g恭喜！已达成当前最高级别武器！#k\r\n";
    } else {
        var curNode = typeChain[curChainIndex];
        var nextNode = typeChain[curChainIndex + 1];
        var nextIndex = curChainIndex + 1;
        var goldCost = 1000000 * nextIndex; // 100W * 进阶等级

        text += "当前武器：#i" + curNode.itemId + "# #b" + curNode.name + "#k\r\n\r\n";
        text += "━━━ ━━━ 可进阶至 ━━━ ━━━\r\n\r\n";
        text += "#i" + nextNode.itemId + "# #b" + nextNode.name + "#k\r\n\r\n";

        text += "进阶费用：#r" + (goldCost / 10000) + "W金币#k\r\n";
        text += "所需材料：\r\n";
        text += "  #i" + curNode.itemId + "# #z" + curNode.itemId + "# x 1\r\n";
        for (var n = 0; n < nextNode.otherMaterials.length; n++) {
            var matId2 = nextNode.otherMaterials[n][0];
            var matQty2 = Math.floor(nextNode.otherMaterials[n][1] * RATE);
            text += "  #i" + matId2 + "# #z" + matId2 + "# x " + matQty2 + "\r\n";
        }
        text += "\r\n";
        text += "#L0#进阶武器#l\r\n\r\n";
    }

    text += "\r\n#L99#" + 返回图标 + "#l\r\n";
    cm.sendSimple(text);
}

// ===== 选择处理 =====

function handleSelection(selection) {
    if (selection == 99) {
        cm.dispose();
        cm.openNpc(9900001, "xy/装备系统/v002/武器中心");
        return;
    }

    if (curChainIndex < 0) {
        // 未拥有武器 → 返回武器中心购买初始武器
        cm.dispose();
        cm.openNpc(9900001, "xy/装备系统/v002/武器中心");
        return;
    } else {
        var typeChain = buildTypeChain(curWeaponType);
        if (curChainIndex >= typeChain.length - 1) {
            cm.sendOk("已达当前最高级别！");
            cm.dispose();
        } else {
            handleUpgrade();
        }
    }
}

/**
 * 处理武器进阶
 */
function handleUpgrade() {
    var typeChain = buildTypeChain(curWeaponType);
    var nextIndex = curChainIndex + 1;
    var nextNode = typeChain[nextIndex];
    var curNode = typeChain[curChainIndex];

    if (!hasItemInEquip(curNode.itemId)) {
        cm.sendOk("#r异常：未找到当前武器，请重试。#k");
        cm.dispose();
        return;
    }

    for (var m = 0; m < nextNode.otherMaterials.length; m++) {
        var matId = nextNode.otherMaterials[m][0];
        var matQty = Math.floor(nextNode.otherMaterials[m][1] * RATE);
        if (!cm.haveItem(matId, matQty)) {
            cm.sendOk("#r材料不足！#k\r\n需要 #i" + matId + "# #b#z" + matId + "##k x #r" + matQty + "#k\r\n请收集材料后再来进阶。");
            cm.dispose();
            return;
        }
    }

    if (!cm.canHold(nextNode.itemId, 1)) {
        cm.sendOk("#r背包空间不足，请清理背包后再来进阶！#k");
        cm.dispose();
        return;
    }

    pendingTargetIndex = nextIndex;

    var goldCost = 1000000 * nextIndex; // 100W * 进阶等级

    var confirmText = "确认进阶武器？\r\n\r\n";
    confirmText += "#i" + curNode.itemId + "# #b" + curNode.name + "#k\r\n";
    confirmText += "  → #i" + nextNode.itemId + "# #b" + nextNode.name + "#k\r\n\r\n";
    confirmText += "进阶费用：#r" + (goldCost / 10000) + "W金币#k\r\n";
    confirmText += "将消耗以下材料：\r\n";
    confirmText += "  #i" + curNode.itemId + "# #z" + curNode.itemId + "# x 1\r\n";
    for (var n = 0; n < nextNode.otherMaterials.length; n++) {
        var matId2 = nextNode.otherMaterials[n][0];
        var matQty2 = Math.floor(nextNode.otherMaterials[n][1] * RATE);
        confirmText += "  #i" + matId2 + "# #z" + matId2 + "# x " + matQty2 + "\r\n";
    }

    cm.sendYesNo(confirmText);
}

// ===== 执行兑换 =====

function doExchange() {
    var typeChain = buildTypeChain(curWeaponType);
    // 安全校验：初始武器（index=0）应通过购买获取，不在此处发放
    if (pendingTargetIndex < 1 || pendingTargetIndex >= typeChain.length) {
        cm.sendOk("进阶数据异常，请重试。");
        cm.dispose();
        return;
    }

    var targetNode = typeChain[pendingTargetIndex];
    var targetItemId = targetNode.itemId;
    var jobId = cm.getPlayer().getJob().getId();
    var jobGroup = getJobGroup(jobId);
    var statKeys = getStatKeysByGroup(jobGroup);
    var equipInv = cm.getPlayer().getInventory(InventoryType.EQUIP);

    // === 进阶武器 ===
    // pendingTargetIndex 至少为1（初始武器通过购买获取，不在此处发放）
    var prevNode = typeChain[pendingTargetIndex - 1];
    var prevItemId = prevNode.itemId;

    if (!hasItemInEquip(prevItemId)) {
        cm.sendOk("#r异常：未找到上一级武器，请重试。#k");
        cm.dispose();
        return;
    }

    for (var m3 = 0; m3 < targetNode.otherMaterials.length; m3++) {
        var matId3 = targetNode.otherMaterials[m3][0];
        var matQty3 = Math.floor(targetNode.otherMaterials[m3][1] * RATE);
        if (!cm.haveItem(matId3, matQty3)) {
            cm.sendOk("#r材料不足！#k");
            cm.dispose();
            return;
        }
    }

    if (!cm.canHold(targetItemId, 1)) {
        cm.sendOk("#r背包空间不足！#k");
        cm.dispose();
        return;
    }

    // 检查金币
    var goldCost = 1000000 * pendingTargetIndex; // 100W * 进阶等级
    if (cm.getMeso() < goldCost) {
        cm.sendOk("#r金币不足！#k\r\n需要 #b" + (goldCost / 10000) + "W#k 金币，当前只有 #r" + Math.floor(cm.getMeso() / 10000) + "W#k 金币。");
        cm.dispose();
        return;
    }

    // 扣除金币
    cm.gainMeso(-goldCost);

    // 扣除上一级武器
    cm.gainItem(prevItemId, -1);

    // 扣除其他材料（应用倍率）
    for (var m4 = 0; m4 < targetNode.otherMaterials.length; m4++) {
        cm.gainItem(targetNode.otherMaterials[m4][0], -Math.floor(targetNode.otherMaterials[m4][1] * RATE));
    }

    // 发放新武器
    cm.gainItem(targetItemId, 1);

    // 设置新武器属性：基础四维20 + 职业双维10/级（累计重算）
    var newEquip = equipInv.findById(targetItemId);
    if (newEquip != null) {
        var statValue = pendingTargetIndex * 10;
        var isFirstStat = (statKeys[0] == "str" || statKeys[1] == "str");
        var isSecondStat = (statKeys[0] == "dex" || statKeys[1] == "dex");
        var isThirdStat = (statKeys[0] == "int" || statKeys[1] == "int");
        var isFourthStat = (statKeys[0] == "luk" || statKeys[1] == "luk");
        newEquip.setStr(20 + (isFirstStat ? statValue : 0));
        newEquip.setDex(20 + (isSecondStat ? statValue : 0));
        newEquip.setInt(20 + (isThirdStat ? statValue : 0));
        newEquip.setLuk(20 + (isFourthStat ? statValue : 0));
        // 物理职业攻击力+10/级，法师魔力+15/级（累计重算，基础值为1）
        if (isPhysicalJobGroup(jobGroup)) {
            newEquip.setWatk(1 + pendingTargetIndex * 10);
            newEquip.setMatk(1);
        } else {
            newEquip.setMatk(1 + pendingTargetIndex * 15);
            newEquip.setWatk(1);
        }
        newEquip.setSpeed(6);
        // 强制推送装备属性更新到客户端，覆盖WZ自带属性
        cm.getPlayer().forceUpdateItem(newEquip);
    }

    var successText = "进阶成功！\r\n\r\n";
    successText += "#i" + targetItemId + "# #b" + targetNode.name + "#k 已放入背包。\r\n";
    successText += "#d该武器为固有道具，不可交换。#k\r\n";
    if (pendingTargetIndex < typeChain.length - 1) {
        successText += "#g继续收集材料来进阶吧！#k";
    } else {
        successText += "#g恭喜！已达成当前最高级别武器！#k";
    }

    cm.sendOk(successText);
    cm.dispose();
}
