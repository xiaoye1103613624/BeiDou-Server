/**
 * @author: 萧曵
 * @npc: 9031003 装备综合工坊
 * @description:
 *   覆盖原"装备收集"强化脚本(旧逻辑已废弃，编码也是乱码，不再保留)。
 *   主菜单按"9031003完整选项"截图对齐为5项官方功能 + 2项已上线的额外功能，跳转关系如下：
 *     #L1 装备进阶  —— cm.openNpc(9031003,"xy/装备系统/v002/武器进阶") 跳转到 BeiDouSpecial/xy/装备系统/v002/武器进阶.js
 *     #L2 装备锻造  —— 本脚本内置(原"打造"，作用于装备栏第1格)
 *     #L3 装备鉴定  —— cm.openNpc(9031003,"装备鉴定") 跳转到 npc/装备鉴定.js
 *     #L4 装备打造  —— cm.openNpc(9031003,"装备打造师") 跳转到 npc/装备打造师.js(锻造师副职业)
 *     #L5 装备强化  —— cm.openNpc(9031003,"装备强化") 跳转到 BeiDouSpecial/装备强化.js(★强化系统)
 *     #L6 经验项链(制作/升级/戒指) —— 跳转到 npc/经验项链.js
 *     #L7 装备战力排行榜      —— 跳转到 npc/装备战力排行榜.js
 *
 *   本脚本内置功能：
 *   装备锻造(原打造，装备栏第1格)：共6级(D1~D6)，1~6级四维(STR/DEX/INT/LUK)依次+1/+1/+2/+3/+4/+5，
 *   2~6级附加生命值/法力值，5~6级额外攻击力+2/魔法力+4；材料为对应邮票+枫叶。
 *
 *   持久化：getOneTimeLog/setOneTimeLog，logid 为 "打造_装备栏1"。
 */

// ========== 打造(装备栏第1格) 配置 ==========
var stamp绿蜗牛 = 4002000, stamp蓝蜗牛 = 4002001, stamp木妖 = 4002002, stamp绿水灵 = 4002003, mapleLeaf = 4001126;

var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var InventoryManipulator = Java.type('org.gms.client.inventory.manipulator.InventoryManipulator');
var craftLevels = [
    {
        level: 1,
        materials: [[stamp绿蜗牛, 25], [mapleLeaf, 100]],
        gold: 2000000,
        gain: {str: 1, dex: 1, int_: 1, luk: 1, hp: 0, mp: 0, watk: 0, matk: 0}
    },
    {
        level: 2,
        materials: [[stamp蓝蜗牛, 25], [mapleLeaf, 200]],
        gold: 4000000,
        gain: {str: 1, dex: 1, int_: 1, luk: 1, hp: 10, mp: 10, watk: 0, matk: 0}
    },
    {
        level: 3,
        materials: [[stamp木妖, 25], [mapleLeaf, 300]],
        gold: 8000000,
        gain: {str: 2, dex: 2, int_: 2, luk: 2, hp: 20, mp: 20, watk: 0, matk: 0}
    },
    {
        level: 4,
        materials: [[stamp绿水灵, 25], [mapleLeaf, 400]],
        gold: 16000000,
        gain: {str: 3, dex: 3, int_: 3, luk: 3, hp: 30, mp: 30, watk: 0, matk: 0}
    },
    {
        level: 5,
        materials: [[stamp绿蜗牛, 15], [stamp蓝蜗牛, 15], [stamp木妖, 15], [stamp绿水灵, 15], [mapleLeaf, 1000]],
        gold: 32000000,
        gain: {str: 4, dex: 4, int_: 4, luk: 4, hp: 50, mp: 50, watk: 2, matk: 4}
    },
    {
        level: 6,
        materials: [[stamp绿蜗牛, 30], [stamp蓝蜗牛, 30], [stamp木妖, 30], [stamp绿水灵, 30], [mapleLeaf, 2000]],
        gold: 64000000,
        gain: {str: 5, dex: 5, int_: 5, luk: 5, hp: 50, mp: 50, watk: 2, matk: 4}
    }
];

var status = 0;
var selCraftLevel = -1;

function start() {
    status = 0;
    action(1, 0, 0);
}

function 获取进度(logid) {
    return cm.getPlayer().getOneTimeLog(logid);
}

function 增加进度(logid) {
    cm.getPlayer().setOneTimeLog(logid);
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    status++;

    if (status == 1) {
        var add = "欢迎使用装备综合工坊，请选择功能\r\n";
        add += "\r\n#L1##b装备进阶#k(武器逐级进阶，需先拥有初始武器)#l";
        add += "\r\n#L2##b装备锻造#k(锻造特殊的装备道具，当前作用于装备栏第1格)#l";
        add += "\r\n#L3##b装备鉴定#k(将带有未鉴定的装备进行鉴定)#l";
        add += "\r\n#L4##b装备打造#k(给装备进行打造升级，增加属性)#l";
        add += "\r\n#L5##b装备强化#k(给装备进行强化，增加属性)#l";
        add += "\r\n\r\n";
        add += "\r\n#L6##b经验项链(制作/升级/戒指)#l";
        add += "\r\n#L7##b装备战力排行榜#l";
        add += "\r\n#L10##b初始武器购买#l";
        cm.sendSimple(add);
    } else if (status == 2 && selection == 1) {
        cm.dispose();
        cm.openNpc(9031003, "xy/装备系统/v002/武器进阶");
        return;
    } else if (status == 2 && selection == 3) {
        cm.dispose();
        cm.openNpc(9031003, "装备鉴定");
        return;
    } else if (status == 2 && selection == 4) {
        cm.dispose();
        cm.openNpc(9031003, "装备打造师");
        return;
    } else if (status == 2 && selection == 5) {
        cm.dispose();
        cm.openNpc(9031003, "装备强化");
        return;
    } else if (status == 2 && selection == 6) {
        cm.dispose();
        cm.openNpc(9031003, "经验项链");
        return;
    } else if (status == 2 && selection == 7) {
        cm.dispose();
        cm.openNpc(9031003, "装备战力排行榜");
        return;
    } else if (status == 2 && selection == 10) {
        cm.dispose();
        cm.openNpc(9031003, "xy/装备系统/v002/武器中心");
        return;
    } else if (status == 2 && selection == 2) {
        var lv = 获取进度("打造_装备栏1");
        if (lv >= craftLevels.length) {
            cm.sendOk("#b打造等级已满级(" + craftLevels.length + "级)，无法继续打造！");
            cm.dispose();
            return;
        }
        var next = craftLevels[lv];
        selCraftLevel = lv;
        var addc = "当前打造等级：#r" + lv + "#k\r\n";
        addc += "下一级(" + next.level + "级)所需材料：\r\n";
        for (var m = 0; m < next.materials.length; m++) {
            addc += "#v " + next.materials[m][0] + "# x" + next.materials[m][1] + "  ";
        }
        addc += "\r\n金币：#r" + next.gold + "#k\r\n";
        addc += "打造成功后属性提升：力量+" + next.gain.str + " 敏捷+" + next.gain.dex + " 智力+" + next.gain.int_ + " 运气+" + next.gain.luk;
        if (next.gain.hp > 0) addc += " 生命值+" + next.gain.hp;
        if (next.gain.mp > 0) addc += " 法力值+" + next.gain.mp;
        if (next.gain.watk > 0) addc += " 攻击力+" + next.gain.watk;
        if (next.gain.matk > 0) addc += " 魔法力+" + next.gain.matk;
        addc += "\r\n\r\n是否进行打造？";
        cm.sendYesNo(addc);
    } else if (status == 3 && type == 1 && selection == 1) {
        执行打造();
    } else {
        cm.dispose();
    }
}

function 执行打造() {
    var next = craftLevels[selCraftLevel];
    var weapon = cm.getInventory(1).getItem(1);
    if (weapon == null) {
        cm.sendOk("#b装备栏第1格没有装备，无法打造！");
        cm.dispose();
        return;
    }
    if (cm.getMeso() < next.gold) {
        cm.sendOk("#b金币不足，无法打造！");
        cm.dispose();
        return;
    }
    for (var i = 0; i < next.materials.length; i++) {
        if (!cm.haveItem(next.materials[i][0], next.materials[i][1])) {
            cm.sendOk("#b材料不足，无法打造！");
            cm.dispose();
            return;
        }
    }

    cm.gainMeso(-next.gold);
    for (var j = 0; j < next.materials.length; j++) {
        cm.gainItem(next.materials[j][0], -next.materials[j][1]);
    }

    var newItem = weapon.copy();
    newItem.setStr((newItem.getStr() + next.gain.str) & 0xFFFF);
    newItem.setDex((newItem.getDex() + next.gain.dex) & 0xFFFF);
    newItem.setInt((newItem.getInt() + next.gain.int_) & 0xFFFF);
    newItem.setLuk((newItem.getLuk() + next.gain.luk) & 0xFFFF);
    newItem.setHp((newItem.getHp() + next.gain.hp) & 0xFFFF);
    newItem.setMp((newItem.getMp() + next.gain.mp) & 0xFFFF);
    newItem.setWatk((newItem.getWatk() + next.gain.watk) & 0xFFFF);
    newItem.setMatk((newItem.getMatk() + next.gain.matk) & 0xFFFF);
    newItem.setFlag(1);

    InventoryManipulator.removeFromSlot(cm.getC(), InventoryType.EQUIP, 1, 1, false);
    InventoryManipulator.addFromDrop(cm.getC(), newItem, false);

    增加进度("打造_装备栏1");
    cm.ShowWZEffect("UI/UIWindow/Quest/icon0");
    cm.sendOk("#b打造成功！当前打造等级：" + next.level);
    cm.dispose();
}
