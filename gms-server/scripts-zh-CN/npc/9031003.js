/**
 * @author: 萧曵
 * @npc: 9031003 装备综合工坊
 * @description:
 *   覆盖原"装备收集"强化脚本(旧逻辑已废弃，编码也是乱码，不再保留)。
 *   主菜单按"9031003完整选项"截图对齐为5项官方功能 + 2项已上线的额外功能，跳转关系如下：
 *     #L1 装备进阶  —— 本脚本内置(原"武器进阶")，目前仅支持武器，防具进阶待补充数据后扩展
 *     #L2 装备锻造  —— 本脚本内置(原"打造"，作用于装备栏第1格)
 *     #L3 装备鉴定  —— cm.openNpc(9031003,"装备鉴定") 跳转到 npc/装备鉴定.js
 *                      (该脚本原位于 BeiDouSpecial/xy/装备系统/v002/ 下，因 NPCScriptManager 的
 *                      BeiDouSpecial 回退查找不支持多级子目录，已复制一份到 npc/ 目录下供跳转使用)
 *     #L4 装备打造  —— cm.openNpc(9031003,"装备打造师") 跳转到 npc/装备打造师.js(锻造师副职业)
 *     #L5 装备强化  —— cm.openNpc(9031003,"装备强化") 跳转到 BeiDouSpecial/装备强化.js(★强化系统)
 *     #L6 经验项链(制作/升级/戒指) —— 跳转到 npc/经验项链.js，对应"7、项链升级(经验项链)"官方菜单(制作项链/升级项链/制作戒指)
 *     #L7 装备战力排行榜      —— 跳转到 npc/装备战力排行榜.js，官方截图未列出，作为额外功能保留
 *
 *   本脚本内置两大功能：
 *   1. 装备进阶(原武器进阶)：5个武器职业分组各自有独立的进阶路线；同一阶段5个分组共享消耗(金币+装备升级石等材料)，
 *      但每个分组在同一阶段获得的属性加成不同。"职业"仅指武器所属职业分组，与角色实际职业无关
 *      (例如战士也可以装备/进阶其他分组的武器)。只有 baseWeaponList 中列出的初始武器可以进入进阶路线。
 *      进阶会消耗背包装备栏第1格当前的武器本身(作为材料一并扣除)，进阶成功后该武器被移除，
 *      改为发放该阶段对应的新武器(targetItemIdByJob，按职业分组各自不同)，而不是在原武器上叠加属性。
 *   2. 装备锻造(原打造，装备栏第1格)：共6级(D1~D6)，1~6级四维(STR/DEX/INT/LUK)依次+1/+1/+2/+3/+4/+5，
 *      2~6级附加生命值/法力值，5~6级额外攻击力+2/魔法力+4；材料为对应邮票(D1~D4各用1种邮票，D5/D6需4种邮票各15/30个)+枫叶，
 *      邮票/枫叶物品ID已在 wz-zh-CN/String.wz/Etc.img.xml 核实(4002000~4002003/4001126)，金币数值截图未给出，暂保留占位。
 *
 *   持久化方案：经查证 org.gms.client.Character.java，getBossLog/setBossLog 系按"自然日重置"的每日计数，
 *   不适合做永久进度；改用 getOneTimeLog(logid)/setOneTimeLog(logid)：该计数永久不重置，且每调用一次 set 仅 +1。
 *   恰好天然契合"阶段/等级"语义——调用次数即当前阶段/等级，无需额外字段，故选用此方案。
 *   logid 命名："武器进阶_<jobGroupId>"，打造使用 "打造_装备栏1"。
 *
 *   !!! 标记 TODO 的数值均为占位，需要按策划数据表回填，回填前不影响脚本运行(仅以占位数值展示) !!!
 */

// ========== 武器进阶 配置 ==========
// 武器职业分组：0=战士 1=法师 2=弓箭手 3=盗贼 4=海盗
var weaponJobGroups = [
    { id: 0, name: "战士武器" },
    { id: 1, name: "法师武器" },
    { id: 2, name: "弓箭手武器" },
    { id: 3, name: "盗贼武器" },
    { id: 4, name: "海盗武器" }
];

// 每个职业分组允许进入进阶路线的初始武器列表(固定列表，未在此列表中的武器不可进阶)
// TODO: 以下itemId均为占位，需按实际初始武器表回填
var baseWeaponList = [
    [0, [1302000 /*TODO 战士初始武器1*/, 1312000 /*TODO 战士初始武器2*/]],
    [1, [1372000 /*TODO 法师初始武器1*/]],
    [2, [1452000 /*TODO 弓箭手初始武器1*/]],
    [3, [1332000 /*TODO 盗贼初始武器1*/]],
    [4, [1492000 /*TODO 海盗初始武器1*/]]
];

// 进阶阶段表：共享消耗(costGold/costUpgradeStone/costExtraMaterials)，按职业分组分别给出属性加成gainByJob
// gainByJob下标与 weaponJobGroups 的 id 对应，格式 [str, dex, int, luk, watk, matk, physDef, magicDef, acc]
// 截图中可清晰辨认的仅"装备升级石"这一材料名称及部分阶段名；具体数值大多被遮挡/像素化，暂留TODO占位
var upgradeStoneId = 4020010; // TODO: 确认"装备升级石"实际物品ID(暂取截图同批次"石"类物品中的一个占位)
var advanceStages = [
    {
        name: "4层年兽叶贴关卡 进阶",
        costGold: 0, // TODO
        costUpgradeStone: 0, // TODO
        extraMaterials: [], // 格式 [itemId, qty]，本阶段截图未显示额外材料
        targetItemIdByJob: [0, 0, 0, 0, 0], // TODO: 该阶段5个职业分组各自对应的新武器itemId(战士/法师/弓箭手/盗贼/海盗)
        gainByJob: [
            [0, 0, 0, 0, 0, 0, 0, 0, 0], // TODO 战士
            [0, 0, 0, 0, 0, 0, 0, 0, 0], // TODO 法师
            [0, 0, 0, 0, 0, 0, 0, 0, 0], // TODO 弓箭手
            [0, 0, 0, 0, 0, 0, 0, 0, 0], // TODO 盗贼
            [0, 0, 0, 0, 0, 0, 0, 0, 0]  // TODO 海盗
        ]
    },
    {
        name: "第1/2/3不速之客",
        costGold: 0, // TODO
        costUpgradeStone: 0, // TODO
        extraMaterials: [],
        targetItemIdByJob: [0, 0, 0, 0, 0], // TODO: 该阶段5个职业分组各自对应的新武器itemId(战士/法师/弓箭手/盗贼/海盗)
        gainByJob: [
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0]
        ]
    },
    {
        name: "最终不速之客 进阶",
        costGold: 0, // TODO
        costUpgradeStone: 0, // TODO
        extraMaterials: [],
        targetItemIdByJob: [0, 0, 0, 0, 0], // TODO: 该阶段5个职业分组各自对应的新武器itemId(战士/法师/弓箭手/盗贼/海盗)
        gainByJob: [
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0]
        ]
    },
    {
        name: "至尊不速之客",
        costGold: 0, // TODO
        costUpgradeStone: 0, // TODO
        extraMaterials: [],
        targetItemIdByJob: [0, 0, 0, 0, 0], // TODO: 该阶段5个职业分组各自对应的新武器itemId(战士/法师/弓箭手/盗贼/海盗)
        gainByJob: [
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0]
        ]
    },
    {
        name: "60级金枫叶枪 进阶",
        costGold: 0, // TODO
        costUpgradeStone: 0, // TODO
        extraMaterials: [[0 /*TODO 装备银造石*/, 0]],
        targetItemIdByJob: [0, 0, 0, 0, 0], // TODO: 该阶段5个职业分组各自对应的新武器itemId(战士/法师/弓箭手/盗贼/海盗)
        gainByJob: [
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0]
        ]
    },
    {
        name: "70级紫枫叶枪",
        costGold: 0, // TODO
        costUpgradeStone: 0, // TODO
        extraMaterials: [[0 /*TODO 普通锻铁石*/, 0]],
        targetItemIdByJob: [0, 0, 0, 0, 0], // TODO: 该阶段5个职业分组各自对应的新武器itemId(战士/法师/弓箭手/盗贼/海盗)
        gainByJob: [
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0]
        ]
    },
    {
        name: "80级紫晶剧枪",
        costGold: 0, // TODO
        costUpgradeStone: 0, // TODO
        extraMaterials: [[0 /*TODO 中级精铁石*/, 0]],
        targetItemIdByJob: [0, 0, 0, 0, 0], // TODO: 该阶段5个职业分组各自对应的新武器itemId(战士/法师/弓箭手/盗贼/海盗)
        gainByJob: [
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0]
        ]
    },
    {
        name: "90级冰岛枪",
        costGold: 0, // TODO
        costUpgradeStone: 0, // TODO
        extraMaterials: [],
        targetItemIdByJob: [0, 0, 0, 0, 0], // TODO: 该阶段5个职业分组各自对应的新武器itemId(战士/法师/弓箭手/盗贼/海盗)
        gainByJob: [
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0]
        ]
    },
    {
        name: "100级传说幽冥怒枪/百战凌晨枪",
        costGold: 0, // TODO
        costUpgradeStone: 0, // TODO
        extraMaterials: [],
        targetItemIdByJob: [0, 0, 0, 0, 0], // TODO: 该阶段5个职业分组各自对应的新武器itemId(战士/法师/弓箭手/盗贼/海盗)
        gainByJob: [
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0],
            [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0, 0, 0, 0]
        ]
    }
];

// ========== 打造(装备栏第1格) 配置 ==========
// 4种邮票物品ID(已在 wz-zh-CN/String.wz/Etc.img.xml 核实): 绿蜗牛4002000/蓝蜗牛4002001/木妖4002002/绿水灵4002003；枫叶=4001126
// 数值依据用户提供的"装备打造(D装备)"截图：D1~D4各用1种邮票x25 + 枫叶，D5/D6需四种邮票各x15/x30 + 枫叶；
// 属性加成对应截图"打造1~6"：1~4级四维(STR/DEX/INT/LUK)+1/+1/+2/+3，2~6级附加生命值/法力值，5~6级额外攻击力+2/魔法力+4
// 金币消耗截图未给出具体数值(仅说明需要金币)，暂保留之前的递增占位，需用户校正
var stamp绿蜗牛 = 4002000, stamp蓝蜗牛 = 4002001, stamp木妖 = 4002002, stamp绿水灵 = 4002003, mapleLeaf = 4001126;

// 装备操作Java类(本服class已去掉"Maple"前缀，不可使用Packages.server.MapleXxx这种老Cosmic命名)
var InventoryType = Java.type('org.gms.client.inventory.InventoryType');
var InventoryManipulator = Java.type('org.gms.client.inventory.manipulator.InventoryManipulator');
var ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');
var craftLevels = [
    { level: 1, materials: [[stamp绿蜗牛, 25], [mapleLeaf, 100]], gold: 2000000, gain: { str: 1, dex: 1, int_: 1, luk: 1, hp: 0, mp: 0, watk: 0, matk: 0 } },
    { level: 2, materials: [[stamp蓝蜗牛, 25], [mapleLeaf, 200]], gold: 4000000, gain: { str: 1, dex: 1, int_: 1, luk: 1, hp: 10, mp: 10, watk: 0, matk: 0 } },
    { level: 3, materials: [[stamp木妖, 25], [mapleLeaf, 300]], gold: 8000000, gain: { str: 2, dex: 2, int_: 2, luk: 2, hp: 20, mp: 20, watk: 0, matk: 0 } },
    { level: 4, materials: [[stamp绿水灵, 25], [mapleLeaf, 400]], gold: 16000000, gain: { str: 3, dex: 3, int_: 3, luk: 3, hp: 30, mp: 30, watk: 0, matk: 0 } },
    { level: 5, materials: [[stamp绿蜗牛, 15], [stamp蓝蜗牛, 15], [stamp木妖, 15], [stamp绿水灵, 15], [mapleLeaf, 1000]], gold: 32000000, gain: { str: 4, dex: 4, int_: 4, luk: 4, hp: 50, mp: 50, watk: 2, matk: 4 } },
    { level: 6, materials: [[stamp绿蜗牛, 30], [stamp蓝蜗牛, 30], [stamp木妖, 30], [stamp绿水灵, 30], [mapleLeaf, 2000]], gold: 64000000, gain: { str: 5, dex: 5, int_: 5, luk: 5, hp: 50, mp: 50, watk: 2, matk: 4 } }
];
var craftSlotEquipType = 1; // 打造作用于装备栏第1格(武器栏)

var status = 0;
var menu = -1; // 1=武器进阶 2=打造
var selJobGroup = -1;
var selStageIndex = -1;
var selCraftLevel = -1;

function start() {
    status = 0;
    action(1, 0, 0);
}

// 永久阶段计数：调用一次 setOneTimeLog 自动 +1，恰好用作"当前阶段/等级"
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
        // 第一层：功能选择 —— 与"9031003完整选项"截图一致的5项官方菜单，外加2个已上线的额外功能
        var add = "欢迎使用装备综合工坊，请选择功能\r\n";
        add += "\r\n#L1##b装备进阶#k#l(给武器进行进阶，防具进阶待补充)";
        add += "\r\n#L2##b装备锻造#k#l(锻造特殊的装备道具，当前作用于装备栏第1格)";
        add += "\r\n#L3##b装备鉴定#k#l(将带有未鉴定的装备进行鉴定)";
        add += "\r\n#L4##b装备打造#k#l(给装备进行打造升级，增加属性)";
        add += "\r\n#L5##b装备强化#k#l(给装备进行强化，增加属性)";
        add += "\r\n\r\n#d---以下为额外功能---#k";
        add += "\r\n#L6##b经验项链(制作/升级/戒指)#k#l";
        add += "\r\n#L7##b装备战力排行榜#k#l";
        cm.sendSimple(add);
    } else if (status == 2 && selection == 3) {
        // 跳转到独立的"装备鉴定"脚本(scripts-zh-CN/npc/装备鉴定.js)，由该脚本接管后续对话
        cm.dispose();
        cm.openNpc(9031003, "装备鉴定");
        return;
    } else if (status == 2 && selection == 4) {
        // 跳转到独立的"装备打造师"脚本(scripts-zh-CN/npc/装备打造师.js)，由该脚本接管后续对话
        cm.dispose();
        cm.openNpc(9031003, "装备打造师");
        return;
    } else if (status == 2 && selection == 5) {
        // 跳转到独立的"装备强化"脚本(scripts-zh-CN/BeiDouSpecial/装备强化.js，通过npc/同名缺失后的回退路径解析)，由该脚本接管后续对话
        cm.dispose();
        cm.openNpc(9031003, "装备强化");
        return;
    } else if (status == 2 && selection == 6) {
        // 跳转到独立的"经验项链"脚本(scripts-zh-CN/npc/经验项链.js)，由该脚本接管后续对话
        cm.dispose();
        cm.openNpc(9031003, "经验项链");
        return;
    } else if (status == 2 && selection == 7) {
        // 跳转到独立的"装备战力排行榜"脚本(scripts-zh-CN/npc/装备战力排行榜.js)，由该脚本接管后续对话
        cm.dispose();
        cm.openNpc(9031003, "装备战力排行榜");
        return;
    } else if (status == 2) {
        menu = selection;
        if (menu == 1) {
            // 武器进阶：选择职业分组
            var add1 = "请选择要进阶的武器职业分组\r\n";
            for (var i = 0; i < weaponJobGroups.length; i++) {
                add1 += "\r\n#L" + weaponJobGroups[i].id + "##b" + weaponJobGroups[i].name + "#k#l";
            }
            cm.sendSimple(add1);
        } else if (menu == 2) {
            // 打造：直接进入打造确认(打造作用于当前装备栏第1格物品)
            var lv = 获取进度("打造_装备栏1");
            if (lv >= craftLevels.length) {
                cm.sendOk("#b打造等级已满级(" + craftLevels.length + "级)，无法继续打造！");
                cm.dispose();
                return;
            }
            var next = craftLevels[lv]; // lv从0开始，对应下一级 craftLevels[lv].level
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
        }
    } else if (status == 3 && menu == 1) {
        // 选定职业分组，展示当前阶段与下一阶段消耗
        selJobGroup = selection;
        var logid = "武器进阶_" + selJobGroup;
        var stage = 获取进度(logid);
        if (stage >= advanceStages.length) {
            cm.sendOk("#b该武器分组已达到最高进阶阶段！");
            cm.dispose();
            return;
        }
        selStageIndex = stage;
        var nextStage = advanceStages[stage];
        var adds = "当前阶段：#r" + stage + "#k (" + (stage == 0 ? "未进阶" : advanceStages[stage - 1].name) + ")\r\n";
        adds += "下一阶段：#b" + nextStage.name + "#k\r\n\r\n";
        adds += "需要消耗：\r\n金币 #r" + nextStage.costGold + "#k\r\n";
        adds += "装备升级石(#v " + upgradeStoneId + "#) x" + nextStage.costUpgradeStone + "\r\n";
        for (var e = 0; e < nextStage.extraMaterials.length; e++) {
            adds += "#v " + nextStage.extraMaterials[e][0] + "# x" + nextStage.extraMaterials[e][1] + "\r\n";
        }
        adds += "#r装备栏第1格当前的武器本身也将作为材料被一并消耗(不可保留)#k\r\n";
        adds += "\r\n是否进行进阶？(仅对装备栏第1格的已进阶路线武器生效，且需在 baseWeaponList 固定列表中)";
        cm.sendYesNo(adds);
    } else if (status == 4 && menu == 1 && type == 1 && selection == 1) {
        // 武器进阶确认执行
        执行武器进阶();
    } else if (status == 4 && menu == 2 && type == 1 && selection == 1) {
        // 打造确认执行
        执行打造();
    } else {
        cm.dispose();
    }
}

function 执行武器进阶() {
    var stage = advanceStages[selStageIndex];
    var weapon = cm.getInventory(1).getItem(1); // 装备栏第1格 = 武器栏(背包装备栏下标从1开始)
    if (weapon == null) {
        cm.sendOk("#b装备栏第1格(武器栏)没有武器，无法进阶！");
        cm.dispose();
        return;
    }
    // 校验是否为固定初始武器列表(仅在阶段0时校验初始武器，已进阶过的武器不再重复校验列表)
    if (selStageIndex == 0 && !检查是否固定初始武器(selJobGroup, weapon.getItemId())) {
        cm.sendOk("#b该武器不在本职业分组的初始进阶武器固定列表中，无法进入进阶路线！");
        cm.dispose();
        return;
    }
    if (cm.getMeso() < stage.costGold) {
        cm.sendOk("#b金币不足，无法进阶！");
        cm.dispose();
        return;
    }
    if (!cm.haveItem(upgradeStoneId, stage.costUpgradeStone)) {
        cm.sendOk("#b装备升级石数量不足，无法进阶！");
        cm.dispose();
        return;
    }
    for (var i = 0; i < stage.extraMaterials.length; i++) {
        if (!cm.haveItem(stage.extraMaterials[i][0], stage.extraMaterials[i][1])) {
            cm.sendOk("#b额外材料不足，无法进阶！");
            cm.dispose();
            return;
        }
    }

    var targetItemId = stage.targetItemIdByJob[selJobGroup];
    if (targetItemId <= 0) {
        cm.sendOk("#b该阶段尚未配置目标武器数据(TODO占位)，暂无法进阶！");
        cm.dispose();
        return;
    }

    // 扣材料和金币(背包装备栏第1格的当前武器本身也作为进阶材料一并消耗，不保留)
    cm.gainMeso(-stage.costGold);
    cm.gainItem(upgradeStoneId, -stage.costUpgradeStone);
    for (var j = 0; j < stage.extraMaterials.length; j++) {
        cm.gainItem(stage.extraMaterials[j][0], -stage.extraMaterials[j][1]);
    }
    // 消耗(移除)当前武器：沿用本服装备转换脚本的标准做法
    InventoryManipulator.removeFromSlot(cm.getC(), InventoryType.EQUIP, 1, 1, false);

    // 发放该阶段对应的新武器(按职业分组取目标武器模板)，并叠加该阶段属性加成
    var gain = stage.gainByJob[selJobGroup];
    var newWeapon = ItemInformationProvider.getInstance().getEquipById(targetItemId).copy();
    newWeapon.setStr((newWeapon.getStr() + gain[0]) & 0xFFFF);
    newWeapon.setDex((newWeapon.getDex() + gain[1]) & 0xFFFF);
    newWeapon.setInt((newWeapon.getInt() + gain[2]) & 0xFFFF);
    newWeapon.setLuk((newWeapon.getLuk() + gain[3]) & 0xFFFF);
    newWeapon.setWatk((newWeapon.getWatk() + gain[4]) & 0xFFFF);
    newWeapon.setMatk((newWeapon.getMatk() + gain[5]) & 0xFFFF);
    newWeapon.setFlag(1); // 上锁
    InventoryManipulator.addFromDrop(cm.getC(), newWeapon, false);

    增加进度("武器进阶_" + selJobGroup);
    cm.ShowWZEffect("UI/UIWindow/Quest/icon0");
    cm.sendOk("#b进阶成功！当前阶段：" + (selStageIndex + 1) + " (" + stage.name + ")");
    cm.dispose();
}

function 检查是否固定初始武器(jobGroup, itemId) {
    for (var i = 0; i < baseWeaponList.length; i++) {
        if (baseWeaponList[i][0] == jobGroup) {
            var list = baseWeaponList[i][1];
            for (var j = 0; j < list.length; j++) {
                if (list[j] == itemId) return true;
            }
            return false;
        }
    }
    return false;
}

function 执行打造() {
    var next = craftLevels[selCraftLevel];
    var weapon = cm.getInventory(1).getItem(1); // 装备栏第1格(背包装备栏下标从1开始)
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
    newItem.setFlag(1); // 上锁

    InventoryManipulator.removeFromSlot(cm.getC(), InventoryType.EQUIP, 1, 1, false);
    InventoryManipulator.addFromDrop(cm.getC(), newItem, false);

    增加进度("打造_装备栏1");
    cm.ShowWZEffect("UI/UIWindow/Quest/icon0");
    cm.sendOk("#b打造成功！当前打造等级：" + (next.level));
    cm.dispose();
}
