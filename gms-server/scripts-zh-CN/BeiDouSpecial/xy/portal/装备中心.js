/**
 * @description 装备中心入口（可挂载到任意NPC或物品）
 * 包含：套装制作、装备强化、装备进阶、天赋学习、经验戒指、时装洗练、血衣合成、转世重生、翅膀称号
 */

var 皇冠 = "#fUI/UIWindow/UserInfo/bossPetCrown#";
var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) { status++; }
    else { cm.dispose(); return; }

    if (status === 0) { showMenu(); }
    else if (status === 1) { doSelect(selection); }
    else { cm.dispose(); }
}

function showMenu() {
    var text = "";
    text += "\t★━━\t#e #r装备中心#k#n \t━━★\r\n";
    text += "\t" + 皇冠.repeat(8) + "\r\n\r\n";
    text += "金币：#r" + new Intl.NumberFormat().format(cm.getPlayer().getMeso()) + "#k\r\n\r\n";
    text += "━━━ 制作与升级 ━━━\r\n";
    text += "#L500##r套装制作#k  #d制作各套装备#k#l\r\n";
    text += "#L104#装备强化  #d强化装备属性#k#l\r\n";
    text += "#L111#装备进阶  #d提升装备品阶#k#l\r\n";
    text += "#L506#血衣合成  #d合成血衣套装#k#l\r\n";
    text += "#L508#装备鉴定  #d鉴定/重铸装备词条#k#l\r\n";
    text += "\r\n━━━ 属性提升 ━━━\r\n";
    text += "#L502#天赋学习  #d消耗AP加点基础属性#k#l\r\n";
    text += "#L503##r经验戒指#k  #d购买提升经验的戒指#k#l\r\n";
    text += "#L504#时装洗练  #d随机洗练时装属性#k#l\r\n";
    text += "\r\n━━━ 外观与其他 ━━━\r\n";
    text += "#L505#翅膀称号  #d购买翅膀与称号#k#l\r\n";
    text += "#L507#转世重生  #d达到最高级后转生#k#l\r\n";
    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function doSelect(selection) {
    switch (selection) {
        case 500: openSub("xy/装备系统/v000/套装制作升级"); break;
        case 104: openSub("装备强化"); break;
        case 111: openSub("xy/装备系统/v002/武器进阶"); break;
        case 506: openSub("xy/血衣合成"); break;
        case 508: openSub("xy/装备系统/v002/装备鉴定"); break;
        case 502: openSub("xy/天赋学习"); break;
        case 503: openSub("xy/经验戒指"); break;
        case 504: openSub("xy/时装洗练"); break;
        case 505: openSub("xy/翅膀称号"); break;
        case 507: openSub("xy/转世重生"); break;
        case 9:   cm.dispose(); break;
        default:
            cm.sendOk("该功能暂未开放，敬请期待！");
            cm.dispose();
    }
}

function openSub(scriptName) {
    cm.dispose();
    cm.openNpc(9900001, scriptName);
}
