/**
 * @description 从新开始（更换职业）
 * 效果：变为 1 级新手系起点，AP/技能点/技能全部重置为基础值，可重新升级转职。
 * 消耗：焕新核心 4310308 ×1 + 1 亿金币
 * 可选起点：冒险家(0) / 贵族(1000) / 战神(2000)
 * 装备：仅卸下普通装备（时装/现金外观保留）；若卸下后装备栏装不下则禁止确认。
 * 入口：xy_拍卖_v001 case 25
 *
 * 注意：对话禁止使用 #v4310308# / #z4310308# —— 客户端缺 Item/Etc/0431.img 节点会闪退。
 */

var COST_ITEM = 4310308;
var COST_MESO = 100000000;
var COST_ITEM_NAME = "焕新核心";

var status = -1;
var selectedJob = -1;
var bagBlocked = false;
var needUnequipCount = 0;

var InventoryType = Java.type("org.gms.client.inventory.InventoryType");
var InventoryManipulator = Java.type("org.gms.client.inventory.manipulator.InventoryManipulator");
var ItemInformationProvider = Java.type("org.gms.server.ItemInformationProvider");
var Stat = Java.type("org.gms.client.Stat");
var PacketCreator = Java.type("org.gms.util.PacketCreator");
var Pair = Java.type("org.gms.util.Pair");
var ArrayList = Java.type("java.util.ArrayList");
var GameConfig = Java.type("org.gms.config.GameConfig");
var GameConstants = Java.type("org.gms.constants.game.GameConstants");

// 选项序号 → 职业ID（勿把 jobId 直接写进 #L，过大易出问题）
var JOB_BY_SEL = {
    1: 0,
    2: 1000,
    3: 2000
};

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    if (status === 0) {
        showMainPage();
    } else if (status === 1) {
        if (bagBlocked || selection === 0) {
            cm.dispose();
            return;
        }
        if (JOB_BY_SEL[selection] === undefined) {
            cm.sendOk("选项无效。");
            cm.dispose();
            return;
        }
        selectedJob = JOB_BY_SEL[selection];
        showConfirm();
    } else if (status === 2) {
        if (type === 1) {
            doReset();
        } else {
            cm.sendOk("已取消。");
            cm.dispose();
        }
    } else {
        cm.dispose();
    }
}

function showMainPage() {
    refreshBagCheck();

    var text = "#e从新开始#n\r\n\r\n";
    text += "将角色重置为 #r1 级#k 新手系起点，并清空：\r\n";
    text += "  - 已分配 AP（力量/敏捷/智力/运气）\r\n";
    text += "  - 剩余 AP / 技能点 SP\r\n";
    text += "  - 已学技能\r\n\r\n";
    text += "消耗：#b" + COST_ITEM_NAME + "#k (" + COST_ITEM + ") × #r1#k  +  #r1 亿#k 金币\r\n";
    text += "普通装备会卸到装备栏；#b时装/现金外观保留#k。\r\n\r\n";

    if (bagBlocked) {
        text += "#r装备栏空位不足，无法卸下当前 " + needUnequipCount + " 件普通装备。#k\r\n";
        text += "请先整理装备背包后再来。\r\n\r\n";
        text += "#L0#关闭#l\r\n";
        cm.sendSimple(text);
        return;
    }

    text += "请选择重置后的职业起点：\r\n";
    text += "#L1##b冒险家（新手）#k#l\r\n";
    text += "#L2##b皇家骑士团（贵族）#k#l\r\n";
    text += "#L3##b战神（传说）#k#l\r\n";
    text += "#L0#取消#l\r\n";
    cm.sendSimple(text);
}

function showConfirm() {
    refreshBagCheck();
    if (bagBlocked) {
        cm.sendOk("#r装备栏空位不足，无法卸装，已取消。#k");
        cm.dispose();
        return;
    }

    var jobName = jobNameOf(selectedJob);
    var text = "#e确认从新开始？#n\r\n\r\n";
    text += "目标：#b" + jobName + "#k（职业 ID " + selectedJob + "）\r\n";
    text += "等级：#r1#k\r\n";
    text += "将消耗：#b" + COST_ITEM_NAME + "#k ×1 + 1 亿金币\r\n";
    if (needUnequipCount > 0) {
        text += "将卸下普通装备：#r" + needUnequipCount + "#k 件\r\n";
    }
    text += "\r\n#r此操作不可撤销，请确认已准备好重新升级。#k";
    cm.sendYesNo(text);
}

function doReset() {
    refreshBagCheck();
    if (bagBlocked) {
        cm.sendOk("#r装备栏空位不足，无法卸装。#k");
        cm.dispose();
        return;
    }
    if (!cm.haveItem(COST_ITEM, 1)) {
        cm.sendOk("缺少 #b" + COST_ITEM_NAME + "#k (" + COST_ITEM + ")。");
        cm.dispose();
        return;
    }
    if (cm.getMeso() < COST_MESO) {
        cm.sendOk("金币不足 1 亿。");
        cm.dispose();
        return;
    }

    var player = cm.getPlayer();
    var client = cm.getClient();

    // 先扣费，再执行重置
    cm.gainItem(COST_ITEM, -1);
    cm.gainMeso(-COST_MESO);

    // 1) 卸普通装备
    unequipNormalGears(client, player);

    // 2) 清 buff
    try {
        player.cancelAllBuffs(false);
    } catch (e) { /* ignore */ }

    // 3) 清技能
    clearAllSkills(player);

    // 4) 清 SP
    zeroAllSp(player);

    // 5) 换职业（走 changeJob 以同步外观/组队等；随后强制刷回基础数值）
    cm.changeJobById(selectedJob);

    // 6) 等级 / 经验
    player.setLevel(1);
    player.setExp(0);

    // 7) AP / 四维 / HPMP / SP 强制为基础值
    applyBeginnerBaseStats(player);

    // 8) 推送属性包
    pushFullStatUpdate(player);
    player.forceUpdateLocalStats();
    player.forceSyncClientDisplayStats();

    var text = "从新开始成功！\r\n\r\n";
    text += "你现在是 #b1 级 " + jobNameOf(selectedJob) + "#k。\r\n";
    text += "AP / 技能点 / 技能已全部重置，请重新升级转职。";
    cm.sendOk(text);
    cm.dispose();
}

function refreshBagCheck() {
    var info = collectNormalEquipped(cm.getPlayer());
    needUnequipCount = info.count;
    var free = cm.getPlayer().getInventory(InventoryType.EQUIP).getNumFreeSlot();
    bagBlocked = needUnequipCount > free;
}

function collectNormalEquipped(player) {
    var ii = ItemInformationProvider.getInstance();
    var equipped = player.getInventory(InventoryType.EQUIPPED);
    var list = new ArrayList();
    var items = equipped.list().toArray();
    for (var i = 0; i < items.length; i++) {
        var it = items[i];
        if (it == null) {
            continue;
        }
        if (ii.isCash(it.getItemId())) {
            continue;
        }
        list.add(it);
    }
    return { count: list.size(), items: list };
}

function unequipNormalGears(client, player) {
    var info = collectNormalEquipped(player);
    var items = info.items.toArray();
    // 从后往前卸，避免遍历中改表
    for (var i = items.length - 1; i >= 0; i--) {
        var it = items[i];
        var src = it.getPosition();
        var dst = player.getInventory(InventoryType.EQUIP).getNextFreeSlot();
        if (dst < 1) {
            break;
        }
        InventoryManipulator.unequip(client, src, dst);
    }
}

function clearAllSkills(player) {
    var skillsMap = player.getSkills();
    var keys = skillsMap.keySet().toArray();
    for (var i = 0; i < keys.length; i++) {
        try {
            player.changeSkillLevel(keys[i], -1, 0, -1);
        } catch (e) { /* ignore single skill */ }
    }
}

function zeroAllSp(player) {
    var sps = player.getRemainingSps();
    for (var i = 0; i < sps.length; i++) {
        player.setRemainingSp(0, i);
    }
}

function applyBeginnerBaseStats(player) {
    var str = 4;
    var dex = 4;
    var int_ = 4;
    var luk = 4;
    var ap = 0;

    // 与创角 CharacterFactoryRecipe 一致
    if (!GameConfig.getServerBoolean("use_starting_ap_4")) {
        if (GameConfig.getServerBoolean("use_auto_assign_starters_ap")) {
            str = 12;
            dex = 5;
            ap = 0;
        } else {
            ap = 9;
        }
    }

    player.setStr(str);
    player.setDex(dex);
    player.setInt(int_);
    player.setLuk(luk);
    player.setRemainingAp(ap);
    player.setHpMpApUsed(0);
    zeroAllSp(player);

    player.updateMaxHpMaxMp(50, 5);
    player.updateHpMp(50, 5);
}

function pushFullStatUpdate(player) {
    var statup = new ArrayList();
    statup.add(new Pair(Stat.STR, java.lang.Long.valueOf(player.getStr())));
    statup.add(new Pair(Stat.DEX, java.lang.Long.valueOf(player.getDex())));
    statup.add(new Pair(Stat.INT, java.lang.Long.valueOf(player.getInt())));
    statup.add(new Pair(Stat.LUK, java.lang.Long.valueOf(player.getLuk())));
    statup.add(new Pair(Stat.AVAILABLEAP, java.lang.Long.valueOf(player.getRemainingAp())));
    statup.add(new Pair(Stat.AVAILABLESP, java.lang.Long.valueOf(player.getRemainingSps()[GameConstants.getSkillBook(player.getJob().getId())])));
    statup.add(new Pair(Stat.HP, java.lang.Long.valueOf(player.getHp())));
    statup.add(new Pair(Stat.MP, java.lang.Long.valueOf(player.getMp())));
    statup.add(new Pair(Stat.MAXHP, java.lang.Long.valueOf(player.getClientMaxHp())));
    statup.add(new Pair(Stat.MAXMP, java.lang.Long.valueOf(player.getClientMaxMp())));
    statup.add(new Pair(Stat.EXP, java.lang.Long.valueOf(player.getExp())));
    statup.add(new Pair(Stat.LEVEL, java.lang.Long.valueOf(player.getLevel())));
    statup.add(new Pair(Stat.JOB, java.lang.Long.valueOf(player.getJob().getId())));
    player.sendPacket(PacketCreator.updatePlayerStats(statup, true, player));
}

function jobNameOf(jobId) {
    if (jobId === 0) {
        return "冒险家（新手）";
    }
    if (jobId === 1000) {
        return "皇家骑士团（贵族）";
    }
    if (jobId === 2000) {
        return "战神（传说）";
    }
    return "未知";
}
