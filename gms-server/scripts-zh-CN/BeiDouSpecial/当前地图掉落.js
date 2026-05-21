/**
 * 功能：展示当前地图怪物及其掉落物爆率（实时计算玩家个人爆率）
 *
 * 爆率公式与服务端实际掉落计算一致：
 *   普通怪物: baseChance% * dropRate * familyDrop * cardRate
 *   BOSS怪物: baseChance% * bossDropRate * familyDrop * cardRate
 *   其中 cardRate 受 ITEM_UP_BY_ITEM / MESO_UP_BY_ITEM 类buff影响
 *   惊天动地(Showdown)为怪物身上debuff，此处无法预先计算，实际掉落时额外生效
 */
var MonsterInformationProvider;
var ItemInformationProvider;
var QuestInfo;

var MapObj;
var List_Mob_All;
var List_Mob_Boss;
var List_Mob;
var nameMaxLen = 0;

function start() {
    if (MapObj == null) {
        MonsterInformationProvider = Java.type('org.gms.server.life.MonsterInformationProvider');
        ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');
        QuestInfo = Java.type('org.gms.server.quest.Quest');
        MapObj = cm.getMap();
        List_Mob_All = MapObj.getAllMonsters();
        // 去重并按BOSS/普通分类
        var seen = new java.util.HashSet();
        List_Mob = new java.util.ArrayList();
        List_Mob_Boss = new java.util.ArrayList();
        for (var i = 0; i < List_Mob_All.size(); i++) {
            var mob = List_Mob_All.get(i);
            if (!seen.contains(mob.getId())) {
                seen.add(mob.getId());
                if (mob.isBoss()) {
                    List_Mob_Boss.add(mob);
                } else {
                    List_Mob.add(mob);
                }
            }
        }
        // 计算最长怪物名长度
        for (var j = 0; j < List_Mob_All.size(); j++) {
            var n = List_Mob_All.get(j).getName();
            if (n && n != 'MISSINGNO' && n.length > nameMaxLen) {
                nameMaxLen = n.length;
            }
        }
    }
    levelmain();
}

function leveldispose() {
    cm.dispose();
}

function levelnull() {
    cm.dispose();
}

/**
 * 第一层：展示当前地图存活怪物列表（每种只显示一次）
 */
function levelmain() {
    if (List_Mob_All.isEmpty()) {
        cm.sendOkLevel('dispose', '当前地图没有存活的怪物，请等待怪物刷新后再进行查询。\r\n#r提示：部分地图（如城镇）不会刷新怪物。#k', 2);
        return;
    }

    var msg = '#e当前地图存活怪物一览#n\r\n';
    msg += '#d' + ''.padStart(26, '——') + '#k\r\n';

    if (!List_Mob_Boss.isEmpty()) {
        msg += '\r\n#e#rBOSS#k#n（' + List_Mob_Boss.size() + ' 种）：\r\n';
        msg += buildMobSelectList(List_Mob_Boss);
    }
    if (!List_Mob.isEmpty()) {
        msg += '\r\n#b普通怪物#k（' + List_Mob.size() + ' 种）：\r\n';
        msg += buildMobSelectList(List_Mob);
    }
    cm.sendNextSelectLevel('ShowDropList', msg, 2);
}

/**
 * 构建怪物选择列表（含图片）
 */
function buildMobSelectList(mobList) {
    var lines = [];
    for (var i = 0; i < mobList.size(); i++) {
        var mob = mobList.get(i);
        var id = mob.getId();
        var name = (!mob.getName() || mob.getName() == 'MISSINGNO') ? '#o' + id + '#' : mob.getName();
        var img = getMobImage(mob);
        var color = mob.isBoss() ? '#r' : '#b';
        lines.push('#L' + id + '#' + img + '\r\n' + color + name + '#k\t[ Lv.' + getLevelImage(mob.getLevel()) + ' ]#l');
    }
    return lines.join('\r\n\r\n') + '\r\n';
}

/**
 * 第二层：展示选中怪物的掉落列表（含个人实时爆率）
 */
function levelShowDropList(mobId) {
    var mob = null;
    for (var i = 0; i < List_Mob_All.size(); i++) {
        if (List_Mob_All.get(i).getId() == mobId) {
            mob = List_Mob_All.get(i);
            break;
        }
    }

    if (mob == null) {
        cm.sendLastLevel('main', '怪物数据未找到，请重新查询。', 2);
        return;
    }

    var player = cm.getPlayer();
    var dropList = MonsterInformationProvider.getInstance().retrieveDrop(mobId);
    var isBoss = mob.isBoss();

    // 构建头部：怪物信息
    var mobName = (!mob.getName() || mob.getName() == 'MISSINGNO') ? '#o' + mobId + '#' : mob.getName();
    var stats = mob.getStats();
    var msg = getMobImage(mob) + '\r\n';
    msg += '#e#b' + mobName + '#k#n\r\n';
    msg += '血量：' + formatNum(mob.getMaxHp()) + '\t\t蓝量：' + mob.getMaxMp() + '\r\n';
    msg += '物攻：' + stats.getPADamage() + '\t\t物防：' + stats.getPDDamage() + '\r\n';
    msg += '魔攻：' + stats.getMADamage() + '\t\t魔防：' + stats.getMDDamage() + '\r\n';

    // 显示当前玩家爆率倍率
    var chRate = isBoss ? player.getBossDropRate() : player.getDropRate();
    msg += '\r\n#d' + ''.padStart(24, '——') + '#k\r\n';
    msg += '你的基础掉率倍率：#e' + chRate.toFixed(2) + 'x#n';
    if (player.isFamilyBuff()) {
        msg += '  家族buff：#e' + player.getFamilyDrop().toFixed(2) + 'x#n';
    }
    msg += '\r\n';

    msg += '#d' + ''.padStart(24, '——') + '#k\r\n';

    if (dropList.isEmpty()) {
        msg += '\r\n该怪物没有掉落物。';
    } else {
        // 构建掉落表格
        msg += '\r\n#b物品名称\t\t\t基础掉率\t\t你的掉率#k\r\n';
        msg += ''.padStart(26, '—') + '\r\n';

        // 过滤有效掉落物
        var validDrops = [];
        for (var d = 0; d < dropList.size(); d++) {
            var drop = dropList.get(d);
            if (drop.itemId > 0) {
                var itemName = ItemInformationProvider.getInstance().getName(drop.itemId);
                if (itemName != null) {
                    validDrops.push(drop);
                }
            }
        }

        for (var d = 0; d < validDrops.length; d++) {
            var drop = validDrops[d];
            var itemId = drop.itemId;
            var itemName = ItemInformationProvider.getInstance().getName(itemId);
            var baseChance = drop.chance / 10000; // 转换为百分比

            // 个人实际爆率 = baseChance * chRate * cardRate * familyDrop
            var cardRate = player.getCardRate(itemId);
            var familyMult = player.isFamilyBuff() ? player.getFamilyDrop() : 1;
            var personalChance = baseChance * chRate * cardRate * familyMult;

            msg += '#L' + itemId + '##v' + itemId + '# #b' + itemName + '#k\t';
            msg += baseChance.toFixed(4) + '%\t';
            msg += '#r' + personalChance.toFixed(4) + '%#k';
            if (drop.questid > 0) {
                msg += '  #d[任务]#k';
            }
            msg += '#l\r\n';
        }
    }

    cm.sendLastLevel('main', msg, 2);
}

/**
 * 获取怪物图片（#f#格式），过大图片用占位图防止客户端卡死
 */
function getMobImage(mob) {
    var movetype = mob.getStats().getMovetype();
    var type;
    if (movetype == 0) {
        type = 'stand';
    } else if (movetype == 1) {
        type = 'fly';
    } else {
        return '#fUI/UIWindow.img/Maker/randomRecipe#';
    }
    if (mob.getStats().getImgwidth() > 160 && mob.getStats().getImgheight() > 250) {
        return '#fMap/Obj/Tdungeon.img/mushCatle/npc/0/0#\r\n(形象过大，不能展示)';
    }
    return '#fMob/' + mob.getId().toString().padStart(7, '0') + '.img/' + type + '/0#';
}

/**
 * 渲染等级数字图片
 */
function getLevelImage(level) {
    return level.toString().split('').map(function(d) {
        return '#fUI/Basic/LevelNo/' + d + '#';
    }).join('');
}

/**
 * 格式化大数字（万/亿）
 */
function formatNum(num) {
    if (num >= 100000000) {
        return (num / 100000000).toFixed(2) + '亿';
    } else if (num >= 10000) {
        return (num / 10000).toFixed(1) + '万';
    }
    return num.toString();
}
