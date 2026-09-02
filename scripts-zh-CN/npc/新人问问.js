/**
 * @author: 萧曵
 * @npc: 新人问问
 * @description:
 *   新人引导FAQ：纯文案说明 + 快捷跳转，不发放任何奖励。
 *   各分类点击后跳转到对应的已有功能NPC脚本，跳转统一借用大厅NPC 9900001 中转
 *   （cm.openNpc(9900001, scriptName) 的解析规则：先尝试 npc/scriptName.js，
 *   找不到再尝试 BeiDouSpecial/scriptName.js，因此 scriptName 既可以是裸文件名
 *   也可以是 "xy/xxx/xxx" 这种嵌套相对路径，已通过 装备中心.js/9900001.js 的现有调用验证）。
 *   打造系统(装备打造师)挂在NPC 9031003 自身菜单下，没有独立脚本名，
 *   因此直接 cm.openNpc(9031003) 打开该NPC本体菜单，再由玩家自行点选"打造"。
 */

var status = -1;
var page = 0;

function start() {
    status = -1;
    page = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) { status++; }
    else { cm.dispose(); return; }

    if (status === 0) {
        showMenu();
    } else if (status === 1) {
        doSelect(selection);
    } else {
        cm.dispose();
    }
}

function showMenu() {
    var text = "";
    text += "\t#e#r新人问问#k#n\r\n#d有疑问点这里，文字说明+一键跳转，看完直接传送过去#k\r\n\r\n";

    text += "━━━ 装备养成 ━━━\r\n";
    text += "#d装备怎么变强？强化/进阶/套装制作都在「装备中心」里#k\r\n";
    text += "#L1#>> 前往装备中心#l\r\n\r\n";

    text += "#d武器怎么升级？买初始武器后逐级消耗材料进阶，链路在「武器中心」#k\r\n";
    text += "#L2#>> 前往武器中心#l\r\n\r\n";

    text += "#d套装/防具怎么变强？「套服进阶」逐级消耗材料+三选一加成方向#k\r\n";
    text += "#L3#>> 前往套服进阶#l\r\n\r\n";

    text += "#d想自己打造装备？找「锻造师」升级品级、用配方打造装备#k\r\n";
    text += "#L4#>> 前往锻造师(装备打造)#l\r\n\r\n";

    text += "━━━ 日常与福利 ━━━\r\n";
    text += "#d每天能领什么？签到/在线奖励/探索/副本/跑环都在「每日活跃」一览#k\r\n";
    text += "#L5#>> 前往每日活跃#l\r\n\r\n";

    text += "#d刚来什么都没有？先看看「新人福利」有没有能领的礼包#k\r\n";
    text += "#L6#>> 前往新人福利#l\r\n\r\n";

    text += "#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function doSelect(selection) {
    switch (selection) {
        case 1:
            openSub("xy/portal/装备中心");
            break;
        case 2:
            openSub("xy/装备系统/v002/武器中心");
            break;
        case 3:
            openSub("xy/装备系统/v002/套服进阶");
            break;
        case 4:
            // 锻造师菜单挂在NPC本体9031003下，没有独立脚本名，直接打开该NPC
            cm.dispose();
            cm.openNpc(9031003);
            break;
        case 5:
            openSub("每日活跃");
            break;
        case 6:
            openSub("新人福利");
            break;
        case 9:
        default:
            cm.dispose();
            break;
    }
}

/**
 * 统一借用大厅NPC 9900001 中转跳转到目标脚本
 * @param scriptName 裸文件名(如"每日活跃")或嵌套相对路径(如"xy/装备系统/v002/武器中心")
 */
function openSub(scriptName) {
    cm.dispose();
    cm.openNpc(9900001, scriptName);
}
